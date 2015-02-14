package com.m11n.hermes.rest.api.core;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jcraft.jsch.Session;
import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.util.SshTunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Component
public class Version {
    private static final Logger logger = LoggerFactory.getLogger(Version.class);

    @Value("${git.branch}")
    private String branche;

    @Value("${git.commit.id.describe}")
    private String description;

    @Value("${git.commit.time}")
    private String commitTime;

    @Value("${git.build.time}")
    private String buildTime;

    @Value("${git.dirty}")
    private String dirty;

    @Value("${git.remote.origin.url}")
    private String originUrl;

    private int aliveInterval;

    private int aliveCountMax;

    private boolean connected;

    private String serverVersion;

    private String clientVersion;

    private String host;

    private int localPort;

    private String magentoServiceClass;

    @Inject
    @JsonIgnore
    private SshTunnel sshTunnel;

    @Inject
    @JsonIgnore
    private MagentoService magentoService;

    @PostConstruct
    public void init() {
        logger.info("=====================================");
        logger.info("=                                   =");
        logger.info("=     Hermes for L-Carb-Shop.de     =");
        logger.info("=                                   =");
        logger.info("=====================================");
        logger.info("Description  : {}", description);
        logger.info("Commit       : {}", commitTime);
        logger.info("Build        : {}", buildTime);
        logger.info("Dirty        : {}", dirty);
        logger.info("URL          : {}", originUrl);

        Session session = sshTunnel.getSession();

        aliveInterval = session.getServerAliveInterval();
        aliveCountMax = session.getServerAliveCountMax();
        connected = session.isConnected();
        host = session.getHost();
        localPort = sshTunnel.getLocalPort();
        serverVersion = session.getServerVersion();
        clientVersion = session.getClientVersion();

        logger.info("=====================================");
        logger.info("Keepalive    : {}", aliveInterval);
        logger.info("Alive max.   : {}", aliveCountMax);
        logger.info("Session      : {}", connected);
        logger.info("Server       : {}", host);
        logger.info("Port         : {}", localPort);
        logger.info("Version      : {}", serverVersion);
        logger.info("Client       : {}", clientVersion);

        magentoServiceClass = magentoService.getClass().getName();

        logger.info("=====================================");
        logger.info("Magento Class: {}", magentoServiceClass);
    }

    public String getBranche() {
        return branche;
    }

    public String getDescription() {
        return description;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public String getDirty() {
        return dirty;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public int getAliveInterval() {
        return aliveInterval;
    }

    public int getAliveCountMax() {
        return aliveCountMax;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public String getHost() {
        return host;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getMagentoServiceClass() {
        return magentoServiceClass;
    }

    @Override
    public String toString() {
        return "Version{" +
                "branche='" + branche + '\'' +
                ", description='" + description + '\'' +
                ", commitTime='" + commitTime + '\'' +
                ", buildTime='" + buildTime + '\'' +
                ", dirty=" + dirty +
                ", originUrl='" + originUrl + '\'' +
                '}';
    }
}
