package net.kkolyan.utils.benchme;

import net.kkolyan.utils.benchme.util.StringLengthUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringLengthUtilTest {
    
    private StringLengthUtil stringLengthUtil;

    @Before
    public void init() {
        stringLengthUtil = new StringLengthUtil();
        String s = stringLengthUtil.ensureLength("asdasdasd");
    }

    @Test
    public void test1() {
        Assert.assertEquals("asdasdasd", stringLengthUtil.ensureLength("asdasdasd"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("asdasd   ", stringLengthUtil.ensureLength("asdasd"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("asd      ", stringLengthUtil.ensureLength("asd"));
    }

    @Test
    public void test4() {
        Assert.assertEquals("asdasdasdasd", stringLengthUtil.ensureLength("asdasdasdasd"));
        Assert.assertEquals("asdasdasd   ", stringLengthUtil.ensureLength("asdasdasd"));
    }
}
