/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.billing;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.payment.Payment;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.payment.Processor;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.math.SafeMath;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.IntList;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.i18n.Money;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Each {@link Account} has an account of all the
 * charges and payments processed.  Each entry in this
 * account is a <code>Transaction</code>.
 *
 * @see  Account
 *
 * @author  AO Industries, Inc.
 */
final public class Transaction extends CachedObjectIntegerKey<Transaction> {

	static final int
		COLUMN_TRANSID = 1,
		COLUMN_ACCOUNTING = 2,
		COLUMN_ADMINISTRATOR = 4
	;
	static final String COLUMN_TIME_name = "time";
	static final String COLUMN_TRANSID_name = "transid";
	static final String COLUMN_SOURCE_ACCOUNTING_name = "source_accounting";

	/**
	 * Represents not being assigned for a field of the <code>int</code> type.
	 */
	public static final int UNASSIGNED = -1;

	private long time;
	private Account.Name accounting;
	private Account.Name source_accounting;
	private User.Name username;
	private String type;
	private String description;

	/**
	 * The quantity in 1000th's of a unit
	 */
	private int quantity;

	private Money rate;

	private String payment_type, payment_info, processor;
	private int creditCardTransaction;

	/**
	 * Payment confirmation.
	 */
	public static final byte WAITING_CONFIRMATION = 0, CONFIRMED = 1, NOT_CONFIRMED = 2;

	/**
	 * The text to display for different confirmation statuses.
	 */
	private static final String[] paymentConfirmedLabels = { "Waiting", "Confirmed", "Failed" };

	public static final int NUM_PAYMENT_CONFIRMATION_STATES = 3;
	static {
		assert paymentConfirmedLabels.length == NUM_PAYMENT_CONFIRMATION_STATES;
	}

	private byte payment_confirmed;

	/**
	 * @param  paymentInfo  (Optional) The card info may have been updated during the transaction.
	 */
	public void approved(final int creditCardTransaction, final String paymentInfo) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.TRANSACTION_APPROVED,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(creditCardTransaction);
					out.writeNullUTF(paymentInfo);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	/**
	 * @deprecated  Please provide updated cardInfo via {@link #approved(int, java.lang.String)}.
	 *
	 * @see  #approved(int, java.lang.String)
	 */
	@Deprecated
	public void approved(int creditCardTransaction) throws IOException, SQLException {
		approved(creditCardTransaction, null);
	}

	/**
	 * @param  paymentInfo  (Optional) The card info may have been updated during the transaction.
	 */
	public void declined(final int creditCardTransaction, final String paymentInfo) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.TRANSACTION_DECLINED,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(creditCardTransaction);
					out.writeNullUTF(paymentInfo);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	/**
	 * @deprecated  Please provide updated cardInfo via {@link #declined(int, java.lang.String)}.
	 *
	 * @see  #declined(int, java.lang.String)
	 */
	@Deprecated
	public void declined(int creditCardTransaction) throws IOException, SQLException {
		declined(creditCardTransaction, null);
	}

	/**
	 * @param  paymentInfo  (Optional) The card info may have been updated during the transaction.
	 */
	public void held(final int creditCardTransaction, final String paymentInfo) throws IOException, SQLException {
		table.getConnector().requestUpdate(
			true,
			AoservProtocol.CommandID.TRANSACTION_HELD,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeCompressedInt(creditCardTransaction);
					out.writeNullUTF(paymentInfo);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	/**
	 * @deprecated  Please provide updated cardInfo via {@link #held(int, java.lang.String)}.
	 *
	 * @see  #held(int, java.lang.String)
	 */
	@Deprecated
	public void held(int creditCardTransaction) throws IOException, SQLException {
		held(creditCardTransaction, null);
	}

	/**
	 * @deprecated  Please directly access via {@link #getPayment()}.
	 *              Beware that {@link #getPayment()} might return {@code null}.
	 *
	 * @see  #getPayment()
	 * @see  Payment#getAuthorizationApprovalCode()
	 */
	@Deprecated
	public String getAprNum() throws SQLException, IOException {
		Payment cct = getPayment();
		return cct==null ? null : cct.getAuthorizationApprovalCode();
	}

	public Account.Name getAccount_name() {
		return accounting;
	}

	public Account getAccount() throws SQLException, IOException {
		Account business = table.getConnector().getAccount().getAccount().get(accounting);
		if (business == null) throw new SQLException("Unable to find Account: " + accounting);
		return business;
	}

	public Account.Name getSourceAccount_name() {
		return source_accounting;
	}

	public Account getSourceAccount() throws SQLException, IOException {
		Account business = table.getConnector().getAccount().getAccount().get(source_accounting);
		if (business == null) throw new SQLException("Unable to find Account: " + source_accounting);
		return business;
	}

	public User.Name getAdministrator_username() {
		return username;
	}

	public Administrator getAdministrator() throws SQLException, IOException {
		User un = table.getConnector().getAccount().getUser().get(username);
		// May be filtered
		if(un == null) return null;
		Administrator administrator = un.getAdministrator();
		if (administrator == null) throw new SQLException("Unable to find Administrator: " + username);
		return administrator;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case 0: return getTime();
			case COLUMN_TRANSID: return pkey;
			case COLUMN_ACCOUNTING: return accounting;
			case 3: return source_accounting;
			case COLUMN_ADMINISTRATOR: return username;
			case 5: return type;
			case 6: return description;
			case 7: return quantity;
			case 8: return rate;
			case 9: return payment_type;
			case 10: return payment_info;
			case 11: return processor;
			case 12: return getPayment_id();
			case 13: return payment_confirmed == CONFIRMED ? "Y" : payment_confirmed == NOT_CONFIRMED ? "N" : "W";
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public String getDescription() {
		return description;
	}

	public String getProcessor_providerId() {
		return processor;
	}

	public Processor getProcessor() throws SQLException, IOException {
		if(processor == null) return null;
		Processor creditCardProcessor = table.getConnector().getPayment().getProcessor().get(processor);
		if(creditCardProcessor == null) throw new SQLException("Unable to find CreditCardProcessor: " + processor);
		return creditCardProcessor;
	}

	public Integer getPayment_id() {
		return creditCardTransaction == -1 ? null : creditCardTransaction;
	}

	public Payment getPayment() throws SQLException, IOException {
		if (creditCardTransaction == -1) return null;
		Payment cct = table.getConnector().getPayment().getPayment().get(creditCardTransaction);
		if (cct == null) throw new SQLException("Unable to find CreditCardTransaction: " + creditCardTransaction);
		return cct;
	}

	public byte getPaymentConfirmed() {
		return payment_confirmed;
	}

	public static String getPaymentConfirmedLabel(int index) {
		return paymentConfirmedLabels[index];
	}

	public String getPaymentInfo() {
		return payment_info;
	}

	public String getPaymentType_name() {
		return payment_type;
	}

	public PaymentType getPaymentType() throws SQLException, IOException {
		if(payment_type == null) return null;
		PaymentType paymentType = table.getConnector().getPayment().getPaymentType().get(payment_type);
		if(paymentType == null) throw new SQLException("Unable to find PaymentType: " + payment_type);
		return paymentType;
	}

	/**
	 * Gets the effective amount of quantity * rate.
	 */
	public Money getAmount() {
		return rate.multiply(BigDecimal.valueOf(quantity, 3), RoundingMode.HALF_UP);
	}

	public int getQuantity() {
		return quantity;
	}

	public Money getRate() {
		return rate;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TRANSACTIONS;
	}

	public long getTime_millis() {
		return time;
	}

	public Timestamp getTime() {
		return new Timestamp(time);
	}

	public int getTransid() {
		return pkey;
	}

	public String getType_name() {
		return type;
	}

	public TransactionType getType() throws SQLException, IOException {
		TransactionType tt = table.getConnector().getBilling().getTransactionType().get(type);
		if (tt == null) throw new SQLException("Unable to find TransactionType: " + type);
		return tt;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			time = result.getTimestamp("time").getTime();
			pkey = result.getInt("transid");
			accounting = Account.Name.valueOf(result.getString("accounting"));
			source_accounting = Account.Name.valueOf(result.getString("source_accounting"));
			username = User.Name.valueOf(result.getString("username"));
			type = result.getString("type");
			description = result.getString("description");
			quantity = SQLUtility.parseDecimal3(result.getString("quantity"));
			rate = MoneyUtil.getMoney(result, "rate.currency", "rate.value");
			payment_type = result.getString("payment_type");
			payment_info = result.getString("payment_info");
			processor = result.getString("processor");
			creditCardTransaction = result.getInt("credit_card_transaction");
			if(result.wasNull()) creditCardTransaction = -1;
			String typeString = result.getString("payment_confirmed");
			if("Y".equals(typeString)) payment_confirmed=CONFIRMED;
			else if("N".equals(typeString)) payment_confirmed=NOT_CONFIRMED;
			else if("W".equals(typeString)) payment_confirmed=WAITING_CONFIRMATION;
			else throw new SQLException("Unknown payment_confirmed '" + typeString + "' for transid=" + pkey);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			time = in.readLong();
			pkey = in.readCompressedInt();
			accounting = Account.Name.valueOf(in.readCompressedUTF()).intern();
			source_accounting = Account.Name.valueOf(in.readCompressedUTF()).intern();
			username = User.Name.valueOf(in.readCompressedUTF()).intern();
			type = in.readCompressedUTF().intern();
			description = in.readCompressedUTF();
			quantity = in.readCompressedInt();
			rate = MoneyUtil.readMoney(in);
			payment_type = InternUtils.intern(in.readNullUTF());
			payment_info = in.readNullUTF();
			processor = InternUtils.intern(in.readNullUTF());
			creditCardTransaction = in.readCompressedInt();
			payment_confirmed = in.readByte();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return
			pkey
			+ "|"
			+ accounting
			+ '|'
			+ source_accounting
			+ '|'
			+ type
			+ '|'
			+ SQLUtility.formatDecimal3(quantity)
			+ '×'
			+ rate
			+ '|'
			+ (
				payment_confirmed == CONFIRMED ? 'Y'
				: payment_confirmed == NOT_CONFIRMED ? 'N'
				: 'W'
			)
		;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeLong(time);
		out.writeCompressedInt(pkey);
		out.writeCompressedUTF(accounting.toString(), 0);
		out.writeCompressedUTF(source_accounting.toString(), 1);
		out.writeCompressedUTF(username.toString(), 2);
		out.writeCompressedUTF(type, 3);
		out.writeCompressedUTF(description, 4);
		out.writeCompressedInt(quantity);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			if(rate != null && rate.getCurrency() == Currency.USD && rate.getScale() == 2) {
				out.writeCompressedInt(SafeMath.castInt(rate.getUnscaledValue()));
			} else {
				out.writeCompressedInt(-1);
			}
		} else {
			MoneyUtil.writeMoney(rate, out);
		}
		out.writeNullUTF(payment_type);
		out.writeNullUTF(payment_info);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_29)<0) {
			out.writeNullUTF(null);
		} else {
			out.writeNullUTF(processor);
			out.writeCompressedInt(creditCardTransaction);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_128)<0) {
			out.writeCompressedInt(-1);
		} else if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_29)<0) {
			out.writeNullUTF(null);
		}
		out.writeByte(payment_confirmed);
	}
}
