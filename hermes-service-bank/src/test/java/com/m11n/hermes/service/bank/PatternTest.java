package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankStatementPattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class PatternTest
{
    private static final Logger logger = LoggerFactory.getLogger(PatternTest.class);

    @Before
    public void setUp() {
        System.setProperty("hermes.config", "src/test/resources/hermes.properties");
    }

    @Test
    public void testOrderIdPattern() throws Exception {
        BankStatementPattern bsp = new BankStatementPattern("default_order_id", 1, ".*(3\\d{8}|3\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d).*", true, 1, "orderId", true);
        bsp.setRegex(Pattern.compile(bsp.getPattern()));

        LineIterator it = IOUtils.lineIterator(PatternTest.class.getClassLoader().getResourceAsStream("data/umsatzliste_2.csv"), "UTF-8");
        it.next(); // NOTE: skip headers

        double hit = .0;
        double miss = .0;
        double total = .0;

        while(it.hasNext()) {
            String sample = it.next();

            Matcher m = bsp.getRegex().matcher(sample);

            if(m.matches()) {
                String value = m.group(bsp.getPatternGroup()).replaceAll(" ", "");

                logger.info("** ORDER {} = {}", bsp.getAttribute(), value);
                hit++;
            } else {
                logger.warn("** ORDER NO MATCH: {}", sample);
                miss++;
            }
            total++;
        }

        logger.warn("** ORDER RATIO - hit:{} vs. miss:{}", (hit / total), (miss / total));
    }

    @Test
    public void testClientIdPattern() throws Exception {
        BankStatementPattern bsp = new BankStatementPattern("default_client_id", 1, ".*(ku?n?de?n?\\.?\\s*)(nr\\.?)?(\\d{4}).*", true, 3, "clientId", true);
        bsp.setRegex(Pattern.compile(bsp.getPattern(), Pattern.CASE_INSENSITIVE));

        LineIterator it = IOUtils.lineIterator(PatternTest.class.getClassLoader().getResourceAsStream("data/umsatzliste_2.csv"), "UTF-8");
        it.next(); // NOTE: skip headers

        double hit = .0;
        double miss = .0;
        double total = .0;

        while(it.hasNext()) {
            String sample = it.next();

            Matcher m = bsp.getRegex().matcher(sample);

            if(m.matches()) {
                String value = m.group(bsp.getPatternGroup()).replaceAll(" ", "");

                logger.info("** CLIENT {} = {}", bsp.getAttribute(), value);
                hit++;
            } else {
                logger.warn("** CLIENT NO MATCH: {}", sample);
                miss++;
            }
            total++;
        }

        logger.warn("** CLIENT RATIO - hit:{} vs. miss:{}", (hit/total), (miss/total));
    }
}
