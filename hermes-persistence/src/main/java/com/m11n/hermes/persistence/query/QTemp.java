package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QTemp is a Querydsl query type for QTemp
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QTemp extends com.mysema.query.sql.RelationalPathBase<QTemp> {

    private static final long serialVersionUID = -254860824;

    public static final QTemp temp = new QTemp("temp");

    public final NumberPath<Double> betrag = createNumber("betrag", Double.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QTemp(String variable) {
        super(QTemp.class, forVariable(variable), "null", "temp");
        addMetadata();
    }

    public QTemp(String variable, String schema, String table) {
        super(QTemp.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QTemp(Path<? extends QTemp> path) {
        super(path.getType(), path.getMetadata(), "null", "temp");
        addMetadata();
    }

    public QTemp(PathMetadata<?> metadata) {
        super(QTemp.class, metadata, "null", "temp");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(betrag, ColumnMetadata.named("Betrag").withIndex(2).ofType(Types.DECIMAL).withSize(8).withDigits(2).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

