package com.m11n.hermes.service.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.m11n.hermes.core.service.SshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.Properties;

@Service
@Scope("prototype")
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

    private Session session;

    @Override
    public void connect() throws Exception {
        Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch ssh = new JSch();
        session = ssh.getSession(username, remoteHost, sshPort);
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
    }

    public void disconnect() throws Exception {
        session.disconnect();
    }

    @Override
    public void copy(String remotePath, String localPath) throws Exception {
        ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
        channel.setOutputStream(new LoggingOutputStream(logger, false));
        channel.setPty(true);
        channel.connect();
        channel.get(remotePath, new FileOutputStream(localPath));
        channel.disconnect();
    }

    public int exec(String command) throws Exception {
        ChannelExec channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        channel.setPty(true);
        channel.setErrStream(new LoggingOutputStream(logger, true));
        channel.setOutputStream(new LoggingOutputStream(logger, false));
        channel.connect();

        int count=50;
        final int delay=100;
        while (true) {
            if (channel.isClosed()) {
                break;
            }
            if (count-- < 0) {
                break;
            }
            Thread.sleep(delay);
        }

        int status = channel.getExitStatus();

        channel.disconnect();

        return status;
    }
}
