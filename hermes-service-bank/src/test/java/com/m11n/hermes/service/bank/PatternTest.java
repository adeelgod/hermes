package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.model.BankStatementPattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
@ActiveProfiles("debug")
public class PatternTest
{
    private static final Logger logger = LoggerFactory.getLogger(PatternTest.class);

    @Before
    public void setUp() {
        System.setProperty("hermes.config", "src/test/resources/hermes.properties");
    }

    @Test
    public void testClientName() throws Exception {
        parse(new BankStatementPattern("default_client_firstname", 2, "^SEPA-GUTSCHRIFT(\\sIM\\sAUFTR\\.V\\.)?\\s+([a-zA-Z]+)\\s+([a-zA-Z]+).*", true, 2, "firstname", true));
        parse(new BankStatementPattern("default_client_lastname", 3, "^SEPA-GUTSCHRIFT(\\sIM\\sAUFTR\\.V\\.)?\\s+([a-zA-Z]+)\\s+([a-zA-Z]+).*", true, 3, "lastname", true));
    }

    @Test
    public void testOrderIdPattern() throws Exception {
        parse(new BankStatementPattern("default_order_id", 1, ".*(b\\s?e\\s?s\\s?t\\s?e\\s?l\\s?l\\s*nr\\.?\\:?)?(3\\d{8}|3\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d\\s?\\d).*", true, 2, "orderId", true));
    }

    @Test
    public void testClientIdPattern() throws Exception {
        parse(new BankStatementPattern("default_client_id", 1, ".*(ku?n?de?n?\\.?\\s*)(nr\\.?)?(\\d{4}).*", true, 3, "clientId", true));
    }

    public void parse(BankStatementPattern bsp) throws Exception {
        if(bsp.getCaseInsensitive()) {
            bsp.setRegex(Pattern.compile(bsp.getPattern(), Pattern.CASE_INSENSITIVE));
        } else {
            bsp.setRegex(Pattern.compile(bsp.getPattern()));
        }

        LineIterator it = IOUtils.lineIterator(PatternTest.class.getClassLoader().getResourceAsStream("data/umsatzliste_2.csv"), "UTF-8");
        it.next(); // NOTE: skip headers

        double hit = .0;
        double miss = .0;
        double total = .0;

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

            Matcher m = bsp.getRegex().matcher(bs.getDescription());

            if(m.matches()) {
                String value = m.group(bsp.getPatternGroup()).replaceAll(" ", "");

                logger.info("** {} = {}", bsp.getAttribute().toUpperCase(), value);
                hit++;
            } else {
                logger.warn("** {} NO MATCH: {}", bsp.getAttribute().toUpperCase(), bs.getDescription());
                miss++;
            }
            total++;
        }

        logger.warn("** {} RATIO - hit:{} vs. miss:{}", bsp.getAttribute().toUpperCase(), (hit/total), (miss/total));
    }
}
