package com.m11n.hermes.rest.server.processor;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes-test.xml"})
public class DocumentSplitProcessorTest
{
    private static final Logger logger = LoggerFactory.getLogger(DocumentSplitProcessorTest.class);

    @Inject
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Before
    public void setUp() {
    }

    @Test
    public void testSplitInvoice() throws Exception {
        producer.sendBody("vm:split", new File("./inbox/invoice2014-09-22_12-01-34.pdf"));
    }

    @Test
    public void testSplitLabels() throws Exception {
        //producer.sendBody("vm:split", new File("./inbox/intraship_labels_2014-09-22_12-08-14.pdf"));
        producer.sendBody("vm:split", new File("./inbox/labels.pdf"));
    }
}
