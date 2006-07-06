package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;

/**
 * The <code>EmailSmtpRelayType</code> of an <code>EmailSmtpRelay</code>
 * controls the servers response.
 *
 * @see  EmailSmtpRelay
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailSmtpRelayType extends GlobalObjectStringKey<EmailSmtpRelayType> {

    static final int COLUMN_NAME=0;

    /**
     * The different relay types.
     */
    public static final String
        ALLOW="allow",
        ALLOW_RELAY="allow_relay",
        DENY_SPAM="deny_spam",
        DENY="deny"
    ;

    private String sendmail_config;
    private String qmail_config;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return sendmail_config;
            case 2: return qmail_config;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getName() {
        return pkey;
    }
    
    public String getSendmailConfig() {
        return sendmail_config;
    }
    
    public String getQmailConfig() {
        return qmail_config;
    }

    protected int getTableIDImpl() {
	return SchemaTable.EMAIL_SMTP_RELAY_TYPES;
    }

    public String getVerb() {
        if(pkey.equals(ALLOW)) return "allowed regular access";
        if(pkey.equals(ALLOW_RELAY)) return "allowed unauthenticated relay access";
        if(pkey.equals(DENY_SPAM)) return "blocked for sending unsolicited bulk email";
        if(pkey.equals(DENY)) return "blocked";
        throw new WrappedException(new SQLException("Unknown value for name: "+pkey));
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getString(1);
	sendmail_config=result.getString(2);
        qmail_config=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
        sendmail_config=in.readUTF();
        qmail_config=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(sendmail_config);
        out.writeUTF(qmail_config);
    }
}