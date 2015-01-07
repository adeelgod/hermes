package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.service.BankService;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.util.Locale;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
@ActiveProfiles("debug")
public class BankServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(BankServiceTest.class);

    @Inject
    private BankService bankService;

    @Before
    public void setUp() {
        System.setProperty("hermes.config", "src/test/resources/hermes.properties");
    }

    @Test
    public void testParse() {
        Money amount = Money.parse("EUR 34.80");

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {} ({})", amount, amount.getAmount());
    }

    public void testNumberFormat() throws Exception {
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", nf.parse("34,80"));
    }
}
