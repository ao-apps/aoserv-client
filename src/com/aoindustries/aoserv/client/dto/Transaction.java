/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author  AO Industries, Inc.
 */
public class Transaction {

    private int transid;
    private Date time;
    private AccountingCode accounting;
    private AccountingCode sourceAccounting;
    private UserId username;
    private String type;
    private BigDecimal quantity;
    private Money rate;
    private String paymentType;
    private String paymentInfo;
    private String processor;
    private Integer creditCardTransaction;
    private String status;

    public Transaction() {
    }

    public Transaction(
        int transid,
        Date time,
        AccountingCode accounting,
        AccountingCode sourceAccounting,
        UserId username,
        String type,
        BigDecimal quantity,
        Money rate,
        String paymentType,
        String paymentInfo,
        String processor,
        Integer creditCardTransaction,
        String status
    ) {
        this.transid = transid;
        this.time = time;
        this.accounting = accounting;
        this.sourceAccounting = sourceAccounting;
        this.username = username;
        this.type = type;
        this.quantity = quantity;
        this.rate = rate;
        this.paymentType = paymentType;
        this.paymentInfo = paymentInfo;
        this.processor = processor;
        this.creditCardTransaction = creditCardTransaction;
        this.status = status;
    }

    public int getTransid() {
        return transid;
    }

    public void setTransid(int transid) {
        this.transid = transid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public void setAccounting(AccountingCode accounting) {
        this.accounting = accounting;
    }

    public AccountingCode getSourceAccounting() {
        return sourceAccounting;
    }

    public void setSourceAccounting(AccountingCode sourceAccounting) {
        this.sourceAccounting = sourceAccounting;
    }

    public UserId getUsername() {
        return username;
    }

    public void setUsername(UserId username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Money getRate() {
        return rate;
    }

    public void setRate(Money rate) {
        this.rate = rate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public Integer getCreditCardTransaction() {
        return creditCardTransaction;
    }

    public void setCreditCardTransaction(Integer creditCardTransaction) {
        this.creditCardTransaction = creditCardTransaction;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
