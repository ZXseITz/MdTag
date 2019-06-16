package ch.zxseitz.mdtag;

import ch.zxseitz.mdtag.id3v2.ID3v2;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class TagHandler {
    private static TagHandler instance = new TagHandler();

    public static TagHandler getInstance() {
        return instance;
    }

    private IParseTag id3v2;

    private TagHandler() {
        this.id3v2 = new ID3v2();
    }

    public Map<Frame, String> parse(Path path) {
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path))) {
            var data = bis.readAllBytes();

            // check ID3v2
            if (data[0] == 0x49 && data[1] == 0x44 && data[2] == 0x33) {
                return id3v2.parse(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
