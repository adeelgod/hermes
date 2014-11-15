package com.m11n.hermes.core.service;

public interface SshService {

    void copy(String remotePath, String localPath) throws Exception;
}
