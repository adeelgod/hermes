package com.m11n.hermes.core.util;

import java.io.FileInputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private PropertiesUtil() {
        // hide
    }

    public static Properties getProperties() throws Exception {
        String file = System.getProperty("hermes.config");

        if(file==null) {
            file = "hermes.properties";
        } else {
            file = file.replace("file:", "");
        }

        Properties p = new Properties();
        p.load(new FileInputStream(file));

        return p;
    }
}
