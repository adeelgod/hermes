Update `hermes_bank_statement` set status = "ignore" where description like"%ABBUCHUNG VOM PAYPAL-KONTO %";
Update `hermes_bank_statement` set status = "ignore" where description_b like"%Amazon.Mktplce%";
Update `hermes_bank_statement` set status = "ignore" where description like"%PayPal Europe%";
Update `hermes_bank_statement` set status = "ignore" where description like"SEPA-GUTSCHRIFT PayPal Europe%";
Update `hermes_bank_statement` set status = "ignore" where description_b like"%AmazonMicropayment%";
Update `hermes_bank_statement` set status = "ignore" where description_b like"%AmazonMicropayment%";
Update `hermes_bank_statement` set status = "ignore" where amount < 0;
Update `hermes_bank_statement` set status = "ignore" where amount > 500;

TRUNCATE TABLE  `mage_custom_order_temp`; insert into mage_custom_order_temp SELECT * FROM `mage_custom_order` WHERE `payment_id` is null and Status != "canceled" and (method = "banktransfer" or method = "cashondelivery" or method = "invoice" or method = "pnsofort" or method = "phoenix_cashondelivery" or ( method = "m2epropayment" and typ = "ebay_vorkasse"));
TRUNCATE TABLE  `hermes_bank_statement_temp`; insert into hermes_bank_statement_temp SELECT * FROM `hermes_bank_statement` WHERE  Status != "confirm" and  Status  != "ignore"
