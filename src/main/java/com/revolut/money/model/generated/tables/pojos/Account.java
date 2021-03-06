/*
 * This file is generated by jOOQ.
 */
package com.revolut.money.model.generated.tables.pojos;


import javax.annotation.Generated;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Account implements Serializable {

    private static final long serialVersionUID = 964996192;

    private Integer    id;
    private BigDecimal balance;

    public Account() {}

    public Account(Account value) {
        this.id = value.id;
        this.balance = value.balance;
    }

    public Account(
        Integer    id,
        BigDecimal balance
    ) {
        this.id = id;
        this.balance = balance;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Account (");

        sb.append(id);
        sb.append(", ").append(balance);

        sb.append(")");
        return sb.toString();
    }
}
