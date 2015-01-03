package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QHermesFormField is a Querydsl query type for QHermesFormField
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHermesFormField extends com.mysema.query.sql.RelationalPathBase<QHermesFormField> {

    private static final long serialVersionUID = 1601488892;

    public static final QHermesFormField hermesFormField = new QHermesFormField("hermes_form_field");

    public final StringPath defaultValue = createString("defaultValue");

    public final StringPath description = createString("description");

    public final StringPath formId = createString("formId");

    public final BooleanPath isColumn = createBoolean("isColumn");

    public final BooleanPath isParameter = createBoolean("isParameter");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> pos = createNumber("pos", Integer.class);

    public final StringPath type = createString("type");

    public final StringPath uuid = createString("uuid");

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QHermesFormField> primary = createPrimaryKey(uuid);

    public QHermesFormField(String variable) {
        super(QHermesFormField.class, forVariable(variable), "null", "hermes_form_field");
        addMetadata();
    }

    public QHermesFormField(String variable, String schema, String table) {
        super(QHermesFormField.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHermesFormField(Path<? extends QHermesFormField> path) {
        super(path.getType(), path.getMetadata(), "null", "hermes_form_field");
        addMetadata();
    }

    public QHermesFormField(PathMetadata<?> metadata) {
        super(QHermesFormField.class, metadata, "null", "hermes_form_field");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(defaultValue, ColumnMetadata.named("default_value").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(description, ColumnMetadata.named("description").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(formId, ColumnMetadata.named("form_id").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(isColumn, ColumnMetadata.named("is_column").withIndex(2).ofType(Types.BIT).withSize(1));
        addMetadata(isParameter, ColumnMetadata.named("is_parameter").withIndex(8).ofType(Types.BIT).withSize(1));
        addMetadata(name, ColumnMetadata.named("name").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(pos, ColumnMetadata.named("pos").withIndex(9).ofType(Types.INTEGER).withSize(10));
        addMetadata(type, ColumnMetadata.named("type").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(uuid, ColumnMetadata.named("uuid").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(width, ColumnMetadata.named("width").withIndex(10).ofType(Types.INTEGER).withSize(10));
    }

}

