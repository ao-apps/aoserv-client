/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An <code>HttpdSharedTomcat</code> stores configuration information
 * about the Jakarta Tomcat JVM under which run one or more
 * <code>HttpdTomcatSharedSite</code>s.
 *
 * @see  HttpdTomcatSharedSite
 * @see  HttpdTomcatSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSharedTomcat extends CachedObjectIntegerKey<HttpdSharedTomcat> implements Disablable, Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=2,
		COLUMN_LINUX_SERVER_ACCOUNT=4,
		COLUMN_TOMCAT4_WORKER=9,
		COLUMN_TOMCAT4_SHUTDOWN_PORT=10
	;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_AO_SERVER_name = "ao_server";

	/**
	 * The default setting of maxPostSize on the &lt;Connector /&gt; in server.xml.
	 * This raises the value from the Tomcat default of 2 MiB to a more real-world
	 * value, such as allowing uploads of pictures from modern digital cameras.
	 *
	 * @see  #getMaxPostSize()
	 */
	public static final int DEFAULT_MAX_POST_SIZE = 16 * 1024 * 1024; // 16 MiB

	/**
	 * The maximum number of sites allowed in one <code>HttpdSharedTomcat</code>.
	 */
	public static final int MAX_SITES = LinuxGroupAccount.MAX_GROUPS - 1;

	public static final int MAX_NAME_LENGTH = 32;

	public static final String OVERFLOW_TEMPLATE = "tomcat";

	public static final String DEFAULT_TOMCAT_VERSION_PREFIX = HttpdTomcatVersion.VERSION_8_0_PREFIX;

	private String name;
	int ao_server;
	private int version;
	int linux_server_account;
	int linux_server_group;
	private boolean isSecure;
	private boolean isOverflow;
	int disable_log;
	int tomcat4_worker;
	int tomcat4_shutdown_port;
	private String tomcat4_shutdown_key;
	private boolean isManual;
	private int maxPostSize;

	@Override
	public boolean canDisable() {
		return disable_log==-1;
	}

	@Override
	public boolean canEnable() throws SQLException, IOException {
		DisableLog dl=getDisableLog();
		if(dl==null) return false;
		else return
			dl.canEnable()
			&& getLinuxServerGroup().getLinuxGroup().getPackage().disable_log==-1
			&& getLinuxServerAccount().disable_log==-1
		;
	}

	@Override
	public List<CannotRemoveReason<HttpdTomcatSharedSite>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<HttpdTomcatSharedSite>> reasons=new ArrayList<>();

		for(HttpdTomcatSharedSite htss : getHttpdTomcatSharedSites()) {
			HttpdSite hs=htss.getHttpdTomcatSite().getHttpdSite();
			reasons.add(new CannotRemoveReason<>("Used by Multi-Site Tomcat website "+hs.getInstallDirectory()+" on "+hs.getAOServer().getHostname(), htss));
		}

		return reasons;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SHARED_TOMCATS, dl.pkey, pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SHARED_TOMCATS, pkey);
	}

	public UnixPath getInstallDirectory() throws SQLException, IOException {
		try {
			return UnixPath.valueOf(
				getAOServer().getServer().getOperatingSystemVersion().getHttpdSharedTomcatsDirectory().toString()
				+ '/' + name
			);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return name;
			case COLUMN_AO_SERVER: return ao_server;
			case 3: return version;
			case COLUMN_LINUX_SERVER_ACCOUNT: return linux_server_account;
			case 5: return linux_server_group;
			case 6: return isSecure;
			case 7: return isOverflow;
			case 8: return disable_log==-1?null:disable_log;
			case COLUMN_TOMCAT4_WORKER: return tomcat4_worker==-1?null:tomcat4_worker;
			case COLUMN_TOMCAT4_SHUTDOWN_PORT: return tomcat4_shutdown_port==-1?null:tomcat4_shutdown_port;
			case 11: return tomcat4_shutdown_key;
			case 12: return isManual;
			case 13: return maxPostSize;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	@Override
	public boolean isDisabled() {
		return disable_log!=-1;
	}

	@Override
	public DisableLog getDisableLog() throws SQLException, IOException {
		if(disable_log==-1) return null;
		DisableLog obj=table.connector.getDisableLogs().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<HttpdTomcatSharedSite> getHttpdTomcatSharedSites() throws IOException, SQLException {
		return table.connector.getHttpdTomcatSharedSites().getHttpdTomcatSharedSites(this);
	}

	public HttpdTomcatVersion getHttpdTomcatVersion() throws SQLException, IOException {
		HttpdTomcatVersion obj=table.connector.getHttpdTomcatVersions().get(version);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatVersion: "+version);
		if(
			obj.getTechnologyVersion(table.connector).getOperatingSystemVersion(table.connector).getPkey()
			!= getAOServer().getServer().getOperatingSystemVersion().getPkey()
		) {
			throw new SQLException("resource/operating system version mismatch on HttpdSharedTomcat: #"+pkey);
		}
		return obj;
	}

	public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
		LinuxServerAccount obj=table.connector.getLinuxServerAccounts().get(linux_server_account);
		if(obj==null) throw new SQLException("Unable to find LinuxServerAccount: "+linux_server_account);
		return obj;
	}

	public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
		LinuxServerGroup obj=table.connector.getLinuxServerGroups().get(linux_server_group);
		if(obj==null) throw new SQLException("Unable to find LinuxServerGroup: "+linux_server_group);
		return obj;
	}

	public String getName() {
		return name;
	}

	public AOServer getAOServer() throws SQLException, IOException {
		AOServer obj=table.connector.getAoServers().get(ao_server);
		if(obj==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return obj;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SHARED_TOMCATS;
	}

	public HttpdWorker getTomcat4Worker() throws SQLException, IOException {
		if(tomcat4_worker==-1) return null;
		HttpdWorker hw=table.connector.getHttpdWorkers().get(tomcat4_worker);
		if(hw==null) throw new SQLException("Unable to find HttpdWorker: "+tomcat4_worker);
		return hw;
	}

	public String getTomcat4ShutdownKey() {
		return tomcat4_shutdown_key;
	}

	public NetBind getTomcat4ShutdownPort() throws IOException, SQLException {
		if(tomcat4_shutdown_port==-1) return null;
		NetBind nb=table.connector.getNetBinds().get(tomcat4_shutdown_port);
		if(nb==null) throw new SQLException("Unable to find NetBind: "+tomcat4_shutdown_port);
		return nb;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey=result.getInt(pos++);
		name=result.getString(pos++);
		ao_server=result.getInt(pos++);
		version=result.getInt(pos++);
		linux_server_account=result.getInt(pos++);
		linux_server_group=result.getInt(pos++);
		isSecure = result.getBoolean(pos++);
		isOverflow = result.getBoolean(pos++);
		disable_log=result.getInt(pos++);
		if(result.wasNull()) disable_log=-1;
		tomcat4_worker=result.getInt(pos++);
		if(result.wasNull()) tomcat4_worker=-1;
		tomcat4_shutdown_port=result.getInt(pos++);
		if(result.wasNull()) tomcat4_shutdown_port=-1;
		tomcat4_shutdown_key=result.getString(pos++);
		isManual=result.getBoolean(pos++);
		maxPostSize = result.getInt(pos++);
	}

	public boolean isManual() {
		return isManual;
	}

	public int getMaxPostSize() {
		return maxPostSize;
	}

	public boolean isOverflow() {
		return isOverflow;
	}

	/**
	 * TODO: Remove this field.
	 *
	 * @deprecated  All shared Tomcats are now not secured internally.
	 */
	@Deprecated
	public boolean isSecure() {
		return isSecure;
	}

	/**
	 * Checks the format of the name of the shared Tomcat, as used in the <code>/wwwgroup</code>
	 * directory.  The name must be 12 characters or less, and comprised of
	 * only <code>a-z</code>,<code>0-9</code>, or <code>-</code>.  The first
	 * character must be <code>a-z</code>.
	 *
	 * // TODO: Self-validating type
	 */
	public static boolean isValidSharedTomcatName(String name) {
		int len = name.length();
		if (len == 0 || len > MAX_NAME_LENGTH)
			return false;
		// The first character must be [a-z]
		char ch = name.charAt(0);
		if (ch < 'a' || ch > 'z')
			return false;
		// The rest may have additional characters
		for (int c = 1; c < len; c++) {
			ch = name.charAt(c);
			if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '-')
				return false;
		}
		return true;
	}

	/**
	 * readImpl method comment.
	 */
	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		name=in.readUTF();
		ao_server=in.readCompressedInt();
		version=in.readCompressedInt();
		linux_server_account=in.readCompressedInt();
		linux_server_group=in.readCompressedInt();
		isSecure = in.readBoolean();
		isOverflow = in.readBoolean();
		disable_log=in.readCompressedInt();
		tomcat4_worker=in.readCompressedInt();
		tomcat4_shutdown_port=in.readCompressedInt();
		tomcat4_shutdown_key=in.readNullUTF();
		isManual=in.readBoolean();
		maxPostSize = in.readInt();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SHARED_TOMCATS, pkey);
	}

	public void setIsManual(boolean isManual) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, pkey, isManual);
	}

	public void setMaxPostSize(final int maxPostSize) throws IOException, SQLException {
		table.connector.requestUpdate(
			true,
			AOServProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE,
			new AOServConnector.UpdateRequest() {
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(pkey);
					out.writeInt(maxPostSize);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AOServProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.connector.tablesUpdated(invalidateList);
				}
			}
		);
	}

	@Override
	String toStringImpl() throws SQLException, IOException {
		return name+" on "+getAOServer().getHostname();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(version);
		out.writeCompressedInt(linux_server_account);
		out.writeCompressedInt(linux_server_group);
		out.writeBoolean(isSecure);
		out.writeBoolean(isOverflow);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeShort(0);
			out.writeShort(7);
			out.writeShort(0);
			out.writeShort(7);
			out.writeShort(0);
			out.writeShort(7);
		}
		out.writeCompressedInt(disable_log);
		out.writeCompressedInt(tomcat4_worker);
		out.writeCompressedInt(tomcat4_shutdown_port);
		out.writeNullUTF(tomcat4_shutdown_key);
		out.writeBoolean(isManual);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_80_1_SNAPSHOT) >= 0) {
			out.writeInt(maxPostSize);
		}
	}
}
