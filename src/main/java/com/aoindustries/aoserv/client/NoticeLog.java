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
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
		COLUMN_PKEY=0,
		COLUMN_ACCOUNTING=2
	;
	static final String COLUMN_PKEY_name = "pkey";
	static final String COLUMN_CREATE_TIME_name = "create_time";

	public static final int NO_TRANSACTION=-1;

	private long create_time;
	private AccountingCode accounting;
	private String billing_contact;
	private String billing_email;
	private int balance;
	private String notice_type;
	private int transid;

	public int getBalance() {
		return balance;
	}

	public String getBillingContact() {
		return billing_contact;
	}

	public String getBillingEmail() {
		return billing_email;
	}

	public Business getBusiness() throws SQLException, IOException {
		Business obj=table.connector.getBusinesses().get(accounting);
		if(obj==null) throw new SQLException("Unable to find Business: "+accounting);
		return obj;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return getCreateTime();
			case COLUMN_ACCOUNTING: return accounting;
			case 3: return billing_contact;
			case 4: return billing_email;
			case 5: return balance;
			case 6: return notice_type;
			case 7: return transid==NO_TRANSACTION?null:transid;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Timestamp getCreateTime() {
		return new Timestamp(create_time);
	}

	public NoticeType getNoticeType() throws SQLException, IOException {
		NoticeType obj=table.connector.getNoticeTypes().get(notice_type);
		if(obj==null) throw new SQLException("Unable to find NoticeType: "+notice_type);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NOTICE_LOG;
	}

	public Transaction getTransaction() throws IOException, SQLException {
		if(transid==-1) return null;
		Transaction obj=table.connector.getTransactions().get(transid);
		if(obj==null) throw new SQLException("Unable to find Transaction: "+transid);
		return obj;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			create_time=result.getTimestamp(2).getTime();
			accounting=AccountingCode.valueOf(result.getString(3));
			billing_contact=result.getString(4);
			billing_email=result.getString(5);
			balance=SQLUtility.getPennies(result.getString(6));
			notice_type=result.getString(7);
			transid=result.getInt(8);
			if(result.wasNull()) transid=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			create_time=in.readLong();
			accounting=AccountingCode.valueOf(in.readUTF()).intern();
			billing_contact=in.readUTF();
			billing_email=in.readUTF();
			balance=in.readCompressedInt();
			notice_type=in.readUTF().intern();
			transid=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	String toStringImpl() {
		return pkey+"|"+accounting+'|'+SQLUtility.getDecimal(balance)+'|'+notice_type;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeLong(create_time);
		out.writeUTF(accounting.toString());
		out.writeUTF(billing_contact);
		out.writeUTF(billing_email);
		out.writeCompressedInt(balance);
		out.writeUTF(notice_type);
		out.writeCompressedInt(transid);
	}
}
