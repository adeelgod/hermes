package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.service.BankService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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

    @Test
    public void testNumberFormat() throws Exception {
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}", nf.parse("34,80"));
    }

    @Test
    public void testExtract() throws Exception {
        LineIterator it = IOUtils.lineIterator(PatternTest.class.getClassLoader().getResourceAsStream("data/umsatzliste_2.csv"), "UTF-8");
        it.next(); // NOTE: skip headers

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

        while(it.hasNext()) {
            String line = it.next();
            String[] parts = line.split(";");

            BankStatement bs = new BankStatement();
            bs.setAccount(parts[0]);
            bs.setTransferDate(df.parse(parts[1]));
            bs.setValutaDate(df.parse(parts[2]));
            bs.setReceiver1(parts[3]);
            bs.setReceiver2(parts[4]);
            bs.setDescription(parts[5]);
            bs.setAmount(new BigDecimal(nf.parse(parts[6]).doubleValue()));
            bs.setCurrency(parts[7]);

            bs = bankService.extract(bs);

            logger.warn("** RATIO {} - {}", bs.getMatching(), bs);
        }
    }
}
