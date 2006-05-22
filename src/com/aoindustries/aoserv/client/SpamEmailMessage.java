package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Every <code>SpamEmailMessage</code> that causes an IP address
 * to be blocked via a <code>EmailSmtpRelay</code> is logged in this
 * table.
 *
 * @see  EmailSmtpRelay
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SpamEmailMessage extends AOServObject<Integer,SpamEmailMessage> implements SingleTableObject<Integer,SpamEmailMessage> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_EMAIL_RELAY=1
    ;

    protected AOServTable<Integer,SpamEmailMessage> table;

    private int pkey;
    private int email_relay;
    private long time;
    private String message;

    boolean equalsImpl(Object O) {
	return
            O instanceof SpamEmailMessage
            && ((SpamEmailMessage)O).pkey==pkey
	;
    }

    public int getPKey() {
        return pkey;
    }
    
    public EmailSmtpRelay getEmailSmtpRelay() {
	EmailSmtpRelay er=table.connector.emailSmtpRelays.get(email_relay);
	if(er==null) throw new WrappedException(new SQLException("Unable to find EmailSmtpRelay: "+email_relay));
	return er;
    }
    
    public long getTime() {
        return time;
    }
    
    public String getMessage() {
        return message;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(email_relay);
            case 2: return Long.valueOf(time);
            case 3: return message;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Integer getKey() {
	return pkey;
    }

    final public AOServTable<Integer,SpamEmailMessage> getTable() {
	return table;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SPAM_EMAIL_MESSAGES;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        email_relay=result.getInt(2);
	time=result.getTimestamp(3).getTime();
        message=result.getString(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        email_relay=in.readCompressedInt();
	time=in.readLong();
        message=in.readUTF();
    }

    public void setTable(AOServTable<Integer,SpamEmailMessage> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(email_relay);
        out.writeLong(time);
        out.writeUTF(message);
    }
}