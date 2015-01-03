package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QHermesPrinterLog is a Querydsl query type for QHermesPrinterLog
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHermesPrinterLog extends com.mysema.query.sql.RelationalPathBase<QHermesPrinterLog> {

    private static final long serialVersionUID = -1290299356;

    public static final QHermesPrinterLog hermesPrinterLog = new QHermesPrinterLog("hermes_printer_log");

    public final StringPath documentId = createString("documentId");

    public final StringPath orderId = createString("orderId");

    public final BooleanPath printed = createBoolean("printed");

    public final DateTimePath<java.sql.Timestamp> printedAt = createDateTime("printedAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> processedAt = createDateTime("processedAt", java.sql.Timestamp.class);

    public final StringPath type = createString("type");

    public final StringPath uuid = createString("uuid");

    public final com.mysema.query.sql.PrimaryKey<QHermesPrinterLog> primary = createPrimaryKey(uuid);

    public QHermesPrinterLog(String variable) {
        super(QHermesPrinterLog.class, forVariable(variable), "null", "hermes_printer_log");
        addMetadata();
    }

    public QHermesPrinterLog(String variable, String schema, String table) {
        super(QHermesPrinterLog.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHermesPrinterLog(Path<? extends QHermesPrinterLog> path) {
        super(path.getType(), path.getMetadata(), "null", "hermes_printer_log");
        addMetadata();
    }

    public QHermesPrinterLog(PathMetadata<?> metadata) {
        super(QHermesPrinterLog.class, metadata, "null", "hermes_printer_log");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(documentId, ColumnMetadata.named("document_id").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(orderId, ColumnMetadata.named("order_id").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(printed, ColumnMetadata.named("printed").withIndex(4).ofType(Types.BIT).withSize(1));
        addMetadata(printedAt, ColumnMetadata.named("printed_at").withIndex(5).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(processedAt, ColumnMetadata.named("processed_at").withIndex(6).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(type, ColumnMetadata.named("type").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(uuid, ColumnMetadata.named("uuid").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

