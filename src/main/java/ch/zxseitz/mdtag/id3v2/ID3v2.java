package ch.zxseitz.mdtag.id3v2;

import ch.zxseitz.mdtag.IParseTag;
import ch.zxseitz.mdtag.Frame;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ID3v2 implements IParseTag {
    public static byte[] id = new byte[] {
           0x49, 0x44, 0x33
    };

    private HashMap<String, Frame> frames;

    public ID3v2() {
        this.frames = new HashMap<>();
        for (var frame : Frame.values()) {
            frames.put(frame.id, frame);
        }
    }

    @Override
    public Map<Frame, String> parse(byte[] data) {
        var metadata = new HashMap<Frame, String>();
        var buffer = ByteBuffer.wrap(data);

        // size
        buffer.position(6);
        var tagSize = unsync(buffer.getInt());

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
                var frame = frames.get(frameId);
                if (frame != null) {
                    metadata.put(frame, frameContent);
                } else {
                    System.out.println("Frame \"" + frameId + "\" is ignored.");
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

    public byte[] write(Map<Frame, String> metadata) {
        var buffer = ByteBuffer.allocate(4096);
        buffer.put(id);



        return buffer.array();
    }

    private static byte[] writeFrame(Frame frame, String content) {
        return new byte[0];
    }

    /**
     * Unsynchronizes a synchronized integer
     *
     * @param value synchronized integer
     * @return unsynchronized integer
     */
    public static int unsync(int value) {
        var pattern = 0b11111111;
        var i = (value >>> 24) & pattern;
        for (var k = 2; k >= 0; k--) {
            i <<= 7;
            i += (value >>> k * 8) & pattern;
        }
        return i;
    }

    /**
     * Synchronizes an unsynchronized integer
     *
     * @param value unsynchronized integer
     * @return synchronized integer
     */
    public static int sync(int value) {
        var pattern = 0b1111111;
        var i = (value >>> 21) & pattern;
        for (var k = 2; k >= 0; k--) {
            i <<= 8;
            i += (value >>> k * 7) & pattern;
        }
        return i;
    }
}
