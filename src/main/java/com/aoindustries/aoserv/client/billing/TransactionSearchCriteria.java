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
import com.aoindustries.aoserv.client.AOServStreamable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.payment.PaymentType;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * A <code>TransactionSearchCriteria</code> stores all the parameters
 * for a <code>Transaction</code> search.wraps all the different
 * ways in which the transaction table may be searched.
 *
 * @author  AO Industries, Inc.
 */
// TODO: 1.83.0: Add Currency to the filters, support in AccountHistory.ao
final public class TransactionSearchCriteria implements AOServStreamable {

	/**
	 * Value representing any in a search.
	 */
	public static final int ANY = -1;

	private long after;

	private long before;
	private int transid;
	private Account.Name account;
	private Account.Name sourceAccount;
	private User.Name administrator;
	private String type;
	private String description;
	private String paymentType;
	private String paymentInfo;
	private byte paymentConfirmed;

	/**
	 * The columns that can be sorted on.
	 *
	 * @deprecated  TODO: 1.83.0: Is sort used?
	 */
	// TODO: 1.83.0: This should probably be an enum
	@Deprecated
	public static final byte
		SORT_TIME = 0,
		SORT_TRANSID = 1,
		SORT_ACCOUNT = 2,
		SORT_SOURCE_ACCOUNT = 3,
		SORT_ADMINISTRATOR = 4,
		SORT_TYPE = 5,
		SORT_DESCRIPTION = 6,
		SORT_PAYMENT_TYPE = 7,
		SORT_PAYMENT_INFO = 8,
		SORT_PAYMENT_CONFIRMED = 9
	;

	/**
	 * The labels for each sort column.
	 *
	 * @deprecated  Is sort used?
	 */
	// This should be part of the enum
	@Deprecated
	public static final String[] sortLabels = {
		"Date/Time",
		"Transaction #",
		"Account",
		"Source Account",
		"Administrator",
		"Transaction Type",
		"Description",
		"Payment Method",
		"Payment Info",
		"Payment Confirmation"
	};

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	private byte sortFirst = 0;

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	private byte sortSecond = 0;

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	private boolean sortDescending = true;

	public TransactionSearchCriteria() {
	}

	public TransactionSearchCriteria(
		long after,
		long before,
		int transid,
		Account account,
		Account sourceAccount,
		Administrator administrator,
		TransactionType type,
		String description,
		PaymentType paymentType,
		String paymentInfo,
		byte paymentConfirmed
	) {
		this.after = after;
		this.before = before;
		this.transid = transid;
		this.account = account == null ? null : account.getName();
		this.sourceAccount = sourceAccount == null ? null : sourceAccount.getName();
		this.administrator = administrator == null ? null : administrator.getUsername_userId();
		this.type = type == null ? null : type.getName();
		this.description = description;
		this.paymentType = paymentType == null ? null : paymentType.getName();
		this.paymentInfo = paymentInfo;
		this.paymentConfirmed = paymentConfirmed;
	}

	public TransactionSearchCriteria(Administrator administrator) throws IOException, SQLException {
		// The current time
		// TODO: 1.83.0: Should this always be UTC?  Always GregorianCalendar?
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());

		// The current year
		int year = cal.get(Calendar.YEAR);

		before = ANY;

		// The beginning of last month starts the default search
		int month = cal.get(Calendar.MONTH);
		if (month == Calendar.JANUARY) {
			year--;
			month = Calendar.DECEMBER;
		} else {
			month--;
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		after = cal.getTime().getTime();

		transid = ANY;
		account = administrator == null ? null : administrator.getUsername().getPackage().getAccount_name();
		sourceAccount = null;
		this.administrator = null;
		type = null;
		description = null;
		paymentType = null;
		paymentInfo = null;
		paymentConfirmed = ANY;
	}

	public long getAfter() {
		return after;
	}

	public long getBefore() {
		return before;
	}

	public Account.Name getAccount() {
		return account;
	}

	public Account.Name getSourceAccount() {
		return sourceAccount;
	}

	public User.Name getAdministrator() {
		return administrator;
	}

	public String getDescription() {
		return description;
	}

	public byte getPaymentConfirmed() {
		return paymentConfirmed;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public String getType() {
		return type;
	}

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	public byte getSortFirst() {
		return sortFirst;
	}

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	public byte getSortSecond() {
		return sortSecond;
	}

	/**
	 * @deprecated  Please use {@link TransactionTable#get(com.aoindustries.aoserv.client.billing.TransactionSearchCriteria)} directly.
	 */
	@Deprecated
	public List<Transaction> getTransactions(AOServConnector connector) throws IOException, SQLException {
		return connector.getBilling().getTransaction().get(this);
	}

	public int getTransid() {
		return transid;
	}

	// This will not be required once all clients are >= protocol 1.83.0
	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			after = in.readLong();
			before = in.readLong();
			transid = in.readCompressedInt();
			account = InternUtils.intern(Account.Name.valueOf(in.readNullUTF()));
			sourceAccount = InternUtils.intern(Account.Name.valueOf(in.readNullUTF()));
			administrator = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			type = InternUtils.intern(in.readNullUTF());
			description = in.readNullUTF();
			paymentType = InternUtils.intern(in.readNullUTF());
			paymentInfo = in.readNullUTF();
			paymentConfirmed = in.readByte();
			sortFirst = in.readByte();
			sortSecond = in.readByte();
			sortDescending = in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public void setAfter(long after) {
		this.after = after;
	}

	public void setBefore(long before) {
		this.before = before;
	}

	public void setAccount(Account.Name account) {
		this.account = account;
	}

	public void setSourceAccount(Account.Name sourceAccount) {
		this.sourceAccount = sourceAccount;
	}

	public void setAdministrator(User.Name administrator) {
		this.administrator = administrator;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPaymentConfirmed(byte paymentConfirmed) {
		this.paymentConfirmed = paymentConfirmed;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	public void setSortDescending(boolean sortDescending) {
		this.sortDescending = sortDescending;
	}

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	public void setSortFirst(byte column) {
		this.sortFirst = column;
	}

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	public void setSortSecond(byte column) {
		this.sortSecond = column;
	}

	public void setTransid(int transid) {
		this.transid = transid;
	}

	/**
	 * @deprecated  Is sort used?
	 */
	@Deprecated
	public boolean sortDescending() {
		return sortDescending;
	}

	/**
	 * @deprecated  This is maintained only for compatibility with the {@link Streamable} interface.
	 * 
	 * @see  #write(CompressedDataOutputStream,AOServProtocol.Version)
	 */
	@Deprecated
	@Override
	public void write(CompressedDataOutputStream out, String version) throws IOException {
		write(out, AoservProtocol.Version.getVersion(version));
	}

	// This will not be required once all clients are >= protocol 1.83.0
	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(after);
			out.writeLong(before);
			out.writeCompressedInt(transid);
			out.writeNullUTF(Objects.toString(account, null));
			out.writeNullUTF(Objects.toString(sourceAccount, null));
			out.writeNullUTF(Objects.toString(administrator, null));
			out.writeNullUTF(type);
			out.writeNullUTF(description);
			out.writeNullUTF(paymentType);
			out.writeNullUTF(paymentInfo);
			out.writeByte(paymentConfirmed);
			out.writeByte(sortFirst);
			out.writeByte(sortSecond);
			out.writeBoolean(sortDescending);
		} else {
			throw new IOException("write only supported for protocol < " + AoservProtocol.Version.VERSION_1_83_0);
		}
	}
}
