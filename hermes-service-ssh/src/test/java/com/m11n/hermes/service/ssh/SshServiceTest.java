package com.m11n.hermes.service.ssh;

import com.m11n.hermes.core.service.SshService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class SshServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(SshServiceTest.class);
    @Inject
    private SshService sshService;

    @Before
    public void setUp() {
    }

    @Test
    public void testCopy() throws Exception {
        sshService.connect();
        sshService.copy(
                "/home/l-carb-shop/public_html/var/intraship/documents/pdf--1/pdf--1f/pdf--1f8/label-00340433836188599101.pdf",
                "target/result.pdf");
        sshService.disconnect();
    }

    @Test
    public void testExec() throws Exception {
        int status;
        sshService.connect();
        status = sshService.exec("ls -la /home/l-carb-shop/public_html/var/intraship/documents\n");
        logger.info("Exec status: {}", status);
        status = sshService.exec("ls -la /home/l-carb-shop/public_html/var/intraship/documents/pdf--f\n");
        logger.info("Exec status: {}", status);
        sshService.disconnect();
    }
}
