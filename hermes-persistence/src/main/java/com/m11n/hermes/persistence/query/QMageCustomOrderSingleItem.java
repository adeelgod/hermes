package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QMageCustomOrderSingleItem is a Querydsl query type for QMageCustomOrderSingleItem
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QMageCustomOrderSingleItem extends com.mysema.query.sql.RelationalPathBase<QMageCustomOrderSingleItem> {

    private static final long serialVersionUID = 751690194;

    public static final QMageCustomOrderSingleItem mageCustomOrderSingleItem = new QMageCustomOrderSingleItem("mage_custom_order_single_item");

    public final NumberPath<Integer> anzahl = createNumber("anzahl", Integer.class);

    public final DateTimePath<java.sql.Timestamp> datumLieferung = createDateTime("datumLieferung", java.sql.Timestamp.class);

    public final NumberPath<Double> gesamtpreisNettoAusItem = createNumber("gesamtpreisNettoAusItem", Double.class);

    public final NumberPath<Integer> itemId = createNumber("itemId", Integer.class);

    public final NumberPath<Integer> jahr = createNumber("jahr", Integer.class);

    public final NumberPath<Integer> monat = createNumber("monat", Integer.class);

    public final NumberPath<Double> mWsT = createNumber("mWsT", Double.class);

    public final NumberPath<Integer> orderId = createNumber("orderId", Integer.class);

    public final StringPath orderStatus = createString("orderStatus");

    public final NumberPath<Double> preisNetto = createNumber("preisNetto", Double.class);

    public final NumberPath<Double> productDB1 = createNumber("productDB1", Double.class);

    public final StringPath productKind = createString("productKind");

    public final StringPath productType = createString("productType");

    public final NumberPath<Double> productWight = createNumber("productWight", Double.class);

    public final NumberPath<Integer> produktId = createNumber("produktId", Integer.class);

    public final StringPath produktName = createString("produktName");

    public final NumberPath<Integer> steuersatz = createNumber("steuersatz", Integer.class);

    public final NumberPath<Double> volumen = createNumber("volumen", Double.class);

    public QMageCustomOrderSingleItem(String variable) {
        super(QMageCustomOrderSingleItem.class, forVariable(variable), "null", "mage_custom_order_single_item");
        addMetadata();
    }

    public QMageCustomOrderSingleItem(String variable, String schema, String table) {
        super(QMageCustomOrderSingleItem.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QMageCustomOrderSingleItem(Path<? extends QMageCustomOrderSingleItem> path) {
        super(path.getType(), path.getMetadata(), "null", "mage_custom_order_single_item");
        addMetadata();
    }

    public QMageCustomOrderSingleItem(PathMetadata<?> metadata) {
        super(QMageCustomOrderSingleItem.class, metadata, "null", "mage_custom_order_single_item");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(anzahl, ColumnMetadata.named("Anzahl").withIndex(9).ofType(Types.INTEGER).withSize(10));
        addMetadata(datumLieferung, ColumnMetadata.named("Datum_Lieferung").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(gesamtpreisNettoAusItem, ColumnMetadata.named("Gesamtpreis_netto_aus_Item").withIndex(11).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(itemId, ColumnMetadata.named("item_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(jahr, ColumnMetadata.named("Jahr").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(monat, ColumnMetadata.named("Monat").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(mWsT, ColumnMetadata.named("MWsT").withIndex(13).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(orderId, ColumnMetadata.named("order_id").withIndex(2).ofType(Types.INTEGER).withSize(10));
        addMetadata(orderStatus, ColumnMetadata.named("order_status").withIndex(4).ofType(Types.VARCHAR).withSize(25).notNull());
        addMetadata(preisNetto, ColumnMetadata.named("Preis_netto").withIndex(10).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(productDB1, ColumnMetadata.named("product_DB1").withIndex(16).ofType(Types.DECIMAL).withSize(10).withDigits(2));
        addMetadata(productKind, ColumnMetadata.named("product_kind").withIndex(15).ofType(Types.VARCHAR).withSize(25));
        addMetadata(productType, ColumnMetadata.named("product_type").withIndex(14).ofType(Types.VARCHAR).withSize(25));
        addMetadata(productWight, ColumnMetadata.named("product_wight").withIndex(17).ofType(Types.DECIMAL).withSize(10).withDigits(2));
        addMetadata(produktId, ColumnMetadata.named("Produkt_id").withIndex(3).ofType(Types.INTEGER).withSize(10));
        addMetadata(produktName, ColumnMetadata.named("Produkt_name").withIndex(8).ofType(Types.VARCHAR).withSize(50));
        addMetadata(steuersatz, ColumnMetadata.named("Steuersatz").withIndex(12).ofType(Types.INTEGER).withSize(10));
        addMetadata(volumen, ColumnMetadata.named("Volumen").withIndex(18).ofType(Types.DECIMAL).withSize(10).withDigits(2));
    }

}

