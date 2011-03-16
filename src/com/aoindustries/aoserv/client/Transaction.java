/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.FastExternalizableReadContext;
import com.aoindustries.io.FastExternalizableWriteContext;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
final public class Transaction extends AOServObjectIntegerKey implements Comparable<Transaction>, DtoFactory<com.aoindustries.aoserv.client.dto.Transaction>, FastExternalizable {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // The scale of the quantity value.
    private static final int QUANTITY_SCALE = 3;

    /**
     * Payment confirmation.
     */
    public enum Status {
        W(0),
        Y(1),
        N(2);

        static final Status valueOfByte(int val) {
            if(val==0) return W;
            if(val==1) return Y;
            if(val==2) return N;
            throw new IllegalArgumentException("Unexpected value: "+val);
        }

        final int byteValue;

        private Status(int byteValue) {
            this.byteValue = byteValue;
        }

        @Override
        public String toString() {
            return ApplicationResources.accessor.getMessage("Transaction.Status."+name()+".toString");
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private long time;
    private AccountingCode accounting;
    private AccountingCode sourceAccounting;
    private UserId username;
    private String type;
    transient private String description;
    private long quantity; // Always has scale = 3
    private Money rate;
    private String paymentType;
    private String paymentInfo;
    private String processor;
    private Integer creditCardTransaction;
    private Status status;

    public Transaction(
        AOServConnector connector,
        int transid,
        long time,
        AccountingCode accounting,
        AccountingCode sourceAccounting,
        UserId username,
        String type,
        long quantity,
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

    //private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    //    in.defaultReadObject();
    //    intern();
    //}

    private void intern() {
        accounting = intern(accounting);
        sourceAccounting = intern(sourceAccounting);
        username = intern(username);
        type = intern(type);
        paymentType = intern(paymentType);
        processor = intern(processor);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = 337047955181031340L;

    public Transaction() {
    }

    @Override
    public long getSerialVersionUID() {
        return super.getSerialVersionUID() ^ serialVersionUID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(time);
        FastExternalizableWriteContext.writeFastUTFInContext(out, accounting.toString());
        FastExternalizableWriteContext.writeFastUTFInContext(out, sourceAccounting.toString());
        FastExternalizableWriteContext.writeFastUTFInContext(out, username.toString());
        FastExternalizableWriteContext.writeFastUTFInContext(out, type);
        out.writeLong(quantity);
        FastExternalizableWriteContext.writeFastObjectInContext(out, rate);
        FastExternalizableWriteContext.writeFastUTFInContext(out, paymentType);
        writeNullUTF(out, paymentInfo);
        FastExternalizableWriteContext.writeFastUTFInContext(out, processor);
        writeNullInteger(out, creditCardTransaction);
        out.writeByte(status.byteValue);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        try {
            time = in.readLong();
            accounting = AccountingCode.valueOf(FastExternalizableReadContext.readFastUTFInContext(in));
            sourceAccounting = AccountingCode.valueOf(FastExternalizableReadContext.readFastUTFInContext(in));
            username = UserId.valueOf(FastExternalizableReadContext.readFastUTFInContext(in));
            type = FastExternalizableReadContext.readFastUTFInContext(in);
            quantity = in.readLong();
            rate = (Money)FastExternalizableReadContext.readFastObjectInContext(in);
            paymentType = FastExternalizableReadContext.readFastUTFInContext(in);
            paymentInfo = readNullUTF(in);
            processor = FastExternalizableReadContext.readFastUTFInContext(in);
            creditCardTransaction = readNullInteger(in);
            status = Status.valueOfByte(in.readByte());
            intern();
        } catch(ValidationException exc) {
            throw new IOException(exc);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Transaction other) {
        int diff = compare(time, other.time);
        if(diff!=0) return diff;
        return compare(getKeyInt(), other.getKeyInt());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique identifier for this transaction")
    public int getTransid() {
    	return getKeyInt();
    }

    @SchemaColumn(order=1, description="the time the transaction occured")
    public Timestamp getTime() {
    	return new Timestamp(time);
    }

    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(Transaction.class, "business");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the identifier for the business")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(accounting);
    }

    public static final MethodColumn COLUMN_SOURCE_BUSINESS = getMethodColumn(Transaction.class, "sourceBusiness");
    @DependencySingleton
    @SchemaColumn(order=3, index=IndexType.INDEXED, description="the source of the charge to this account")
    public Business getSourceBusiness() throws RemoteException {
        return getConnector().getBusinesses().get(sourceAccounting);
    }

    public static final MethodColumn COLUMN_BUSINESS_ADMINISTRATOR = getMethodColumn(Transaction.class, "businessAdministrator");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=4, index=IndexType.INDEXED, description="the admin involved in the transaction")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        try {
            return getConnector().getBusinessAdministrators().get(username);
        } catch(NoSuchElementException err) {
            return null;
        }
    }

    public static final MethodColumn COLUMN_TYPE = getMethodColumn(Transaction.class, "type");
    @DependencySingleton
    @SchemaColumn(order=5, index=IndexType.INDEXED, description="the type of transaction")
    public TransactionType getType() throws RemoteException {
        return getConnector().getTransactionTypes().get(type);
    }

    @SchemaColumn(order=6, description="description of the transaction")
    synchronized public String getDescription() throws RemoteException {
        if(description==null) description = new GetTransactionDescriptionCommand(this).execute(getConnector());
        return description;
    }

    @SchemaColumn(order=7, description="the quantity of the rate applied to the account")
    public BigDecimal getQuantity() {
    	return BigDecimal.valueOf(quantity, QUANTITY_SCALE);
    }

    @SchemaColumn(order=8, description="the amount per unit of quantity")
    public Money getRate() {
    	return rate;
    }

    public static final MethodColumn COLUMN_PAYMENT_TYPE = getMethodColumn(Transaction.class, "paymentType");
    @DependencySingleton
    @SchemaColumn(order=9, index=IndexType.INDEXED, description="the type of payment made")
    public PaymentType getPaymentType() throws RemoteException {
        if (paymentType == null) return null;
        return getConnector().getPaymentTypes().get(paymentType);
    }

    @SchemaColumn(order=10, description="the payment info, such as last four of a credit card number or a check number")
    public String getPaymentInfo() {
    	return paymentInfo;
    }

    public static final MethodColumn COLUMN_PROCESSOR = getMethodColumn(Transaction.class, "processor");
    @DependencySingleton
    @SchemaColumn(order=11, index=IndexType.INDEXED, description="the credit card processor that handled the payment")
    public CreditCardProcessor getProcessor() throws RemoteException {
        if(processor==null) return null;
        return getConnector().getCreditCardProcessors().get(processor);
    }

    public static final MethodColumn COLUMN_CREDIT_CARD_TRANSACTION = getMethodColumn(Transaction.class, "creditCardTransaction");
    /**
     * May be filtered.
     */
    @SchemaColumn(order=12, index=IndexType.UNIQUE, description="the credit card transaction for this transaction")
    @DependencySingleton
    public CreditCardTransaction getCreditCardTransaction() throws RemoteException {
        if(creditCardTransaction==null) return null;
        return getConnector().getCreditCardTransactions().filterUnique(CreditCardTransaction.COLUMN_PKEY, creditCardTransaction);
    }

    @SchemaColumn(order=13, description="the status of the transaction")
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
            dto.getQuantity().movePointRight(QUANTITY_SCALE).longValueExact(),
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
            getKeyInt(),
            time,
            getDto(accounting),
            getDto(sourceAccounting),
            getDto(username),
            type,
            getQuantity(),
            getDto(rate),
            paymentType,
            paymentInfo,
            processor,
            creditCardTransaction,
            status.name()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return
            getKeyInt()
            + "|"
            + accounting
            + '|'
            + sourceAccounting
            + '|'
            + type
            + '|'
            + getQuantity()
            + 'x'
            + rate
            + '|'
            + status.name()
        ;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
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
