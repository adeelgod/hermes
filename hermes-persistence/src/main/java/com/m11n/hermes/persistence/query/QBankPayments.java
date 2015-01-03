package com.m11n.hermes.persistence.query;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QBankPayments is a Querydsl query type for QBankPayments
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QBankPayments extends com.mysema.query.sql.RelationalPathBase<QBankPayments> {

    private static final long serialVersionUID = 1766459293;

    public static final QBankPayments bankPayments = new QBankPayments("bank_payments");

    public final NumberPath<Double> betrag = createNumber("betrag", Double.class);

    public final StringPath buchungsdatum = createString("buchungsdatum");

    public final StringPath empfaenger1 = createString("empfaenger1");

    public final StringPath empfaenger2 = createString("empfaenger2");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> kontoNr = createNumber("kontoNr", Integer.class);

    public final NumberPath<Integer> orderId = createNumber("orderId", Integer.class);

    public final DatePath<java.sql.Date> valuta = createDate("valuta", java.sql.Date.class);

    public final StringPath verwendungszweck = createString("verwendungszweck");

    public final StringPath waehrung = createString("waehrung");

    public final com.mysema.query.sql.PrimaryKey<QBankPayments> primary = createPrimaryKey(id);

    public QBankPayments(String variable) {
        super(QBankPayments.class, forVariable(variable), "null", "bank_payments");
        addMetadata();
    }

    public QBankPayments(String variable, String schema, String table) {
        super(QBankPayments.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QBankPayments(Path<? extends QBankPayments> path) {
        super(path.getType(), path.getMetadata(), "null", "bank_payments");
        addMetadata();
    }

    public QBankPayments(PathMetadata<?> metadata) {
        super(QBankPayments.class, metadata, "null", "bank_payments");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(betrag, ColumnMetadata.named("Betrag").withIndex(8).ofType(Types.DECIMAL).withSize(10).withDigits(2).notNull());
        addMetadata(buchungsdatum, ColumnMetadata.named("Buchungsdatum").withIndex(3).ofType(Types.VARCHAR).withSize(15).notNull());
        addMetadata(empfaenger1, ColumnMetadata.named("Empfaenger 1").withIndex(5).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(empfaenger2, ColumnMetadata.named("Empfaenger 2").withIndex(6).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(kontoNr, ColumnMetadata.named("Konto_nr").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(orderId, ColumnMetadata.named("order_id").withIndex(10).ofType(Types.INTEGER).withSize(10));
        addMetadata(valuta, ColumnMetadata.named("Valuta").withIndex(4).ofType(Types.DATE).withSize(10).notNull());
        addMetadata(verwendungszweck, ColumnMetadata.named("Verwendungszweck").withIndex(7).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(waehrung, ColumnMetadata.named("Waehrung").withIndex(9).ofType(Types.VARCHAR).withSize(5).notNull());
    }

}

