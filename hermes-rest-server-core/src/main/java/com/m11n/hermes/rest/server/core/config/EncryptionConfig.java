package com.m11n.hermes.rest.server.core.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfig {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionConfig.class);

    @Bean
    public StringEncryptor encryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        //encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        //encryptor.setAlgorithm("PBEWITHSHA256AND128BITAES-CBC-BC");
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        //encryptor.setProvider(new BouncyCastleProvider());
        encryptor.setPassword("hermes");

        //logger.info("PASSWORD: '{}'", encryptor.encrypt("edgtds45"));

        return encryptor;
    }
}
