package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QMageCustomShipments is a Querydsl query type for QMageCustomShipments
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QMageCustomShipments extends com.mysema.query.sql.RelationalPathBase<QMageCustomShipments> {

    private static final long serialVersionUID = 434519362;

    public static final QMageCustomShipments mageCustomShipments = new QMageCustomShipments("mage_custom_shipments");

    public final StringPath customerEmail = createString("customerEmail");

    public final StringPath customerName = createString("customerName");

    public final DateTimePath<java.sql.Timestamp> daytimeLastStatus = createDateTime("daytimeLastStatus", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> daytimeLastStatusChange = createDateTime("daytimeLastStatusChange", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> daytimeSend = createDateTime("daytimeSend", java.sql.Timestamp.class);

    public final NumberPath<Integer> incrementId = createNumber("incrementId", Integer.class);

    public final StringPath lastStatus = createString("lastStatus");

    public final NumberPath<Integer> orderId = createNumber("orderId", Integer.class);

    public final StringPath trackingNo = createString("trackingNo");

    public final com.mysema.query.sql.PrimaryKey<QMageCustomShipments> primary = createPrimaryKey(orderId);

    public QMageCustomShipments(String variable) {
        super(QMageCustomShipments.class, forVariable(variable), "null", "mage_custom_shipments");
        addMetadata();
    }

    public QMageCustomShipments(String variable, String schema, String table) {
        super(QMageCustomShipments.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QMageCustomShipments(Path<? extends QMageCustomShipments> path) {
        super(path.getType(), path.getMetadata(), "null", "mage_custom_shipments");
        addMetadata();
    }

    public QMageCustomShipments(PathMetadata<?> metadata) {
        super(QMageCustomShipments.class, metadata, "null", "mage_custom_shipments");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(customerEmail, ColumnMetadata.named("customer_email").withIndex(5).ofType(Types.VARCHAR).withSize(200));
        addMetadata(customerName, ColumnMetadata.named("customer_name").withIndex(4).ofType(Types.VARCHAR).withSize(200));
        addMetadata(daytimeLastStatus, ColumnMetadata.named("Daytime_last_status").withIndex(7).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(daytimeLastStatusChange, ColumnMetadata.named("Daytime_last_status_change").withIndex(8).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(daytimeSend, ColumnMetadata.named("Daytime_send").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(incrementId, ColumnMetadata.named("increment_id").withIndex(2).ofType(Types.INTEGER).withSize(10));
        addMetadata(lastStatus, ColumnMetadata.named("last_status").withIndex(9).ofType(Types.VARCHAR).withSize(200));
        addMetadata(orderId, ColumnMetadata.named("order_id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(trackingNo, ColumnMetadata.named("tracking_no").withIndex(3).ofType(Types.VARCHAR).withSize(50));
    }

}

