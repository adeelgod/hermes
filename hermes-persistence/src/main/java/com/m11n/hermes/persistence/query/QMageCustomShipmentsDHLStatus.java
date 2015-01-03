package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QMageCustomShipmentsDHLStatus is a Querydsl query type for QMageCustomShipmentsDHLStatus
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QMageCustomShipmentsDHLStatus extends com.mysema.query.sql.RelationalPathBase<QMageCustomShipmentsDHLStatus> {

    private static final long serialVersionUID = 97254232;

    public static final QMageCustomShipmentsDHLStatus mageCustomShipmentsDHLStatus = new QMageCustomShipmentsDHLStatus("mage_custom_shipments_DHL_status");

    public final StringPath status = createString("status");

    public final DateTimePath<java.sql.Timestamp> statusTime = createDateTime("statusTime", java.sql.Timestamp.class);

    public final StringPath trackingNo = createString("trackingNo");

    public QMageCustomShipmentsDHLStatus(String variable) {
        super(QMageCustomShipmentsDHLStatus.class, forVariable(variable), "null", "mage_custom_shipments_DHL_status");
        addMetadata();
    }

    public QMageCustomShipmentsDHLStatus(String variable, String schema, String table) {
        super(QMageCustomShipmentsDHLStatus.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QMageCustomShipmentsDHLStatus(Path<? extends QMageCustomShipmentsDHLStatus> path) {
        super(path.getType(), path.getMetadata(), "null", "mage_custom_shipments_DHL_status");
        addMetadata();
    }

    public QMageCustomShipmentsDHLStatus(PathMetadata<?> metadata) {
        super(QMageCustomShipmentsDHLStatus.class, metadata, "null", "mage_custom_shipments_DHL_status");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(status, ColumnMetadata.named("Status").withIndex(3).ofType(Types.LONGVARCHAR).withSize(65535));
        addMetadata(statusTime, ColumnMetadata.named("Status_time").withIndex(2).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(trackingNo, ColumnMetadata.named("tracking_no").withIndex(1).ofType(Types.VARCHAR).withSize(50).notNull());
    }

}

