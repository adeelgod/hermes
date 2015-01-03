package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBankStatementPattern is a Querydsl query type for QBankStatementPattern
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBankStatementPattern extends com.mysema.query.sql.RelationalPathBase<QBankStatementPattern> {

    private static final long serialVersionUID = 1889875857;

    public static final QBankStatementPattern bankStatementPattern = new QBankStatementPattern("bank_statement_pattern");

    public final StringPath attribute = createString("attribute");

    public final BooleanPath caseInsensitive = createBoolean("caseInsensitive");

    public final StringPath name = createString("name");

    public final StringPath pattern = createString("pattern");

    public final NumberPath<Integer> patternGroup = createNumber("patternGroup", Integer.class);

    public final NumberPath<Integer> pos = createNumber("pos", Integer.class);

    public final BooleanPath stopOnFirstMatch = createBoolean("stopOnFirstMatch");

    public final StringPath uuid = createString("uuid");

    public final com.mysema.query.sql.PrimaryKey<QBankStatementPattern> primary = createPrimaryKey(uuid);

    public QBankStatementPattern(String variable) {
        super(QBankStatementPattern.class, forVariable(variable), "null", "bank_statement_pattern");
        addMetadata();
    }

    public QBankStatementPattern(String variable, String schema, String table) {
        super(QBankStatementPattern.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBankStatementPattern(Path<? extends QBankStatementPattern> path) {
        super(path.getType(), path.getMetadata(), "null", "bank_statement_pattern");
        addMetadata();
    }

    public QBankStatementPattern(PathMetadata<?> metadata) {
        super(QBankStatementPattern.class, metadata, "null", "bank_statement_pattern");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(attribute, ColumnMetadata.named("attribute").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(caseInsensitive, ColumnMetadata.named("case_insensitive").withIndex(8).ofType(Types.BIT).withSize(1));
        addMetadata(name, ColumnMetadata.named("name").withIndex(3).ofType(Types.VARCHAR).withSize(255));
        addMetadata(pattern, ColumnMetadata.named("pattern").withIndex(4).ofType(Types.LONGVARCHAR).withSize(2147483647));
        addMetadata(patternGroup, ColumnMetadata.named("pattern_group").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(pos, ColumnMetadata.named("pos").withIndex(7).ofType(Types.INTEGER).withSize(10));
        addMetadata(stopOnFirstMatch, ColumnMetadata.named("stop_on_first_match").withIndex(6).ofType(Types.BIT).withSize(1));
        addMetadata(uuid, ColumnMetadata.named("uuid").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

