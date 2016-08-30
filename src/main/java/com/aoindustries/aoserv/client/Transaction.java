/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.IntList;
import com.aoindustries.util.InternUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Each <code>Business</code> has an account of all the
 * charges and payments processed.  Each entry in this
 * account is a <code>Transaction</code>.
 *
 * @see  Business
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
	private int transid;
	private AccountingCode accounting;
	private AccountingCode source_accounting;
	private String username;
	private String type;
	private String description;

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

	public void approved(final int creditCardTransaction) throws IOException, SQLException {
		table.connector.requestUpdate(
			true,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_APPROVED.ordinal());
					out.writeCompressedInt(transid);
					out.writeCompressedInt(creditCardTransaction);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
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

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_DECLINED.ordinal());
					out.writeCompressedInt(transid);
					out.writeCompressedInt(creditCardTransaction);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
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

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.TRANSACTION_HELD.ordinal());
					out.writeCompressedInt(transid);
					out.writeCompressedInt(creditCardTransaction);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
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

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case 0: return getTime();
			case COLUMN_TRANSID: return transid;
			case COLUMN_ACCOUNTING: return accounting;
			case 3: return source_accounting;
			case 4: return username;
			case 5: return type;
			case 6: return description;
			case 7: return quantity;
			case 8: return rate;
			case 9: return payment_type;
			case 10: return payment_info;
			case 11: return processor;
			case 12: return creditCardTransaction==-1 ? null : creditCardTransaction;
			case 13: return payment_confirmed==CONFIRMED?"Y":payment_confirmed==NOT_CONFIRMED?"N":"W";
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getDescription() {
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

	@Override
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
	@Override
	public AOServTable<Integer,Transaction> getTable() {
		return table;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TRANSACTIONS;
	}

	public Timestamp getTime() {
		return new Timestamp(time);
	}

	public int getTransID() {
		return transid;
	}

	public TransactionType getType() throws SQLException, IOException {
		TransactionType tt = table.connector.getTransactionTypes().get(type);
		if (tt == null) throw new SQLException("Unable to find TransactionType: " + type);
		return tt;
	}

	@Override
	int hashCodeImpl() {
		return transid;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			time = result.getTimestamp(pos++).getTime();
			transid = result.getInt(pos++);
			accounting = AccountingCode.valueOf(result.getString(pos++));
			source_accounting = AccountingCode.valueOf(result.getString(pos++));
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
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			time=in.readLong();
			transid=in.readCompressedInt();
			accounting=AccountingCode.valueOf(in.readCompressedUTF()).intern();
			source_accounting=AccountingCode.valueOf(in.readCompressedUTF()).intern();
			username=in.readCompressedUTF().intern();
			type=in.readCompressedUTF().intern();
			description=in.readCompressedUTF();
			quantity=in.readCompressedInt();
			rate=in.readCompressedInt();
			payment_type=InternUtils.intern(in.readNullUTF());
			payment_info=in.readNullUTF();
			processor = InternUtils.intern(in.readNullUTF());
			creditCardTransaction = in.readCompressedInt();
			payment_confirmed=in.readByte();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void setTable(AOServTable<Integer,Transaction> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
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

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeLong(time);
		out.writeCompressedInt(transid);
		out.writeCompressedUTF(accounting.toString(), 0);
		out.writeCompressedUTF(source_accounting.toString(), 1);
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