package ch.zxseitz.mdtag;

public enum Frame {
    ALBUM("TALB"),
    COMPOSER("TCOM"),
    CONTENT_TYPE("TCON"),
    ENCODED_BY("TENC"),
    LYRICIST("TEXT"),
    CONTENT_GROUP("TIT1"),
    TITLE("TIT2"),
    SUBTITLE("TIT3"),
    LANGUAGE("TLAN"),
    LENGTH("TLEN"),
    LEAD_ARTIST("TPE1"),
    BAND("TPE2"),
    CONDUCTOR("TPE3"),
    INTERPRETED("TPE4"),
    PUBLISHER("TPUB"),
    TRACK("TRCK"),
    YEAR("TYER");

    public final String id;
    Frame(String id) {this.id = id;}
}
