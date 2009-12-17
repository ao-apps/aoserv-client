package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * When a user successfully logs into either the POP3 or
 * IMAP mail systems, access from their host is
 * granted to the SMTP server via a <code>EmailSmtpRelay</code>.
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSmtpRelay extends CachedObjectIntegerKey<EmailSmtpRelay> implements Removable, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ACCOUNTING=1
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_HOST_name = "host";
    static final String COLUMN_ACCOUNTING_name = "accounting";

    public static final long NO_EXPIRATION=-1;

    /**
     * Keep the SMTP relay history for three months (92 days).
     */
    public static final int HISTORY_DAYS=92;

    String accounting;
    int ao_server;
    String host;
    String type;
    private long created;
    private long last_refreshed;
    private int refresh_count;
    private long expiration;
    int disable_log;

    public int addSpamEmailMessage(String message) throws IOException, SQLException {
        return table.connector.getSpamEmailMessages().addSpamEmailMessage(this, message);
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() throws IOException, SQLException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getBusiness().disable_log==-1;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.EMAIL_SMTP_RELAYS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.EMAIL_SMTP_RELAYS, pkey);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_ACCOUNTING: return accounting;
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

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public DisableLog getDisableLog() throws IOException, SQLException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public long getExpiration() {
	return expiration;
    }

    public String getHost() {
	return host;
    }
    
    public EmailSmtpRelayType getType() throws SQLException, IOException {
        EmailSmtpRelayType esrt=table.connector.getEmailSmtpRelayTypes().get(type);
        if(esrt==null) throw new SQLException("Unable to find EmailSmtpRelayType: "+type);
        return esrt;
    }

    public long getLastRefreshed() {
    	return last_refreshed;
    }

    /**
     * May be filtered.
     */
    public Business getBusiness() throws IOException, SQLException {
        // May be filtered
    	return table.connector.getBusinesses().get(accounting);
    }

    public int getRefreshCount() {
        return refresh_count;
    }

    public AOServer getAOServer() throws SQLException, IOException {
        if(ao_server==-1) return null;
	AOServer ao=table.connector.getAoServers().get(ao_server);
	if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
	return ao;
    }

    public List<SpamEmailMessage> getSpamEmailMessages() throws IOException, SQLException {
        return table.connector.getSpamEmailMessages().getSpamEmailMessages(this);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_SMTP_RELAYS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        accounting=result.getString(2);
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
        accounting=in.readUTF().intern();
        ao_server=in.readCompressedInt();
        host=in.readUTF();
        type=in.readUTF().intern();
        created=in.readLong();
        last_refreshed=in.readLong();
        refresh_count=in.readCompressedInt();
        expiration=in.readLong();
        disable_log=in.readCompressedInt();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness(),
            getAOServer(),
            getDisableLog()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public void refresh(long minDuration) throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REFRESH_EMAIL_SMTP_RELAY,
            pkey,
            minDuration
        );
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_SMTP_RELAYS,
            pkey
    	);
    }

    @Override
    protected String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return accounting+" "+getType().getVerb()+" from "+host+" to "+getAOServer().getHostname();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(accounting);
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