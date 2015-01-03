package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QHermesForm is a Querydsl query type for QHermesForm
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QHermesForm extends com.mysema.query.sql.RelationalPathBase<QHermesForm> {

    private static final long serialVersionUID = 535395230;

    public static final QHermesForm hermesForm = new QHermesForm("hermes_form");

    public final BooleanPath accessPublic = createBoolean("accessPublic");

    public final StringPath db = createString("db");

    public final StringPath description = createString("description");

    public final BooleanPath executeOnStartup = createBoolean("executeOnStartup");

    public final NumberPath<Integer> fontSize = createNumber("fontSize", Integer.class);

    public final StringPath menu = createString("menu");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> pos = createNumber("pos", Integer.class);

    public final BooleanPath printable = createBoolean("printable");

    public final StringPath schedule = createString("schedule");

    public final StringPath sqlStatement = createString("sqlStatement");

    public final StringPath uuid = createString("uuid");

    public final com.mysema.query.sql.PrimaryKey<QHermesForm> primary = createPrimaryKey(uuid);

    public QHermesForm(String variable) {
        super(QHermesForm.class, forVariable(variable), "null", "hermes_form");
        addMetadata();
    }

    public QHermesForm(String variable, String schema, String table) {
        super(QHermesForm.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHermesForm(Path<? extends QHermesForm> path) {
        super(path.getType(), path.getMetadata(), "null", "hermes_form");
        addMetadata();
    }

    public QHermesForm(PathMetadata<?> metadata) {
        super(QHermesForm.class, metadata, "null", "hermes_form");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(accessPublic, ColumnMetadata.named("access_public").withIndex(2).ofType(Types.BIT).withSize(1));
        addMetadata(db, ColumnMetadata.named("db").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(description, ColumnMetadata.named("description").withIndex(4).ofType(Types.VARCHAR).withSize(255));
        addMetadata(executeOnStartup, ColumnMetadata.named("execute_on_startup").withIndex(5).ofType(Types.BIT).withSize(1));
        addMetadata(fontSize, ColumnMetadata.named("font_size").withIndex(6).ofType(Types.INTEGER).withSize(10));
        addMetadata(menu, ColumnMetadata.named("menu").withIndex(7).ofType(Types.VARCHAR).withSize(255));
        addMetadata(name, ColumnMetadata.named("name").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(pos, ColumnMetadata.named("pos").withIndex(9).ofType(Types.INTEGER).withSize(10));
        addMetadata(printable, ColumnMetadata.named("printable").withIndex(10).ofType(Types.BIT).withSize(1));
        addMetadata(schedule, ColumnMetadata.named("schedule").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(sqlStatement, ColumnMetadata.named("sql_statement").withIndex(12).ofType(Types.LONGVARCHAR).withSize(2147483647));
        addMetadata(uuid, ColumnMetadata.named("uuid").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

