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

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.stream.StreamableInput;
import com.aoindustries.io.stream.StreamableOutput;
import com.aoindustries.net.Email;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.sql.UnmodifiableTimestamp;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>NoticeLog</code> entry is created when a client has been
 * notified of either a failed credit card transaction or a past due
 * debt.
 *
 * @see  NoticeType
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeLog extends CachedObjectIntegerKey<NoticeLog> {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_ACCOUNTING = 2
	;
	static final String COLUMN_PKEY_name = "pkey";
	static final String COLUMN_CREATE_TIME_name = "create_time";

	public static final int NO_TRANSACTION = -1;

	private UnmodifiableTimestamp create_time;
	private Account.Name accounting;
	private String billing_contact;
	private Email billing_email;
	private String notice_type;
	private int transid;

	/** Protocol compatibility */
	private int balance;

	public int getId() {
		return pkey;
	}

	public UnmodifiableTimestamp getCreateTime() {
		return create_time;
	}

	public Account.Name getAccount_name() {
		return accounting;
	}

	public Account getAccount() throws SQLException, IOException {
		Account obj = table.getConnector().getAccount().getAccount().get(accounting);
		if(obj == null) throw new SQLException("Unable to find Account: " + accounting);
		return obj;
	}

	public String getBillingContact() {
		return billing_contact;
	}

	public Email getBillingEmail() {
		return billing_email;
	}

	public String getNoticeType_type() {
		return notice_type;
	}

	public NoticeType getNoticeType() throws SQLException, IOException {
		NoticeType obj = table.getConnector().getBilling().getNoticeType().get(notice_type);
		if(obj == null) throw new SQLException("Unable to find NoticeType: " + notice_type);
		return obj;
	}

	public Integer getTransaction_id() {
		return transid == NO_TRANSACTION ? null : transid;
	}

	public Transaction getTransaction() throws IOException, SQLException {
		if(transid == NO_TRANSACTION) return null;
		Transaction obj = table.getConnector().getBilling().getTransaction().get(transid);
		if(obj == null) throw new SQLException("Unable to find Transaction: " + transid);
		return obj;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return create_time;
			case COLUMN_ACCOUNTING: return accounting;
			case 3: return billing_contact;
			case 4: return billing_email;
			case 5: return notice_type;
			case 6: return getTransaction_id();
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NOTICE_LOG;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt("id");
			create_time = UnmodifiableTimestamp.valueOf(result.getTimestamp("create_time"));
			accounting = Account.Name.valueOf(result.getString("accounting"));
			billing_contact = result.getString("billing_contact");
			billing_email = Email.valueOf(result.getString("billing_email"));
			notice_type = result.getString("notice_type");
			transid = result.getInt("transid");
			if(result.wasNull()) transid = NO_TRANSACTION;
			// Protocol compatibility
			balance = SQLUtility.parseDecimal2(result.getString("balance"));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			pkey = in.readCompressedInt();
			create_time = in.readUnmodifiableTimestamp();
			accounting = Account.Name.valueOf(in.readUTF()).intern();
			billing_contact = in.readUTF();
			billing_email = Email.valueOf(in.readUTF());
			notice_type = in.readUTF().intern();
			transid = in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String toStringImpl() {
		return pkey + "|" + accounting + '|' + notice_type;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(create_time.getTime());
		} else {
			out.writeTimestamp(create_time);
		}
		out.writeUTF(accounting.toString());
		out.writeUTF(billing_contact);
		out.writeUTF(billing_email.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeCompressedInt(balance);
		}
		out.writeUTF(notice_type);
		out.writeCompressedInt(transid);
	}

	public List<NoticeLogBalance> getBalances() throws IOException, SQLException {
		return table.getConnector().getBilling().getNoticeLogBalance().getNoticeLogBalances(this);
	}
}
