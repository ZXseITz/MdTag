package ch.zxseitz.mdtag.id3v2;

import ch.zxseitz.mdtag.IParseTag;
import ch.zxseitz.mdtag.Frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static ch.zxseitz.mdtag.Frame.*;

public class ID3v2 implements IParseTag {
    @Override
    public Map<Frame, String> parse(byte[] data) {
        var metadata = new HashMap<Frame, String>();
        var buffer = ByteBuffer.wrap(data);

        // size
        buffer.position(6);
        var tagSize = unsync(buffer);

        var frameIdBytes = new byte[4];
        // parse frames
        while (buffer.position() < tagSize) {
            buffer.get(frameIdBytes);
            var frameId = new String(frameIdBytes);
            var frameSize = buffer.getInt();
            var tag1 = buffer.get();
            var tag2 = buffer.get();
            var pos = buffer.position();

            // parse only text frames
            if (frameIdBytes[0] == 0x54) {
                var frameContent = buffer.get() == 0x0
                        ? parseISOLatin1(buffer, frameSize - 1)
                        : parseUnicode(buffer, frameSize - 1);
                switch (frameId) {
                    case "TALB":
                        metadata.put(ALBUM, frameContent);
                        break;
                    case "TCOM":
                        metadata.put(COMPOSER, frameContent);
                        break;
                    case "TCON":
                        metadata.put(CONTENT_TYPE, frameContent);
                        break;
                    case "TENC":
                        metadata.put(ENCODED_BY, frameContent);
                        break;
                    case "TEXT":
                        metadata.put(LYRICIST, frameContent);
                        break;
                    case "TIT1":
                        metadata.put(CONTENT_GROUP, frameContent);
                        break;
                    case "TIT2":
                        metadata.put(TITLE, frameContent);
                        break;
                    case "TIT3":
                        metadata.put(SUBTITLE, frameContent);
                        break;
                    case "TLAN":
                        metadata.put(LANGUAGE, frameContent);
                        break;
                    case "TLEN":
                        metadata.put(LENGTH, frameContent);
                        break;
                    case "TPE1":
                        metadata.put(LEAD_ARTIST, frameContent);
                        break;
                    case "TPE2":
                        metadata.put(BAND, frameContent);
                        break;
                    case "TPE3":
                        metadata.put(CONDUCTOR, frameContent);
                        break;
                    case "TPE4":
                        metadata.put(INTERPRETED, frameContent);
                        break;
                    case "TPUB":
                        metadata.put(PUBLISHER, frameContent);
                        break;
                    case "TRCK":
                        metadata.put(TRACK, frameContent);
                        break;
                    case "TYER":
                        metadata.put(YEAR, frameContent);
                    default:
                        System.out.println("Frame: " + frameId + " is ignored.");
                }
            }

            // override position if string was terminated
            buffer.position(pos + frameSize);
        }

        return metadata;
    }

    private static String parseISOLatin1(ByteBuffer buffer, int length) {
        var start = buffer.position();
        var count = 0;
        while (count < length && buffer.get() != 0x0) {
            count++;
        }
        return new String(buffer.array(), start, count, StandardCharsets.ISO_8859_1);
    }

    private static String parseUnicode(ByteBuffer buffer, int length) {
        var start = buffer.position();
        var count = 0;
        while (count < length && (buffer.get() != 0x0 || buffer.get() != 0x0)) {
            count += 2;
        }
        return new String(buffer.array(), start, count, StandardCharsets.UTF_16);
    }

    /**
     * Unsynchronizes an synchronized integer
     *
     * @param buffer Byte buffer to parse. Reads the next 4 bytes from the current position.
     * @return
     */
    public static int unsync(ByteBuffer buffer) {
        int i = buffer.get();
        for (int k = 1; k < 4; k++) {
            i <<= 7;
            i |= buffer.get();
        }
        return i;
    }
}
