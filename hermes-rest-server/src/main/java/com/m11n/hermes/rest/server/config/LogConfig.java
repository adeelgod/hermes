package com.m11n.hermes.rest.server.config;

import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class LogConfig {
    public static void configure() {
        // NOTE: necessary for JUL to correctly bridge to backend (Logback)
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }
}
