/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.Business;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.CreditCardProcessor;
import com.aoindustries.aoserv.client.PaymentType;
import com.aoindustries.aoserv.client.Transaction;
import com.aoindustries.aoserv.client.TransactionType;
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
        @Param(name="business") Business business,
        @Param(name="sourceBusiness") Business sourceBusiness,
        @Param(name="businessAdministrator") BusinessAdministrator businessAdministrator,
        @Param(name="type") TransactionType type,
        @Param(name="description") String description,
        @Param(name="quantity") BigDecimal quantity,
        @Param(name="rate") Money rate,
        @Param(name="paymentType", nullable=true) PaymentType paymentType,
        @Param(name="paymentInfo", nullable=true) String paymentInfo,
        @Param(name="processor", nullable=true) CreditCardProcessor processor,
        @Param(name="status", syntax="{Y|N|W}") Transaction.Status status
    ) {
        this.accounting = business.getAccounting();
        this.sourceAccounting = sourceBusiness.getAccounting();
        this.username = businessAdministrator.getUserId();
        this.type = type.getName();
        this.description = description;
        this.quantity = quantity;
        this.rate = rate;
        this.paymentType = paymentType==null ? null : paymentType.getName();
        this.paymentInfo = nullIfEmpty(paymentInfo);
        this.processor = processor==null ? null : processor.getProviderId();
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
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
