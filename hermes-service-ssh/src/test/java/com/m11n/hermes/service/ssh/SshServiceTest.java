package com.m11n.hermes.service.ssh;

import com.m11n.hermes.core.service.SshService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class SshServiceTest {
    @Inject
    private SshService sshService;

    @Before
    public void setUp() {
    }

    @Test
    public void testCopy() throws Exception {
        sshService.copy("/home/l-carb-shop.de/public_html/var/intraship/documents/pdf--1/pdf--1f/pdf--1f8/label-00340433836188599101.pdf", "target/result.pdf");
    }
}
