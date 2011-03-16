/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.i18n.Money;
import java.rmi.RemoteException;
import java.sql.Date;

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
final public class BankTransaction extends AOServObjectIntegerKey implements Comparable<BankTransaction>, DtoFactory<com.aoindustries.aoserv.client.dto.BankTransaction> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -1135821486666616828L;

    final private long date;
    private String bankAccount;
    private String processor;
    private UserId administrator;
    private String type;
    private String expenseCode;
    final private String description;
    final private String checkNo;
    final private Money amount;

    public BankTransaction(
        AOServConnector connector,
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
        super(connector, transid);
        this.date = date;
        this.bankAccount = bankAccount;
        this.processor = processor;
        this.administrator = administrator;
        this.type = type;
        this.expenseCode = expenseCode;
        this.description = description;
        this.checkNo = checkNo;
        this.amount = amount;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        bankAccount = intern(bankAccount);
        processor = intern(processor);
        administrator = intern(administrator);
        type = intern(type);
        expenseCode = intern(expenseCode);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(BankTransaction other) {
        int diff = compare(date, other.date);
        if(diff!=0) return diff;
        return compare(getKeyInt(), other.getKeyInt());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, description="the date the transaction occured")
    public Date getDate() {
    	return new Date(date);
    }

    @SchemaColumn(order=1, index=IndexType.PRIMARY_KEY, description="a unique identifier for the transaction")
    public int getTransid() {
    	return getKeyInt();
    }

    public static final MethodColumn COLUMN_BANK_ACCOUNT = getMethodColumn(BankTransaction.class, "bankAccount");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the account the transaction is for")
    public BankAccount getBankAccount() throws RemoteException {
    	return getConnector().getBankAccounts().get(bankAccount);
    }

    public static final MethodColumn COLUMN_PROCESSOR = getMethodColumn(BankTransaction.class, "processor");
    @DependencySingleton
    @SchemaColumn(order=3, index=IndexType.INDEXED, description="the credit card processor used by this transaction")
    public CreditCardProcessor getProcessor() throws RemoteException {
        if (processor == null) return null;
        return getConnector().getCreditCardProcessors().get(processor);
    }

    public static final MethodColumn COLUMN_ADMINISTRATOR = getMethodColumn(BankTransaction.class, "administrator");
    @DependencySingleton
    @SchemaColumn(order=4, index=IndexType.INDEXED, description="the business_administrator who made this transaction")
    public MasterUser getAdministrator() throws RemoteException {
    	return getConnector().getMasterUsers().get(administrator);
    }

    public static final MethodColumn COLUMN_TYPE = getMethodColumn(BankTransaction.class, "type");
    @DependencySingleton
    @SchemaColumn(order=5, index=IndexType.INDEXED, description="the type of transaction")
    public BankTransactionType getType() throws RemoteException {
        return getConnector().getBankTransactionTypes().get(type);
    }

    public static final MethodColumn COLUMN_EXPENSE_CODE = getMethodColumn(BankTransaction.class, "expenseCode");
    @DependencySingleton
    @SchemaColumn(order=6, index=IndexType.INDEXED, description="the category in which this expense belongs")
    public ExpenseCategory getExpenseCode() throws RemoteException {
        if(expenseCode==null) return null;
        return getConnector().getExpenseCategories().get(expenseCode);
    }

    @SchemaColumn(order=7, description="a description of the transaction")
    public String getDescription() {
    	return description;
    }

    @SchemaColumn(order=8, description="the check number (if available)")
    public String getCheckNo() {
    	return checkNo;
    }

    @SchemaColumn(order=9, description="the amount (negative for withdrawal)")
    public Money getAmount() {
    	return amount;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public BankTransaction(AOServConnector connector, com.aoindustries.aoserv.client.dto.BankTransaction dto) throws ValidationException {
        this(
            connector,
            getTimeMillis(dto.getDate()),
            dto.getTransid(),
            dto.getBankAccount(),
            dto.getProcessor(),
            getUserId(dto.getAdministrator()),
            dto.getType(),
            dto.getExpenseCode(),
            dto.getDescription(),
            dto.getCheckNo(),
            getMoney(dto.getAmount())
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.BankTransaction getDto() {
        return new com.aoindustries.aoserv.client.dto.BankTransaction(
            date,
            getKeyInt(),
            bankAccount,
            processor,
            getDto(administrator),
            type,
            expenseCode,
            description,
            checkNo,
            getDto(amount)
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return getKeyInt()+"|"+administrator+'|'+type+'|'+amount;
    }
    // </editor-fold>
}
