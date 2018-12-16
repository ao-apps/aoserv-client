/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.web.tomcat;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Disablable;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.account.DisableLog;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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
 * @see  SharedTomcatSite
 * @see  Site
 *
 * @author  AO Industries, Inc.
 */
final public class SharedTomcat extends CachedObjectIntegerKey<SharedTomcat> implements Disablable, Removable {

	static final int
		COLUMN_PKEY = 0,
		COLUMN_AO_SERVER = 2,
		COLUMN_LINUX_SERVER_ACCOUNT = 4,
		COLUMN_TOMCAT4_WORKER = 7,
		COLUMN_TOMCAT4_SHUTDOWN_PORT = 8
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

	public static final int MAX_NAME_LENGTH = 32;

	public static final String DEFAULT_TOMCAT_VERSION_PREFIX = Version.VERSION_9_0_PREFIX;

	private String name;
	int ao_server;
	private int version;
	int linux_server_account;
	int linux_server_group;
	int disable_log;
	int tomcat4_worker;
	int tomcat4_shutdown_port;
	private String tomcat4_shutdown_key;
	private boolean isManual;
	private int maxPostSize;
	private boolean unpackWARs;
	private boolean autoDeploy;

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
			&& !getLinuxServerGroup().getLinuxGroup().getPackage().isDisabled()
			&& !getLinuxServerAccount().isDisabled()
		;
	}

	@Override
	public List<CannotRemoveReason<SharedTomcatSite>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<SharedTomcatSite>> reasons=new ArrayList<>();

		for(SharedTomcatSite htss : getHttpdTomcatSharedSites()) {
			com.aoindustries.aoserv.client.web.Site hs=htss.getHttpdTomcatSite().getHttpdSite();
			reasons.add(new CannotRemoveReason<>("Used by Multi-Site Tomcat website "+hs.getInstallDirectory()+" on "+hs.getAoServer().getHostname(), htss));
		}

		return reasons;
	}

	@Override
	public void disable(DisableLog dl) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.DISABLE, Table.TableID.HTTPD_SHARED_TOMCATS, dl.getPkey(), pkey);
	}

	@Override
	public void enable() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.ENABLE, Table.TableID.HTTPD_SHARED_TOMCATS, pkey);
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
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return name;
			case COLUMN_AO_SERVER: return ao_server;
			case 3: return version;
			case COLUMN_LINUX_SERVER_ACCOUNT: return linux_server_account;
			case 5: return linux_server_group;
			case 6: return disable_log==-1?null:disable_log;
			case COLUMN_TOMCAT4_WORKER: return tomcat4_worker==-1?null:tomcat4_worker;
			case COLUMN_TOMCAT4_SHUTDOWN_PORT: return tomcat4_shutdown_port==-1?null:tomcat4_shutdown_port;
			case 9: return tomcat4_shutdown_key;
			case 10: return isManual;
			case 11: return maxPostSize==-1 ? null : maxPostSize;
			case 12: return unpackWARs;
			case 13: return autoDeploy;
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
		DisableLog obj=table.getConnector().getAccount().getDisableLog().get(disable_log);
		if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
		return obj;
	}

	public List<SharedTomcatSite> getHttpdTomcatSharedSites() throws IOException, SQLException {
		return table.getConnector().getWeb_tomcat().getSharedTomcatSite().getHttpdTomcatSharedSites(this);
	}

	public Version getHttpdTomcatVersion() throws SQLException, IOException {
		Version obj=table.getConnector().getWeb_tomcat().getVersion().get(version);
		if(obj==null) throw new SQLException("Unable to find HttpdTomcatVersion: "+version);
		if(
			obj.getTechnologyVersion(table.getConnector()).getOperatingSystemVersion(table.getConnector()).getPkey()
			!= getAOServer().getServer().getOperatingSystemVersion_id()
		) {
			throw new SQLException("resource/operating system version mismatch on HttpdSharedTomcat: #"+pkey);
		}
		return obj;
	}

	public void setHttpdTomcatVersion(Version version) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_VERSION, pkey, version.getPkey());
	}

	public int getLinuxServerAccount_pkey() {
		return linux_server_account;
	}

	public UserServer getLinuxServerAccount() throws SQLException, IOException {
		UserServer obj=table.getConnector().getLinux().getUserServer().get(linux_server_account);
		if(obj==null) throw new SQLException("Unable to find LinuxServerAccount: "+linux_server_account);
		return obj;
	}

	public int getLinuxServerGroup_pkey() {
		return linux_server_group;
	}

	public GroupServer getLinuxServerGroup() throws SQLException, IOException {
		GroupServer obj=table.getConnector().getLinux().getGroupServer().get(linux_server_group);
		if(obj==null) throw new SQLException("Unable to find LinuxServerGroup: "+linux_server_group);
		return obj;
	}

	public String getName() {
		return name;
	}

	public Server getAOServer() throws SQLException, IOException {
		Server obj=table.getConnector().getLinux().getServer().get(ao_server);
		if(obj==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return obj;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.HTTPD_SHARED_TOMCATS;
	}

	public Worker getTomcat4Worker() throws SQLException, IOException {
		if(tomcat4_worker==-1) return null;
		Worker hw=table.getConnector().getWeb_tomcat().getWorker().get(tomcat4_worker);
		if(hw==null) throw new SQLException("Unable to find HttpdWorker: "+tomcat4_worker);
		return hw;
	}

	public String getTomcat4ShutdownKey() {
		return tomcat4_shutdown_key;
	}

	public Bind getTomcat4ShutdownPort() throws IOException, SQLException {
		if(tomcat4_shutdown_port==-1) return null;
		Bind nb=table.getConnector().getNet().getBind().get(tomcat4_shutdown_port);
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
		disable_log=result.getInt(pos++);
		if(result.wasNull()) disable_log=-1;
		tomcat4_worker=result.getInt(pos++);
		if(result.wasNull()) tomcat4_worker=-1;
		tomcat4_shutdown_port=result.getInt(pos++);
		if(result.wasNull()) tomcat4_shutdown_port=-1;
		tomcat4_shutdown_key=result.getString(pos++);
		isManual=result.getBoolean(pos++);
		maxPostSize = result.getInt(pos++);
		if(result.wasNull()) maxPostSize = -1;
		unpackWARs = result.getBoolean(pos++);
		autoDeploy = result.getBoolean(pos++);
	}

	public boolean isManual() {
		return isManual;
	}

	public void setIsManual(boolean isManual) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_IS_MANUAL, pkey, isManual);
	}

	/**
	 * Gets the max post size or {@code -1} of not limited.
	 */
	public int getMaxPostSize() {
		return maxPostSize;
	}

	public void setMaxPostSize(final int maxPostSize) throws IOException, SQLException {
		table.getConnector().requestUpdate(true,
			AoservProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_MAX_POST_SIZE,
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
					if(code==AoservProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
					else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public void afterRelease() {
					table.getConnector().tablesUpdated(invalidateList);
				}
			}
		);
	}

	/**
	 * Gets the <code>unpackWARs</code> setting for this Tomcat.
	 */
	public boolean getUnpackWARs() {
		return unpackWARs;
	}

	public void setUnpackWARs(boolean unpackWARs) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_UNPACK_WARS, pkey, unpackWARs);
	}

	/**
	 * Gets the <code>autoDeploy</code> setting for this Tomcat.
	 */
	public boolean getAutoDeploy() {
		return autoDeploy;
	}

	public void setAutoDeploy(boolean autoDeploy) throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.SET_HTTPD_SHARED_TOMCAT_AUTO_DEPLOY, pkey, autoDeploy);
	}

	/**
	 * Checks the format of the name of the shared Tomcat, as used in the <code>/wwwgroup</code>
	 * directory.  The name must be 12 characters or less, and comprised of
	 * only <code>a-z</code>,<code>0-9</code>, or <code>-</code>.  The first
	 * character must be <code>a-z</code>.
	 * <p>
	 * Note: This matches the check constraint on the httpd_shared_tomcats table.
	 * Note: This matches keepWwwgroupDirs in HttpdSharedTomcatManager.
	 * </p>
	 * // TODO: Self-validating type
	 */
	public static boolean isValidSharedTomcatName(String name) {
		// These are the other files/directories that may exist under /www.  To avoid
		// potential conflicts, these may not be used as site names.
		if(
			// Other filesystem patterns
			   "lost+found".equals(name)
			|| "aquota.group".equals(name)
			|| "aquota.user".equals(name)
		) return false;

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
		disable_log=in.readCompressedInt();
		tomcat4_worker=in.readCompressedInt();
		tomcat4_shutdown_port=in.readCompressedInt();
		tomcat4_shutdown_key=in.readNullUTF();
		isManual=in.readBoolean();
		maxPostSize = in.readInt();
		unpackWARs = in.readBoolean();
		autoDeploy = in.readBoolean();
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true, AoservProtocol.CommandID.REMOVE, Table.TableID.HTTPD_SHARED_TOMCATS, pkey);
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		return name+" on "+getAOServer().getHostname();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeCompressedInt(ao_server);
		out.writeCompressedInt(version);
		out.writeCompressedInt(linux_server_account);
		out.writeCompressedInt(linux_server_group);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_81_9) <= 0) {
			out.writeBoolean(false); // is_secure
			out.writeBoolean(false); // is_overflow
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_30)<=0) {
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
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_80_1) >= 0) {
			out.writeInt(maxPostSize);
			out.writeBoolean(unpackWARs);
			out.writeBoolean(autoDeploy);
		}
	}
}
