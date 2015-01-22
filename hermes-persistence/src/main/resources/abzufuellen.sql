TRUNCATE TABLE  productsales_month; insert into productsales_month SELECT  `Jahr` ,`monat`, product_kind,  sum(anzahl ) FROM  `mage_custom_order_single_item`  GROUP BY  `jahr` ,  `monat`, product_kind   ORDER BY   `jahr` ,  `monat`, product_kind ;
TRUNCATE TABLE  `produkt_statistik` ;
INSERT INTO `produkt_statistik` (`product_id`, `product_name`, `product_kind`) select `product_id`, `product_name`, `product_kind` from   l_carb_shop_de.mage_custom_product_type where product_type = "Eimer" and Aktiv = 1;
update  `produkt_statistik`  as a left join   l_carb_shop_de.mage_cataloginventory_stock_item as b on a.`product_id` = b.`product_id` set a.bestand = b.Qty;
update  `produkt_statistik`  as a left join     productsales_month as b on a.`product_Kind` = b.`product_kind` set a.Verkauf_tag_1=b.Anzahl / (DAY( DATE( NOW( ) ) ) -2) where b.monat = month( DATE( NOW( ) ) ) and b.jahr = year( DATE( NOW( ) ) );
update  `produkt_statistik`  set Reicht_tage = bestand / Verkauf_tag_1;
SELECT `product_name`, `bestand`, `verkauf_tag_1`, `reicht_tage` FROM `produkt_statistik` ORDER BY  `reicht_tage` ASC ;