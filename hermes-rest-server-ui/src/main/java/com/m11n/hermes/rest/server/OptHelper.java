package com.m11n.hermes.rest.server;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

public class OptHelper {
    private static final Logger logger = LoggerFactory.getLogger(OptHelper.class);

    private static final String OPT_SECURITY = "security";
    private static final String OPT_CORS = "cors";
    private static final String OPT_HELP = "help";
    private static final String OPT_DEBUG = "debug";
    private static final String OPT_CONFIG = "config";
    private static final String OPT_DOMAIN = "domain";
    private static final String OPT_HOST = "host";
    private static final String OPT_PORT = "port";
    private static final String OPT_JERSEY = "jersey";
    private static final String OPT_SPRING = "spring";
    private static final String OPT_PERFORMANCE = "performance";
    private static final String OPT_COMSAT = "comsat";
    private static final String OPT_ENCODING = "encoding";

    private OptionSpec<String> security;
    private OptionSpec<String> cors;
    private OptionSpec help;
    private OptionSpec debug;
    private OptionSpec comsat;
    private OptionSpec<String> config;
    private OptionSpec<String> domain;
    private OptionSpec<String> host;
    private OptionSpec<Integer> port;
    private OptionSpec<String> jersey;
    private OptionSpec<String> spring;
    private OptionSpec performance;
    private OptionSpec<String> encoding;
    private OptionSet options;
    private OptionParser parser;

    public OptHelper(String... args) throws Exception {
        String configPath = System.getProperty("hermes.config")==null ? "" : System.getProperty("hermes.config");
        parser = new OptionParser();
        security = parser.accepts(OPT_SECURITY).withOptionalArg().defaultsTo("/*").ofType(String.class);
        cors = parser.accepts(OPT_CORS).withOptionalArg().defaultsTo("/*").ofType(String.class);
        help = parser.accepts(OPT_HELP);
        debug = parser.accepts(OPT_DEBUG);
        comsat = parser.accepts(OPT_COMSAT);
        config = parser.accepts(OPT_CONFIG).withRequiredArg().defaultsTo(configPath).ofType(String.class);
        domain = parser.accepts(OPT_DOMAIN).withRequiredArg().defaultsTo("localhost").ofType(String.class);
        host = parser.accepts(OPT_HOST).withRequiredArg().defaultsTo("127.0.0.1").ofType(String.class);
        port = parser.accepts(OPT_PORT).withRequiredArg().defaultsTo("8080").ofType(Integer.class);
        jersey = parser.accepts(OPT_JERSEY).withRequiredArg().defaultsTo("/*").ofType(String.class);
        spring = parser.accepts(OPT_SPRING).withRequiredArg().defaultsTo("/*").ofType(String.class);
        encoding = parser.accepts(OPT_ENCODING).withRequiredArg().defaultsTo("UTF-8").ofType(String.class);
        performance = parser.accepts(OPT_PERFORMANCE);

        options = parser.parse( args );
    }

    public String getSecurity() {
        return options.valueOf(security).replaceAll("\"", "");
    }

    public String getCors() {
        return options.valueOf(cors);
    }

    public String getConfig() {
        return options.valueOf(config);
    }

    public String getDomain() {
        return options.valueOf(domain).replaceAll("\"", "");
    }

    public String getHost() {
        return options.valueOf(host).replaceAll("\"", "");
    }

    public String getEncoding() {
        return options.valueOf(encoding).replaceAll("\"", "");
    }

    public Integer getPort() {
        return options.valueOf(port);
    }

    public String getJersey() {
        return options.valueOf(jersey).replaceAll("\"", "");
    }

    public String getSpring() {
        return options.valueOf(spring).replaceAll("\"", "");
    }

    public boolean hasSecurity() {
        return options.has(security);
    }

    public boolean hasEncoding() {
        return options.has(encoding);
    }

    public boolean hasCors() {
        return options.has(cors);
    }

    public boolean hasHelp() {
        return options.has(help);
    }

    public boolean hasDebug() {
        return options.has(debug);
    }

    public boolean hasComsat() {
        return options.has(comsat);
    }

    public boolean hasConfig() {
        return options.has(config);
    }

    public boolean hasDomain() {
        return options.has(domain);
    }

    public boolean hasHost() {
        return options.has(host);
    }

    public boolean hasPort() {
        return options.has(port);
    }

    public boolean hasJersey() {
        return options.has(jersey);
    }

    public boolean hasSpring() {
        return options.has(spring);
    }

    public boolean hasPerformance() {
        return options.has(performance);
    }

    public void printHelp() throws Exception {
        StringWriter writer = new StringWriter();
        parser.printHelpOn(writer);

        logger.info(writer.toString());
    }
}
