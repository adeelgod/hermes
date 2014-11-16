package com.m11n.hermes.persistence;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.*;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class IntrashipDocumentRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(IntrashipDocumentRepositoryTest.class);

    @Inject
    private IntrashipDocumentRepository intrashipDocumentRepository;

    @Test
    public void testFindDocumentUrl() {
        String url = intrashipDocumentRepository.findFilePath("00340433836188598456");

        logger.debug("*********************************** DOCUMENT URL: {}", url);

        assertEquals("/home/l-carb-shop.de/public_html/var/intraship/documents/pdf--3/pdf--33/pdf--334/label-00340433836188598456.pdf", url);
    }
}
