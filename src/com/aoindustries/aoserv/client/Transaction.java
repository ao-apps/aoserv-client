package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Each <code>Business</code> has an account of all the
 * charges and payments processed.  Each entry in this
 * account is a <code>Transaction</code>.
 *
 * @see  Business
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Transaction extends AOServObject<Integer,Transaction> implements SingleTableObject<Integer,Transaction> {

    static final int
        COLUMN_TRANSID=1,
        COLUMN_ACCOUNTING=2
    ;
    static final String COLUMN_TIME_name = "time";
    static final String COLUMN_TRANSID_name = "transid";

    protected AOServTable<Integer,Transaction> table;

    /**
     * Represents not being assigned for a field of the <code>int</code> type.
     */
    public static final int UNASSIGNED = -1;

    private long time;
    int transid;
    String
        accounting,
        source_accounting,
        username,
        type,
        description
    ;

    /**
     * The quantity in 1000th's of a unit
     */
    private int quantity;

    /**
     * The rate in pennies.
     */
    private int rate;

    private String payment_type, payment_info, processor;
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

    private byte payment_confirmed;

    public void approved(int creditCardTransaction) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_APPROVED.ordinal());
                out.writeCompressedInt(transid);
                out.writeCompressedInt(creditCardTransaction);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void declined(int creditCardTransaction) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_DECLINED.ordinal());
                out.writeCompressedInt(transid);
                out.writeCompressedInt(creditCardTransaction);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void held(int creditCardTransaction) {
        try {
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_HELD.ordinal());
                out.writeCompressedInt(transid);
                out.writeCompressedInt(creditCardTransaction);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    boolean equalsImpl(Object O) {
	return
            O instanceof Transaction
            && ((Transaction)O).transid==transid
	;
    }

    /**
     * @deprecated  Please directly access via <code>getCreditCardTransaction()</code>.
     *              Beware that <code>getCreditCardTransaction()</code> might return <code>null</code>.
     *
     * @see  #getCreditCardTransaction()
     * @see  CreditCardTransaction#getAuthorizationApprovalCode()
     */
    public String getAprNum() {
        CreditCardTransaction cct = getCreditCardTransaction();
        return cct==null ? null : cct.getAuthorizationApprovalCode();
    }

    public Business getBusiness() {
	Business business = table.connector.businesses.get(accounting);
	if (business == null) throw new WrappedException(new SQLException("Unable to find Business: " + accounting));
	return business;
    }
    
    public Business getSourceBusiness() {
	Business business = table.connector.businesses.get(source_accounting);
	if (business == null) throw new WrappedException(new SQLException("Unable to find Business: " + source_accounting));
	return business;
    }

    public BusinessAdministrator getBusinessAdministrator() {
        Username un=table.connector.usernames.get(username);
        // May be filtered
        if(un==null) return null;
        BusinessAdministrator business_administrator = un.getBusinessAdministrator();
        if (business_administrator == null) throw new WrappedException(new SQLException("Unable to find BusinessAdministrator: " + username));
        return business_administrator;
    }

    public Object getColumn(int i) {
        switch(i) {
            case 0: return new java.sql.Date(time);
            case COLUMN_TRANSID: return Integer.valueOf(transid);
            case COLUMN_ACCOUNTING: return accounting;
            case 3: return source_accounting;
            case 4: return username;
            case 5: return type;
            case 6: return description;
            case 7: return Integer.valueOf(quantity);
            case 8: return Integer.valueOf(rate);
            case 9: return payment_type;
            case 10: return payment_info;
            case 11: return processor;
            case 12: return creditCardTransaction==-1 ? null : Integer.valueOf(creditCardTransaction);
            case 13: return payment_confirmed==CONFIRMED?"Y":payment_confirmed==NOT_CONFIRMED?"N":"W";
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDescription() {
	return description;
    }

    public CreditCardProcessor getCreditCardProcessor() {
	if (processor == null) return null;
	CreditCardProcessor creditCardProcessor = table.connector.creditCardProcessors.get(processor);
	if (creditCardProcessor == null) throw new WrappedException(new SQLException("Unable to find CreditCardProcessor: " + processor));
	return creditCardProcessor;
    }

    public CreditCardTransaction getCreditCardTransaction() {
	if (creditCardTransaction == -1) return null;
	CreditCardTransaction cct = table.connector.creditCardTransactions.get(creditCardTransaction);
	if (cct == null) throw new WrappedException(new SQLException("Unable to find CreditCardTransaction: " + creditCardTransaction));
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

    public PaymentType getPaymentType() {
	if (payment_type == null) return null;
	PaymentType paymentType = table.connector.paymentTypes.get(payment_type);
	if (paymentType == null) throw new WrappedException(new SQLException("Unable to find PaymentType: " + payment_type));
	return paymentType;
    }

    public Integer getKey() {
	return transid;
    }

    public long getPennies() {
        long pennies=(long)quantity*(long)rate/(long)100;
        int fraction=(int)(pennies%10);
        pennies/=10;
        if(fraction>=5) pennies++;
        else if(fraction<=-5) pennies--;
        return pennies;
    }

    public int getQuantity() {
	return quantity;
    }

    public int getRate() {
	return rate;
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<Integer,Transaction> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TRANSACTIONS;
    }

    public long getTime() {
	return time;
    }

    public int getTransID() {
	return transid;
    }

    public TransactionType getType() {
        TransactionType tt = table.connector.transactionTypes.get(type);
        if (tt == null) throw new WrappedException(new SQLException("Unable to find TransactionType: " + type));
        return tt;
    }

    int hashCodeImpl() {
	return transid;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
	time = result.getTimestamp(pos++).getTime();
	transid = result.getInt(pos++);
	accounting = result.getString(pos++);
        source_accounting = result.getString(pos++);
	username = result.getString(pos++);
	type = result.getString(pos++);
	description = result.getString(pos++);
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
	else throw new SQLException("Unknown payment_confirmed '" + typeString + "' for transid=" + transid);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	time=in.readLong();
	transid=in.readCompressedInt();
	accounting=in.readCompressedUTF().intern();
        source_accounting=in.readCompressedUTF().intern();
	username=in.readCompressedUTF().intern();
	type=in.readCompressedUTF().intern();
	description=in.readCompressedUTF();
	quantity=in.readCompressedInt();
	rate=in.readCompressedInt();
	payment_type=StringUtility.intern(in.readNullUTF());
	payment_info=in.readNullUTF();
	processor = StringUtility.intern(in.readNullUTF());
        creditCardTransaction = in.readCompressedInt();
	payment_confirmed=in.readByte();
    }

    public void setTable(AOServTable<Integer,Transaction> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    String toStringImpl() {
	return
            transid
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
	out.writeCompressedInt(transid);
	out.writeCompressedUTF(accounting, 0);
        out.writeCompressedUTF(source_accounting, 1);
	out.writeCompressedUTF(username, 2);
	out.writeCompressedUTF(type, 3);
	out.writeCompressedUTF(description, 4);
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
}