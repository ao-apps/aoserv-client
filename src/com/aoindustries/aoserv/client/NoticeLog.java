package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>NoticeLog</code> entry is created when a client has been
 * notified of either a failed credit card transaction or a past due
 * debt.
 *
 * @see  NoticeType
 *
 * @version  1.0a
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
    String
        accounting,
        billing_contact,
        billing_email
    ;
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

    public Business getBusiness() {
	Business obj=table.connector.businesses.get(accounting);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find Business: "+accounting));
	return obj;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return new java.sql.Date(create_time);
            case COLUMN_ACCOUNTING: return accounting;
            case 3: return billing_contact;
            case 4: return billing_email;
            case 5: return Integer.valueOf(balance);
            case 6: return notice_type;
            case 7: return transid==NO_TRANSACTION?null:Integer.valueOf(transid);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getCreateTime() {
	return create_time;
    }

    public NoticeType getNoticeType() {
	NoticeType obj=table.connector.noticeTypes.get(notice_type);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find NoticeType: "+notice_type));
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NOTICE_LOG;
    }

    public Transaction getTransaction() {
	if(transid==-1) return null;
	Transaction obj=table.connector.transactions.get(transid);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find Transaction: "+transid));
	return obj;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	create_time=result.getTimestamp(2).getTime();
	accounting=result.getString(3);
	billing_contact=result.getString(4);
	billing_email=result.getString(5);
	balance=SQLUtility.getPennies(result.getString(6));
	notice_type=result.getString(7);
	transid=result.getInt(8);
	if(result.wasNull()) transid=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	create_time=in.readLong();
	accounting=in.readUTF().intern();
	billing_contact=in.readUTF();
	billing_email=in.readUTF();
	balance=in.readCompressedInt();
	notice_type=in.readUTF().intern();
	transid=in.readCompressedInt();
    }

    String toStringImpl() {
	return pkey+"|"+accounting+'|'+SQLUtility.getDecimal(balance)+'|'+notice_type;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeLong(create_time);
	out.writeUTF(accounting);
	out.writeUTF(billing_contact);
	out.writeUTF(billing_email);
	out.writeCompressedInt(balance);
	out.writeUTF(notice_type);
	out.writeCompressedInt(transid);
    }
}