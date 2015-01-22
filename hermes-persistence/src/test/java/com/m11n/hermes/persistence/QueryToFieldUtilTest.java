package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.persistence.util.QueryToFieldsUtil;
import org.apache.commons.io.IOUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class QueryToFieldUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(QueryToFieldUtilTest.class);

    @Inject
    private QueryToFieldsUtil queryToFieldsUtil;

    @Test
    public void testToFields() throws Exception {
        List<FormField> fields = queryToFieldsUtil.toFields("auswertung", IOUtils.toString(QueryToFieldUtilTest.class.getClassLoader().getResourceAsStream("abzufuellen.sql")));

        for(FormField field : fields) {
            logger.debug("++++++++++++++++ FIELD: {}", field);
        }
    }
}
