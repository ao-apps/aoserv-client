package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An <code>AOServer</code> stores the details about a server that runs the AOServ distribution.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOServer extends CachedObjectIntegerKey<AOServer> {

    static final int COLUMN_SERVER=0;
    static final int COLUMN_HOSTNAME=1;
    static final String COLUMN_HOSTNAME_name = "hostname";

    private String hostname;
    int daemon_bind;
    private String daemon_key;
    private int pool_size;
    private int distro_hour;
    private long last_distro_time;
    int failover_server;
    private String daemon_device_id;
    int daemon_connect_bind;
    private String time_zone;
    int jilter_bind;
    private boolean restrict_outbound_email;
    private String daemon_connect_address;
    private int failover_batch_size;

    public int addCvsRepository(
        String path,
        LinuxServerAccount lsa,
        LinuxServerGroup lsg,
        long mode
    ) {
	return table.connector.cvsRepositories.addCvsRepository(
            this,
            path,
            lsa,
            lsg,
            mode
	);
    }

    public int addEmailDomain(String domain, Package packageObject) {
	return table.connector.emailDomains.addEmailDomain(domain, this, packageObject);
    }

    public int addEmailPipe(String path, Package packageObject) {
	return table.connector.emailPipes.addEmailPipe(this, path, packageObject);
    }

    public int addHttpdJBossSite(
	String siteName,
	Package packageObj,
	LinuxAccount siteUser,
	LinuxGroup siteGroup,
	String serverAdmin,
	boolean useApache,
	IPAddress ipAddress,
	String primaryHttpHostname,
	String[] altHttpHostnames,
	int jBossVersion,
	String contentSrc
    ) {
        return table.connector.httpdJBossSites.addHttpdJBossSite(
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
    ) {
        return table.connector.httpdSharedTomcats.addHttpdSharedTomcat(
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
        String serverAdmin,
        boolean useApache,
        IPAddress ipAddress,
        String primaryHttpHostname,
        String[] altHttpHostnames,
        String sharedTomcatName,
        HttpdTomcatVersion version,
        String contentSrc
    ) {
        return table.connector.httpdTomcatSharedSites.addHttpdTomcatSharedSite(
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
	String serverAdmin,
	boolean useApache,
	IPAddress ipAddress,
	String primaryHttpHostname,
	String[] altHttpHostnames,
	HttpdTomcatVersion tomcatVersion,
	String contentSrc
    ) {
        return table.connector.httpdTomcatStdSites.addHttpdTomcatStdSite(
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

    public List<AOServerDaemonHost> getAOServerDaemonHosts() {
	return table.connector.aoServerDaemonHosts.getAOServerDaemonHosts(this);
    }

    public List<BackupPartition> getBackupPartitions() {
        return table.connector.backupPartitions.getBackupPartitions(this);
    }

    public BackupPartition getBackupPartitionForPath(String path) {
        return table.connector.backupPartitions.getBackupPartitionForPath(this, path);
    }

    public List<BlackholeEmailAddress> getBlackholeEmailAddresses() {
	return table.connector.blackholeEmailAddresses.getBlackholeEmailAddresses(this);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER: return Integer.valueOf(pkey);
            case COLUMN_HOSTNAME: return hostname;
            case 2: return daemon_bind==-1?null:Integer.valueOf(daemon_bind);
            case 3: return daemon_key;
            case 4: return Integer.valueOf(pool_size);
            case 5: return Integer.valueOf(distro_hour);
            case 6: return last_distro_time==-1?null:new Date(last_distro_time);
            case 7: return failover_server==-1?null:Integer.valueOf(failover_server);
            case 8: return daemon_device_id;
            case 9: return daemon_connect_bind==-1?null:Integer.valueOf(daemon_connect_bind);
            case 10: return time_zone;
            case 11: return jilter_bind;
            case 12: return restrict_outbound_email;
            case 13: return daemon_connect_address;
            case 14: return failover_batch_size;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public CvsRepository getCvsRepository(String path) {
        return table.connector.cvsRepositories.getCvsRepository(this, path);
    }

    public List<CvsRepository> getCvsRepositories() {
        return table.connector.cvsRepositories.getCvsRepositories(this);
    }

    /**
     * Gets the unique hostname for this server.  Should be resolvable in DNS to ease maintenance.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Gets the port information to bind to.
     */
    public NetBind getDaemonBind() {
	if(daemon_bind==-1) return null;
        // May be filtered
        return table.connector.netBinds.get(daemon_bind);
    }

    /**
     * Gets the port information to connect to.
     */
    public NetBind getDaemonConnectBind() {
	if(daemon_connect_bind==-1) return null;
        // May be filtered
        return table.connector.netBinds.get(daemon_connect_bind);
    }
    
    public TimeZone getTimeZone() {
        TimeZone tz=table.connector.timeZones.get(time_zone);
        if(tz==null) throw new WrappedException(new SQLException("Unable to find TimeZone: "+time_zone));
        return tz;
    }

    public NetBind getJilterBind() {
	if(jilter_bind==-1) return null;
        // May be filtered
        return table.connector.netBinds.get(jilter_bind);
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
    public String getDaemonConnectAddress() {
        return daemon_connect_address;
    }
    
    /**
     * Gets the number of filesystem entries sent per batch during failover replications.
     */
    public int getFailoverBatchSize() {
        return failover_batch_size;
    }

    public NetDeviceID getDaemonDeviceID() {
        NetDeviceID ndi=table.connector.netDeviceIDs.get(daemon_device_id);
        if(ndi==null) throw new WrappedException(new SQLException("Unable to find NetDeviceID: "+daemon_device_id));
        return ndi;
    }

    public IPAddress getDaemonIPAddress() {
        NetBind nb=getDaemonBind();
        if(nb==null) throw new WrappedException(new SQLException("Unable to find daemon NetBind for AOServer: "+pkey));
        IPAddress ia=nb.getIPAddress();
        String ip=ia.getIPAddress();
        if(ip.equals(IPAddress.WILDCARD_IP)) {
            NetDeviceID ndi=getDaemonDeviceID();
            NetDevice nd=getServer().getNetDevice(ndi.getName());
            if(nd==null) throw new WrappedException(new SQLException("Unable to find NetDevice: "+ndi.getName()+" on "+pkey));
            ia=nd.getPrimaryIPAddress();
            if(ia==null) throw new WrappedException(new SQLException("Unable to find primary IPAddress: "+ndi.getName()+" on "+pkey));
        }
        return ia;
    }

    public String getDaemonKey() {
	return daemon_key;
    }

    public int getDistroHour() {
        return distro_hour;
    }

    public List<EmailAddress> getEmailAddresses() {
	return table.connector.emailAddresses.getEmailAddresses(this);
    }

    public EmailDomain getEmailDomain(String domain) {
        return table.connector.emailDomains.getEmailDomain(this, domain);
    }

    public List<EmailDomain> getEmailDomains() {
	return table.connector.emailDomains.getEmailDomains(this);
    }

    public List<EmailForwarding> getEmailForwarding() {
	return table.connector.emailForwardings.getEmailForwarding(this);
    }

    /**
     * Rename to getEmailList when all uses updated.
     */
    public EmailList getEmailList(String path) {
        return table.connector.emailLists.getEmailList(this, path);
    }

    public List<EmailListAddress> getEmailListAddresses() {
	return table.connector.emailListAddresses.getEmailListAddresses(this);
    }

    public List<EmailPipeAddress> getEmailPipeAddresses() {
	return table.connector.emailPipeAddresses.getEmailPipeAddresses(this);
    }

    public List<EmailPipe> getEmailPipes() {
	return table.connector.emailPipes.getEmailPipes(this);
    }

    public EmailSmtpRelay getEmailSmtpRelay(Package pk, String host) {
	return table.connector.emailSmtpRelays.getEmailSmtpRelay(pk, this, host);
    }

    /**
     * Gets all of the smtp relays settings that apply to either all servers or this server specifically.
     */
    public List<EmailSmtpRelay> getEmailSmtpRelays() {
	return table.connector.emailSmtpRelays.getEmailSmtpRelays(this);
    }

    public AOServer getFailoverServer() throws SQLException {
        if(failover_server==-1) return null;
        AOServer se=table.connector.aoServers.get(failover_server);
        if(se==null) new SQLException("Unable to find AOServer: "+failover_server);
        return se;
    }

    public List<FTPGuestUser> getFTPGuestUsers() {
	return table.connector.ftpGuestUsers.getFTPGuestUsers(this);
    }

    public List<HttpdServer> getHttpdServers() {
	return table.connector.httpdServers.getHttpdServers(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() {
	return table.connector.httpdSharedTomcats.getHttpdSharedTomcats(this);
    }

    public HttpdSharedTomcat getHttpdSharedTomcat(String jvmName) {
	return table.connector.httpdSharedTomcats.getHttpdSharedTomcat(jvmName, this);
    }

    public HttpdSite getHttpdSite(String siteName) {
	return table.connector.httpdSites.getHttpdSite(siteName, this);
    }

    public List<HttpdSite> getHttpdSites() {
	return table.connector.httpdSites.getHttpdSites(this);
    }

    public long getLastDistroTime() {
        return last_distro_time;
    }

    public List<LinuxAccAddress> getLinuxAccAddresses() {
	return table.connector.linuxAccAddresses.getLinuxAccAddresses(this);
    }

    public List<LinuxAccount> getLinuxAccounts() {
	List<LinuxServerAccount> lsa=getLinuxServerAccounts();
	int len=lsa.size();
	List<LinuxAccount> la=new ArrayList<LinuxAccount>(len);
	for(int c=0;c<len;c++) la.add(lsa.get(c).getLinuxAccount());
	return la;
    }

    public List<LinuxGroup> getLinuxGroups() {
	List<LinuxServerGroup> lsg=getLinuxServerGroups();
	int len=lsg.size();
	List<LinuxGroup> lg=new ArrayList<LinuxGroup>(len);
	for(int c=0;c<len;c++) lg.add(lsg.get(c).getLinuxGroup());
	return lg;
    }

    public LinuxServerAccount getLinuxServerAccount(String username) {
        return table.connector.linuxServerAccounts.getLinuxServerAccount(this, username);
    }

    public LinuxServerAccount getLinuxServerAccount(int uid) {
        return table.connector.linuxServerAccounts.getLinuxServerAccount(this, uid);
    }

    public List<LinuxServerAccount> getLinuxServerAccounts() {
	return table.connector.linuxServerAccounts.getLinuxServerAccounts(this);
    }

    public LinuxServerGroup getLinuxServerGroup(int gid) {
        return table.connector.linuxServerGroups.getLinuxServerGroup(this, gid);
    }

    public LinuxServerGroup getLinuxServerGroup(String groupName) {
        return table.connector.linuxServerGroups.getLinuxServerGroup(this, groupName);
    }

    public List<LinuxServerGroup> getLinuxServerGroups() {
	return table.connector.linuxServerGroups.getLinuxServerGroups(this);
    }

    public List<MajordomoServer> getMajordomoServers() {
        return table.connector.majordomoServers.getMajordomoServers(this);
    }

    private static final Map<Integer,Object> mrtgLocks = new HashMap<Integer,Object>();

    public void getMrtgFile(String filename, OutputStream out) {
        try {
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
                                table.connector.errorHandler.reportWarning(err, null);
                            }
                        }
                    }
                } while(mrtgLocks.containsKey(pkey));
                mrtgLocks.put(pkey, Boolean.TRUE);
                mrtgLocks.notifyAll();
            }

            try {
                AOServConnection connection=table.connector.getConnection();
                try {
                    CompressedDataOutputStream masterOut=connection.getOutputStream();
                    masterOut.writeCompressedInt(AOServProtocol.CommandID.GET_MRTG_FILE.ordinal());
                    masterOut.writeCompressedInt(pkey);
                    masterOut.writeUTF(filename);
                    masterOut.flush();

                    CompressedDataInputStream in=connection.getInputStream();
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
                        BufferManager.release(buff);
                    }
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    table.connector.releaseConnection(connection);
                }
            } finally {
                synchronized(mrtgLocks) {
                    mrtgLocks.remove(pkey);
                    mrtgLocks.notifyAll();
                }
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public MySQLServer getMySQLServer(String name) {
	return table.connector.mysqlServers.getMySQLServer(name, this);
    }

    public List<MySQLServer> getMySQLServers() {
	return table.connector.mysqlServers.getMySQLServers(this);
    }

    public MySQLServer getPreferredMySQLServer() {
        Profiler.startProfile(Profiler.UNKNOWN, AOServer.class, "getPreferredMySQLServer()", null);
        try {
            // Look for the most-preferred version that has an instance on the server
            List<MySQLServer> pss=getMySQLServers();
            String[] preferredVersions=MySQLServer.getPreferredVersions();
            for(int c=0;c<preferredVersions.length;c++) {
                String version=preferredVersions[c];
                for(int d=0;d<pss.size();d++) {
                    MySQLServer ps=pss.get(d);
                    if(ps.getVersion().getVersion().equals(version)) {
                        return ps;
                    }
                }
            }

            // Default to first available server if no preferred ones round
            return pss.isEmpty()?null:pss.get(0);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public List<AOServer> getNestedAOServers() {
        return table.connector.aoServers.getNestedAOServers(this);
    }

    public int getPoolSize() {
	return pool_size;
    }

    public PostgresServer getPostgresServer(String name) {
	return table.connector.postgresServers.getPostgresServer(name, this);
    }

    public List<PostgresServer> getPostgresServers() {
	return table.connector.postgresServers.getPostgresServers(this);
    }

    public PostgresServer getPreferredPostgresServer() {
        Profiler.startProfile(Profiler.UNKNOWN, AOServer.class, "getPreferredPostgresServer()", null);
        try {
            // Look for the most-preferred version that has an instance on the server
            List<PostgresServer> pss=getPostgresServers();
            String[] preferredMinorVersions=PostgresVersion.getPreferredMinorVersions();
            for(int c=0;c<preferredMinorVersions.length;c++) {
                String version=preferredMinorVersions[c];
                for(int d=0;d<pss.size();d++) {
                    PostgresServer ps=pss.get(d);
                    if(ps.getPostgresVersion().getMinorVersion().equals(version)) {
                        return ps;
                    }
                }
            }

            // Default to first available server if no preferred ones round
            return pss.isEmpty()?null:pss.get(0);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public IPAddress getPrimaryIPAddress() {
        NetDeviceID ndi=getDaemonDeviceID();
        String name=ndi.getName();
        NetDevice nd=getServer().getNetDevice(name);
        if(nd==null) throw new WrappedException(new SQLException("Unable to find NetDevice: "+name+" on "+pkey));
        return nd.getPrimaryIPAddress();
    }

    public PrivateFTPServer getPrivateFTPServer(String path) {
	return table.connector.privateFTPServers.getPrivateFTPServer(this, path);
    }

    public List<PrivateFTPServer> getPrivateFTPServers() {
	return table.connector.privateFTPServers.getPrivateFTPServers(this);
    }

    public Server getServer() {
        Server se=table.connector.servers.get(pkey);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+pkey));
        return se;
    }

    public List<SystemEmailAlias> getSystemEmailAliases() {
	return table.connector.systemEmailAliases.getSystemEmailAliases(this);
    }
    
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.AO_SERVERS;
    }

    public boolean isEmailDomainAvailable(String domain) {
        return table.connector.emailDomains.isEmailDomainAvailable(this, domain);
    }

    public boolean isHomeUsed(String directory) {
	return table.connector.linuxServerAccounts.isHomeUsed(this, directory);
    }

    public boolean isMySQLServerNameAvailable(String name) {
	return table.connector.mysqlServers.isMySQLServerNameAvailable(name, this);
    }

    public boolean isPostgresServerNameAvailable(String name) {
	return table.connector.postgresServers.isPostgresServerNameAvailable(name, this);
    }

    void initImpl(ResultSet result) throws SQLException {
        int pos = 1;
        pkey=result.getInt(pos++);
        hostname=result.getString(pos++);
	daemon_bind=result.getInt(pos++);
	if(result.wasNull()) daemon_bind=-1;
	daemon_key=result.getString(pos++);
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
        daemon_connect_address=result.getString(pos++);
        failover_batch_size=result.getInt(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        hostname=in.readUTF();
	daemon_bind=in.readCompressedInt();
	daemon_key=in.readUTF();
	pool_size=in.readCompressedInt();
        distro_hour=in.readCompressedInt();
        last_distro_time=in.readLong();
        failover_server=in.readCompressedInt();
        daemon_device_id=StringUtility.intern(in.readNullUTF());
        daemon_connect_bind=in.readCompressedInt();
        time_zone=in.readUTF().intern();
        jilter_bind=in.readCompressedInt();
        restrict_outbound_email=in.readBoolean();
        daemon_connect_address=StringUtility.intern(in.readNullUTF());
        failover_batch_size=in.readCompressedInt();
    }

    public void restartApache() {
        table.connector.requestUpdate(AOServProtocol.CommandID.RESTART_APACHE, pkey);
    }

    public void restartCron() {
        table.connector.requestUpdate(AOServProtocol.CommandID.RESTART_CRON, pkey);
    }

    public void restartXfs() {
        table.connector.requestUpdate(AOServProtocol.CommandID.RESTART_XFS, pkey);
    }

    public void restartXvfb() {
        table.connector.requestUpdate(AOServProtocol.CommandID.RESTART_XVFB, pkey);
    }

    public long requestDaemonAccess(int daemonCommandCode, int param1) {
        return table.connector.requestLongQuery(
            AOServProtocol.CommandID.REQUEST_DAEMON_ACCESS,
            pkey,
            daemonCommandCode,
            param1
        );
    }

    public void setLastDistroTime(long distroTime) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_LAST_DISTRO_TIME, pkey, distroTime);
    }

    public void startApache() {
        table.connector.requestUpdate(AOServProtocol.CommandID.START_APACHE, pkey);
    }

    public void startCron() {
        table.connector.requestUpdate(AOServProtocol.CommandID.START_CRON, pkey);
    }

    public void startDistro(boolean includeUser) {
        table.connector.distroFiles.startDistro(this, includeUser);
    }

    public void startXfs() {
        table.connector.requestUpdate(AOServProtocol.CommandID.START_XFS, pkey);
    }

    public void startXvfb() {
        table.connector.requestUpdate(AOServProtocol.CommandID.START_XVFB, pkey);
    }

    public void stopApache() {
        table.connector.requestUpdate(AOServProtocol.CommandID.STOP_APACHE, pkey);
    }

    public void stopCron() {
        table.connector.requestUpdate(AOServProtocol.CommandID.STOP_CRON, pkey);
    }

    public void stopXfs() {
        table.connector.requestUpdate(AOServProtocol.CommandID.STOP_XFS, pkey);
    }

    public void stopXvfb() {
        table.connector.requestUpdate(AOServProtocol.CommandID.STOP_XVFB, pkey);
    }

    protected String toStringImpl() {
        return hostname;
    }

    public void waitForHttpdSiteRebuild() {
	table.connector.httpdSites.waitForRebuild(this);
    }

    public void waitForLinuxAccountRebuild() {
	table.connector.linuxAccounts.waitForRebuild(this);
    }

    public void waitForMySQLDatabaseRebuild() {
	table.connector.mysqlDatabases.waitForRebuild(this);
    }

    public void waitForMySQLDBUserRebuild() {
	table.connector.mysqlDBUsers.waitForRebuild(this);
    }

    public void waitForMySQLServerRebuild() {
	table.connector.mysqlServers.waitForRebuild(this);
    }

    public void waitForMySQLUserRebuild() {
	table.connector.mysqlUsers.waitForRebuild(this);
    }

    public void waitForPostgresDatabaseRebuild() {
	table.connector.postgresDatabases.waitForRebuild(this);
    }

    public void waitForPostgresServerRebuild() {
	table.connector.postgresServers.waitForRebuild(this);
    }

    public void waitForPostgresUserRebuild() {
	table.connector.postgresUsers.waitForRebuild(this);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeCompressedInt(1);
            out.writeCompressedInt(2000);
            out.writeCompressedInt(1024);
            out.writeCompressedInt(2);
            out.writeCompressedInt(240);
            out.writeNullUTF(null);
            out.writeBoolean(false);
            out.writeBoolean(false);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_4)<0) out.writeBoolean(true);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeBoolean(false);
            out.writeUTF("AOServer #"+pkey);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_31)>=0) {
            out.writeUTF(hostname);
        }
	out.writeCompressedInt(daemon_bind);
	out.writeUTF(daemon_key);
	out.writeCompressedInt(pool_size);
        out.writeCompressedInt(distro_hour);
        out.writeLong(last_distro_time);
        out.writeCompressedInt(failover_server);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeCompressedInt(60*1000);
            out.writeCompressedInt(5*60*1000);
            out.writeBoolean(false);
        }
        out.writeNullUTF(daemon_device_id);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeNullUTF(null);
            out.writeCompressedInt(1200*100);
            out.writeBoolean(true);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)>=0) {
                out.writeNullUTF(null);
                out.writeNullUTF(null);
            } else if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_104)>=0) {
                out.writeUTF(AOServProtocol.FILTERED);
                out.writeUTF(AOServProtocol.FILTERED);
            }
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_119)>=0) out.writeCompressedInt(daemon_connect_bind);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_2)>=0) out.writeUTF(time_zone);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_7)>=0) out.writeCompressedInt(jilter_bind);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_8)>=0) out.writeBoolean(restrict_outbound_email);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_11)>=0) out.writeNullUTF(daemon_connect_address);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_12)>=0) out.writeCompressedInt(failover_batch_size);
    }

    /**
     * Gets the 3ware RAID report.
     */
    public String get3wareRaidReport() {
        return table.connector.requestStringQuery(AOServProtocol.CommandID.GET_AO_SERVER_3WARE_RAID_REPORT, pkey);
    }

    /**
     * Gets the MD RAID report.
     */
    public String getMdRaidReport() {
        return table.connector.requestStringQuery(AOServProtocol.CommandID.GET_AO_SERVER_MD_RAID_REPORT, pkey);
    }

    /**
     * Gets the DRBD report.
     */
    public String getDrbdReport() {
        return table.connector.requestStringQuery(AOServProtocol.CommandID.GET_AO_SERVER_DRBD_REPORT, pkey);
    }

}