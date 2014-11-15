package com.m11n.hermes.service.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.m11n.hermes.core.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Properties;

@Service
public class DefaultSshService implements SshService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSshService.class);

    @Value("${hermes.ssh.port}")
    private int sshPort;

    @Value("${hermes.ssh.username}")
    private String username;

    @Value("${hermes.ssh.password}")
    private String password;

    @Value("${hermes.ssh.remote.host}")
    private String remoteHost;

    @Override
    public void copy(String remotePath, String localPath) throws Exception {
        Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch ssh = new JSch();
        Session session = ssh.getSession(username, remoteHost, sshPort);
        session.setConfig(config);
        session.setPassword(password);
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftp = (ChannelSftp) channel;

        sftp.get(remotePath, new FileOutputStream(localPath));
        channel.disconnect();
        session.disconnect();
    }
}
