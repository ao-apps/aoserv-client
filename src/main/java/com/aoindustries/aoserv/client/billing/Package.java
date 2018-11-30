/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.account.Username;
import com.aoindustries.aoserv.client.backup.BackupReport;
import com.aoindustries.aoserv.client.backup.MysqlReplication;
import com.aoindustries.aoserv.client.dns.Zone;
import com.aoindustries.aoserv.client.email.Domain;
import com.aoindustries.aoserv.client.email.Pipe;
import com.aoindustries.aoserv.client.email.SendmailServer;
import com.aoindustries.aoserv.client.email.SmtpRelay;
import com.aoindustries.aoserv.client.email.SmtpRelayType;
import com.aoindustries.aoserv.client.linux.Group;
import com.aoindustries.aoserv.client.linux.GroupType;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.mysql.Database;
import com.aoindustries.aoserv.client.mysql.User;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.scm.CvsRepository;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.Site;
import com.aoindustries.aoserv.client.web.tomcat.SharedTomcat;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.InetAddress;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * A <code>Package</code> is a set of resources and its associated price.
 * A <code>Business</code> may multiple <code>Package</code>s, each with
 * their own monthly fee and sets of resources such as web sites, databases
 * and users.  Security is not maintained between <code>Package</code>s,
 * only between <code>Business</code>es.  If intra-account security is
 * required, please use child <code>Business</code>es.
 *
 * @see  Account
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
final public class Package extends CachedObjectIntegerKey<Package> implements Disablable, Comparable<Package> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_NAME=1,
		COLUMN_ACCOUNTING=2,
		COLUMN_PACKAGE_DEFINITION=3
	;
	public static final String COLUMN_NAME_name = "name";

	/**
	 * The default inbound email burst before rate limiting.
	 */
	public static final int DEFAULT_EMAIL_IN_BURST = 1000;

	/**
	 * The default sustained inbound email rate in emails/second.
	 */
	public static final float DEFAULT_EMAIL_IN_RATE = 10f;

	/**
	 * The default outbound email burst before rate limiting.
	 */
	public static final int DEFAULT_EMAIL_OUT_BURST = 200;

	/**
	 * The default sustained outbound email rate in emails/second.
	 */
	public static final float DEFAULT_EMAIL_OUT_RATE = .2f;

	/**
	 * The default relay email burst before rate limiting.
	 */
	public static final int DEFAULT_EMAIL_RELAY_BURST = 100;

	/**
	 * The default sustained relay email rate in emails/second.
	 */
	public static final float DEFAULT_EMAIL_RELAY_RATE = .1f;

	AccountingCode name;
	AccountingCode accounting;
	int package_definition;
	private long created;
	private UserId created_by;
	int disable_log;
	private int email_in_burst;
	private float email_in_rate;
	private int email_out_burst;
	private float email_out_rate;
	private int email_relay_burst;
	private float email_relay_rate;

	public void addDNSZone(String zone, InetAddress ip, int ttl) throws IOException, SQLException {
		table.getConnector().getDnsZones().addDNSZone(this, zone, ip, ttl);
	}

	public int addEmailSmtpRelay(Server aoServer, HostAddress host, SmtpRelayType type, long duration) throws IOException, SQLException {
		return table.getConnector().getEmailSmtpRelays().addEmailSmtpRelay(this, aoServer, host, type, duration);
	}

	public void addLinuxGroup(GroupId name, GroupType type) throws IOException, SQLException {
		addLinuxGroup(name, type.getName());
	}

	public void addLinuxGroup(GroupId name, String type) throws IOException, SQLException {
		table.getConnector().getLinuxGroups().addLinuxGroup(name, this, type);
	}

	public void addUsername(UserId username) throws IOException, SQLException {
		table.getConnector().getUsernames().addUsername(this, username);
	}

	@Override
	public boolean canDisable() throws IOException, SQLException {
		// Is already disabled
		if(disable_log!=-1) return false;

		// Can only disabled when all dependent objects are already disabled
		for(SharedTomcat hst : getHttpdSharedTomcats()) if(!hst.isDisabled()) return false;
		for(Pipe ep : getEmailPipes()) if(!ep.isDisabled()) return false;
		for(CvsRepository cr : getCvsRepositories()) if(!cr.isDisabled()) return false;
		for(Username un : getUsernames()) if(!un.isDisabled()) return false;
		for(Site hs : getHttpdSites()) if(!hs.isDisabled()) return false;
		for(com.aoindustries.aoserv.client.email.List el : getEmailLists()) if(!el.isDisabled()) return false;
		for(SmtpRelay ssr : getEmailSmtpRelays()) if(!ssr.isDisabled()) return false;

		return true;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return dl.canEnable() && !getBusiness().isDisabled();
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.PACKAGES, dl.getPkey(), name);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.PACKAGES, name);
	}

	public List<BackupReport> getBackupReports() throws IOException, SQLException {
		return table.getConnector().getBackupReports().getBackupReports(this);
	}

	public AccountingCode getBusiness_accounting() {
		return accounting;
	}

	public Account getBusiness() throws SQLException, IOException {
		Account accountingObject = table.getConnector().getBusinesses().get(accounting);
		if (accountingObject == null) throw new SQLException("Unable to find Business: " + accounting);
		return accountingObject;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_NAME: return name;
			case COLUMN_ACCOUNTING: return accounting;
			case COLUMN_PACKAGE_DEFINITION: return package_definition;
			case 4: return getCreated();
			case 5: return created_by;
			case 6: return disable_log==-1?null:disable_log;
			case 7: return email_in_burst==-1 ? null : email_in_burst;
			case 8: return Float.isNaN(email_in_rate) ? null : email_in_rate;
			case 9: return email_out_burst==-1 ? null : email_out_burst;
			case 10: return Float.isNaN(email_out_rate) ? null : email_out_rate;
			case 11: return email_relay_burst==-1 ? null : email_relay_burst;
			case 12: return Float.isNaN(email_relay_rate) ? null : email_relay_rate;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Timestamp getCreated() {
	return new Timestamp(created);
	}

	public Administrator getCreatedBy() throws SQLException, IOException {
		Administrator createdByObject = table.getConnector().getUsernames().get(created_by).getBusinessAdministrator();
		if (createdByObject == null) throw new SQLException("Unable to find BusinessAdministrator: " + created_by);
		return createdByObject;
	}

	public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
		return table.getConnector().getCvsRepositories().getCvsRepositories(this);
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.getConnector().getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	/**
	 * Gets the inbound burst limit for emails, the number of emails that may be sent before limiting occurs.
	 * A value of <code>-1</code> indicates unlimited.
	 */
	public int getEmailInBurst() {
		return email_in_burst;
	}

	/**
	 * Gets the inbound sustained email rate in emails/second.
	 * A value of <code>Float.NaN</code> indicates unlimited.
	 */
	public float getEmailInRate() {
		return email_in_rate;
	}

	/**
	 * Gets the outbound burst limit for emails, the number of emails that may be sent before limiting occurs.
	 * A value of <code>-1</code> indicates unlimited.
	 */
	public int getEmailOutBurst() {
		return email_out_burst;
	}

	/**
	 * Gets the outbound sustained email rate in emails/second.
	 * A value of <code>Float.NaN</code> indicates unlimited.
	 */
	public float getEmailOutRate() {
		return email_out_rate;
	}

	/**
	 * Gets the relay burst limit for emails, the number of emails that may be sent before limiting occurs.
	 * A value of <code>-1</code> indicates unlimited.
	 */
	public int getEmailRelayBurst() {
		return email_relay_burst;
	}

	/**
	 * Gets the relay sustained email rate in emails/second.
	 * A value of <code>Float.NaN</code> indicates unlimited.
	 */
	public float getEmailRelayRate() {
		return email_relay_rate;
	}

	public List<Zone> getDNSZones() throws IOException, SQLException {
		return table.getConnector().getDnsZones().getDNSZones(this);
	}

	public List<com.aoindustries.aoserv.client.email.List> getEmailLists() throws IOException, SQLException {
		return table.getConnector().getEmailLists().getEmailLists(this);
	}

	public List<Pipe> getEmailPipes() throws IOException, SQLException {
		return table.getConnector().getEmailPipes().getEmailPipes(this);
	}

	public List<SharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
		return table.getConnector().getHttpdSharedTomcats().getHttpdSharedTomcats(this);
	}

	public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
		return table.getConnector().getHttpdServers().getHttpdServers(this);
	}

	public List<Site> getHttpdSites() throws IOException, SQLException {
		return table.getConnector().getHttpdSites().getHttpdSites(this);
	}

	public List<IpAddress> getIPAddresses() throws IOException, SQLException {
		return table.getConnector().getIpAddresses().getIPAddresses(this);
	}

	public List<Group> getLinuxGroups() throws IOException, SQLException {
		return table.getConnector().getLinuxGroups().getLinuxGroups(this);
	}

	public List<Database> getMySQLDatabases() throws IOException, SQLException {
		return table.getConnector().getMysqlDatabases().getMySQLDatabases(this);
	}

	public List<MysqlReplication> getFailoverMySQLReplications() throws IOException, SQLException {
		return table.getConnector().getFailoverMySQLReplications().getFailoverMySQLReplications(this);
	}

	public List<User> getMySQLUsers() throws IOException, SQLException {
		return table.getConnector().getMysqlUsers().getMySQLUsers(this);
	}

	public AccountingCode getName() {
		return name;
	}

	public List<Bind> getNetBinds() throws IOException, SQLException {
		return table.getConnector().getNetBinds().getNetBinds(this);
	}

	public List<Bind> getNetBinds(IpAddress ip) throws IOException, SQLException {
		return table.getConnector().getNetBinds().getNetBinds(this, ip);
	}

	public PackageDefinition getPackageDefinition() throws SQLException, IOException {
		PackageDefinition pd = table.getConnector().getPackageDefinitions().get(package_definition);
		if(pd == null) throw new SQLException("Unable to find PackageDefinition: "+package_definition);
		return pd;
	}

	public List<com.aoindustries.aoserv.client.postgresql.Database> getPostgresDatabases() throws IOException, SQLException {
		return table.getConnector().getPostgresDatabases().getPostgresDatabases(this);
	}

	public List<com.aoindustries.aoserv.client.postgresql.User> getPostgresUsers() throws SQLException, IOException {
		return table.getConnector().getPostgresUsers().getPostgresUsers(this);
	}

	public List<SendmailServer> getSendmailServers() throws IOException, SQLException {
		return table.getConnector().getSendmailServers().getSendmailServers(this);
	}

	public Host getServer(String name) throws IOException, SQLException {
		return table.getConnector().getServers().getServer(this, name);
	}

	public List<Host> getServers() throws IOException, SQLException {
		return table.getConnector().getServers().getServers(this);
	}

	public List<Domain> getEmailDomains() throws IOException, SQLException {
		return table.getConnector().getEmailDomains().getEmailDomains(this);
	}

	public List<SmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
		return table.getConnector().getEmailSmtpRelays().getEmailSmtpRelays(this);
	}

	public List<Certificate> getSslCertificates() throws IOException, SQLException {
		return table.getConnector().getSslCertificates().getSslCertificates(this);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PACKAGES;
	}

	public List<Username> getUsernames() throws IOException, SQLException {
		return table.getConnector().getUsernames().getUsernames(this);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			name = AccountingCode.valueOf(result.getString(pos++));
			accounting = AccountingCode.valueOf(result.getString(pos++));
			package_definition = result.getInt(pos++);
			created = result.getTimestamp(pos++).getTime();
			created_by = UserId.valueOf(result.getString(pos++));
			disable_log=result.getInt(pos++);
			if(result.wasNull()) disable_log = -1;
			email_in_burst=result.getInt(pos++);
			if(result.wasNull()) email_in_burst = -1;
			email_in_rate=result.getFloat(pos++);
			if(result.wasNull()) email_in_rate = Float.NaN;
			email_out_burst=result.getInt(pos++);
			if(result.wasNull()) email_out_burst = -1;
			email_out_rate=result.getFloat(pos++);
			if(result.wasNull()) email_out_rate = Float.NaN;
			email_relay_burst=result.getInt(pos++);
			if(result.wasNull()) email_relay_burst = -1;
			email_relay_rate=result.getFloat(pos++);
			if(result.wasNull()) email_relay_rate = Float.NaN;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			name = AccountingCode.valueOf(in.readUTF()).intern();
			accounting=AccountingCode.valueOf(in.readUTF()).intern();
			package_definition=in.readCompressedInt();
			created=in.readLong();
			created_by = UserId.valueOf(in.readUTF()).intern();
			disable_log=in.readCompressedInt();
			email_in_burst=in.readCompressedInt();
			email_in_rate=in.readFloat();
			email_out_burst=in.readCompressedInt();
			email_out_rate=in.readFloat();
			email_relay_burst=in.readCompressedInt();
			email_relay_rate=in.readFloat();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name.toString());
		out.writeUTF(accounting.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122)<=0) {
			out.writeUTF("unknown");
			out.writeCompressedInt(0);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_123)>=0) {
			out.writeCompressedInt(package_definition);
		}
		out.writeLong(created);
		out.writeUTF(created_by.toString());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_122)<=0) {
			out.writeCompressedInt(-1);
			out.writeCompressedInt(200);
			out.writeCompressedInt(-1);
			out.writeCompressedInt(100);
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
			out.writeCompressedInt(256);
			out.writeLong(64*1024*1024);
			out.writeCompressedInt(256);
			out.writeLong(64*1024*1024);
		}
		out.writeCompressedInt(disable_log);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_24)>=0) {
			out.writeCompressedInt(email_in_burst);
			out.writeFloat(email_in_rate);
			out.writeCompressedInt(email_out_burst);
			out.writeFloat(email_out_rate);
			out.writeCompressedInt(email_relay_burst);
			out.writeFloat(email_relay_rate);
		}        
	}

	@Override
	public int compareTo(Package o) {
		return name.compareTo(o.name);
	}
}
