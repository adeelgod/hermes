package com.m11n.hermes.service.dhl;

import com.m11n.hermes.core.model.DhlTrackingStatus;
import jodd.jerry.Jerry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class JerryDhlService extends AbstractDhlService {
    private static final Logger logger = LoggerFactory.getLogger(JerryDhlService.class);

    private static final String SUCCESS_MESSAGE = "the shipment has been successfully delivered".toLowerCase();

    private static final String SUCCESS_MESSAGE_COD = "The C.O.D. amount has been transferred to the payee".toLowerCase();

    private static final String SUCCESS_MESSAGE_PICKUP = "The recipient has picked up the shipment from".toLowerCase();

    private static final String SUCCESS_MESSAGE_READY = "The shipment is ready for pick-up".toLowerCase(); // TODO: success or pending?!?

    private static final String PENDING_MESSAGE = "The instruction data for this shipment have been provided by the sender to DHL electronically".toLowerCase();

    private static final String PENDING_PROCESSED_DESTINATION_MESSAGE = "The shipment has been processed in the destination".toLowerCase();

    private static final String PENDING_PROCESSED_PARCEL_CENTER_MESSAGE = "The shipment has been processed in the parcel center".toLowerCase();

    private static final String ERROR_MESSAGE_OLD = "we do not currently have information about shipment".toLowerCase();

    private static final String ERROR_MESSAGE = "No information is currently available for this shipment".toLowerCase();

    private static final int ROWS_OFFSET = 2;

    private static final int MAX_ROWS_PARSE = 5;

    private static final int prefix = "Status from ".length();

    private SimpleDateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");

    public JerryDhlService() {

    }

    public DhlTrackingStatus getTrackingStatus(String code) {
        try {
            String html = get("http://nolp.dhl.de/nextt-online-public/set_identcodes.do?idc=" + code);

            Jerry doc = Jerry.jerry(html);
//            OLD ERROR MESSAGE
//            String error = doc.$(".col > h2:nth-child(1)").text();

            String error = doc.$(".panel-heading > h1.panel-title").text();

            DhlTrackingStatus status = new DhlTrackingStatus();

            if(!StringUtils.isEmpty(error) && error.toLowerCase().trim().startsWith(ERROR_MESSAGE)) {
                status.setStatus("error");
                status.setDate(new Date());
                status.setMessage(error);
            } else {
                String date = null;
                String message = null;
                String next = null;

                for(int row=ROWS_OFFSET; row<MAX_ROWS_PARSE + ROWS_OFFSET; row++) {
                    date = doc.$(".table > tbody:nth-child(2) > tr:nth-child(" + row + ") > td:nth-child(2)").text();
                    message = doc.$(".table > tbody:nth-child(2) > tr:nth-child(" + row + ") > td:nth-child(3)").text();
                    next = doc.$(".table > tbody:nth-child(2) > tr:nth-child(" + row+1 + ") > td:nth-child(3)").text();

                    if(!StringUtils.isEmpty(date) && date.toLowerCase().contains("status from")) {
                        break;
                    }
                }

                try {
                    status.setDate(StringUtils.isEmpty(date) ? new Date() : df.parse(date.substring(prefix).trim()));
                } catch(ParseException pe) {
                    logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ## {}", code);
                    logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ## {} - {}", date, date.substring(prefix).trim());
                    logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ## {}", message);
                    logger.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ## {}", next);
                    status.setDate(new Date());
                }

                if(!StringUtils.isEmpty(message)) {
                    status.setMessage(message.trim());
                    if(status.getMessage().toLowerCase().startsWith(SUCCESS_MESSAGE) || status.getMessage().toLowerCase().startsWith(SUCCESS_MESSAGE_COD) || status.getMessage().toLowerCase().startsWith(SUCCESS_MESSAGE_PICKUP)) {
                        status.setStatus("success");
                    } else {
                        status.setStatus("warning");
                    }
                }

                if(!StringUtils.isEmpty(next)) {
                    status.setNext(next.trim());
                }
            }

            return status;
        } catch(Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) throws Exception {
        logger.debug("{}", new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm").parse("Sat, 13.12.2014 12:19"));
    }
}
