/*
 * Copyright 2001-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An <code>HttpdServer</code> represents one running instance of the
 * Apache web server.  Each physical server may run any number of
 * Apache web servers, and each of those may respond to multiple
 * IP addresses and ports, and serve content for many sites.
 *
 * @see  HttpdBind
 * @see  HttpdSite
 * @see  HttpdSiteBind
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdServer extends CachedObjectIntegerKey<HttpdServer> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=1,
		COLUMN_PACKAGE=10
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_NUMBER_name = "number";

	/**
	 * The highest recommended number of sites to bind in one server.
	 */
	public static final int RECOMMENDED_MAXIMUM_BINDS=128;

	int ao_server;
	private int number;
	private boolean can_add_sites;
	// TODO: Remove this field
	private boolean is_mod_jk;
	private int max_binds;
	int linux_server_account;
	int linux_server_group;
	private int mod_php_version;
	private boolean use_suexec;
	private int packageNum;
	private boolean is_shared;
	private boolean use_mod_perl;
	private int timeout;
	private int max_concurrency;

	public boolean canAddSites() {
		return can_add_sites;
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_AO_SERVER: return ao_server;
			case 2: return number;
			case 3: return can_add_sites;
			case 4: return is_mod_jk;
			case 5: return max_binds;
			case 6: return linux_server_account;
			case 7: return linux_server_group;
			case 8: return mod_php_version== -1 ? null : mod_php_version;
			case 9: return use_suexec;
			case COLUMN_PACKAGE: return packageNum;
			case 11: return is_shared;
			case 12: return use_mod_perl;
			case 13: return timeout;
			case 14: return max_concurrency;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public List<HttpdBind> getHttpdBinds() throws IOException, SQLException {
		return table.connector.getHttpdBinds().getHttpdBinds(this);
	}

	public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
		return table.connector.getHttpdSites().getHttpdSites(this);
	}

	public List<HttpdWorker> getHttpdWorkers() throws IOException, SQLException {
		return table.connector.getHttpdWorkers().getHttpdWorkers(this);
	}

	public int getMaxBinds() {
		return max_binds;
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount lsa=table.connector.getLinuxServerAccounts().get(linux_server_account);
		if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+linux_server_account);
		return lsa;
	}

	public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
		LinuxServerGroup lsg=table.connector.getLinuxServerGroups().get(linux_server_group);
		if(lsg==null) throw new SQLException("Unable to find LinuxServerGroup: "+linux_server_group);
		return lsg;
	}

	public TechnologyVersion getModPhpVersion() throws SQLException, IOException {
		if(mod_php_version==-1) return null;
		TechnologyVersion tv=table.connector.getTechnologyVersions().get(mod_php_version);
		if(tv==null) throw new SQLException("Unable to find TechnologyVersion: "+mod_php_version);
		if(
			tv.getOperatingSystemVersion(table.connector).getPkey()
			!= getAOServer().getServer().getOperatingSystemVersion().getPkey()
		) {
			throw new SQLException("mod_php/operating system version mismatch on HttpdServer: #"+pkey);
		}
		return tv;
	}

	public boolean useSuexec() {
		return use_suexec;
	}

	public Package getPackage() throws IOException, SQLException {
		// Package may be filtered
		return table.connector.getPackages().get(packageNum);
	}

	public boolean isShared() {
		return is_shared;
	}

	public boolean useModPERL() {
		return use_mod_perl;
	}

	/**
	 * Gets the timeout value in seconds.
	 */
	public int getTimeOut() {
		return timeout;
	}

	/**
	 * Gets the maximum concurrency of this server (number of children processes/threads).
	 */
	public int getMaxConcurrency() {
		return max_concurrency;
	}

	public int getNumber() {
		return number;
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer obj=table.connector.getAoServers().get(ao_server);
		if(obj==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SERVERS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos=1;
		pkey=result.getInt(pos++);
		ao_server=result.getInt(pos++);
		number=result.getInt(pos++);
		can_add_sites=result.getBoolean(pos++);
		is_mod_jk=result.getBoolean(pos++);
		max_binds=result.getInt(pos++);
		linux_server_account=result.getInt(pos++);
		linux_server_group=result.getInt(pos++);
		mod_php_version=result.getInt(pos++);
		if(result.wasNull()) mod_php_version=-1;
		use_suexec=result.getBoolean(pos++);
		packageNum=result.getInt(pos++);
		is_shared=result.getBoolean(pos++);
		use_mod_perl=result.getBoolean(pos++);
		timeout=result.getInt(pos++);
		max_concurrency=result.getInt(pos++);
	}

	/**
	 * @deprecated  All servers now use mod_jk, mod_jserv is no longer supported.
	 */
	@Deprecated
	public boolean isModJK() {
		return is_mod_jk;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		ao_server=in.readCompressedInt();
		number=in.readCompressedInt();
		can_add_sites=in.readBoolean();
		is_mod_jk=in.readBoolean();
		max_binds=in.readCompressedInt();
		linux_server_account=in.readCompressedInt();
		linux_server_group=in.readCompressedInt();
		mod_php_version=in.readCompressedInt();
		use_suexec=in.readBoolean();
		packageNum=in.readCompressedInt();
		is_shared=in.readBoolean();
		use_mod_perl=in.readBoolean();
		timeout=in.readCompressedInt();
		max_concurrency=in.readCompressedInt();
	}

	@Override
	String toStringImpl() {
		return "httpd"+number;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(number);
		out.writeBoolean(can_add_sites);
		out.writeBoolean(is_mod_jk);
		out.writeCompressedInt(max_binds);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_102)>=0) {
			out.writeCompressedInt(linux_server_account);
			out.writeCompressedInt(linux_server_group);
			out.writeCompressedInt(mod_php_version);
			out.writeBoolean(use_suexec);
			out.writeCompressedInt(packageNum);
			if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) out.writeCompressedInt(-1);
			out.writeBoolean(is_shared);
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_103)>=0) {
			out.writeBoolean(use_mod_perl);
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_130)>=0) {
			out.writeCompressedInt(timeout);
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_68)>=0) {
			out.writeCompressedInt(max_concurrency);
		}
	}
}