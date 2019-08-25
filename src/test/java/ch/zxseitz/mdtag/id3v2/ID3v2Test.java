package ch.zxseitz.mdtag.id3v2;

import org.junit.Assert;
import org.junit.Test;

public class ID3v2Test {
    @Test
    public void testSync() {
        var x = 249504231;
        var y = 1987855207;

        Assert.assertEquals(y, ID3v2.sync(x));
    }

    @Test
    public void testUnSync() {
        var x = 1987855207;
        var y = 249504231;

        Assert.assertEquals(y, ID3v2.unsync(x));
    }
}
