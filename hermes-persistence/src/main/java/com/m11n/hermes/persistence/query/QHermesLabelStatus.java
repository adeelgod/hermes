package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QHermesLabelStatus is a Querydsl query type for QHermesLabelStatus
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHermesLabelStatus extends com.mysema.query.sql.RelationalPathBase<QHermesLabelStatus> {

    private static final long serialVersionUID = -1462027284;

    public static final QHermesLabelStatus hermesLabelStatus = new QHermesLabelStatus("hermes_label_status");

    public final StringPath status = createString("status");

    public final StringPath text = createString("text");

    public final StringPath uuid = createString("uuid");

    public final com.mysema.query.sql.PrimaryKey<QHermesLabelStatus> primary = createPrimaryKey(uuid);

    public QHermesLabelStatus(String variable) {
        super(QHermesLabelStatus.class, forVariable(variable), "null", "hermes_label_status");
        addMetadata();
    }

    public QHermesLabelStatus(String variable, String schema, String table) {
        super(QHermesLabelStatus.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHermesLabelStatus(Path<? extends QHermesLabelStatus> path) {
        super(path.getType(), path.getMetadata(), "null", "hermes_label_status");
        addMetadata();
    }

    public QHermesLabelStatus(PathMetadata<?> metadata) {
        super(QHermesLabelStatus.class, metadata, "null", "hermes_label_status");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(status, ColumnMetadata.named("status").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(text, ColumnMetadata.named("text").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(uuid, ColumnMetadata.named("uuid").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

