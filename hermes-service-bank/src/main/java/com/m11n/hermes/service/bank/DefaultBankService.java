package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.service.BankService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

@Service
public class DefaultBankService implements BankService {
    public BankStatement convert(Map<String, String> entry) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        String tmp = entry.get("account") + "|" + entry.get("transferDate") + "|" + entry.get("valutaDate") + "|" + entry.get("receiver1") + "|" + entry.get("receiver2") + "|" + entry.get("description") + "|" + entry.get("amount") + "|" + entry.get("currency");

        BankStatement bs = new BankStatement();
        bs.setHash(DigestUtils.sha256Hex(tmp));
        bs.setAccount(entry.get("account"));
        bs.setTransferDate(df.parse(entry.get("transferDate")));
        bs.setValutaDate(df.parse(entry.get("valutaDate")));
        bs.setReceiver1(entry.get("receiver1"));
        bs.setReceiver2(entry.get("receiver2"));
        bs.setDescription(entry.get("description"));
        bs.setAmount(nf.parse(entry.get("amount")).doubleValue());
        bs.setCurrency(entry.get("currency"));

        return bs;
    }
}
