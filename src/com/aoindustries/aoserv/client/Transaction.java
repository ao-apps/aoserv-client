package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.i18n.Money;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Set;

/**
 * Each <code>Business</code> has an account of all the
 * charges and payments processed.  Each entry in this
 * account is a <code>Transaction</code>.
 *
 * @see  Business
 *
 * @author  AO Industries, Inc.
 */
final public class Transaction extends AOServObjectIntegerKey<Transaction> implements BeanFactory<com.aoindustries.aoserv.client.beans.Transaction> {

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
    final private Timestamp time;
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
        TransactionService<?,?> service,
        int transid,
        Timestamp time,
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
        super(service, transid);
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
    protected int compareToImpl(Transaction other) {
        int diff = time.compareTo(other.time);
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
    	return time;
    }

    @SchemaColumn(order=2, name="accounting", description="the identifier for the business")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=3, name="source_accounting", description="the source of the charge to this account")
    public Business getSourceBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(sourceAccounting);
    }

    /**
     * May be filtered.
     */
    @SchemaColumn(order=4, name="username", description="the admin involved in the transaction")
    public BusinessAdministrator getBusinessAdministrator() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().filterUnique(BusinessAdministrator.COLUMN_USERNAME, username);
    }

    @SchemaColumn(order=5, name="type", description="the type of transaction")
    public TransactionType getType() throws RemoteException {
        return getService().getConnector().getTransactionTypes().get(type);
    }

    /* TODO
    @SchemaColumn(order=6, name="description", description="description of the transaction")
    synchronized public String getDescription() throws RemoteException {
        if(description==null) description = new GetTransactionDescriptionCommand(key).execute(getService().getConnector());
        return description;
    }
     */

    @SchemaColumn(order=6, name="quantity", description="the quantity of the rate applied to the account")
    public BigDecimal getQuantity() {
    	return quantity;
    }

    @SchemaColumn(order=7, name="rate", description="the amount per unit of quantity")
    public Money getRate() {
    	return rate;
    }

    @SchemaColumn(order=8, name="payment_type", description="the type of payment made")
    public PaymentType getPaymentType() throws RemoteException {
        if (paymentType == null) return null;
        return getService().getConnector().getPaymentTypes().get(paymentType);
    }

    @SchemaColumn(order=9, name="payment_info", description="the payment info, such as last four of a credit card number or a check number")
    public String getPaymentInfo() {
    	return paymentInfo;
    }

    @SchemaColumn(order=10, name="processor", description="the credit card processor that handled the payment")
    public CreditCardProcessor getProcessor() throws RemoteException {
        if(processor==null) return null;
        return getService().getConnector().getCreditCardProcessors().get(processor);
    }

    /* TODO
    @SchemaColumn(order=12, name="credit_card_transaction", description="the credit card transaction for this transaction")
    public CreditCardTransaction getCreditCardTransaction() throws RemoteException {
        if(creditCardTransaction==null) return null;
        return getService().getConnector().getCreditCardTransactions().get(creditCardTransaction);
    }
     */

    @SchemaColumn(order=11, name="status", description="the status of the transaction")
    public Status getStatus() {
    	return status;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.Transaction getBean() {
        return new com.aoindustries.aoserv.client.beans.Transaction(
            key,
            time,
            getBean(accounting),
            getBean(sourceAccounting),
            getBean(username),
            type,
            quantity,
            getBean(rate),
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
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getBusiness(),
            getSourceBusiness(),
            getBusinessAdministrator(),
            getType(),
            getPaymentType(),
            getProcessor()
            // TODO: getCreditCardTransaction()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            // TODO: getNoticeLogs()
        );
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
        return getService().getConnector().getNoticeLogs().getIndexedRows(NoticeLog.COLUMN_TRANSID, pkey);
    } */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
    public void approved(final int creditCardTransaction) throws RemoteException {
        getService().getConnector().requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_APPROVED.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(creditCardTransaction);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void declined(final int creditCardTransaction) throws IOException, SQLException {
        getService().getConnector().requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_DECLINED.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(creditCardTransaction);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void held(final int creditCardTransaction) throws IOException, SQLException {
        getService().getConnector().requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_HELD.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(creditCardTransaction);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    getService().getConnector().tablesUpdated(invalidateList);
                }
            }
        );
    }
    */

    /**
     * @deprecated  Please directly access via <code>getCreditCardTransaction()</code>.
     *              Beware that <code>getCreditCardTransaction()</code> might return <code>null</code>.
     *
     * @see  #getCreditCardTransaction()
     * @see  CreditCardTransaction#getAuthorizationApprovalCode()
     */
    /* TODO
    public String getAprNum() throws SQLException, IOException {
        CreditCardTransaction cct = getCreditCardTransaction();
        return cct==null ? null : cct.getAuthorizationApprovalCode();
    }
    */

    /* TODO
    private long getPennies() {
        long pennies=(long)quantity*(long)rate/(long)100;
        int fraction=(int)(pennies%10);
        pennies/=10;
        if(fraction>=5) pennies++;
        else if(fraction<=-5) pennies--;
        return pennies;
    }
    */

    /**
     * Gets the amount of the transaction, which is the quantity*rate scaled back
     * to two digits, rounding half_up.
     */
    /* TODO
    public BigDecimal getAmount() {
        BigDecimal amount = getQuantity().multiply(getRate()).setScale(2, RoundingMode.HALF_UP);
        if(!amount.equals(BigDecimal.valueOf(getPennies(), 2))) throw new AssertionError("amount!=pennies");
        return amount;
    }*/
    // </editor-fold>
}