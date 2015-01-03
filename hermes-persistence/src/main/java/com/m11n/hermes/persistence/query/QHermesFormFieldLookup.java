package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QHermesFormFieldLookup is a Querydsl query type for QHermesFormFieldLookup
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHermesFormFieldLookup extends com.mysema.query.sql.RelationalPathBase<QHermesFormFieldLookup> {

    private static final long serialVersionUID = -1960415082;

    public static final QHermesFormFieldLookup hermesFormFieldLookup = new QHermesFormFieldLookup("hermes_form_field_lookup");

    public final StringPath formFieldId = createString("formFieldId");

    public final StringPath value = createString("value");

    public QHermesFormFieldLookup(String variable) {
        super(QHermesFormFieldLookup.class, forVariable(variable), "null", "hermes_form_field_lookup");
        addMetadata();
    }

    public QHermesFormFieldLookup(String variable, String schema, String table) {
        super(QHermesFormFieldLookup.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHermesFormFieldLookup(Path<? extends QHermesFormFieldLookup> path) {
        super(path.getType(), path.getMetadata(), "null", "hermes_form_field_lookup");
        addMetadata();
    }

    public QHermesFormFieldLookup(PathMetadata<?> metadata) {
        super(QHermesFormFieldLookup.class, metadata, "null", "hermes_form_field_lookup");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(formFieldId, ColumnMetadata.named("form_field_id").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(value, ColumnMetadata.named("value").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    }

}

