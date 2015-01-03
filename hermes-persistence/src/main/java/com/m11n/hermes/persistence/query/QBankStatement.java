package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBankStatement is a Querydsl query type for QBankStatement
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBankStatement extends com.mysema.query.sql.RelationalPathBase<QBankStatement> {

    private static final long serialVersionUID = 1222000383;

    public static final QBankStatement bankStatement = new QBankStatement("bank_statement");

    public final StringPath account = createString("account");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final NumberPath<Double> amountDiff = createNumber("amountDiff", Double.class);

    public final StringPath clientId = createString("clientId");

    public final StringPath currency = createString("currency");

    public final StringPath description = createString("description");

    public final StringPath ebayName = createString("ebayName");

    public final StringPath firstname = createString("firstname");

    public final StringPath hash = createString("hash");

    public final StringPath invoiceId = createString("invoiceId");

    public final StringPath lastname = createString("lastname");

    public final StringPath orderId = createString("orderId");

    public final StringPath receiver1 = createString("receiver1");

    public final StringPath receiver2 = createString("receiver2");

    public final StringPath status = createString("status");

    public final DateTimePath<java.sql.Timestamp> transferDate = createDateTime("transferDate", java.sql.Timestamp.class);

    public final StringPath uuid = createString("uuid");

    public final DateTimePath<java.sql.Timestamp> valutaDate = createDateTime("valutaDate", java.sql.Timestamp.class);

    public final com.mysema.query.sql.PrimaryKey<QBankStatement> primary = createPrimaryKey(uuid);

    public QBankStatement(String variable) {
        super(QBankStatement.class, forVariable(variable), "null", "bank_statement");
        addMetadata();
    }

    public QBankStatement(String variable, String schema, String table) {
        super(QBankStatement.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBankStatement(Path<? extends QBankStatement> path) {
        super(path.getType(), path.getMetadata(), "null", "bank_statement");
        addMetadata();
    }

    public QBankStatement(PathMetadata<?> metadata) {
        super(QBankStatement.class, metadata, "null", "bank_statement");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(account, ColumnMetadata.named("account").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(amount, ColumnMetadata.named("amount").withIndex(3).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(amountDiff, ColumnMetadata.named("amount_diff").withIndex(4).ofType(Types.DECIMAL).withSize(8).withDigits(2));
        addMetadata(clientId, ColumnMetadata.named("client_id").withIndex(5).ofType(Types.VARCHAR).withSize(255));
        addMetadata(currency, ColumnMetadata.named("currency").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(description, ColumnMetadata.named("description").withIndex(7).ofType(Types.LONGVARCHAR).withSize(2147483647));
        addMetadata(ebayName, ColumnMetadata.named("ebay_name").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(firstname, ColumnMetadata.named("firstname").withIndex(9).ofType(Types.VARCHAR).withSize(255));
        addMetadata(hash, ColumnMetadata.named("hash").withIndex(10).ofType(Types.VARCHAR).withSize(255));
        addMetadata(invoiceId, ColumnMetadata.named("invoice_id").withIndex(11).ofType(Types.VARCHAR).withSize(255));
        addMetadata(lastname, ColumnMetadata.named("lastname").withIndex(12).ofType(Types.VARCHAR).withSize(255));
        addMetadata(orderId, ColumnMetadata.named("order_id").withIndex(13).ofType(Types.VARCHAR).withSize(255));
        addMetadata(receiver1, ColumnMetadata.named("receiver1").withIndex(14).ofType(Types.VARCHAR).withSize(255));
        addMetadata(receiver2, ColumnMetadata.named("receiver2").withIndex(15).ofType(Types.VARCHAR).withSize(255));
        addMetadata(status, ColumnMetadata.named("status").withIndex(16).ofType(Types.VARCHAR).withSize(255));
        addMetadata(transferDate, ColumnMetadata.named("transfer_date").withIndex(17).ofType(Types.TIMESTAMP).withSize(19));
        addMetadata(uuid, ColumnMetadata.named("uuid").withIndex(1).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(valutaDate, ColumnMetadata.named("valuta_date").withIndex(18).ofType(Types.TIMESTAMP).withSize(19));
    }

}

