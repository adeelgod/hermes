insert ignore into mage_custom_order (`Bestellung`, `order_id`, `weight`, `Kundenummer`, `Kunden_email`, `Kunden_vorname`, `Kunden_name`, `Datum_Kauf`, `GesamtPreis_der_Bestellung_Brutto`,`total_refunded_netto`,`discount`, `erstattet`, `Versandkosten_netto`, `Versandkosten_erstattet`, `Nachnahme`) select b.increment_id, b.entity_id, b.weight, b.customer_id, b.customer_email, b.customer_firstname, b.customer_lastname,  b.created_at,  b.grand_total ,b.base_total_offline_refunded +base_total_online_refunded ,-b.base_discount_amount, b.base_total_refunded, b.base_shipping_amount, b.shipping_refunded, b.cod_fee from  l_carb_shop_de.mage_sales_flat_order as b   ;

update  mage_custom_order as a left join l_carb_shop_de.mage_m2epro_order as b on b.magento_order_id = a.order_id set a.m2e_order_id = b.id where b.magento_order_id = a.order_id;

update  mage_custom_order as a left join l_carb_shop_de.mage_m2epro_ebay_order  as b on b.order_id    =  a.m2e_order_id set a.ebay_order_id = b.ebay_order_id , a.Kunden_ebay_name = b.buyer_user_id where b.order_id    =  a.m2e_order_id ;

update  mage_custom_order as a left join l_carb_shop_de.mage_m2epro_amazon_order as b on b.order_id    =  a.m2e_order_id set a.amazon_order_id = b.amazon_order_id where b.order_id    =  a.m2e_order_id  ;

update  mage_custom_order as a left join l_carb_shop_de.mage_m2epro_ebay_order_external_transaction   as b on b.order_id    =  a.m2e_order_id set a.`Paypal_id_ebay`=b.transaction_id , a.`Paypal_Umsatz_ebay` =b.`sum` , a.`Paypal_gebueren_ebay`=b.fee  where b.order_id    =  a.m2e_order_id   ;

update  mage_custom_order as a left join l_carb_shop_de.mage_sales_flat_invoice    as b on b.order_id    =  a.order_id  set a.`invoice_id`=b.`entity_id` , a.`Paypal_id_shop` =b.`transaction_id`, a.VAT = b.tax_amount   where b.order_id    =  a.order_id    ;

update  mage_custom_order as a left join l_carb_shop_de.mage_sales_flat_order_payment    as b on b.parent_id    =  a.order_id  set a.`method`=b.`method`    where b.parent_id    =  a.order_id    ;

update  mage_custom_order as a left join l_carb_shop_de.mage_sales_flat_order    as b on b.entity_id    =  a.order_id  set a.`status`=b.`status`    where b.entity_id    =  a.order_id    ;

update  mage_custom_order as a left join l_carb_shop_de.mage_sales_flat_shipment    as b on b.order_id    =  a.order_id  set a.`shipping_id`=b.`entity_id`, a.`Datum_Lieferung`  =b.updated_at, a.`Monat` = date_format(b.created_at,'%c') ,a. `Jahr` = date_format(b.created_at,'%Y')    where b.order_id    =  a.order_id    ;

update  mage_custom_order as a left join l_carb_shop_de.mage_sales_flat_order    as b on b.entity_id    =  a.order_id  set a.`status`=b.`status`    where b.entity_id    =  a.order_id    ;

update  mage_custom_order as a left join l_carb_shop_de.mage_sales_flat_shipment_track    as b on b.parent_id    =  a.shipping_id  set a.`shipping_lable`=b.`track_number`    where b.parent_id    =  a.shipping_id      ;

update  mage_custom_order as a left join l_carb_shop_de.mage_sales_flat_order_address    as b on b.parent_id    =  a.order_id  set a.`ort`=b.`city`, a.land = b.country_id, a.PLZ = b.postcode    where  b.parent_id    =  a.order_id    ;

insert ignore into mage_custom_order_item( `Item_id`, order_id, `Produkt_id`, `Produkt_name`, `Anzahl`, `Preis_netto`, `Gesamtpreis_netto_aus_Item`, `Steuersatz`, `MWsT`) select a.item_id, a.order_id,  a.product_id, a.name, a.qty_ordered, a.price, a.qty_ordered* a.price as summe_produkte, a.tax_percent, a.tax_amount  from l_carb_shop_de.mage_sales_flat_order_item as a;

INSERT ignore INTO `mage_custom_order_single_item` (`item_id`, `order_id`, `Produkt_id`, `Produkt_name`, `Anzahl`, `Preis_netto`, `Gesamtpreis_netto_aus_Item`, `Steuersatz`, `MWsT`, `product_type`, `product_kind`, `product_DB1`, `product_wight`, `Volumen`) select a.`item_id`, a.`order_id`, a.`Produkt_id`, a.`Produkt_name`, a.`Anzahl`, a.`Preis_netto`, a.`Gesamtpreis_netto_aus_Item`, a.`Steuersatz`, a.`MWsT`, b.`product_type`, b.`product_kind`, b.`product_DB1`, b.`product_wight`, b.`Volumen` from `mage_custom_order_item` as a left join  l_carb_shop_de.mage_custom_product_type as b on a.`Produkt_id` = b.`Product_id` where  a.`Produkt_id` = b.`Product_id` and (product_type = "Eimer" or product_type = "Zusatz");

INSERT ignore INTO `mage_custom_order_single_item` (`item_id`, `order_id`, `Produkt_id`, `Produkt_name`, `Anzahl`, `Preis_netto`, `Gesamtpreis_netto_aus_Item`, `Steuersatz`, `MWsT`, `product_type`, `product_kind`, `product_DB1`, `product_wight`, `Volumen`) select a.`item_id`, a.`order_id`, c.`product_id_base`, a.`Produkt_name`, a.`Anzahl`, a.`Preis_netto`, a.`Gesamtpreis_netto_aus_Item`, a.`Steuersatz`, a.`MWsT`, b.`product_type`, b.`product_kind`, b.`product_DB1`, b.`product_wight`, b.`Volumen` from `mage_custom_order_item` as a left join  l_carb_shop_de.mage_custom_product_type as b on a.`Produkt_id` = b.`Product_id`  left join  l_carb_shop_de.mage_custom_transfer_stock as c on a.`Produkt_id` = c.`product_id_to_be_transfered` where  a.`Produkt_id` = b.`Product_id` and (product_type = "ebay_verb" or product_type = "amazon_verb" or product_type = "Bundle") and a.`Produkt_id` = c.`product_id_to_be_transfered`;

Update  `mage_custom_order_single_item` as a left join  l_carb_shop_de.mage_custom_product_type as b on a.`Produkt_id` = b.`Product_id`  set a.Produkt_name = b.`product_name`, a.product_type = b.`product_type`, a.`product_kind` = b.`product_kind`, a. product_DB1  = b. product_DB1, a.product_wight = b.product_wight, a.Volumen = b.Volumen, a.Steuersatz = b.tax where a.`Produkt_id` = b.`Product_id`;

Update  `mage_custom_order_single_item` as a left join  mage_custom_order as b on a.`order_id` = b.`order_id`  set a.order_status = b.`Status` , a.Datum_Lieferung = b.Datum_Lieferung, a.monat = b.monat, a.jahr = b.jahr where a.`order_id` = b.`order_id`;