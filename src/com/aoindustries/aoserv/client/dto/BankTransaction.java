/*
 * Copyright 2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

import java.util.Calendar;

/**
 * @author  AO Industries, Inc.
 */
public class BankTransaction extends AOServObject {

    private long date;
    private int transid;
    private String bankAccount;
    private String processor;
    private UserId administrator;
    private String type;
    private String expenseCode;
    private String description;
    private String checkNo;
    private Money amount;

    public BankTransaction() {
    }

    public BankTransaction(
        long date,
        int transid,
        String bankAccount,
        String processor,
        UserId administrator,
        String type,
        String expenseCode,
        String description,
        String checkNo,
        Money amount
    ) {
        this.date = date;
        this.transid = transid;
        this.bankAccount = bankAccount;
        this.processor = processor;
        this.administrator = administrator;
        this.type = type;
        this.expenseCode = expenseCode;
        this.description = description;
        this.checkNo = checkNo;
        this.amount = amount;
    }

    public Calendar getDate() {
        return DtoUtils.getCalendar(date);
    }

    public void setDate(Calendar date) {
        this.date = date.getTimeInMillis();
    }

    public int getTransid() {
        return transid;
    }

    public void setTransid(int transid) {
        this.transid = transid;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public UserId getAdministrator() {
        return administrator;
    }

    public void setAdministrator(UserId administrator) {
        this.administrator = administrator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpenseCode() {
        return expenseCode;
    }

    public void setExpenseCode(String expenseCode) {
        this.expenseCode = expenseCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }
}
