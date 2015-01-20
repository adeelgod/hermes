package com.m11n.hermes.service.dhl;

import com.m11n.hermes.core.model.DhlTrackingStatus;
import jodd.jerry.Jerry;
import org.springframework.stereotype.Service;

@Service
public class JerryDhlService extends AbstractDhlService {
    //private static final Logger logger = LoggerFactory.getLogger(JerryDhlService.class);

    public JerryDhlService() {

    }

    public DhlTrackingStatus getTrackingStatus(String code) {
        String html = get("http://nolp.dhl.de/nextt-online-public/set_identcodes.do?idc=" + code);

        Jerry doc = Jerry.jerry(html);

        String error = doc.$(".col > h2:nth-child(1)").text();

        DhlTrackingStatus status = new DhlTrackingStatus();
        status.setDate(doc.$(".table > tbody:nth-child(2) > tr:nth-child(2) > td:nth-child(2)").text());
        status.setMessage(doc.$(".table > tbody:nth-child(2) > tr:nth-child(2) > td:nth-child(3)").text());
        status.setNext(doc.$(".table > tbody:nth-child(2) > tr:nth-child(2) > td:nth-child(3)").text());

        // TODO: needs more work

        return status;
    }
}
