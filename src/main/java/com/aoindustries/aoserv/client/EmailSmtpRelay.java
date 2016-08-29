/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.HostAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

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
		COLUMN_PACKAGE=1
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_HOST_name = "host";
	static final String COLUMN_PACKAGE_name = "package";

	/**
	 * Keep the SMTP relay history for three months (92 days).
	 */
	public static final int HISTORY_DAYS=92;

	String packageName;
	int ao_server;
	private HostAddress host;
	String type;
	private long created;
	private long last_refreshed;
	private int refresh_count;
	private long expiration;
	int disable_log;

	public int addSpamEmailMessage(String message) throws IOException, SQLException {
		return table.connector.getSpamEmailMessages().addSpamEmailMessage(this, message);
	}

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws IOException, SQLException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && getPackage().disable_log==-1;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.EMAIL_SMTP_RELAYS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.EMAIL_SMTP_RELAYS, pkey);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_PACKAGE: return packageName;
			case 2: return ao_server==-1?null:ao_server;
			case 3: return host;
			case 4: return type;
			case 5: return getCreated();
			case 6: return getLastRefreshed();
			case 7: return refresh_count;
			case 8: return getExpiration();
			case 9: return disable_log==-1?null:disable_log;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Timestamp getCreated() {
		return new Timestamp(created);
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws IOException, SQLException {
		if(disable_log==-1) return null;
		DisableLog obj=table.connector.getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public Timestamp getExpiration() {
		return expiration==-1 ? null : new Timestamp(expiration);
	}

	public HostAddress getHost() {
		return host;
	}

	public EmailSmtpRelayType getType() throws SQLException, IOException {
		EmailSmtpRelayType esrt=table.connector.getEmailSmtpRelayTypes().get(type);
		if(esrt==null) throw new SQLException("Unable to find EmailSmtpRelayType: "+type);
		return esrt;
	}

	public Timestamp getLastRefreshed() {
		return new Timestamp(last_refreshed);
	}

	public Package getPackage() throws IOException, SQLException {
		// May be filtered
		return table.connector.getPackages().get(packageName);
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

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_SMTP_RELAYS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			packageName=result.getString(2);
			ao_server=result.getInt(3);
			if(result.wasNull()) ao_server=-1;
			host=HostAddress.valueOf(result.getString(4));
			type=result.getString(5);
			created=result.getTimestamp(6).getTime();
			last_refreshed=result.getTimestamp(7).getTime();
			refresh_count=result.getInt(8);
			Timestamp T=result.getTimestamp(9);
			expiration=T==null?-1:T.getTime();
			disable_log=result.getInt(10);
			if(result.wasNull()) disable_log=-1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			packageName=in.readUTF().intern();
			ao_server=in.readCompressedInt();
			host=HostAddress.valueOf(in.readUTF());
			type=in.readUTF().intern();
			created=in.readLong();
			last_refreshed=in.readLong();
			refresh_count=in.readCompressedInt();
			expiration=in.readLong();
			disable_log=in.readCompressedInt();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public void refresh(long minDuration) throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REFRESH_EMAIL_SMTP_RELAY,
			pkey,
			minDuration
		);
	}

	@Override
	public List<CannotRemoveReason> getCannotRemoveReasons() {
		return Collections.emptyList();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(
			true,
			AOServProtocol.CommandID.REMOVE,
			SchemaTable.TableID.EMAIL_SMTP_RELAYS,
			pkey
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return packageName+" "+getType().getVerb()+" from "+host+" to "+getAOServer().getHostname();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(packageName);
		out.writeCompressedInt(ao_server);
		out.writeUTF(host.toString());
		out.writeUTF(type);
		out.writeLong(created);
		out.writeLong(last_refreshed);
		out.writeCompressedInt(refresh_count);
		out.writeLong(expiration);
		out.writeCompressedInt(disable_log);
	}
}
