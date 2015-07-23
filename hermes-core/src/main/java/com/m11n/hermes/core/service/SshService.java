package com.m11n.hermes.core.service;

public interface SshService {

    void connect() throws Exception;

    void disconnect() throws Exception;

    void copy(String remotePath, String localPath) throws Exception;

    void upload(String localPath, String remotePath) throws Exception;

    int exec(String command) throws Exception;
}
