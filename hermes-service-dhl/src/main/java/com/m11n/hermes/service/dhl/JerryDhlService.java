package com.m11n.hermes.service.dhl;

import jodd.jerry.Jerry;
import org.springframework.stereotype.Service;

@Service
public class JerryDhlService extends AbstractDhlService {
    //private static final Logger logger = LoggerFactory.getLogger(JerryDhlService.class);

    public JerryDhlService() {

    }

    public String getTrackingStatus(String code) {
        String html = get("http://nolp.dhl.de/nextt-online-public/set_identcodes.do?idc=" + code);

        Jerry doc = Jerry.jerry(html);

        String result = doc.$(".col > h2:nth-child(1)").text();

        // TODO: needs more work

        return result;
    }
}
