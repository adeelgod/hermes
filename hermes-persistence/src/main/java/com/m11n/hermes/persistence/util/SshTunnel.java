package com.m11n.hermes.persistence.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class SshTunnel {
    private static final Logger logger = LoggerFactory.getLogger(SshTunnel.class);

    private String host = "188.138.99.252";
    private int port = 3306;
    private final String username = "print";
    private final String password = "edgtds45";
    private int localPort = 13306;

    private Session session;

    @PostConstruct
    public void start() {
        int assignedPort = 0;
        try {
            JSch jsch = new JSch();

            // Create SSH session.  Port 22 is your SSH port which
            // is open in your firewall setup.
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            // Additional SSH options.  See your ssh_config manual for
            // more options.  Set options according to your requirements.
            java.util.Properties config = new java.util.Properties();
            //config.put("Compression", "yes");
            //config.put("ConnectionAttempts","2");
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.setDaemonThread(true);

            // Connect
            session.connect();

            Channel channel = session.openChannel("shell");
            channel.connect();

            logger.info("######## CHANNEL CONNECTED: {}", channel.isConnected());

            // NOTE: the second host parameter is the binding address of the MySQL server... which is listening only on localhost
            //assignedPort = session.setPortForwardingL("127.0.0.1", localPort, host, port);
            assignedPort = session.setPortForwardingL(localPort, "localhost", port);

            logger.info("######## SESSION CONNECTED: {}", session.isConnected());

            logger.info("######## SERVER: {}", session.getServerVersion());
            logger.info("######## SERVER: {}", session.getHost());
            logger.info("######## CLIENT: {}", session.getClientVersion());
            logger.info("######## FWD: {}", session.getPortForwardingL());

            logger.info("##################### ASSIGNED PORT: {}", assignedPort);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

        if (assignedPort == 0) {
            throw new RuntimeException("Port forwarding failed !");
        }
    }

    @PreDestroy
    public void stop() {
        if(session!=null) {
            session.disconnect();
            logger.info("##################### DISCONNECTED.");
        }
    }

    public void restart() {
        stop();
        start();
    }

    private void test() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            java.sql.Connection connection = java.sql.DriverManager.getConnection("jdbc:mysql://127.0.0.1:" + localPort + "/Auswertung?user=" + username + "&password=" + password);

            java.sql.DatabaseMetaData metadata = connection.getMetaData();

            // Get all the tables and views
            String[] tableType = {"TABLE"};
            java.sql.ResultSet tables = metadata.getTables(null, null, "%", tableType);
            String tableName;
            while (tables.next()) {
                tableName = tables.getString(3);

                logger.info("################################ TABLE: {}", tableName);
            }

        } catch (ClassNotFoundException
                | IllegalAccessException
                | InstantiationException
                | java.sql.SQLException e) {
            logger.error(e.toString(), e);
        }
    }

    public static void main(String... args) throws Exception {
        SshTunnel tunnel = new SshTunnel();
        tunnel.start();
        tunnel.test();
        System.in.read();
        tunnel.stop();
    }
}
