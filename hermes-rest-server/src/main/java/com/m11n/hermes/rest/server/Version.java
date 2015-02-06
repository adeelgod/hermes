package com.m11n.hermes.rest.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void init() {
        logger.info("=====================================");
        logger.info("=                                   =");
        logger.info("=     Hermes for L-Carb-Shop.de     =");
        logger.info("=                                   =");
        logger.info("=====================================");
        logger.info(description);
        logger.info(commitTime);
        logger.info(buildTime);
        logger.info(dirty);
        logger.info(originUrl);
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
