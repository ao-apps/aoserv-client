package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    static final String COLUMN_PKEY_name = "pkey";

    protected AOServTable<Integer,SpamEmailMessage> table;

    private int pkey;
    private int email_relay;
    private long time;
    private String message;

    @Override
    boolean equalsImpl(Object O) {
	return
            O instanceof SpamEmailMessage
            && ((SpamEmailMessage)O).pkey==pkey
	;
    }

    public int getPkey() {
        return pkey;
    }
    
    public EmailSmtpRelay getEmailSmtpRelay() throws SQLException, IOException {
	EmailSmtpRelay er=table.connector.emailSmtpRelays.get(email_relay);
	if(er==null) throw new SQLException("Unable to find EmailSmtpRelay: "+email_relay);
	return er;
    }
    
    public long getTime() {
        return time;
    }
    
    public String getMessage() {
        return message;
    }

    @Override
    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(email_relay);
            case 2: return new Date(time);
            case 3: return message;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    @Override
    public Integer getKey() {
	return pkey;
    }

    @Override
    final public AOServTable<Integer,SpamEmailMessage> getTable() {
	return table;
    }

    @Override
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SPAM_EMAIL_MESSAGES;
    }

    @Override
    int hashCodeImpl() {
	return pkey;
    }

    @Override
    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        email_relay=result.getInt(2);
	time=result.getTimestamp(3).getTime();
        message=result.getString(4);
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        email_relay=in.readCompressedInt();
	time=in.readLong();
        message=in.readUTF();
    }

    @Override
    public void setTable(AOServTable<Integer,SpamEmailMessage> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    @Override
    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(email_relay);
        out.writeLong(time);
        out.writeUTF(message);
    }
}