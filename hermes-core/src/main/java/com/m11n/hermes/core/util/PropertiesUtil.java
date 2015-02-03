package com.m11n.hermes.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

public final class PropertiesUtil {
    private PropertiesUtil() {
        // hide
    }

    public static Properties getProperties() throws Exception {
        Properties p = new Properties();
        String hermesPath = System.getProperty("hermes.config");
        String commonPath = hermesPath.substring(0, hermesPath.lastIndexOf("/")) + "/common.properties";

        hermesPath = hermesPath.replace("file:", "");
        commonPath = commonPath.replace("file:", "");

        if(new File(commonPath).exists()) {
            p.load(new FileInputStream(commonPath));
        }

        p.load(new FileInputStream(hermesPath));

        return p;
    }

    public static Properties getHermesProperties() throws Exception {
        return load(System.getProperty("hermes.config"));
    }

    public static Properties getCommonProperties() throws Exception {
        String hermesPath = System.getProperty("hermes.config");
        String commonPath = hermesPath.substring(0, hermesPath.lastIndexOf("/")) + "/common.properties";

        try {
            return load(commonPath);
        } catch (Exception e) {
        }

        return new Properties();
    }

    public static void save(Properties properties) throws Exception {
        Properties hermesProperties = getHermesProperties();
        Properties commonProperties = getCommonProperties();

        for(Map.Entry<Object, Object> commonEntry : commonProperties.entrySet()) {
            if(properties.containsKey(commonEntry.getKey())) {
                commonProperties.put(commonEntry.getKey(), properties.get(commonEntry.getKey()));
            }
        }
        for(Map.Entry<Object, Object> hermesEntry : hermesProperties.entrySet()) {
            if(properties.containsKey(hermesEntry.getKey())) {
                hermesProperties.put(hermesEntry.getKey(), properties.get(hermesEntry.getKey()));
            }
        }

        hermesProperties.store(new FileOutputStream("hermes.properties"), "Hermes");
        commonProperties.store(new FileOutputStream("common.properties"), "Common");
    }

    private static Properties load(String file) throws Exception {
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
