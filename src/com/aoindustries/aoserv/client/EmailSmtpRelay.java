package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * When a user successfully logs into either the POP3 or
 * IMAP mail systems, access from their host is
 * granted to the SMTP server via a <code>EmailSmtpRelay</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSmtpRelay extends CachedObjectIntegerKey<EmailSmtpRelay> implements Removable, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_PACKAGE=1
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_HOST_name = "host";
    static final String COLUMN_PACKAGE_name = "package";

    public static final long NO_EXPIRATION=-1;

    /**
     * Keep the SMTP relay history for three months (92 days).
     */
    public static final int HISTORY_DAYS=92;

    String packageName;
    int ao_server;
    String host;
    String type;
    private long created;
    private long last_refreshed;
    private int refresh_count;
    private long expiration;
    int disable_log;

    public int addSpamEmailMessage(String message) {
        return table.connector.spamEmailMessages.addSpamEmailMessage(this, message);
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getPackage().disable_log==-1;
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.EMAIL_SMTP_RELAYS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.EMAIL_SMTP_RELAYS, pkey);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_PACKAGE: return packageName;
            case 2: return ao_server==-1?null:Integer.valueOf(ao_server);
            case 3: return host;
            case 4: return type;
            case 5: return new java.sql.Date(created);
            case 6: return new java.sql.Date(last_refreshed);
            case 7: return Integer.valueOf(refresh_count);
            case 8: return expiration==NO_EXPIRATION?null:new java.sql.Date(expiration);
            case 9: return disable_log==-1?null:Integer.valueOf(disable_log);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getCreated() {
	return created;
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public long getExpiration() {
	return expiration;
    }

    public String getHost() {
	return host;
    }
    
    public EmailSmtpRelayType getType() {
        EmailSmtpRelayType esrt=table.connector.emailSmtpRelayTypes.get(type);
        if(esrt==null) throw new WrappedException(new SQLException("Unable to find EmailSmtpRelayType: "+type));
        return esrt;
    }

    public long getLastRefreshed() {
	return last_refreshed;
    }

    public Package getPackage() {
        // May be filtered
	return table.connector.packages.get(packageName);
    }

    public int getRefreshCount() {
	return refresh_count;
    }

    public AOServer getAOServer() {
        if(ao_server==-1) return null;
	AOServer ao=table.connector.aoServers.get(ao_server);
	if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
	return ao;
    }

    public List<SpamEmailMessage> getSpamEmailMessages() {
        return table.connector.spamEmailMessages.getSpamEmailMessages(this);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_SMTP_RELAYS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	packageName=result.getString(2);
	ao_server=result.getInt(3);
        if(result.wasNull()) ao_server=-1;
	host=result.getString(4);
        type=result.getString(5);
	created=result.getTimestamp(6).getTime();
	last_refreshed=result.getTimestamp(7).getTime();
	refresh_count=result.getInt(8);
	Timestamp T=result.getTimestamp(9);
	expiration=T==null?NO_EXPIRATION:T.getTime();
        disable_log=result.getInt(10);
        if(result.wasNull()) disable_log=-1;
    }

    public static boolean isValidHost(String host) {
        return
            IPAddress.isValidIPAddress(host)
            || EmailDomain.isValidFormat(host)
        ;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	packageName=in.readUTF().intern();
	ao_server=in.readCompressedInt();
	host=in.readUTF();
        type=in.readUTF().intern();
	created=in.readLong();
	last_refreshed=in.readLong();
	refresh_count=in.readCompressedInt();
	expiration=in.readLong();
        disable_log=in.readCompressedInt();
    }

    public void refresh(long minDuration) {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REFRESH_EMAIL_SMTP_RELAY,
            pkey,
            minDuration
	);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_SMTP_RELAYS,
            pkey
	);
    }

    protected String toStringImpl() {
        return packageName+" "+getType().getVerb()+" from "+host+" to "+getAOServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(packageName);
	out.writeCompressedInt(ao_server);
	out.writeUTF(host);
        out.writeUTF(type);
	out.writeLong(created);
	out.writeLong(last_refreshed);
	out.writeCompressedInt(refresh_count);
	out.writeLong(expiration);
        out.writeCompressedInt(disable_log);
    }
}