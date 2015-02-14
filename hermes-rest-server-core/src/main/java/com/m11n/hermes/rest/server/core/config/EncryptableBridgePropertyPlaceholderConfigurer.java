package com.m11n.hermes.rest.server.core.config;

import org.apache.camel.spring.spi.BridgePropertyPlaceholderConfigurer;
import org.jasypt.commons.CommonUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.TextEncryptor;

public class EncryptableBridgePropertyPlaceholderConfigurer extends BridgePropertyPlaceholderConfigurer {
    private final StringEncryptor stringEncryptor;
    private final TextEncryptor textEncryptor;

    public EncryptableBridgePropertyPlaceholderConfigurer(final StringEncryptor stringEncryptor) {
        super();
        CommonUtils.validateNotNull(stringEncryptor, "Encryptor cannot be null");
        this.stringEncryptor = stringEncryptor;
        this.textEncryptor = null;
    }

    public EncryptableBridgePropertyPlaceholderConfigurer(final TextEncryptor textEncryptor) {
        super();
        CommonUtils.validateNotNull(textEncryptor, "Encryptor cannot be null");
        this.stringEncryptor = null;
        this.textEncryptor = textEncryptor;
    }

    @Override
    protected String convertPropertyValue(final String originalValue) {
        if (!PropertyValueEncryptionUtils.isEncryptedValue(originalValue)) {
            return originalValue;
        }
        if (this.stringEncryptor != null) {
            return PropertyValueEncryptionUtils.decrypt(originalValue, this.stringEncryptor);

        }
        return PropertyValueEncryptionUtils.decrypt(originalValue, this.textEncryptor);
    }

    @Override
    protected String resolveSystemProperty(final String key) {
        return convertPropertyValue(super.resolveSystemProperty(key));
    }
}
