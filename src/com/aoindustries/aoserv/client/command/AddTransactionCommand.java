/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.Transaction;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.util.i18n.Money;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class AddTransactionCommand extends RemoteCommand<Integer> {

    private static final long serialVersionUID = 1L;

    final private AccountingCode accounting;
    final private AccountingCode sourceAccounting;
    final private UserId username;
    final private String type;
    final private String description;
    final private BigDecimal quantity;
    final private Money rate;
    final private String paymentType;
    final private String paymentInfo;
    final private String processor;
    final private Transaction.Status status;

    public AddTransactionCommand(
        @Param(name="accounting") AccountingCode accounting,
        @Param(name="sourceAccounting") AccountingCode sourceAccounting,
        @Param(name="username") UserId username,
        @Param(name="type") String type,
        @Param(name="description") String description,
        @Param(name="quantity") BigDecimal quantity,
        @Param(name="rate") Money rate,
        @Param(name="paymentType", nullable=true) String paymentType,
        @Param(name="paymentInfo", nullable=true) String paymentInfo,
        @Param(name="processor", nullable=true) String processor,
        @Param(name="status", syntax="{Y|N|W}") Transaction.Status status
    ) {
        this.accounting = accounting;
        this.sourceAccounting = sourceAccounting;
        this.username = username;
        this.type = type;
        this.description = description;
        this.quantity = quantity;
        this.rate = rate;
        this.paymentType = nullIfEmpty(paymentType);
        this.paymentInfo = nullIfEmpty(paymentInfo);
        this.processor = nullIfEmpty(processor);
        this.status = status;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public AccountingCode getSourceAccounting() {
        return sourceAccounting;
    }

    public UserId getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Money getRate() {
        return rate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public String getProcessor() {
        return processor;
    }

    public Transaction.Status getStatus() {
        return status;
    }

    @Override
    public boolean isReadOnlyCommand() {
        return false;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
