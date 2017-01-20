package com.xiaohongshu.richedittextpro;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void subStr() throws Exception {

        String sourceStr = "123456";
        String test = sourceStr.substring(0, 3);
        assertEquals(test, "123");
        assertEquals(sourceStr, "123456");
    }
}