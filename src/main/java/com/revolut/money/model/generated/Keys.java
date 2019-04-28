/*
 * This file is generated by jOOQ.
 */
package com.revolut.money.model.generated;


import com.revolut.money.model.generated.tables.Accounts;
import com.revolut.money.model.generated.tables.Transactions;
import com.revolut.money.model.generated.tables.records.AccountsRecord;
import com.revolut.money.model.generated.tables.records.TransactionsRecord;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;

import javax.annotation.Generated;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>PUBLIC</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<TransactionsRecord, Integer> IDENTITY_TRANSACTIONS = Identities0.IDENTITY_TRANSACTIONS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AccountsRecord> CONSTRAINT_A = UniqueKeys0.CONSTRAINT_A;
    public static final UniqueKey<TransactionsRecord> CONSTRAINT_F = UniqueKeys0.CONSTRAINT_F;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<TransactionsRecord, Integer> IDENTITY_TRANSACTIONS = Internal.createIdentity(Transactions.TRANSACTIONS, Transactions.TRANSACTIONS.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<AccountsRecord> CONSTRAINT_A = Internal.createUniqueKey(Accounts.ACCOUNTS, "CONSTRAINT_A", Accounts.ACCOUNTS.ID);
        public static final UniqueKey<TransactionsRecord> CONSTRAINT_F = Internal.createUniqueKey(Transactions.TRANSACTIONS, "CONSTRAINT_F", Transactions.TRANSACTIONS.ID);
    }
}
