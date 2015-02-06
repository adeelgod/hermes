package com.m11n.hermes.service.bank;

import org.junit.Test;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.InputStream;

public class EncodingTest {
    @Test
    public void testDetection() throws Exception {
        byte[] buf = new byte[4096];
        InputStream is = EncodingTest.class.getClassLoader().getResourceAsStream("data/umsatzliste_14.txt");

        // (1)
        UniversalDetector detector = new UniversalDetector(null);

        // (2)
        int nread;
        while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();

        // (4)
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        // (5)
        detector.reset();
    }
}
