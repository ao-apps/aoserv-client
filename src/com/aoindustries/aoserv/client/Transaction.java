/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

/**
 * Each <code>Business</code> has an account of all the
 * charges and payments processed.  Each entry in this
 * account is a <code>Transaction</code>.
 *
 * @see  Business
 *
 * @author  AO Industries, Inc.
 */
final public class Transaction extends AOServObjectIntegerKey implements Comparable<Transaction>, DtoFactory<com.aoindustries.aoserv.client.dto.Transaction> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * Payment confirmation.
     */
    public enum Status {
        W,
        Y,
        N;

        @Override
        public String toString() {
            return ApplicationResources.accessor.getMessage("Transaction.Status."+name()+".toString");
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private long time;
    private AccountingCode accounting;
    private AccountingCode sourceAccounting;
    private UserId username;
    private String type;
    transient private String description;
    final private BigDecimal quantity;
    final private Money rate;
    private String paymentType;
    final private String paymentInfo;
    private String processor;
    final private Integer creditCardTransaction;
    final private Status status;

    public Transaction(
        AOServConnector connector,
        int transid,
        long time,
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
        Status status
    ) {
        super(connector, transid);
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
        intern();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        accounting = intern(accounting);
        sourceAccounting = intern(sourceAccounting);
        username = intern(username);
        type = intern(type);
        paymentType = intern(paymentType);
        processor = intern(processor);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Transaction other) {
        int diff = AOServObjectUtils.compare(time, other.time);
        if(diff!=0) return diff;
        return AOServObjectUtils.compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="transid", index=IndexType.PRIMARY_KEY, description="the unique identifier for this transaction")
    public int getTransid() {
    	return key;
    }

    @SchemaColumn(order=1, name="time", description="the time the transaction occured")
    public Timestamp getTime() {
    	return new Timestamp(time);
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=2, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the identifier for the business")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(accounting);
    }

    static final String COLUMN_SOURCE_ACCOUNTING = "source_accounting";
    @SchemaColumn(order=3, name=COLUMN_SOURCE_ACCOUNTING, index=IndexType.INDEXED, description="the source of the charge to this account")
    public Business getSourceBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(sourceAccounting);
    }

    static final String COLUMN_USERNAME = "username";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=4, name=COLUMN_USERNAME, index=IndexType.INDEXED, description="the admin involved in the transaction")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        try {
            return getConnector().getBusinessAdministrators().get(username);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    static final String COLUMN_TYPE = "type";
    @SchemaColumn(order=5, name=COLUMN_TYPE, index=IndexType.INDEXED, description="the type of transaction")
    public TransactionType getType() throws RemoteException {
        return getConnector().getTransactionTypes().get(type);
    }

    @SchemaColumn(order=6, name="description", description="description of the transaction")
    synchronized public String getDescription() throws RemoteException {
        if(description==null) description = new GetTransactionDescriptionCommand(this).execute(getConnector());
        return description;
    }

    @SchemaColumn(order=7, name="quantity", description="the quantity of the rate applied to the account")
    public BigDecimal getQuantity() {
    	return quantity;
    }

    @SchemaColumn(order=8, name="rate", description="the amount per unit of quantity")
    public Money getRate() {
    	return rate;
    }

    static final String COLUMN_PAYMENT_TYPE = "payment_type";
    @SchemaColumn(order=9, name=COLUMN_PAYMENT_TYPE, index=IndexType.INDEXED, description="the type of payment made")
    public PaymentType getPaymentType() throws RemoteException {
        if (paymentType == null) return null;
        return getConnector().getPaymentTypes().get(paymentType);
    }

    @SchemaColumn(order=10, name="payment_info", description="the payment info, such as last four of a credit card number or a check number")
    public String getPaymentInfo() {
    	return paymentInfo;
    }

    static final String COLUMN_PROCESSOR = "processor";
    @SchemaColumn(order=11, name=COLUMN_PROCESSOR, index=IndexType.INDEXED, description="the credit card processor that handled the payment")
    public CreditCardProcessor getProcessor() throws RemoteException {
        if(processor==null) return null;
        return getConnector().getCreditCardProcessors().get(processor);
    }

    static final String COLUMN_CREDIT_CARD_TRANSACTION = "credit_card_transaction";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=12, name=COLUMN_CREDIT_CARD_TRANSACTION, index=IndexType.UNIQUE, description="the credit card transaction for this transaction")
    public CreditCardTransaction getCreditCardTransaction() throws RemoteException {
        if(creditCardTransaction==null) return null;
        return getConnector().getCreditCardTransactions().filterUnique(CreditCardTransaction.COLUMN_PKEY, creditCardTransaction);
    }

    @SchemaColumn(order=13, name="status", description="the status of the transaction")
    public Status getStatus() {
    	return status;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Transaction(AOServConnector connector, com.aoindustries.aoserv.client.dto.Transaction dto) throws ValidationException {
        this(
            connector,
            dto.getTransid(),
            getTimeMillis(dto.getTime()),
            getAccountingCode(dto.getAccounting()),
            getAccountingCode(dto.getSourceAccounting()),
            getUserId(dto.getUsername()),
            dto.getType(),
            dto.getQuantity(),
            getMoney(dto.getRate()),
            dto.getPaymentType(),
            dto.getPaymentInfo(),
            dto.getProcessor(),
            dto.getCreditCardTransaction(),
            Transaction.Status.valueOf(dto.getStatus())
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Transaction getDto() {
        return new com.aoindustries.aoserv.client.dto.Transaction(
            key,
            time,
            getDto(accounting),
            getDto(sourceAccounting),
            getDto(username),
            type,
            quantity,
            getDto(rate),
            paymentType,
            paymentInfo,
            processor,
            creditCardTransaction,
            status.name()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getSourceBusiness());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessAdministrator());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPaymentType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getProcessor());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreditCardTransaction());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getNoticeLogs());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return
            key
            + "|"
            + accounting
            + '|'
            + sourceAccounting
            + '|'
            + type
            + '|'
            + quantity
            + 'x'
            + rate
            + '|'
            + status.name()
        ;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<NoticeLog> getNoticeLogs() throws IOException, SQLException {
        return getConnector().getNoticeLogs().getIndexedRows(NoticeLog.COLUMN_TRANSID, pkey);
    } */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Monetary Calculations">
    /**
     * Gets the amount of the transaction, which is the quantity*rate scaled
     * to the number of digits for the currency, rounding half_up.
     */
    public Money getAmount() {
        return getRate().multiply(getQuantity(), RoundingMode.HALF_UP);
    }
    // </editor-fold>
}