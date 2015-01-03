package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QMageCustomOrder is a Querydsl query type for QMageCustomOrder
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QMageCustomOrder extends com.mysema.query.sql.RelationalPathBase<QMageCustomOrder> {

    private static final long serialVersionUID = -1215758601;

    public static final QMageCustomOrder mageCustomOrder = new QMageCustomOrder("mage_custom_order");

    public final StringPath amazonOrderId = createString("amazonOrderId");

    public final NumberPath<Integer> bestellung = createNumber("bestellung", Integer.class);

    public final NumberPath<Double> betragPaypal = createNumber("betragPaypal", Double.class);

    public final NumberPath<Double> bezahlkosten = createNumber("bezahlkosten", Double.class);

    public final DateTimePath<java.sql.Timestamp> datumKauf = createDateTime("datumKauf", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> datumLieferung = createDateTime("datumLieferung", java.sql.Timestamp.class);

    public final NumberPath<Double> db1 = createNumber("db1", Double.class);

    public final NumberPath<Double> dB1Proz = createNumber("dB1Proz", Double.class);

    public final StringPath dHLAccount = createString("dHLAccount");

    public final NumberPath<Double> discount = createNumber("discount", Double.class);

    public final StringPath ebayOrderId = createString("ebayOrderId");

    public final NumberPath<Double> erstattet = createNumber("erstattet", Double.class);

    public final StringPath firma = createString("firma");

    public final NumberPath<Double> gesamtPreisDerBestellungBrutto = createNumber("gesamtPreisDerBestellungBrutto", Double.class);

    public final NumberPath<Integer> inPaypal = createNumber("inPaypal", Integer.class);

    public final NumberPath<Integer> invoiceId = createNumber("invoiceId", Integer.class);

    public final NumberPath<Integer> jahr = createNumber("jahr", Integer.class);

    public final StringPath kundenEbayName = createString("kundenEbayName");

    public final StringPath kundenEmail = createString("kundenEmail");

    public final StringPath kundenName = createString("kundenName");

    public final NumberPath<Integer> kundenummer = createNumber("kundenummer", Integer.class);

    public final StringPath kundenVorname = createString("kundenVorname");

    public final StringPath land = createString("land");

    public final DateTimePath<java.sql.Timestamp> letzteAenderung = createDateTime("letzteAenderung", java.sql.Timestamp.class);

    public final NumberPath<Integer> m2eOrderId = createNumber("m2eOrderId", Integer.class);

    public final StringPath method = createString("method");

    public final NumberPath<Integer> monat = createNumber("monat", Integer.class);

    public final NumberPath<Double> nachnahme = createNumber("nachnahme", Double.class);

    public final NumberPath<Integer> orderId = createNumber("orderId", Integer.class);

    public final StringPath ort = createString("ort");

    public final NumberPath<Integer> paymentId = createNumber("paymentId", Integer.class);

    public final NumberPath<Double> paypalGebuerenEbay = createNumber("paypalGebuerenEbay", Double.class);

    public final StringPath paypalIdEbay = createString("paypalIdEbay");

    public final StringPath paypalIdShop = createString("paypalIdShop");

    public final NumberPath<Double> paypalUmsatzEbay = createNumber("paypalUmsatzEbay", Double.class);

    public final NumberPath<Double> paypalUmsatzShop = createNumber("paypalUmsatzShop", Double.class);

    public final StringPath plz = createString("plz");

    public final NumberPath<Integer> priceM2e = createNumber("priceM2e", Integer.class);

    public final NumberPath<Integer> pricePaypal = createNumber("pricePaypal", Integer.class);

    public final NumberPath<Integer> shippingId = createNumber("shippingId", Integer.class);

    public final StringPath shippingLable = createString("shippingLable");

    public final StringPath status = createString("status");

    public final StringPath str1 = createString("str1");

    public final StringPath str2 = createString("str2");

    public final StringPath telefon = createString("telefon");

    public final NumberPath<Double> totalRefundedBrutto = createNumber("totalRefundedBrutto", Double.class);

    public final NumberPath<Double> totalRefundedNetto = createNumber("totalRefundedNetto", Double.class);

    public final StringPath typ = createString("typ");

    public final NumberPath<Double> vat = createNumber("vat", Double.class);

    public final NumberPath<Double> versandkosten = createNumber("versandkosten", Double.class);

    public final NumberPath<Double> versandkostenErstattet = createNumber("versandkostenErstattet", Double.class);

    public final NumberPath<Double> versandkostenNetto = createNumber("versandkostenNetto", Double.class);

    public final NumberPath<Double> warenwert = createNumber("warenwert", Double.class);

    public final NumberPath<Double> weight = createNumber("weight", Double.class);

    public final com.mysema.query.sql.PrimaryKey<QMageCustomOrder> primary = createPrimaryKey(orderId);

    public QMageCustomOrder(String variable) {
        super(QMageCustomOrder.class, forVariable(variable), "null", "mage_custom_order");
        addMetadata();
    }

    public QMageCustomOrder(String variable, String schema, String table) {
        super(QMageCustomOrder.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QMageCustomOrder(Path<? extends QMageCustomOrder> path) {
        super(path.getType(), path.getMetadata(), "null", "mage_custom_order");
        addMetadata();
    }

    public QMageCustomOrder(PathMetadata<?> metadata) {
        super(QMageCustomOrder.class, metadata, "null", "mage_custom_order");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(amazonOrderId, ColumnMetadata.named("amazon_order_id").withIndex(6).ofType(Types.VARCHAR).withSize(50));
        addMetadata(bestellung, ColumnMetadata.named("Bestellung").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(betragPaypal, ColumnMetadata.named("betrag_paypal").withIndex(51).ofType(Types.DECIMAL).withSize(10).withDigits(2).notNull());
        addMetadata(bezahlkosten, ColumnMetadata.named("bezahlkosten").withIndex(40).ofType(Types.DECIMAL).withSize(10).withDigits(2).notNull());
        addMetadata(datumKauf, ColumnMetadata.named("Datum_Kauf").withIndex(24).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(datumLieferung, ColumnMetadata.named("Datum_Lieferung").withIndex(25).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(db1, ColumnMetadata.named("DB1").withIndex(42).ofType(Types.DECIMAL).withSize(10).withDigits(4).notNull());
        addMetadata(dB1Proz, ColumnMetadata.named("DB1_proz").withIndex(43).ofType(Types.DECIMAL).withSize(8).withDigits(6).notNull());
        addMetadata(dHLAccount, ColumnMetadata.named("DHL_Account").withIndex(23).ofType(Types.VARCHAR).withSize(50));
        addMetadata(discount, ColumnMetadata.named("discount").withIndex(31).ofType(Types.DECIMAL).withSize(10).withDigits(2));
        addMetadata(ebayOrderId, ColumnMetadata.named("ebay_order_id").withIndex(5).ofType(Types.VARCHAR).withSize(50));
        addMetadata(erstattet, ColumnMetadata.named("erstattet").withIndex(34).ofType(Types.DECIMAL).withSize(8).withDigits(2).notNull());
        addMetadata(firma, ColumnMetadata.named("Firma").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(gesamtPreisDerBestellungBrutto, ColumnMetadata.named("GesamtPreis_der_Bestellung_Brutto").withIndex(28).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(inPaypal, ColumnMetadata.named("in_paypal").withIndex(50).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(invoiceId, ColumnMetadata.named("invoice_id").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(jahr, ColumnMetadata.named("Jahr").withIndex(27).ofType(Types.INTEGER).withSize(10));
        addMetadata(kundenEbayName, ColumnMetadata.named("Kunden_ebay_name").withIndex(16).ofType(Types.VARCHAR).withSize(100));
        addMetadata(kundenEmail, ColumnMetadata.named("Kunden_email").withIndex(12).ofType(Types.VARCHAR).withSize(100));
        addMetadata(kundenName, ColumnMetadata.named("Kunden_name").withIndex(15).ofType(Types.VARCHAR).withSize(100));
        addMetadata(kundenummer, ColumnMetadata.named("Kundenummer").withIndex(11).ofType(Types.INTEGER).withSize(10));
        addMetadata(kundenVorname, ColumnMetadata.named("Kunden_vorname").withIndex(13).ofType(Types.VARCHAR).withSize(100));
        addMetadata(land, ColumnMetadata.named("Land").withIndex(20).ofType(Types.VARCHAR).withSize(2));
        addMetadata(letzteAenderung, ColumnMetadata.named("Letzte_Aenderung").withIndex(54).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(m2eOrderId, ColumnMetadata.named("m2e_order_id").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(method, ColumnMetadata.named("method").withIndex(49).ofType(Types.VARCHAR).withSize(50));
        addMetadata(monat, ColumnMetadata.named("Monat").withIndex(26).ofType(Types.INTEGER).withSize(10));
        addMetadata(nachnahme, ColumnMetadata.named("Nachnahme").withIndex(37).ofType(Types.DECIMAL).withSize(8).withDigits(2).notNull());
        addMetadata(orderId, ColumnMetadata.named("order_id").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(ort, ColumnMetadata.named("Ort").withIndex(19).ofType(Types.VARCHAR).withSize(100));
        addMetadata(paymentId, ColumnMetadata.named("payment_id").withIndex(53).ofType(Types.INTEGER).withSize(10));
        addMetadata(paypalGebuerenEbay, ColumnMetadata.named("Paypal_gebueren_ebay").withIndex(46).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(paypalIdEbay, ColumnMetadata.named("Paypal_id_ebay").withIndex(44).ofType(Types.VARCHAR).withSize(50));
        addMetadata(paypalIdShop, ColumnMetadata.named("Paypal_id_shop").withIndex(48).ofType(Types.VARCHAR).withSize(50));
        addMetadata(paypalUmsatzEbay, ColumnMetadata.named("Paypal_Umsatz_ebay").withIndex(45).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(paypalUmsatzShop, ColumnMetadata.named("Paypal_Umsatz_shop").withIndex(47).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(plz, ColumnMetadata.named("PLZ").withIndex(21).ofType(Types.VARCHAR).withSize(10));
        addMetadata(priceM2e, ColumnMetadata.named("price_m2e").withIndex(33).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(pricePaypal, ColumnMetadata.named("Price_paypal").withIndex(32).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(shippingId, ColumnMetadata.named("shipping_id").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(shippingLable, ColumnMetadata.named("shipping_lable").withIndex(8).ofType(Types.VARCHAR).withSize(50));
        addMetadata(status, ColumnMetadata.named("Status").withIndex(10).ofType(Types.VARCHAR).withSize(50));
        addMetadata(str1, ColumnMetadata.named("str1").withIndex(17).ofType(Types.VARCHAR).withSize(100));
        addMetadata(str2, ColumnMetadata.named("str2").withIndex(18).ofType(Types.VARCHAR).withSize(100));
        addMetadata(telefon, ColumnMetadata.named("Telefon").withIndex(22).ofType(Types.VARCHAR).withSize(50).notNull());
        addMetadata(totalRefundedBrutto, ColumnMetadata.named("total_refunded_brutto").withIndex(30).ofType(Types.DECIMAL).withSize(10).withDigits(2).notNull());
        addMetadata(totalRefundedNetto, ColumnMetadata.named("total_refunded_netto").withIndex(29).ofType(Types.DECIMAL).withSize(10).withDigits(2));
        addMetadata(typ, ColumnMetadata.named("typ").withIndex(52).ofType(Types.VARCHAR).withSize(50));
        addMetadata(vat, ColumnMetadata.named("VAT").withIndex(38).ofType(Types.DECIMAL).withSize(10).withDigits(4).notNull());
        addMetadata(versandkosten, ColumnMetadata.named("Versandkosten").withIndex(41).ofType(Types.DECIMAL).withSize(10).withDigits(2).notNull());
        addMetadata(versandkostenErstattet, ColumnMetadata.named("Versandkosten_erstattet").withIndex(36).ofType(Types.DECIMAL).withSize(8).withDigits(2).notNull());
        addMetadata(versandkostenNetto, ColumnMetadata.named("Versandkosten_netto").withIndex(35).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(warenwert, ColumnMetadata.named("Warenwert").withIndex(39).ofType(Types.DECIMAL).withSize(10).withDigits(2).notNull());
        addMetadata(weight, ColumnMetadata.named("weight").withIndex(9).ofType(Types.DECIMAL).withSize(10).withDigits(4));
    }

}

