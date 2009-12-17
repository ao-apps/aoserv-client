package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * Each <code>Business</code> has an account of all the
 * charges and payments processed.  Each entry in this
 * account is a <code>Transaction</code>.
 *
 * @see  Business
 *
 * @author  AO Industries, Inc.
 */
final public class Transaction extends CachedObjectIntegerKey<Transaction> {

    static final int
        COLUMN_TRANSID = 1,
        COLUMN_ACCOUNTING = 2,
        COLUMN_SOURCE_ACCOUNTING = 3,
        COLUMN_USERNAME = 4,
        COLUMN_TYPE = 5,
        COLUMN_PAYMENT_TYPE = 9,
        COLUMN_PROCESSOR = 11,
        COLUMN_CREDIT_CARD_TRANSACTION = 12
    ;
    static final String COLUMN_TIME_name = "time";
    static final String COLUMN_TRANSID_name = "transid";

    /**
     * Represents not being assigned for a field of the <code>int</code> type.
     */
    public static final int UNASSIGNED = -1;

    private long time;
    String
        accounting,
        source_accounting,
        username,
        type
    ;
    private String description;

    /**
     * The quantity in 1000th's of a unit
     */
    private int quantity;

    /**
     * The rate in pennies.
     */
    private int rate;

    String payment_type, payment_info, processor;
    private int creditCardTransaction;

    /**
     * Payment confirmation.
     */
    public static final byte WAITING_CONFIRMATION = 0, CONFIRMED = 1, NOT_CONFIRMED = 2;

    /**
     * The text to display for different confirmation statuses.
     */
    private static final String[] paymentConfirmationLabels = { "Waiting", "Confirmed", "Failed" };

    public static final int NUM_PAYMENT_CONFIRMATION_STATES=3;

    byte payment_confirmed;

    public void approved(final int creditCardTransaction) throws IOException, SQLException {
        table.connector.requestUpdate(
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
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void declined(final int creditCardTransaction) throws IOException, SQLException {
        table.connector.requestUpdate(
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
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    public void held(final int creditCardTransaction) throws IOException, SQLException {
        table.connector.requestUpdate(
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
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    /**
     * @deprecated  Please directly access via <code>getCreditCardTransaction()</code>.
     *              Beware that <code>getCreditCardTransaction()</code> might return <code>null</code>.
     *
     * @see  #getCreditCardTransaction()
     * @see  CreditCardTransaction#getAuthorizationApprovalCode()
     */
    public String getAprNum() throws SQLException, IOException {
        CreditCardTransaction cct = getCreditCardTransaction();
        return cct==null ? null : cct.getAuthorizationApprovalCode();
    }

    public Business getBusiness() throws SQLException, IOException {
        Business business = table.connector.getBusinesses().get(accounting);
        if (business == null) throw new SQLException("Unable to find Business: " + accounting);
        return business;
    }
    
    public Business getSourceBusiness() throws SQLException, IOException {
        Business business = table.connector.getBusinesses().get(source_accounting);
        if (business == null) throw new SQLException("Unable to find Business: " + source_accounting);
        return business;
    }

    public BusinessAdministrator getBusinessAdministrator() throws SQLException, IOException {
        Username un=table.connector.getUsernames().get(username);
        // May be filtered
        if(un==null) return null;
        BusinessAdministrator business_administrator = un.getBusinessAdministrator();
        if (business_administrator == null) throw new SQLException("Unable to find BusinessAdministrator: " + username);
        return business_administrator;
    }

    Object getColumnImpl(int i) throws IOException, SQLException {
        switch(i) {
            case 0: return new java.sql.Date(time);
            case COLUMN_TRANSID: return pkey;
            case COLUMN_ACCOUNTING: return accounting;
            case COLUMN_SOURCE_ACCOUNTING: return source_accounting;
            case COLUMN_USERNAME: return username;
            case COLUMN_TYPE: return type;
            case 6: return getDescription();
            case 7: return Integer.valueOf(quantity);
            case 8: return Integer.valueOf(rate);
            case COLUMN_PAYMENT_TYPE: return payment_type;
            case 10: return payment_info;
            case COLUMN_PROCESSOR: return processor;
            case COLUMN_CREDIT_CARD_TRANSACTION: return creditCardTransaction==-1 ? null : Integer.valueOf(creditCardTransaction);
            case 13: return payment_confirmed==CONFIRMED?"Y":payment_confirmed==NOT_CONFIRMED?"N":"W";
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    synchronized public String getDescription() throws IOException, SQLException {
        if(description==null) description = table.connector.requestLongStringQuery(true, AOServProtocol.CommandID.GET_TRANSACTION_DESCRIPTION, pkey);
        return description;
    }

    public CreditCardProcessor getCreditCardProcessor() throws SQLException, IOException {
        if (processor == null) return null;
        CreditCardProcessor creditCardProcessor = table.connector.getCreditCardProcessors().get(processor);
        if (creditCardProcessor == null) throw new SQLException("Unable to find CreditCardProcessor: " + processor);
        return creditCardProcessor;
    }

    public CreditCardTransaction getCreditCardTransaction() throws SQLException, IOException {
        if (creditCardTransaction == -1) return null;
        CreditCardTransaction cct = table.connector.getCreditCardTransactions().get(creditCardTransaction);
        if (cct == null) throw new SQLException("Unable to find CreditCardTransaction: " + creditCardTransaction);
        return cct;
    }

    public byte getPaymentConfirmation() {
    	return payment_confirmed;
    }

    public static String getPaymentConfirmationLabel(int index) {
    	return paymentConfirmationLabels[index];
    }

    public String getPaymentInfo() {
    	return payment_info;
    }

    public PaymentType getPaymentType() throws SQLException, IOException {
        if (payment_type == null) return null;
        PaymentType paymentType = table.connector.getPaymentTypes().get(payment_type);
        if (paymentType == null) throw new SQLException("Unable to find PaymentType: " + payment_type);
        return paymentType;
    }

    public long getPennies() {
        long pennies=(long)quantity*(long)rate/(long)100;
        int fraction=(int)(pennies%10);
        pennies/=10;
        if(fraction>=5) pennies++;
        else if(fraction<=-5) pennies--;
        return pennies;
    }

    public BigDecimal getQuantity() {
    	return BigDecimal.valueOf(quantity, 3);
    }

    public BigDecimal getRate() {
    	return BigDecimal.valueOf(rate, 2);
    }

    /**
     * Gets the amount of the transaction, which is the quantity*rate scaled back
     * to two digits, rounding half_up.
     */
    public BigDecimal getAmount() {
        return getQuantity().multiply(getRate()).setScale(2, RoundingMode.HALF_UP);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TRANSACTIONS;
    }

    public long getTime() {
    	return time;
    }

    public int getTransID() {
    	return pkey;
    }

    public TransactionType getType() throws SQLException, IOException {
        TransactionType tt = table.connector.getTransactionTypes().get(type);
        if (tt == null) throw new SQLException("Unable to find TransactionType: " + type);
        return tt;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        time = result.getTimestamp(pos++).getTime();
        pkey = result.getInt(pos++);
        accounting = result.getString(pos++);
        source_accounting = result.getString(pos++);
        username = result.getString(pos++);
        type = result.getString(pos++);
        quantity = SQLUtility.getMillis(result.getString(pos++));
        rate = SQLUtility.getPennies(result.getString(pos++));
        payment_type = result.getString(pos++);
        payment_info = result.getString(pos++);
        processor = result.getString(pos++);
        creditCardTransaction = result.getInt(pos++);
        if(result.wasNull()) creditCardTransaction = -1;
        String typeString = result.getString(pos++);
        if("Y".equals(typeString)) payment_confirmed=CONFIRMED;
        else if("N".equals(typeString)) payment_confirmed=NOT_CONFIRMED;
        else if("W".equals(typeString)) payment_confirmed=WAITING_CONFIRMATION;
        else throw new SQLException("Unknown payment_confirmed '" + typeString + "' for transid=" + pkey);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        time=in.readLong();
        pkey=in.readCompressedInt();
    	accounting=in.readCompressedUTF().intern();
        source_accounting=in.readCompressedUTF().intern();
        username=in.readCompressedUTF().intern();
        type=in.readCompressedUTF().intern();
        quantity=in.readCompressedInt();
        rate=in.readCompressedInt();
        payment_type=StringUtility.intern(in.readNullUTF());
        payment_info=in.readNullUTF();
        processor = StringUtility.intern(in.readNullUTF());
        creditCardTransaction = in.readCompressedInt();
    	payment_confirmed=in.readByte();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness(),
            getSourceBusiness(),
            getBusinessAdministrator(),
            getCreditCardProcessor(),
            getCreditCardTransaction()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getNoticeLogs()
        );
    }

    @Override
    String toStringImpl(Locale userLocale) {
	return
            pkey
            +"|"
            +accounting
            +'|'
            +source_accounting
            +'|'
            +type
            +'|'
            +SQLUtility.getMilliDecimal(quantity)
            +'x'
            +SQLUtility.getDecimal(rate)
            +'|'
            +(
                payment_confirmed==CONFIRMED?'Y'
                :payment_confirmed==NOT_CONFIRMED?'N'
                :'W'
            )
	;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeLong(time);
        out.writeCompressedInt(pkey);
        out.writeCompressedUTF(accounting, 0);
        out.writeCompressedUTF(source_accounting, 1);
        out.writeCompressedUTF(username, 2);
        out.writeCompressedUTF(type, 3);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_61)<=0) out.writeCompressedUTF("Descriptions are unavailable for client version<=1.61", 4);
        out.writeCompressedInt(quantity);
        out.writeCompressedInt(rate);
        out.writeNullUTF(payment_type);
        out.writeNullUTF(payment_info);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_29)<0) {
            out.writeNullUTF(null);
        } else {
            out.writeNullUTF(processor);
            out.writeCompressedInt(creditCardTransaction);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_128)<0) {
            out.writeCompressedInt(-1);
        } else if(version.compareTo(AOServProtocol.Version.VERSION_1_29)<0) {
            out.writeNullUTF(null);
        }
    	out.writeByte(payment_confirmed);
    }

    public List<NoticeLog> getNoticeLogs() throws IOException, SQLException {
        return table.connector.getNoticeLogs().getIndexedRows(NoticeLog.COLUMN_TRANSID, pkey);
    }
}