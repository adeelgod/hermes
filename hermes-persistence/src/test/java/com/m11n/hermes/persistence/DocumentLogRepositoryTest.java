package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.DocumentLog;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Date;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class DocumentLogRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(DocumentLogRepositoryTest.class);

    @Inject
    private DocumentLogRepository documentLogRepository;

    @Test
    public void testSave() {
        DocumentLog log = new DocumentLog();
        log.setDocumentId("lkjasdfök");
        log.setOrderId("kjöasdfkj");
        log.setProcessedAt(new Date());

        log = documentLogRepository.save(log);

        logger.debug("*********************************** ENTITY ID: {}", log.getUid());
    }
}
