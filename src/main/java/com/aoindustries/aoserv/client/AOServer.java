/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2014, 2015, 2016, 2017, 2018  AO Industries, Inc.
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

import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.aoserv.client.validator.Gecos;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.LinuxId;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.PostgresServerName;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.dto.DtoFactory;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.DomainName;
import com.aoindustries.net.Email;
import com.aoindustries.net.HostAddress;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.InetAddress;
import com.aoindustries.net.Port;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An <code>AOServer</code> stores the details about a server that runs the AOServ distribution.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServer
	extends CachedObjectIntegerKey<AOServer>
	implements DtoFactory<com.aoindustries.aoserv.client.dto.AOServer>
{

	static final int COLUMN_SERVER=0;
	static final int COLUMN_HOSTNAME=1;
	static final String COLUMN_HOSTNAME_name = "hostname";

	private DomainName hostname;
	int daemon_bind;
	private HashedPassword daemon_key;
	private int pool_size;
	private int distro_hour;
	private long last_distro_time;
	int failover_server;
	private String daemon_device_id;
	int daemon_connect_bind;
	private String time_zone;
	int jilter_bind;
	private boolean restrict_outbound_email;
	private HostAddress daemon_connect_address;
	private int failover_batch_size;
	private float monitoring_load_low;
	private float monitoring_load_medium;
	private float monitoring_load_high;
	private float monitoring_load_critical;
	private LinuxId uid_min;
	private LinuxId gid_min;
	private long sftp_umask;

	public int addCvsRepository(
		UnixPath path,
		LinuxServerAccount lsa,
		LinuxServerGroup lsg,
		long mode
	) throws IOException, SQLException {
		return table.connector.getCvsRepositories().addCvsRepository(
			this,
			path,
			lsa,
			lsg,
			mode
		);
	}

	public int addEmailDomain(DomainName domain, Package packageObject) throws SQLException, IOException {
		return table.connector.getEmailDomains().addEmailDomain(domain, this, packageObject);
	}

	public int addEmailPipe(String command, Package packageObject) throws IOException, SQLException {
		return table.connector.getEmailPipes().addEmailPipe(this, command, packageObject);
	}

	public int addHttpdJBossSite(
		String siteName,
		Package packageObj,
		LinuxAccount siteUser,
		LinuxGroup siteGroup,
		Email serverAdmin,
		boolean useApache,
		IPAddress ipAddress,
		DomainName primaryHttpHostname,
		DomainName[] altHttpHostnames,
		HttpdJBossVersion jBossVersion,
		UnixPath contentSrc
	) throws IOException, SQLException {
		return table.connector.getHttpdJBossSites().addHttpdJBossSite(
			this,
			siteName,
			packageObj,
			siteUser,
			siteGroup,
			serverAdmin,
			useApache,
			ipAddress,
			primaryHttpHostname,
			altHttpHostnames,
			jBossVersion,
			contentSrc
		);
	}

	public int addHttpdSharedTomcat(
		String name,
		HttpdTomcatVersion version,
		LinuxServerAccount lsa,
		LinuxServerGroup lsg,
		boolean isSecure,
		boolean isOverflow
	) throws IOException, SQLException {
		return table.connector.getHttpdSharedTomcats().addHttpdSharedTomcat(
			name,
			this,
			version,
			lsa,
			lsg,
			isSecure,
			isOverflow
		);
	}

	public int addHttpdTomcatSharedSite(
		String siteName,
		Package packageObj,
		LinuxAccount siteUser,
		LinuxGroup siteGroup,
		Email serverAdmin,
		boolean useApache,
		IPAddress ipAddress,
		DomainName primaryHttpHostname,
		DomainName[] altHttpHostnames,
		String sharedTomcatName,
		HttpdTomcatVersion version,
		UnixPath contentSrc
	) throws IOException, SQLException {
		return table.connector.getHttpdTomcatSharedSites().addHttpdTomcatSharedSite(
			this,
			siteName,
			packageObj,
			siteUser,
			siteGroup,
			serverAdmin,
			useApache,
			ipAddress,
			primaryHttpHostname,
			altHttpHostnames,
			sharedTomcatName,
			version,
			contentSrc
		);
	}

	public int addHttpdTomcatStdSite(
		String siteName,
		Package packageObj,
		LinuxAccount jvmUser,
		LinuxGroup jvmGroup,
		Email serverAdmin,
		boolean useApache,
		IPAddress ipAddress,
		DomainName primaryHttpHostname,
		DomainName[] altHttpHostnames,
		HttpdTomcatVersion tomcatVersion,
		UnixPath contentSrc
	) throws IOException, SQLException {
		return table.connector.getHttpdTomcatStdSites().addHttpdTomcatStdSite(
			this,
			siteName,
			packageObj,
			jvmUser,
			jvmGroup,
			serverAdmin,
			useApache,
			ipAddress,
			primaryHttpHostname,
			altHttpHostnames,
			tomcatVersion,
			contentSrc
		);
	}

	/**
	 * Adds a new system group.  This is for the AOServ Daemon to register newly
	 * installed local system groups, such as those added through routine RPM
	 * installation.  The master will check that the requested group matches
	 * expected settings.
	 */
	public int addSystemGroup(GroupId groupName, int gid) throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().addSystemGroup(
			this,
			groupName,
			gid
		);
	}

	/**
	 * Adds a new system user.  This is for the AOServ Daemon to register newly
	 * installed local system users, such as those added through routine RPM
	 * installation.  The master will check that the requested user matches
	 * expected settings.
	 */
	public int addSystemUser(
		UserId username,
		int uid,
		int gid,
		Gecos fullName,
		Gecos officeLocation,
		Gecos officePhone,
		Gecos homePhone,
		UnixPath home,
		UnixPath shell
	) throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().addSystemUser(
			this,
			username,
			uid,
			gid,
			fullName,
			officeLocation,
			officePhone,
			homePhone,
			home,
			shell
		);
	}

	public List<AOServerDaemonHost> getAOServerDaemonHosts() throws IOException, SQLException {
		return table.connector.getAoServerDaemonHosts().getAOServerDaemonHosts(this);
	}

	public List<BackupPartition> getBackupPartitions() throws IOException, SQLException {
		return table.connector.getBackupPartitions().getBackupPartitions(this);
	}

	public BackupPartition getBackupPartitionForPath(String path) throws IOException, SQLException {
		return table.connector.getBackupPartitions().getBackupPartitionForPath(this, path);
	}

	public List<BlackholeEmailAddress> getBlackholeEmailAddresses() throws IOException, SQLException {
		return table.connector.getBlackholeEmailAddresses().getBlackholeEmailAddresses(this);
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_SERVER: return pkey;
			case COLUMN_HOSTNAME: return hostname;
			case 2: return daemon_bind==-1?null:daemon_bind;
			case 3: return daemon_key;
			case 4: return pool_size;
			case 5: return distro_hour;
			case 6: return getLastDistroTime();
			case 7: return failover_server==-1?null:failover_server;
			case 8: return daemon_device_id;
			case 9: return daemon_connect_bind==-1?null:daemon_connect_bind;
			case 10: return time_zone;
			case 11: return jilter_bind;
			case 12: return restrict_outbound_email;
			case 13: return daemon_connect_address;
			case 14: return failover_batch_size;
			case 15: return Float.isNaN(monitoring_load_low) ? null : monitoring_load_low;
			case 16: return Float.isNaN(monitoring_load_medium) ? null : monitoring_load_medium;
			case 17: return Float.isNaN(monitoring_load_high) ? null : monitoring_load_high;
			case 18: return Float.isNaN(monitoring_load_critical) ? null : monitoring_load_critical;
			case 19: return uid_min;
			case 20: return gid_min;
			case 21: return sftp_umask==-1 ? null : sftp_umask;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public CvsRepository getCvsRepository(UnixPath path) throws IOException, SQLException {
		return table.connector.getCvsRepositories().getCvsRepository(this, path);
	}

	public List<CvsRepository> getCvsRepositories() throws IOException, SQLException {
		return table.connector.getCvsRepositories().getCvsRepositories(this);
	}

	/**
	 * Gets the unique hostname for this server.  Should be resolvable in DNS to ease maintenance.
	 */
	public DomainName getHostname() {
		return hostname;
	}

	/**
	 * Gets the port information to bind to.
	 */
	public NetBind getDaemonBind() throws IOException, SQLException {
		if(daemon_bind==-1) return null;
		// May be filtered
		return table.connector.getNetBinds().get(daemon_bind);
	}

	/**
	 * Gets the port information to connect to.
	 */
	public NetBind getDaemonConnectBind() throws IOException, SQLException {
		if(daemon_connect_bind==-1) return null;
		// May be filtered
		return table.connector.getNetBinds().get(daemon_connect_bind);
	}

	public TimeZone getTimeZone() throws SQLException, IOException {
		TimeZone tz=table.connector.getTimeZones().get(time_zone);
		if(tz==null) throw new SQLException("Unable to find TimeZone: "+time_zone);
		return tz;
	}

	public NetBind getJilterBind() throws IOException, SQLException {
		if(jilter_bind==-1) return null;
		// May be filtered
		return table.connector.getNetBinds().get(jilter_bind);
	}

	public boolean getRestrictOutboundEmail() {
		return restrict_outbound_email;
	}

	/**
	 * Gets the address that should be connected to in order to reach this server.
	 * This overrides both getDaemonConnectBind and getDaemonBind.
	 *
	 * @see  #getDaemonConnectBind
	 * @see  #getDaemonBind
	 */
	public HostAddress getDaemonConnectAddress() {
		return daemon_connect_address;
	}

	/**
	 * Gets the number of filesystem entries sent per batch during failover replications.
	 */
	public int getFailoverBatchSize() {
		return failover_batch_size;
	}

	/**
	 * Gets the 5-minute load average that is considered a low-priority alert or
	 * <code>NaN</code> if no alert allowed at this level.
	 */
	public float getMonitoringLoadLow() {
		return monitoring_load_low;
	}

	/**
	 * Gets the 5-minute load average that is considered a medium-priority alert or
	 * <code>NaN</code> if no alert allowed at this level.
	 */
	public float getMonitoringLoadMedium() {
		return monitoring_load_medium;
	}

	/**
	 * Gets the 5-minute load average that is considered a high-priority alert or
	 * <code>NaN</code> if no alert allowed at this level.
	 */
	public float getMonitoringLoadHigh() {
		return monitoring_load_high;
	}

	/**
	 * Gets the 5-minute load average that is considered a critical-priority alert or
	 * <code>NaN</code> if no alert allowed at this level.  This is the level
	 * that will alert people 24x7.
	 */
	public float getMonitoringLoadCritical() {
		return monitoring_load_critical;
	}

	/**
	 * Gets the min value for automatic uid selection in useradd.
	 *
	 * @see  LinuxAccount#UID_MAX
	 */
	public LinuxId getUidMin() {
		return uid_min;
	}

	/**
	 * Gets the min value for automatic gid selection in groupadd.
	 *
	 * @see  LinuxGroup#GID_MAX
	 */
	public LinuxId getGidMin() {
		return gid_min;
	}

	/**
	 * Gets the optional umask for the sftp-server or <code>-1</code> for none.
	 */
	public long getSftpUmask() {
		return sftp_umask;
	}

	public NetDeviceID getDaemonDeviceID() throws SQLException, IOException {
		NetDeviceID ndi=table.connector.getNetDeviceIDs().get(daemon_device_id);
		if(ndi==null) throw new SQLException("Unable to find NetDeviceID: "+daemon_device_id);
		return ndi;
	}

	public IPAddress getDaemonIPAddress() throws SQLException, IOException {
		NetBind nb=getDaemonBind();
		if(nb==null) throw new SQLException("Unable to find daemon NetBind for AOServer: "+pkey);
		IPAddress ia=nb.getIPAddress();
		InetAddress ip=ia.getInetAddress();
		if(ip.isUnspecified()) {
			NetDeviceID ndi=getDaemonDeviceID();
			NetDevice nd=getServer().getNetDevice(ndi.getName());
			if(nd==null) throw new SQLException("Unable to find NetDevice: "+ndi.getName()+" on "+pkey);
			ia=nd.getPrimaryIPAddress();
			if(ia==null) throw new SQLException("Unable to find primary IPAddress: "+ndi.getName()+" on "+pkey);
		}
		return ia;
	}

	public HashedPassword getDaemonKey() {
		return daemon_key;
	}

	public int getDistroHour() {
		return distro_hour;
	}

	public List<EmailAddress> getEmailAddresses() throws IOException, SQLException {
		return table.connector.getEmailAddresses().getEmailAddresses(this);
	}

	public EmailDomain getEmailDomain(DomainName domain) throws IOException, SQLException {
		return table.connector.getEmailDomains().getEmailDomain(this, domain);
	}

	public List<EmailDomain> getEmailDomains() throws IOException, SQLException {
		return table.connector.getEmailDomains().getEmailDomains(this);
	}

	public List<EmailForwarding> getEmailForwarding() throws SQLException, IOException {
		return table.connector.getEmailForwardings().getEmailForwarding(this);
	}

	/**
	 * Rename to getEmailList when all uses updated.
	 */
	public EmailList getEmailList(UnixPath path) throws IOException, SQLException {
		return table.connector.getEmailLists().getEmailList(this, path);
	}

	public List<EmailListAddress> getEmailListAddresses() throws IOException, SQLException {
		return table.connector.getEmailListAddresses().getEmailListAddresses(this);
	}

	public List<EmailPipeAddress> getEmailPipeAddresses() throws IOException, SQLException {
		return table.connector.getEmailPipeAddresses().getEmailPipeAddresses(this);
	}

	public List<EmailPipe> getEmailPipes() throws IOException, SQLException {
		return table.connector.getEmailPipes().getEmailPipes(this);
	}

	public EmailSmtpRelay getEmailSmtpRelay(Package pk, HostAddress host) throws IOException, SQLException {
		return table.connector.getEmailSmtpRelays().getEmailSmtpRelay(pk, this, host);
	}

	/**
	 * Gets all of the smtp relays settings that apply to either all servers or this server specifically.
	 */
	public List<EmailSmtpRelay> getEmailSmtpRelays() throws IOException, SQLException {
		return table.connector.getEmailSmtpRelays().getEmailSmtpRelays(this);
	}

	public AOServer getFailoverServer() throws SQLException, IOException {
		if(failover_server==-1) return null;
		AOServer se=table.connector.getAoServers().get(failover_server);
		if(se==null) throw new SQLException("Unable to find AOServer: "+failover_server);
		return se;
	}

	public List<FTPGuestUser> getFTPGuestUsers() throws IOException, SQLException {
		return table.connector.getFtpGuestUsers().getFTPGuestUsers(this);
	}

	public List<HttpdServer> getHttpdServers() throws IOException, SQLException {
		return table.connector.getHttpdServers().getHttpdServers(this);
	}

	public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException, SQLException {
		return table.connector.getHttpdSharedTomcats().getHttpdSharedTomcats(this);
	}

	public HttpdSharedTomcat getHttpdSharedTomcat(String jvmName) throws IOException, SQLException {
		return table.connector.getHttpdSharedTomcats().getHttpdSharedTomcat(jvmName, this);
	}

	public HttpdSite getHttpdSite(String siteName) throws IOException, SQLException {
		return table.connector.getHttpdSites().getHttpdSite(siteName, this);
	}

	public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
		return table.connector.getHttpdSites().getHttpdSites(this);
	}

	public Timestamp getLastDistroTime() {
		return last_distro_time==-1 ? null : new Timestamp(last_distro_time);
	}

	public List<LinuxAccAddress> getLinuxAccAddresses() throws IOException, SQLException {
		return table.connector.getLinuxAccAddresses().getLinuxAccAddresses(this);
	}

	public List<LinuxAccount> getLinuxAccounts() throws SQLException, IOException {
		List<LinuxServerAccount> lsa=getLinuxServerAccounts();
		int len=lsa.size();
		List<LinuxAccount> la=new ArrayList<>(len);
		for(int c=0;c<len;c++) la.add(lsa.get(c).getLinuxAccount());
		return la;
	}

	public List<LinuxGroup> getLinuxGroups() throws SQLException, IOException {
		List<LinuxServerGroup> lsg=getLinuxServerGroups();
		int len=lsg.size();
		List<LinuxGroup> lg=new ArrayList<>(len);
		for(int c=0;c<len;c++) lg.add(lsg.get(c).getLinuxGroup());
		return lg;
	}

	public LinuxServerAccount getLinuxServerAccount(UserId username) throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().getLinuxServerAccount(this, username);
	}

	public LinuxServerAccount getLinuxServerAccount(LinuxId uid) throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().getLinuxServerAccount(this, uid);
	}

	public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().getLinuxServerAccounts(this);
	}

	public LinuxServerGroup getLinuxServerGroup(LinuxId gid) throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().getLinuxServerGroup(this, gid);
	}

	public LinuxServerGroup getLinuxServerGroup(GroupId groupName) throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().getLinuxServerGroup(this, groupName);
	}

	public List<LinuxServerGroup> getLinuxServerGroups() throws IOException, SQLException {
		return table.connector.getLinuxServerGroups().getLinuxServerGroups(this);
	}

	public List<MajordomoServer> getMajordomoServers() throws IOException, SQLException {
		return table.connector.getMajordomoServers().getMajordomoServers(this);
	}

	private static final Map<Integer,Object> mrtgLocks = new HashMap<>();

	public void getMrtgFile(final String filename, final OutputStream out) throws IOException, SQLException {
		// Only one MRTG graph per server at a time, if don't get the lock in 15 seconds, return an error
		synchronized(mrtgLocks) {
			long startTime = System.currentTimeMillis();
			do {
				if(mrtgLocks.containsKey(pkey)) {
					long currentTime = System.currentTimeMillis();
					if(startTime > currentTime) startTime = currentTime;
					else if((currentTime - startTime)>=15000) throw new IOException("15 second timeout reached while trying to get lock to access server #"+pkey);
					else {
						try {
							mrtgLocks.wait(startTime + 15000 - currentTime);
						} catch(InterruptedException err) {
							InterruptedIOException ioErr = new InterruptedIOException();
							ioErr.initCause(err);
							throw ioErr;
						}
					}
				}
			} while(mrtgLocks.containsKey(pkey));
			mrtgLocks.put(pkey, Boolean.TRUE);
			mrtgLocks.notifyAll();
		}

		try {
			table.connector.requestUpdate(
				false,
				AOServProtocol.CommandID.GET_MRTG_FILE,
				new AOServConnector.UpdateRequest() {
					@Override
					public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
						masterOut.writeCompressedInt(pkey);
						masterOut.writeUTF(filename);
					}

					@Override
					public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
						byte[] buff=BufferManager.getBytes();
						try {
							int code;
							while((code=in.readByte())==AOServProtocol.NEXT) {
								int len=in.readShort();
								in.readFully(buff, 0, len);
								out.write(buff, 0, len);
							}
							AOServProtocol.checkResult(code, in);
						} finally {
							BufferManager.release(buff, false);
						}
					}

					@Override
					public void afterRelease() {
					}
				}
			);
		} finally {
			synchronized(mrtgLocks) {
				mrtgLocks.remove(pkey);
				mrtgLocks.notifyAll();
			}
		}
	}

	public MySQLServer getMySQLServer(MySQLServerName name) throws IOException, SQLException {
		return table.connector.getMysqlServers().getMySQLServer(name, this);
	}

	public List<MySQLServer> getMySQLServers() throws IOException, SQLException {
		return table.connector.getMysqlServers().getMySQLServers(this);
	}

	public MySQLServer getPreferredMySQLServer() throws IOException, SQLException {
		// Look for the most-preferred version that has an instance on the server
		List<MySQLServer> pss=getMySQLServers();
		for(String versionPrefix : MySQLServer.PREFERRED_VERSION_PREFIXES) {
			for (MySQLServer ps : pss) {
				if(ps.getVersion().getVersion().startsWith(versionPrefix)) {
					return ps;
				}
			}
		}

		// Default to first available server if no preferred ones round
		return pss.isEmpty()?null:pss.get(0);
	}

	public List<AOServer> getNestedAOServers() throws IOException, SQLException {
		return table.connector.getAoServers().getNestedAOServers(this);
	}

	public int getPoolSize() {
		return pool_size;
	}

	public PostgresServer getPostgresServer(PostgresServerName name) throws IOException, SQLException {
		return table.connector.getPostgresServers().getPostgresServer(name, this);
	}

	public List<PostgresServer> getPostgresServers() throws IOException, SQLException {
		return table.connector.getPostgresServers().getPostgresServers(this);
	}

	public PostgresServer getPreferredPostgresServer() throws SQLException, IOException {
		// Look for the most-preferred version that has an instance on the server
		List<PostgresServer> pss=getPostgresServers();
		String[] preferredMinorVersions=PostgresVersion.getPreferredMinorVersions();
		for(String version : preferredMinorVersions) {
			for (PostgresServer ps : pss) {
				if(ps.getPostgresVersion().getMinorVersion().equals(version)) {
					return ps;
				}
			}
		}

		// Default to first available server if no preferred ones round
		return pss.isEmpty()?null:pss.get(0);
	}

	public IPAddress getPrimaryIPAddress() throws SQLException, IOException {
		NetDeviceID ndi=getDaemonDeviceID();
		String name=ndi.getName();
		NetDevice nd=getServer().getNetDevice(name);
		if(nd==null) throw new SQLException("Unable to find NetDevice: "+name+" on "+pkey);
		return nd.getPrimaryIPAddress();
	}

	/*
	public PrivateFTPServer getPrivateFTPServer(String path) {
		return table.connector.privateFTPServers.getPrivateFTPServer(this, path);
	}*/

	public List<PrivateFTPServer> getPrivateFTPServers() throws IOException, SQLException {
		return table.connector.getPrivateFTPServers().getPrivateFTPServers(this);
	}

	public Server getServer() throws SQLException, IOException {
		Server se=table.connector.getServers().get(pkey);
		if(se==null) throw new SQLException("Unable to find Server: "+pkey);
		return se;
	}

	public List<SystemEmailAlias> getSystemEmailAliases() throws IOException, SQLException {
		return table.connector.getSystemEmailAliases().getSystemEmailAliases(this);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AO_SERVERS;
	}

	public boolean isEmailDomainAvailable(DomainName domain) throws SQLException, IOException {
		return table.connector.getEmailDomains().isEmailDomainAvailable(this, domain);
	}

	public boolean isHomeUsed(UnixPath directory) throws IOException, SQLException {
		return table.connector.getLinuxServerAccounts().isHomeUsed(this, directory);
	}

	public boolean isMySQLServerNameAvailable(MySQLServerName name) throws IOException, SQLException {
		return table.connector.getMysqlServers().isMySQLServerNameAvailable(name, this);
	}

	public boolean isPostgresServerNameAvailable(PostgresServerName name) throws IOException, SQLException {
		return table.connector.getPostgresServers().isPostgresServerNameAvailable(name, this);
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey=result.getInt(pos++);
			hostname = DomainName.valueOf(result.getString(pos++));
			daemon_bind=result.getInt(pos++);
			if(result.wasNull()) daemon_bind=-1;
			daemon_key=HashedPassword.valueOf(result.getString(pos++));
			pool_size=result.getInt(pos++);
			distro_hour=result.getInt(pos++);
			Timestamp T=result.getTimestamp(pos++);
			last_distro_time=T==null?-1:T.getTime();
			failover_server=result.getInt(pos++);
			if(result.wasNull()) failover_server=-1;
			daemon_device_id=result.getString(pos++);
			daemon_connect_bind=result.getInt(pos++);
			time_zone=result.getString(pos++);
			jilter_bind=result.getInt(pos++);
			if(result.wasNull()) jilter_bind=-1;
			restrict_outbound_email=result.getBoolean(pos++);
			daemon_connect_address=HostAddress.valueOf(result.getString(pos++));
			failover_batch_size=result.getInt(pos++);
			monitoring_load_low = result.getFloat(pos++);
			if(result.wasNull()) monitoring_load_low = Float.NaN;
			monitoring_load_medium = result.getFloat(pos++);
			if(result.wasNull()) monitoring_load_medium = Float.NaN;
			monitoring_load_high = result.getFloat(pos++);
			if(result.wasNull()) monitoring_load_high = Float.NaN;
			monitoring_load_critical = result.getFloat(pos++);
			if(result.wasNull()) monitoring_load_critical = Float.NaN;
			uid_min = LinuxId.valueOf(result.getInt(pos++));
			gid_min = LinuxId.valueOf(result.getInt(pos++));
			sftp_umask = result.getLong(pos++);
			if(result.wasNull()) sftp_umask = -1;
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=in.readCompressedInt();
			hostname=DomainName.valueOf(in.readUTF());
			daemon_bind=in.readCompressedInt();
			daemon_key=HashedPassword.valueOf(in.readUTF());
			pool_size=in.readCompressedInt();
			distro_hour=in.readCompressedInt();
			last_distro_time=in.readLong();
			failover_server=in.readCompressedInt();
			daemon_device_id=InternUtils.intern(in.readNullUTF());
			daemon_connect_bind=in.readCompressedInt();
			time_zone=in.readUTF().intern();
			jilter_bind=in.readCompressedInt();
			restrict_outbound_email=in.readBoolean();
			daemon_connect_address=InternUtils.intern(HostAddress.valueOf(in.readNullUTF()));
			failover_batch_size=in.readCompressedInt();
			monitoring_load_low = in.readFloat();
			monitoring_load_medium = in.readFloat();
			monitoring_load_high = in.readFloat();
			monitoring_load_critical = in.readFloat();
			uid_min = LinuxId.valueOf(in.readCompressedInt());
			gid_min = LinuxId.valueOf(in.readCompressedInt());
			sftp_umask = in.readLong();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public void restartApache() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.RESTART_APACHE, pkey);
	}

	public void restartCron() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.RESTART_CRON, pkey);
	}

	public void restartXfs() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.RESTART_XFS, pkey);
	}

	public void restartXvfb() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.RESTART_XVFB, pkey);
	}

	public static class DaemonAccess {

		private final String protocol;
		private final HostAddress host;
		private final Port port;
		private final long key;

		public DaemonAccess(String protocol, HostAddress host, Port port, long key) {
			this.protocol = protocol;
			this.host = host;
			this.port = port;
			this.key = key;
		}

		public String getProtocol() {
			return protocol;
		}

		public HostAddress getHost() {
			return host;
		}

		public Port getPort() {
			return port;
		}

		public long getKey() {
			return key;
		}
	}

	public void setLastDistroTime(Timestamp distroTime) throws IOException, SQLException {
		table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_LAST_DISTRO_TIME, pkey, distroTime.getTime());
	}

	public void startApache() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.START_APACHE, pkey);
	}

	public void startCron() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.START_CRON, pkey);
	}

	public void startDistro(boolean includeUser) throws IOException, SQLException {
		table.connector.getDistroFiles().startDistro(this, includeUser);
	}

	public void startXfs() throws IOException, SQLException {
		table.connector.requestUpdate(false,AOServProtocol.CommandID.START_XFS, pkey);
	}

	public void startXvfb() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.START_XVFB, pkey);
	}

	public void stopApache() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.STOP_APACHE, pkey);
	}

	public void stopCron() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.STOP_CRON, pkey);
	}

	public void stopXfs() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.STOP_XFS, pkey);
	}

	public void stopXvfb() throws IOException, SQLException {
		table.connector.requestUpdate(false, AOServProtocol.CommandID.STOP_XVFB, pkey);
	}

	@Override
	String toStringImpl() {
		return hostname.toString();
	}

	public void waitForHttpdSiteRebuild() throws IOException, SQLException {
		table.connector.getHttpdSites().waitForRebuild(this);
	}

	public void waitForLinuxAccountRebuild() throws IOException, SQLException {
		table.connector.getLinuxAccounts().waitForRebuild(this);
	}

	public void waitForMySQLDatabaseRebuild() throws IOException, SQLException {
		table.connector.getMysqlDatabases().waitForRebuild(this);
	}

	public void waitForMySQLDBUserRebuild() throws IOException, SQLException {
		table.connector.getMysqlDBUsers().waitForRebuild(this);
	}

	public void waitForMySQLServerRebuild() throws IOException, SQLException {
		table.connector.getMysqlServers().waitForRebuild(this);
	}

	public void waitForMySQLUserRebuild() throws IOException, SQLException {
		table.connector.getMysqlUsers().waitForRebuild(this);
	}

	public void waitForPostgresDatabaseRebuild() throws IOException, SQLException {
		table.connector.getPostgresDatabases().waitForRebuild(this);
	}

	public void waitForPostgresServerRebuild() throws IOException, SQLException {
		table.connector.getPostgresServers().waitForRebuild(this);
	}

	public void waitForPostgresUserRebuild() throws IOException, SQLException {
		table.connector.getPostgresUsers().waitForRebuild(this);
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeCompressedInt(1);
			out.writeCompressedInt(2000);
			out.writeCompressedInt(1024);
			out.writeCompressedInt(2);
			out.writeCompressedInt(240);
			out.writeNullUTF(null);
			out.writeBoolean(false);
			out.writeBoolean(false);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_4)<0) out.writeBoolean(true);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeBoolean(false);
			out.writeUTF("AOServer #"+pkey);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
			out.writeUTF(hostname.toString());
		}
		out.writeCompressedInt(daemon_bind);
		out.writeUTF(daemon_key.toString());
		out.writeCompressedInt(pool_size);
		out.writeCompressedInt(distro_hour);
		out.writeLong(last_distro_time);
		out.writeCompressedInt(failover_server);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeCompressedInt(60*1000);
			out.writeCompressedInt(5*60*1000);
			out.writeBoolean(false);
		}
		out.writeNullUTF(daemon_device_id);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
			out.writeNullUTF(null);
			out.writeCompressedInt(1200*100);
			out.writeBoolean(true);
			if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0) {
				out.writeNullUTF(null);
				out.writeNullUTF(null);
			} else if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_104)>=0) {
				out.writeUTF(AOServProtocol.FILTERED);
				out.writeUTF(AOServProtocol.FILTERED);
			}
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_119)>=0) out.writeCompressedInt(daemon_connect_bind);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_2)>=0) out.writeUTF(time_zone);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_7)>=0) out.writeCompressedInt(jilter_bind);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_8)>=0) out.writeBoolean(restrict_outbound_email);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_11)>=0) out.writeNullUTF(ObjectUtils.toString(daemon_connect_address));
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_12)>=0) out.writeCompressedInt(failover_batch_size);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_35)>=0) {
			out.writeFloat(monitoring_load_low);
			out.writeFloat(monitoring_load_medium);
			out.writeFloat(monitoring_load_high);
			out.writeFloat(monitoring_load_critical);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_80)>=0) {
			out.writeCompressedInt(uid_min.getId());
			out.writeCompressedInt(gid_min.getId());
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_81_5) >= 0) {
			out.writeLong(sftp_umask);
		}
	}

	/**
	 * Gets the 3ware RAID report.
	 */
	public String get3wareRaidReport() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_3WARE_RAID_REPORT, pkey);
	}

	/**
	 * Gets the /proc/mdstat report.
	 */
	public String getMdStatReport() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_MD_STAT_REPORT, pkey);
	}

	public enum RaidLevel {
		linear,
		raid0,
		raid1,
		raid4,
		raid5,
		raid6,
		raid10
	}

	/**
	 * The results of the most recent weekly RAID check.
	 */
	public static class MdMismatchReport {

		final private String device;
		final private RaidLevel level;
		final private long count;

		MdMismatchReport(
			String device,
			RaidLevel level,
			long count
		) {
			this.device = device;
			this.level = level;
			this.count = count;
		}

		/**
		 * The device that was checked.
		 */
		public String getDevice() {
			return device;
		}

		/**
		 * The RAID level of the device.
		 */
		public RaidLevel getLevel() {
			return level;
		}

		/**
		 * The number bytes that did not match.
		 */
		public long getCount() {
			return count;
		}
	}

	/**
	 * Gets the MD mismatch report.
	 */
	public List<MdMismatchReport> getMdMismatchReport() throws IOException, SQLException, ParseException {
		return parseMdMismatchReport(table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_MD_MISMATCH_REPORT, pkey));
	}

	/**
	 * Parses a MD mismatch report.
	 */
	public static List<MdMismatchReport> parseMdMismatchReport(String mismatchReport) throws ParseException {
		List<String> lines = StringUtility.splitLines(mismatchReport);
		int lineNum = 0;
		List<MdMismatchReport> reports = new ArrayList<>(lines.size());
		for(String line : lines) {
			lineNum++;
			List<String> values = StringUtility.splitString(line, '\t');
			if(values.size() != 3) {
				throw new ParseException(
					accessor.getMessage(
						"AOServer.MdMismatchReport.ParseException.badColumnCount",
						line
					),
					lineNum
				);
			}

			// Device
			String device = values.get(0);
			if(!device.startsWith("/dev/md")) {
				throw new ParseException(
					accessor.getMessage(
						"AOServer.MdMismatchReport.ParseException.badDeviceStart",
						device
					),
					lineNum
				);
			}

			// Level
			RaidLevel level = RaidLevel.valueOf(values.get(1));

			// Count
			String countString = values.get(2);
			long count;
			try {
				count = Long.parseLong(countString);
			} catch(NumberFormatException e) {
				ParseException parseException = new ParseException(
					accessor.getMessage(
						"AOServer.MdMismatchReport.ParseException.countNotNumber",
						countString
					),
					lineNum
				);
				parseException.initCause(e);
				throw parseException;
			}

			reports.add(new MdMismatchReport(device, level, count));
		}
		return reports;
	}

	public static class DrbdReport {

		/**
		 * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-connection-states
		 */
		public enum ConnectionState {
			Unconfigured,
			StandAlone,
			Disconnecting,
			Unconnected,
			Timeout,
			BrokenPipe,
			NetworkFailure,
			ProtocolError,
			TearDown,
			WFConnection,
			WFReportParams,
			Connected,
			StartingSyncS,
			StartingSyncT,
			WFBitMapS,
			WFBitMapT,
			WFSyncUUID,
			SyncSource,
			SyncTarget,
			PausedSyncS,
			PausedSyncT,
			VerifyS,
			VerifyT
		}

		/**
		 * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-roles
		 */
		public enum Role {
			Unconfigured,
			Primary,
			Secondary,
			Unknown
		}

		/**
		 * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-disk-states
		 */
		public enum DiskState {
			Unconfigured,
			Diskless,
			Attaching,
			Failed,
			Negotiating,
			Inconsistent,
			Outdated,
			DUnknown,
			Consistent,
			UpToDate,
		}

		final private String device;
		final private String resourceHostname;
		final private String resourceDevice;
		final private ConnectionState connectionState;
		final private DiskState localDiskState;
		final private DiskState remoteDiskState;
		final private Role localRole;
		final private Role remoteRole;
		final private Long lastVerified;
		final private Long outOfSync;

		DrbdReport(
			String device,
			String resourceHostname,
			String resourceDevice,
			ConnectionState connectionState,
			DiskState localDiskState,
			DiskState remoteDiskState,
			Role localRole,
			Role remoteRole,
			Long lastVerified,
			Long outOfSync
		) {
			this.device = device;
			this.resourceHostname = resourceHostname;
			this.resourceDevice = resourceDevice;
			this.connectionState = connectionState;
			this.localDiskState = localDiskState;
			this.remoteDiskState = remoteDiskState;
			this.localRole = localRole;
			this.remoteRole = remoteRole;
			this.lastVerified = lastVerified;
			this.outOfSync = outOfSync;
		}

		public ConnectionState getConnectionState() {
			return connectionState;
		}

		public String getDevice() {
			return device;
		}

		public DiskState getLocalDiskState() {
			return localDiskState;
		}

		public Role getLocalRole() {
			return localRole;
		}

		public DiskState getRemoteDiskState() {
			return remoteDiskState;
		}

		public Role getRemoteRole() {
			return remoteRole;
		}

		public String getResourceDevice() {
			return resourceDevice;
		}

		public String getResourceHostname() {
			return resourceHostname;
		}

		/**
		 * Gets the time verification was last started from this node
		 * or <code>null</code> if never started.
		 */
		public Long getLastVerified() {
			return lastVerified;
		}

		/**
		 * Gets the number of kilobytes of data out of sync, in Kibibytes.
		 * @link http://www.drbd.org/users-guide/ch-admin.html#s-performance-indicators
		 */
		public Long getOutOfSync() {
			return outOfSync;
		}
	}

	/**
	 * Gets the DRBD report.
	 */
	public List<DrbdReport> getDrbdReport() throws IOException, SQLException, ParseException {
		return parseDrbdReport(table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_DRBD_REPORT, pkey));
	}

	/**
	 * Parses a DRBD report.
	 */
	public static List<DrbdReport> parseDrbdReport(String drbdReport) throws ParseException {
		List<String> lines = StringUtility.splitLines(drbdReport);
		int lineNum = 0;
		List<DrbdReport> reports = new ArrayList<>(lines.size());
		for(String line : lines) {
			lineNum++;
			List<String> values = StringUtility.splitString(line, '\t');
			if(values.size() != 7) {
				throw new ParseException(
					accessor.getMessage(
						"AOServer.DrbdReport.ParseException.badColumnCount",
						line
					),
					lineNum
				);
			}

			// Device
			String device = values.get(0);
			if(!device.startsWith("/dev/drbd")) {
				throw new ParseException(
					accessor.getMessage(
						"AOServer.DrbdReport.ParseException.badDeviceStart",
						device
					),
					lineNum
				);
			}

			// Resource
			String resource = values.get(1);
			int dashPos = resource.lastIndexOf('-');
			if(dashPos==-1) throw new ParseException(
				accessor.getMessage(
					"AOServer.DrbdReport.ParseException.noDash",
					resource
				),
				lineNum
			);
			String domUHostname = resource.substring(0, dashPos);
			String domUDevice = resource.substring(dashPos+1);
			if(
				domUDevice.length()!=4
				|| domUDevice.charAt(0)!='x'
				|| domUDevice.charAt(1)!='v'
				|| domUDevice.charAt(2)!='d'
				|| domUDevice.charAt(3)<'a'
				|| domUDevice.charAt(3)>'z'
			) throw new ParseException(
				accessor.getMessage(
					"AOServer.DrbdReport.ParseException.unexpectedResourceEnding",
					domUDevice
				),
				lineNum
			);

			// Connection State
			String connectionStateString = values.get(2);
			final DrbdReport.ConnectionState connectionState =
				"null".equals(connectionStateString)
				? null
				: DrbdReport.ConnectionState.valueOf(connectionStateString);

			// Disk states
			String ds = values.get(3);
			final DrbdReport.DiskState localDiskState;
			final DrbdReport.DiskState remoteDiskState;
			if("null".equals(ds)) {
				localDiskState = null;
				remoteDiskState = null;
			} else if(DrbdReport.DiskState.Unconfigured.name().equals(ds)) {
				localDiskState = DrbdReport.DiskState.Unconfigured;
				remoteDiskState = DrbdReport.DiskState.Unconfigured;
			} else {
				int dsSlashPos = ds.indexOf('/');
				if(dsSlashPos==-1) throw new ParseException(
					accessor.getMessage(
						"AOServer.DrbdReport.ParseException.noSlashInDiskStates",
						ds
					),
					lineNum
				);
				localDiskState = DrbdReport.DiskState.valueOf(ds.substring(0, dsSlashPos));
				remoteDiskState = DrbdReport.DiskState.valueOf(ds.substring(dsSlashPos+1));
			}

			// Roles
			String state = values.get(4);
			final DrbdReport.Role localRole;
			final DrbdReport.Role remoteRole;
			if("null".equals(state)) {
				localRole = null;
				remoteRole = null;
			} else if(DrbdReport.Role.Unconfigured.name().equals(state)) {
				localRole = DrbdReport.Role.Unconfigured;
				remoteRole = DrbdReport.Role.Unconfigured;
			} else {
				int slashPos = state.indexOf('/');
				if(slashPos==-1) throw new ParseException(
					accessor.getMessage(
						"AOServer.DrbdReport.ParseException.noSlashInState",
						state
					),
					lineNum
				);
				localRole = DrbdReport.Role.valueOf(state.substring(0, slashPos));
				remoteRole = DrbdReport.Role.valueOf(state.substring(slashPos+1));
			}

			// Last Verified
			String lastVerifiedString = values.get(5);
			Long lastVerified =
				"null".equals(lastVerifiedString)
				? null
				: (Long.parseLong(lastVerifiedString)*1000)
			;

			// Out of Sync
			String outOfSyncString = values.get(6);
			Long outOfSync =
				"null".equals(outOfSyncString)
				? null
				: Long.parseLong(outOfSyncString)
			;

			reports.add(
				new DrbdReport(
					device,
					domUHostname,
					domUDevice,
					connectionState,
					localDiskState,
					remoteDiskState,
					localRole,
					remoteRole,
					lastVerified,
					outOfSync
				)
			);
		}
		return reports;
	}

	public static class LvmReport {

		private static boolean overlaps(long start1, long size1, long start2, long size2) {
			return
				(start2+size2)>start1
				&& (start1+size1)>start2
			;
		}

		public static class VolumeGroup implements Comparable<VolumeGroup> {

			/**
			 * Parses the output of vgs --noheadings --separator=$'\t' --units=b -o vg_name,vg_extent_size,vg_extent_count,vg_free_count,pv_count,lv_count
			 */
			private static Map<String,VolumeGroup> parseVgsReport(String vgs) throws ParseException {
				List<String> lines = StringUtility.splitLines(vgs);
				int size = lines.size();
				Map<String,VolumeGroup> volumeGroups = new HashMap<>(size*4/3+1);
				for(int c=0;c<size;c++) {
					final int lineNum = c+1;
					String line = lines.get(c);
					List<String> fields = StringUtility.splitString(line, '\t');
					if(fields.size()!=6) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.VolumeGroup.parseVgsReport.badColumnCount",
							6,
							fields.size()
						),
						lineNum
					);
					String vgExtentSize = fields.get(1).trim();
					if(!vgExtentSize.endsWith("B")) {
						throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.VolumeGroup.parseVgsReport.invalidateVgExtentSize",
								vgExtentSize
							),
							lineNum
						);
					}
					vgExtentSize = vgExtentSize.substring(0, vgExtentSize.length()-1);
					String vgName = fields.get(0).trim();
					if(
						volumeGroups.put(
							vgName,
							new VolumeGroup(
								vgName,
								Integer.parseInt(vgExtentSize),
								Long.parseLong(fields.get(2).trim()),
								Long.parseLong(fields.get(3).trim()),
								Integer.parseInt(fields.get(4).trim()),
								Integer.parseInt(fields.get(5).trim())
							)
						)!=null
					) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.VolumeGroup.parseVgsReport.vgNameFoundTwice",
							vgName
						),
						lineNum
					);
				}
				return Collections.unmodifiableMap(volumeGroups);
			}

			private final String vgName;
			private final int vgExtentSize;
			private final long vgExtentCount;
			private final long vgFreeCount;
			private final int pvCount;
			private final int lvCount;
			private final Map<String,LogicalVolume> logicalVolumes = new HashMap<>();
			private final Map<String,LogicalVolume> unmodifiableLogicalVolumes = Collections.unmodifiableMap(logicalVolumes);

			private VolumeGroup(String vgName, int vgExtentSize, long vgExtentCount, long vgFreeCount, int pvCount, int lvCount) {
				this.vgName = vgName;
				this.vgExtentSize = vgExtentSize;
				this.vgExtentCount = vgExtentCount;
				this.vgFreeCount = vgFreeCount;
				this.pvCount = pvCount;
				this.lvCount = lvCount;
			}

			@Override
			public String toString() {
				return vgName;
			}

			/**
			 * Sorts ascending by:
			 * <ol>
			 *   <li>vgName</li>
			 * </ol>
			 */
			@Override
			public int compareTo(VolumeGroup other) {
				return vgName.compareTo(other.vgName);
			}

			public int getLvCount() {
				return lvCount;
			}

			public int getPvCount() {
				return pvCount;
			}

			public long getVgExtentCount() {
				return vgExtentCount;
			}

			public int getVgExtentSize() {
				return vgExtentSize;
			}

			public long getVgFreeCount() {
				return vgFreeCount;
			}

			public String getVgName() {
				return vgName;
			}

			public LogicalVolume getLogicalVolume(String lvName) {
				return logicalVolumes.get(lvName);
			}

			public Map<String,LogicalVolume> getLogicalVolumes() {
				return unmodifiableLogicalVolumes;
			}
		}

		public static class PhysicalVolume implements Comparable<PhysicalVolume> {

			/**
			 * Parses the output of pvs --noheadings --separator=$'\t' --units=b -o pv_name,pv_pe_count,pv_pe_alloc_count,pv_size,vg_name
			 */
			private static Map<String,PhysicalVolume> parsePvsReport(String pvs, Map<String,VolumeGroup> volumeGroups) throws ParseException {
				List<String> lines = StringUtility.splitLines(pvs);
				int size = lines.size();
				Map<String,PhysicalVolume> physicalVolumes = new HashMap<>(size*4/3+1);
				Map<String,Integer> vgPhysicalVolumeCounts = new HashMap<>(volumeGroups.size()*4/3+1);
				Map<String,Long> vgExtentCountTotals = new HashMap<>(volumeGroups.size()*4/3+1);
				Map<String,Long> vgAllocCountTotals = new HashMap<>(volumeGroups.size()*4/3+1);
				for(int c=0;c<size;c++) {
					final int lineNum = c+1;
					String line = lines.get(c);
					List<String> fields = StringUtility.splitString(line, '\t');
					if(fields.size()!=5) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.PhysicalVolume.parsePvsReport.badColumnCount",
							5,
							fields.size()
						),
						lineNum
					);
					String pvName = fields.get(0).trim();
					String vgName = fields.get(4).trim();
					long pvPeCount = Long.parseLong(fields.get(1).trim());
					long pvPeAllocCount = Long.parseLong(fields.get(2).trim());
					String pvSizeString = fields.get(3).trim();
					if(pvSizeString.endsWith("B")) pvSizeString = pvSizeString.substring(0, pvSizeString.length()-1);
					long pvSize = Long.parseLong(pvSizeString);
					VolumeGroup volumeGroup;
					if(vgName.length()==0) {
						if(pvPeCount!=0 || pvPeAllocCount!=0) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.PhysicalVolume.parsePvsReport.invalidValues",
								pvPeCount,
								pvPeAllocCount,
								vgName
							),
							lineNum
						);
						volumeGroup = null;
					} else {
						if(pvPeCount<1 && pvPeAllocCount<0 && pvPeAllocCount>pvPeCount) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.PhysicalVolume.parsePvsReport.invalidValues",
								pvPeCount,
								pvPeAllocCount,
								vgName
							),
							lineNum
						);
						volumeGroup = volumeGroups.get(vgName);
						if(volumeGroup==null) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.PhysicalVolume.parsePvsReport.volumeGroupNotFound",
								vgName
							),
							lineNum
						);
						// Add to totals for consistency checks
						Integer count = vgPhysicalVolumeCounts.get(vgName);
						vgPhysicalVolumeCounts.put(
							vgName,
							count==null ? 1 : (count+1)
						);
						Long vgExtentCountTotal = vgExtentCountTotals.get(vgName);
						vgExtentCountTotals.put(
							vgName,
							vgExtentCountTotal==null ? pvPeCount : (vgExtentCountTotal+pvPeCount)
						);
						Long vgFreeCountTotal = vgAllocCountTotals.get(vgName);
						vgAllocCountTotals.put(
							vgName,
							vgFreeCountTotal==null ? pvPeAllocCount : (vgFreeCountTotal+pvPeAllocCount)
						);
					}
					if(
						physicalVolumes.put(
							pvName,
							new PhysicalVolume(
								pvName,
								pvPeCount,
								pvPeAllocCount,
								pvSize,
								volumeGroup
							)
						)!=null
					) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.PhysicalVolume.parsePvsReport.pvNameFoundTwice",
							pvName
						),
						lineNum
					);
				}
				for(Map.Entry<String,VolumeGroup> entry : volumeGroups.entrySet()) {
					// Make sure counts match vgs report
					VolumeGroup volumeGroup = entry.getValue();
					String vgName = entry.getKey();
					// Check pvCount
					int expectedPvCount = volumeGroup.getPvCount();
					Integer actualPvCountI = vgPhysicalVolumeCounts.get(vgName);
					int actualPvCount = actualPvCountI==null ? 0 : actualPvCountI;
					if(expectedPvCount!=actualPvCount) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.PhysicalVolume.parsePvsReport.mismatchPvCount",
							vgName
						),
						0
					);
					// Check vgExtentCount
					long expectedVgExtentCount = volumeGroup.getVgExtentCount();
					Long actualVgExtentCountL = vgExtentCountTotals.get(vgName);
					long actualVgExtentCount = actualVgExtentCountL==null ? 0 : actualVgExtentCountL;
					if(expectedVgExtentCount!=actualVgExtentCount) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.PhysicalVolume.parsePvsReport.badVgExtentCount",
							vgName
						),
						0
					);
					// Check vgFreeCount
					long expectedVgFreeCount = volumeGroup.getVgFreeCount();
					Long vgAllocCountTotalL = vgAllocCountTotals.get(vgName);
					long actualVgFreeCount = vgAllocCountTotalL==null ? expectedVgExtentCount : (expectedVgExtentCount-vgAllocCountTotalL);
					if(expectedVgFreeCount!=actualVgFreeCount) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.PhysicalVolume.parsePvsReport.badVgFreeCount",
							vgName
						),
						0
					);
				}
				return Collections.unmodifiableMap(physicalVolumes);
			}

			private final String pvName;
			private final long pvPeCount;
			private final long pvPeAllocCount;
			private final long pvSize;
			private final VolumeGroup volumeGroup;

			private PhysicalVolume(String pvName, long pvPeCount, long pvPeAllocCount, long pvSize, VolumeGroup volumeGroup) {
				this.pvName = pvName;
				this.pvPeCount = pvPeCount;
				this.pvPeAllocCount = pvPeAllocCount;
				this.pvSize = pvSize;
				this.volumeGroup = volumeGroup;
			}

			@Override
			public String toString() {
				return pvName;
			}

			/**
			 * Sorts ascending by:
			 * <ol>
			 *   <li>pvName</li>
			 * </ol>
			 */
			@Override
			public int compareTo(PhysicalVolume other) {
				return pvName.compareTo(other.pvName);
			}

			public String getPvName() {
				return pvName;
			}

			/**
			 * The number of extents allocated, this is 0 when not allocated.
			 */
			public long getPvPeAllocCount() {
				return pvPeAllocCount;
			}

			/**
			 * The total number of extents, this is 0 when not allocated.
			 */
			public long getPvPeCount() {
				return pvPeCount;
			}

			/**
			 * The size of the physical volume in bytes.  This is always available,
			 * even when not allocated.
			 */
			public long getPvSize() {
				return pvSize;
			}

			public VolumeGroup getVolumeGroup() {
				return volumeGroup;
			}
		}

		public static class LogicalVolume implements Comparable<LogicalVolume> {

			/**
			 * Parses the output from lvs --noheadings --separator=$'\t' -o vg_name,lv_name,seg_count,segtype,stripes,seg_start_pe,seg_pe_ranges
			 */
			private static void parseLvsReport(String lvs, Map<String,VolumeGroup> volumeGroups, Map<String,PhysicalVolume> physicalVolumes) throws ParseException {
				final List<String> lines = StringUtility.splitLines(lvs);
				final int size = lines.size();
				for(int c=0;c<size;c++) {
					final int lineNum = c+1;
					final String line = lines.get(c);
					final List<String> fields = StringUtility.splitString(line, '\t');
					if(fields.size()!=7) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.LogicalVolume.parseLsvReport.badColumnCount",
							7,
							fields.size()
						),
						lineNum
					);
					final String vgName = fields.get(0).trim();
					final String lvName = fields.get(1).trim();
					final int segCount = Integer.parseInt(fields.get(2).trim());
					final SegmentType segType = SegmentType.valueOf(fields.get(3).trim());
					final int stripeCount = Integer.parseInt(fields.get(4).trim());
					final long segStartPe = Long.parseLong(fields.get(5).trim());
					final List<String> segPeRanges = StringUtility.splitString(fields.get(6).trim(), ' ');

					// Find the volume group
					VolumeGroup volumeGroup = volumeGroups.get(vgName);
					if(volumeGroup==null) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.LogicalVolume.parseLsvReport.volumeGroupNotFound",
							vgName
						),
						lineNum
					);

					// Find or add the logical volume
					if(segCount<1) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.LogicalVolume.parseLsvReport.badSegCount",
							segCount
						),
						lineNum
					);
					LogicalVolume logicalVolume = volumeGroup.getLogicalVolume(lvName);
					if(logicalVolume==null) {
						logicalVolume = new LogicalVolume(volumeGroup, lvName, segCount);
						volumeGroup.logicalVolumes.put(lvName, logicalVolume);
					} else {
						if(segCount!=logicalVolume.segCount) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.LogicalVolume.parseLsvReport.segCountChanged",
								logicalVolume.segCount,
								segCount
							),
							lineNum
						);
					}

					// Add the segment
					if(stripeCount<1) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.LogicalVolume.parseLsvReport.badStripeCount",
							stripeCount
						),
						lineNum
					);
					if(segPeRanges.size()!=stripeCount) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.LogicalVolume.parseLsvReport.mismatchStripeCount"
						),
						lineNum
					);
					Segment newSegment = new Segment(logicalVolume, segType, stripeCount, segStartPe);
					// Check no overlap in segments
					for(Segment existingSegment : logicalVolume.segments) {
						if(newSegment.overlaps(existingSegment)) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.LogicalVolume.parseLsvReport.segmentOverlap",
								existingSegment,
								newSegment
							),
							lineNum
						);
					}
					logicalVolume.segments.add(newSegment);

					// Add the stripes
					for(String segPeRange : segPeRanges) {
						int colonPos = segPeRange.indexOf(':');
						if(colonPos==-1) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.LogicalVolume.parseLsvReport.segPeRangeNoColon",
								segPeRange
							),
							lineNum
						);
						int dashPos = segPeRange.indexOf('-', colonPos+1);
						if(dashPos==-1) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.LogicalVolume.parseLsvReport.segPeRangeNoDash",
								segPeRange
							),
							lineNum
						);
						String stripeDevice = segPeRange.substring(0, colonPos).trim();
						PhysicalVolume stripePv = physicalVolumes.get(stripeDevice);
						if(stripePv==null) throw new ParseException(
							accessor.getMessage(
								"AOServer.LvmReport.LogicalVolume.parseLsvReport.physicalVolumeNotFound",
								stripeDevice
							),
							lineNum
						);
						long firstPe = Long.parseLong(segPeRange.substring(colonPos+1, dashPos).trim());
						if(firstPe<0) throw new AssertionError("firstPe<0: "+firstPe);
						long lastPe = Long.parseLong(segPeRange.substring(dashPos+1).trim());
						if(lastPe<firstPe) throw new AssertionError("lastPe<firstPe: "+lastPe+"<"+firstPe);
						// Make sure no overlap with other stripes in the same physical volume
						Stripe newStripe = new Stripe(newSegment, stripePv, firstPe, lastPe);
						for(VolumeGroup existingVG : volumeGroups.values()) {
							for(LogicalVolume existingLV : existingVG.logicalVolumes.values()) {
								for(Segment existingSegment : existingLV.segments) {
									for(Stripe existingStripe : existingSegment.stripes) {
										if(newStripe.overlaps(existingStripe)) throw new ParseException(
											accessor.getMessage(
												"AOServer.LvmReport.LogicalVolume.parseLsvReport.stripeOverlap",
												existingStripe,
												newStripe
											),
											lineNum
										);
									}
								}
							}
						}
						newSegment.stripes.add(newStripe);
					}
					Collections.sort(newSegment.stripes);
				}

				// Final cleaning and sanity checks
				for(VolumeGroup volumeGroup : volumeGroups.values()) {
					// Make sure counts match vgs report
					int expectedLvCount = volumeGroup.getLvCount();
					int actualLvCount = volumeGroup.logicalVolumes.size();
					if(expectedLvCount!=actualLvCount) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.LogicalVolume.parseLsvReport.mismatchLvCount",
							volumeGroup
						),
						0
					);

					// Check vgExtentCount and vgFreeCount matches total in logicalVolumes
					long totalLvExtents = 0;
					for(LogicalVolume lv : volumeGroup.logicalVolumes.values()) {
						for(Segment segment : lv.segments) {
							for(Stripe stripe : segment.stripes) {
								totalLvExtents += stripe.getLastPe()-stripe.getFirstPe()+1;
							}
						}
					}
					long expectedFreeCount = volumeGroup.vgFreeCount;
					long actualFreeCount = volumeGroup.vgExtentCount - totalLvExtents;
					if(expectedFreeCount!=actualFreeCount) throw new ParseException(
						accessor.getMessage(
							"AOServer.LvmReport.LogicalVolume.parseLsvReport.mismatchFreeCount",
							volumeGroup
						),
						0
					);

					// Sort segments by segStartPe
					for(LogicalVolume logicalVolume : volumeGroup.logicalVolumes.values()) {
						Collections.sort(logicalVolume.segments);
					}
				}
			}

			private final VolumeGroup volumeGroup;
			private final String lvName;
			private final int segCount;
			private final List<Segment> segments = new ArrayList<>();
			private final List<Segment> unmodifiableSegments = Collections.unmodifiableList(segments);

			private LogicalVolume(VolumeGroup volumeGroup, String lvName, int segCount) {
				this.volumeGroup = volumeGroup;
				this.lvName = lvName;
				this.segCount = segCount;
			}

			@Override
			public String toString() {
				return volumeGroup+"/"+lvName;
			}

			/**
			 * Sorts ascending by:
			 * <ol>
			 *   <li>volumeGroup</li>
			 *   <li>lvName</li>
			 * </ol>
			 */
			@Override
			public int compareTo(LogicalVolume other) {
				int diff = volumeGroup.compareTo(other.volumeGroup);
				if(diff!=0) return diff;
				return lvName.compareTo(other.lvName);
			}

			public VolumeGroup getVolumeGroup() {
				return volumeGroup;
			}

			public String getLvName() {
				return lvName;
			}

			public int getSegCount() {
				return segCount;
			}

			public List<Segment> getSegments() {
				return unmodifiableSegments;
			}
		}

		public enum SegmentType {
			linear,
			striped
		}

		public static class Segment implements Comparable<Segment> {

			private final LogicalVolume logicalVolume;
			private final SegmentType segtype;
			private final int stripeCount;
			private final long segStartPe;
			private final List<Stripe> stripes = new ArrayList<>();
			private final List<Stripe> unmodifiableStripes = Collections.unmodifiableList(stripes);

			private Segment(LogicalVolume logicalVolume, SegmentType segtype, int stripeCount, long segStartPe) {
				this.logicalVolume = logicalVolume;
				this.segtype = segtype;
				this.stripeCount = stripeCount;
				this.segStartPe = segStartPe;
			}

			@Override
			public String toString() {
				return logicalVolume+"("+segStartPe+"-"+getSegEndPe()+")";
			}

			/**
			 * Sorts ascending by:
			 * <ol>
			 *   <li>logicalVolume</li>
			 *   <li>segStartPe</li>
			 * </ol>
			 */
			@Override
			public int compareTo(Segment other) {
				int diff = logicalVolume.compareTo(other.logicalVolume);
				if(diff!=0) return diff;
				if(segStartPe<other.segStartPe) return -1;
				if(segStartPe>other.segStartPe) return 1;
				return 0;
			}

			public LogicalVolume getLogicalVolume() {
				return logicalVolume;
			}

			public SegmentType getSegtype() {
				return segtype;
			}

			public int getStripeCount() {
				return stripeCount;
			}

			public long getSegStartPe() {
				return segStartPe;
			}

			/**
			 * Gets the last logical physical extent as determined by counting
			 * the total size of the stripes and using the following function:
			 * <pre>segStartPe + totalStripePE - 1</pre>
			 */
			public long getSegEndPe() {
				long segmentCount = 0;
				for(Stripe stripe : stripes) segmentCount += stripe.getLastPe()-stripe.getFirstPe()+1;
				return segStartPe+segmentCount-1;
			}

			public List<Stripe> getStripes() {
				return unmodifiableStripes;
			}

			public boolean overlaps(Segment other) {
				// Doesn't overlap self
				return
					this!=other
					&& logicalVolume==other.logicalVolume
					&& LvmReport.overlaps(
						segStartPe,
						getSegEndPe()-segStartPe+1,
						other.segStartPe,
						other.getSegEndPe()-other.segStartPe+1
					)
				;
			}
		}

		public static class Stripe implements Comparable<Stripe> {

			private final Segment segment;
			private final PhysicalVolume physicalVolume;
			private final long firstPe;
			private final long lastPe;

			private Stripe(Segment segment, PhysicalVolume physicalVolume, long firstPe, long lastPe) {
				this.segment = segment;
				this.physicalVolume = physicalVolume;
				this.firstPe = firstPe;
				this.lastPe = lastPe;
			}

			@Override
			public String toString() {
				return segment+":"+physicalVolume+"("+firstPe+"-"+lastPe+")";
			}

			/**
			 * Sorts ascending by:
			 * <ol>
			 *   <li>segment</li>
			 *   <li>firstPe</li>
			 * </ol>
			 */
			@Override
			public int compareTo(Stripe other) {
				int diff = segment.compareTo(other.segment);
				if(diff!=0) return diff;
				if(firstPe<other.firstPe) return -1;
				if(firstPe>other.firstPe) return 1;
				return 0;
			}

			public Segment getSegment() {
				return segment;
			}

			public PhysicalVolume getPhysicalVolume() {
				return physicalVolume;
			}

			public long getFirstPe() {
				return firstPe;
			}

			public long getLastPe() {
				return lastPe;
			}

			public boolean overlaps(Stripe other) {
				// Doesn't overlap self
				return
					this!=other
					&& physicalVolume==other.physicalVolume
					&& LvmReport.overlaps(
						firstPe,
						lastPe-firstPe+1,
						other.firstPe,
						other.lastPe-other.firstPe+1
					)
				;
			}
		}

		private final Map<String,VolumeGroup> volumeGroups;
		private final Map<String,PhysicalVolume> physicalVolumes;

		private LvmReport(String vgs, String pvs, String lvs) throws ParseException {
			this.volumeGroups = VolumeGroup.parseVgsReport(vgs);
			this.physicalVolumes = PhysicalVolume.parsePvsReport(pvs, volumeGroups);
			LogicalVolume.parseLvsReport(lvs, volumeGroups, physicalVolumes);
		}

		public PhysicalVolume getPhysicalVolume(String pvName) {
			return physicalVolumes.get(pvName);
		}

		public Map<String, PhysicalVolume> getPhysicalVolumes() {
			return physicalVolumes;
		}

		public VolumeGroup getVolumeGroup(String vgName) {
			return volumeGroups.get(vgName);
		}

		public Map<String, VolumeGroup> getVolumeGroups() {
			return volumeGroups;
		}
	}

	/**
	 * Gets the LVM report.
	 */
	public LvmReport getLvmReport() throws IOException, SQLException, ParseException {
		try {
			return table.connector.requestResult(
				true,
				AOServProtocol.CommandID.GET_AO_SERVER_LVM_REPORT,
				new AOServConnector.ResultRequest<LvmReport>() {
					String vgs;
					String pvs;
					String lvs;
					@Override
					public void writeRequest(CompressedDataOutputStream out) throws IOException {
						out.writeCompressedInt(pkey);
					}
					@Override
					public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
						int code=in.readByte();
						if(code==AOServProtocol.DONE) {
							vgs = in.readUTF();
							pvs = in.readUTF();
							lvs = in.readUTF();
						} else {
							AOServProtocol.checkResult(code, in);
							throw new IOException("Unexpected response code: "+code);
						}
					}
					@Override
					public LvmReport afterRelease() {
						try {
							return new LvmReport(vgs, pvs, lvs);
						} catch(ParseException err) {
							throw new WrappedException(err);
						}
					}
				}
			);
		} catch(WrappedException err) {
			Throwable cause = err.getCause();
			if(cause instanceof ParseException) throw (ParseException)cause;
			throw err;
		}
	}

	/**
	 * Gets the hard drive temperature report.
	 */
	public String getHddTempReport() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_HDD_TEMP_REPORT, pkey);
	}

	/**
	 * Gets the model of each hard drive on the server.  The key
	 * is the device name and the value is the model name.
	 */
	public Map<String,String> getHddModelReport() throws IOException, SQLException, ParseException {
		String report = table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_HDD_MODEL_REPORT, pkey);
		List<String> lines = StringUtility.splitLines(report);
		int lineNum = 0;
		Map<String,String> results = new HashMap<>(lines.size()*4/3+1);
		for(String line : lines) {
			lineNum++;
			int colonPos = line.indexOf(':');
			if(colonPos==-1) throw new ParseException(
				accessor.getMessage(
					"AOServer.getHddModelReport.ParseException.noColon",
					line
				),
				lineNum
			);
			String device = line.substring(0, colonPos).trim();
			String model = line.substring(colonPos+1).trim();
			if(results.put(device, model)!=null) throw new ParseException(
				accessor.getMessage(
					"AOServer.getHddModelReport.ParseException.duplicateDevice",
					device
				),
				lineNum
			);
		}
		return results;
	}

	/**
	 * Gets the filesystem states report.
	 * 
	 * @Deprecated  Use {@code getFilesystemsCsvReport()} instead to let the API parse the report.
	 */
	public String getFilesystemsCsvReport() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_FILESYSTEMS_CSV_REPORT, pkey);
	}

	public static class FilesystemReport {

		private final String mountPoint;
		private final String device;
		private final long bytes;
		private final long used;
		private final long free;
		private final byte use;
		private final Long inodes;
		private final Long inodesUsed;
		private final Long inodesFree;
		private final Byte inodeUse;
		private final String fsType;
		private final String mountOptions;
		private final String extState;
		private final String extMaxMount;
		private final String extCheckInterval;

		private FilesystemReport(
			String mountPoint,
			String device,
			long bytes,
			long used,
			long free,
			byte use,
			Long inodes,
			Long inodesUsed,
			Long inodesFree,
			Byte inodeUse,
			String fsType,
			String mountOptions,
			String extState,
			String extMaxMount,
			String extCheckInterval
		) {
			this.mountPoint = mountPoint;
			this.device = device;
			this.bytes = bytes;
			this.used = used;
			this.free = free;
			this.use = use;
			this.inodes = inodes;
			this.inodesUsed = inodesUsed;
			this.inodesFree = inodesFree;
			this.inodeUse = inodeUse;
			this.fsType = fsType;
			this.mountOptions = mountOptions;
			this.extState = extState;
			this.extMaxMount = extMaxMount;
			this.extCheckInterval = extCheckInterval;
		}

		public String getMountPoint() {
			return mountPoint;
		}

		public String getDevice() {
			return device;
		}

		public long getBytes() {
			return bytes;
		}

		public long getUsed() {
			return used;
		}

		public long getFree() {
			return free;
		}

		public byte getUse() {
			return use;
		}

		public Long getInodes() {
			return inodes;
		}

		public Long getInodesUsed() {
			return inodesUsed;
		}

		public Long getInodesFree() {
			return inodesFree;
		}

		public Byte getInodeUse() {
			return inodeUse;
		}

		public String getFsType() {
			return fsType;
		}

		public String getMountOptions() {
			return mountOptions;
		}

		public String getExtState() {
			return extState;
		}

		public String getExtMaxMount() {
			return extMaxMount;
		}

		public String getExtCheckInterval() {
			return extCheckInterval;
		}

		/**
		 * Checks that this filesystem matches the expected configuration for an AOServer.
		 * 
		 * @return  the message describing the configuration warning or {@code null} if all configs OK.
		 */
		public String getConfigMessage() {
			switch (fsType) {
				case "ext3":
					// Make sure extmaxmount is -1
					if(!"-1".equals(extMaxMount)) {
						return accessor.getMessage("AOServer.FilesystemReport.configMessage.extmaxmount.ext3", extMaxMount);
					}
					// Make sure extchkint is 0
					if(!"0 (<none>)".equals(extCheckInterval)) {
						return accessor.getMessage("AOServer.FilesystemReport.configMessage.extchkint.ext3", extCheckInterval);
					}
					return null;
				case "ext2":
					// Make sure extmaxmount is never -1
					if("-1".equals(extMaxMount)) {
						return accessor.getMessage("AOServer.FilesystemReport.configMessage.extmaxmount.ext2", extMaxMount);
					}
					// Make sure extchkint is never 0
					if("0 (<none>)".equals(extCheckInterval)) {
						return accessor.getMessage("AOServer.FilesystemReport.configMessage.extchkint.ext2", extCheckInterval);
					}
					return null;
				default:
					// No specific expectations for other types of filesystems
					return null;
			}
		}

		/**
		 * Checks that this filesystem is in a clean state and does not require any corrective action.
		 */
		public boolean isClean() {
			switch (fsType) {
				case "ext3":
					return "clean".equals(extState);
				case "ext2":
					return
						"not clean".equals(extState) // Normal state when mounted
						|| "clean".equals(extState)
					;
				default:
					// Other types of filesystems are assumed to be clean until we have more information
					return true;
			}
		}
	}

	private static Byte parsePercent(String value) throws NumberFormatException {
		if(value.isEmpty()) return null;
		if(!value.endsWith("%")) throw new NumberFormatException("Percentage does not end with '%': " + value);
		return Byte.parseByte(value.substring(0, value.length()-1));
	}

	private static Long parseLong(String value) throws NumberFormatException {
		if(value.isEmpty()) return null;
		return Long.parseLong(value);
	}

	public Map<String,FilesystemReport> getFilesystemsReport() throws IOException, SQLException {
		Map<String,FilesystemReport> reports = new LinkedHashMap<>();
		// Extremely simple CSV parser, but sufficient for the known format of the source data
		List<String> lines = StringUtility.splitLines(getFilesystemsCsvReport());
		if(lines.isEmpty()) throw new IOException("No lines from report");
		for(int i=0, numLines=lines.size(); i<numLines; i++) {
			String line = lines.get(i);
			List<String> columns = StringUtility.splitString(line, "\",\"");
			if(columns.size() != 15) throw new IOException("Line does not have 15 columns: " + columns.size());
			String mountPoint = columns.get(0);
			if(!mountPoint.startsWith("\"")) throw new AssertionError();
			mountPoint = mountPoint.substring(1);
			String extchkint = columns.get(14);
			if(!extchkint.endsWith("\"")) throw new AssertionError();
			extchkint = extchkint.substring(0, extchkint.length() - 1);
			if(i == 0) {
				if(
					!"mountpoint".equals(mountPoint)
					|| !"device".equals(columns.get(1))
					|| !"bytes".equals(columns.get(2))
					|| !"used".equals(columns.get(3))
					|| !"free".equals(columns.get(4))
					|| !"use".equals(columns.get(5))
					|| !"inodes".equals(columns.get(6))
					|| !"iused".equals(columns.get(7))
					|| !"ifree".equals(columns.get(8))
					|| !"iuse".equals(columns.get(9))
					|| !"fstype".equals(columns.get(10))
					|| !"mountoptions".equals(columns.get(11))
					|| !"extstate".equals(columns.get(12))
					|| !"extmaxmount".equals(columns.get(13))
					|| !"extchkint".equals(extchkint)
				) throw new IOException("First line is not the expected column labels");
			} else {
				if(
					reports.put(
						mountPoint,
						new FilesystemReport(
							mountPoint,
							columns.get(1), // device
							Long.parseLong(columns.get(2)), // bytes
							Long.parseLong(columns.get(3)), // used
							Long.parseLong(columns.get(4)), // free
							parsePercent(columns.get(5)), // use
							parseLong(columns.get(6)), // inodes
							parseLong(columns.get(7)), // inodesUsed
							parseLong(columns.get(8)), // inodesFree
							parsePercent(columns.get(9)), // inodeUse
							columns.get(10), // fsType
							columns.get(11), // mountOptions
							columns.get(12), // extState
							columns.get(13), // extMaxMount
							extchkint // extCheckInterval
						)
					) != null
				) throw new IOException("Duplicate mount point: " + mountPoint);
			}
		}
		return AoCollections.optimalUnmodifiableMap(reports);
	}

	/**
	 * Gets the output of /proc/loadavg
	 */
	public String getLoadAvgReport() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_LOADAVG_REPORT, pkey);
	}

	/**
	 * Gets the output of /proc/meminfo
	 */
	public String getMemInfoReport() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_MEMINFO_REPORT, pkey);
	}

	/**
	 * Checks a port from the daemon's point of view.  This is required for monitoring of private and loopback IPs.
	 */
	public String checkPort(InetAddress ipAddress, Port port, String appProtocol, HttpParameters monitoringParameters) throws IOException, SQLException {
		return table.connector.requestStringQuery(
			true,
			AOServProtocol.CommandID.AO_SERVER_CHECK_PORT,
			pkey,
			ipAddress.toString(),
			port,
			appProtocol,
			NetBind.encodeParameters(monitoringParameters)
		);
	}

	/**
	 * Gets the current system time in milliseconds.
	 */
	public long getSystemTimeMillis() throws IOException, SQLException {
		return table.connector.requestLongQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_SYSTEM_TIME_MILLIS, pkey);
	}

	public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
		return table.connector.getFailoverMySQLReplications().getFailoverMySQLReplications(this);
	}

	/**
	 * Gets the status line of a SMTP server from the server from the provided source IP.
	 */
	public String checkSmtpBlacklist(InetAddress sourceIp, InetAddress connectIp) throws IOException, SQLException {
		return table.connector.requestStringQuery(false, AOServProtocol.CommandID.AO_SERVER_CHECK_SMTP_BLACKLIST, pkey, sourceIp, connectIp);
	}

	/**
	 * Gets UPS status report
	 */
	public String getUpsStatus() throws IOException, SQLException {
		return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_UPS_STATUS, pkey);
	}

	// <editor-fold defaultstate="collapsed" desc="DTO">
	@Override
	public com.aoindustries.aoserv.client.dto.AOServer getDto() {
		return new com.aoindustries.aoserv.client.dto.AOServer(
			getPkey(),
			getDto(hostname),
			daemon_bind==-1 ? null : daemon_bind,
			getDto(daemon_key),
			pool_size,
			distro_hour,
			last_distro_time==-1 ? null : last_distro_time,
			failover_server==-1 ? null : failover_server,
			daemon_device_id,
			daemon_connect_bind==-1 ? null : daemon_connect_bind,
			time_zone,
			jilter_bind==-1 ? null : jilter_bind,
			restrict_outbound_email,
			getDto(daemon_connect_address),
			failover_batch_size,
			Float.isNaN(monitoring_load_low) ? null : monitoring_load_low,
			Float.isNaN(monitoring_load_medium) ? null : monitoring_load_medium,
			Float.isNaN(monitoring_load_high) ? null : monitoring_load_high,
			Float.isNaN(monitoring_load_critical) ? null : monitoring_load_critical,
			getDto(uid_min),
			getDto(gid_min),
			sftp_umask==-1 ? null : sftp_umask
		);
	}
	// </editor-fold>
}
