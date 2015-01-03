package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QMageCustomOrderItem is a Querydsl query type for QMageCustomOrderItem
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QMageCustomOrderItem extends com.mysema.query.sql.RelationalPathBase<QMageCustomOrderItem> {

    private static final long serialVersionUID = -1131046230;

    public static final QMageCustomOrderItem mageCustomOrderItem = new QMageCustomOrderItem("mage_custom_order_item");

    public final NumberPath<Integer> anzahl = createNumber("anzahl", Integer.class);

    public final DateTimePath<java.sql.Timestamp> datumLieferung = createDateTime("datumLieferung", java.sql.Timestamp.class);

    public final NumberPath<Double> gesamtpreisNettoAusItem = createNumber("gesamtpreisNettoAusItem", Double.class);

    public final NumberPath<Integer> itemId = createNumber("itemId", Integer.class);

    public final NumberPath<Integer> jahr = createNumber("jahr", Integer.class);

    public final NumberPath<Integer> monat = createNumber("monat", Integer.class);

    public final NumberPath<Double> mWsT = createNumber("mWsT", Double.class);

    public final NumberPath<Integer> orderId = createNumber("orderId", Integer.class);

    public final NumberPath<Double> preisNetto = createNumber("preisNetto", Double.class);

    public final NumberPath<Integer> produktId = createNumber("produktId", Integer.class);

    public final StringPath produktName = createString("produktName");

    public final StringPath status = createString("status");

    public final NumberPath<Integer> steuersatz = createNumber("steuersatz", Integer.class);

    public final StringPath typ = createString("typ");

    public final com.mysema.query.sql.PrimaryKey<QMageCustomOrderItem> primary = createPrimaryKey(itemId);

    public QMageCustomOrderItem(String variable) {
        super(QMageCustomOrderItem.class, forVariable(variable), "null", "mage_custom_order_item");
        addMetadata();
    }

    public QMageCustomOrderItem(String variable, String schema, String table) {
        super(QMageCustomOrderItem.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QMageCustomOrderItem(Path<? extends QMageCustomOrderItem> path) {
        super(path.getType(), path.getMetadata(), "null", "mage_custom_order_item");
        addMetadata();
    }

    public QMageCustomOrderItem(PathMetadata<?> metadata) {
        super(QMageCustomOrderItem.class, metadata, "null", "mage_custom_order_item");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(anzahl, ColumnMetadata.named("Anzahl").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(datumLieferung, ColumnMetadata.named("Datum_Lieferung").withIndex(11).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(gesamtpreisNettoAusItem, ColumnMetadata.named("Gesamtpreis_netto_aus_Item").withIndex(7).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(itemId, ColumnMetadata.named("item_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(jahr, ColumnMetadata.named("Jahr").withIndex(13).ofType(Types.INTEGER).withSize(10));
        addMetadata(monat, ColumnMetadata.named("Monat").withIndex(12).ofType(Types.INTEGER).withSize(10));
        addMetadata(mWsT, ColumnMetadata.named("MWsT").withIndex(9).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(orderId, ColumnMetadata.named("order_id").withIndex(2).ofType(Types.INTEGER).withSize(10));
        addMetadata(preisNetto, ColumnMetadata.named("Preis_netto").withIndex(6).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(produktId, ColumnMetadata.named("Produkt_id").withIndex(3).ofType(Types.INTEGER).withSize(10));
        addMetadata(produktName, ColumnMetadata.named("Produkt_name").withIndex(4).ofType(Types.VARCHAR).withSize(50));
        addMetadata(status, ColumnMetadata.named("Status").withIndex(10).ofType(Types.VARCHAR).withSize(25));
        addMetadata(steuersatz, ColumnMetadata.named("Steuersatz").withIndex(8).ofType(Types.INTEGER).withSize(10));
        addMetadata(typ, ColumnMetadata.named("typ").withIndex(14).ofType(Types.VARCHAR).withSize(25));
    }

}

