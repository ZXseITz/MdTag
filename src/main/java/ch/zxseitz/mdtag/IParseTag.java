package ch.zxseitz.mdtag;

import java.util.Map;

public interface IParseTag {
    Map<Frame, String> parse(byte[] data);
}
