package com.m11n.hermes.core.util;

import org.apache.commons.lang.StringUtils;

public final class PathUtil {
    private PathUtil() {
        // hide
    }

    public static String segment(String orderId) {
        String result = StringUtils.rightPad(orderId.substring(0, 5), orderId.length(), "0");
        result = result + "/" + StringUtils.rightPad(orderId.substring(0, 7), orderId.length(), "0");
        result = result + "/" + orderId;

        return result;
    }
}
