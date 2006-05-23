package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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

    private int
        num_cpu,
        cpu_speed,
        ram,
        rack,
        disk
    ;

    private String wildcard_https;

    private boolean
        is_interbase,
        is_dns
    ;
    private boolean is_router;
    private String iptables_name;
    int daemon_bind;
    private String daemon_key;
    private int pool_size;
    private int distro_hour;
    private long last_distro_time;
    int failover_server;
    private int server_report_delay;
    private int server_report_interval;
    private boolean is_qmail;
    private String daemon_device_id;
    private String xeroscape_name;
    private int value;
    private boolean monitoring_enabled;
    private String emailmon_password;
    private String ftpmon_password;
    int daemon_connect_bind;
    private String time_zone;
    int jilter_bind;

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

    public int addNetBind(
        Package pk,
        IPAddress ia,
        NetPort netPort,
        NetProtocol netProtocol,
        Protocol appProtocol,
        boolean openFirewall,
        boolean monitoringEnabled
    ) {
        return table.connector.netBinds.addNetBind(
            this,
            pk,
            ia,
            netPort,
            netProtocol,
            appProtocol,
            openFirewall,
            monitoringEnabled
        );
    }

    public List<AOServerDaemonHost> getAOServerDaemonHosts() {
	return table.connector.aoServerDaemonHosts.getAOServerDaemonHosts(this);
    }

    public IPAddress getAvailableIPAddress() {
	for(IPAddress ip : getIPAddresses()) {
            if(
                ip.isAvailable()
                && ip.isAlias()
                && !ip.getNetDevice().getNetDeviceID().isLoopback()
            ) return ip;
	}
	return null;
    }

    public List<BackupPartition> getBackupPartitions() {
        return table.connector.backupPartitions.getBackupPartitions(this);
    }

    public BackupPartition getBackupPartitionForDevice(String device) {
        return table.connector.backupPartitions.getBackupPartitionForDevice(this, device);
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
            case 1: return Integer.valueOf(num_cpu);
            case 2: return Integer.valueOf(cpu_speed);
            case 3: return Integer.valueOf(ram);
            case 4: return rack==-1?null:Integer.valueOf(rack);
            case 5: return Integer.valueOf(disk);
            case 6: return wildcard_https;
            case 7: return is_interbase?Boolean.TRUE:Boolean.FALSE;
            case 8: return is_dns?Boolean.TRUE:Boolean.FALSE;
            case 9: return is_router?Boolean.TRUE:Boolean.FALSE;
            case 10: return iptables_name;
            case 11: return daemon_bind==-1?null:Integer.valueOf(daemon_bind);
            case 12: return daemon_key;
            case 13: return Integer.valueOf(pool_size);
            case 14: return Integer.valueOf(distro_hour);
            case 15: return last_distro_time==-1?null:new Date(last_distro_time);
            case 16: return failover_server==-1?null:Integer.valueOf(failover_server);
            case 17: return Integer.valueOf(server_report_delay);
            case 18: return Integer.valueOf(server_report_interval);
            case 19: return is_qmail?Boolean.TRUE:Boolean.FALSE;
            case 20: return daemon_device_id;
            case 21: return xeroscape_name;
            case 22: return Integer.valueOf(value);
            case 23: return monitoring_enabled?Boolean.TRUE:Boolean.FALSE;
            case 24: return emailmon_password;
            case 25: return ftpmon_password;
            case 26: return daemon_connect_bind==-1?null:Integer.valueOf(daemon_connect_bind);
            case 27: return time_zone;
            case 28: return jilter_bind;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getCPUSpeed() {
	return cpu_speed;
    }

    public CvsRepository getCvsRepository(String path) {
        return table.connector.cvsRepositories.getCvsRepository(this, path);
    }

    public List<CvsRepository> getCvsRepositories() {
        return table.connector.cvsRepositories.getCvsRepositories(this);
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

    public NetDeviceID getDaemonDeviceID() {
        NetDeviceID ndi=table.connector.netDeviceIDs.get(daemon_device_id);
        if(ndi==null) throw new WrappedException(new SQLException("Unable to find NetDeviceID: "+daemon_device_id));
        return ndi;
    }

    public String generateInterBaseDBGroupName(String template_base, String template_added) {
        return table.connector.interBaseDBGroups.generateInterBaseDBGroupName(
            this,
            template_base,
            template_added
        );
    }

    public IPAddress getDaemonIPAddress() {
        NetBind nb=getDaemonBind();
        if(nb==null) throw new WrappedException(new SQLException("Unable to find daemon NetBind for AOServer: "+pkey));
        IPAddress ia=nb.getIPAddress();
        String ip=ia.getIPAddress();
        if(ip.equals(IPAddress.WILDCARD_IP)) {
            NetDeviceID ndi=getDaemonDeviceID();
            NetDevice nd=getNetDevice(ndi.getName());
            if(nd==null) throw new WrappedException(new SQLException("Unable to find NetDevice: "+ndi.getName()+" on "+pkey));
            ia=nd.getPrimaryIPAddress();
            if(ia==null) throw new WrappedException(new SQLException("Unable to find primary IPAddress: "+ndi.getName()+" on "+pkey));
        }
        return ia;
    }

    public String getDaemonKey() {
	return daemon_key;
    }

    public int getDisk() {
	return disk;
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

    public List<EmailSmtpRelay> getEmailSmtpRelays() {
	return table.connector.emailSmtpRelays.getEmailSmtpRelays(this);
    }

    /**
     * Gets the list of all replications coming from this server.
     */
    public List<FailoverFileReplication> getFailoverFileReplications() {
        return table.connector.failoverFileReplications.getFailoverFileReplications(this);
    }

    public AOServer getFailoverServer() {
        if(failover_server==-1) return null;
        AOServer se=table.connector.aoServers.get(failover_server);
        if(se==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+failover_server));
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

    public List<InterBaseBackup> getInterBaseBackups() {
	return table.connector.interBaseBackups.getInterBaseBackups(this);
    }

    public List<InterBaseDatabase> getInterBaseDatabases() {
	return table.connector.interBaseDatabases.getInterBaseDatabases(this);
    }

    public InterBaseDBGroup getInterBaseDBGroup(String name) {
        return table.connector.interBaseDBGroups.getInterBaseDBGroup(this, name);
    }

    public List<InterBaseDBGroup> getInterBaseDBGroups() {
        return table.connector.interBaseDBGroups.getInterBaseDBGroups(this);
    }

    public InterBaseServerUser getInterBaseServerUser(String username) {
	return table.connector.interBaseServerUsers.getInterBaseServerUser(username, this);
    }

    public List<InterBaseServerUser> getInterBaseServerUsers() {
	return table.connector.interBaseServerUsers.getInterBaseServerUsers(this);
    }

    public List<IPAddress> getIPAddresses() {
	return table.connector.ipAddresses.getIPAddresses(this);
    }

    public String getIPTablesName() {
	return iptables_name;
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
                    masterOut.writeCompressedInt(AOServProtocol.GET_MRTG_FILE);
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

    public NetBind getNetBind(
        IPAddress ipAddress,
        NetPort port,
        NetProtocol netProtocol
    ) {
        return table.connector.netBinds.getNetBind(this, ipAddress, port, netProtocol);
    }

    public List<NetBind> getNetBinds() {
	return table.connector.netBinds.getNetBinds(this);
    }

    public List<NetBind> getNetBinds(IPAddress ipAddress) {
	return table.connector.netBinds.getNetBinds(this, ipAddress);
    }

    public List<NetBind> getNetBinds(Protocol protocol) {
	return table.connector.netBinds.getNetBinds(this, protocol);
    }

    public NetDevice getNetDevice(String deviceID) {
	return table.connector.netDevices.getNetDevice(this, deviceID);
    }

    public List<NetDevice> getNetDevices() {
	return table.connector.netDevices.getNetDevices(this);
    }

    public int getNumCPU() {
	return num_cpu;
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
            String[] preferredVersions=PostgresVersion.getPreferredVersions();
            for(int c=0;c<preferredVersions.length;c++) {
                String version=preferredVersions[c];
                for(int d=0;d<pss.size();d++) {
                    PostgresServer ps=pss.get(d);
                    if(ps.getPostgresVersion().getTechnologyVersion(table.connector).getVersion().equals(version)) {
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
        NetDevice nd=getNetDevice(name);
        if(nd==null) throw new WrappedException(new SQLException("Unable to find NetDevice: "+name+" on "+pkey));
        return nd.getPrimaryIPAddress();
    }

    public PrivateFTPServer getPrivateFTPServer(String path) {
	return table.connector.privateFTPServers.getPrivateFTPServer(this, path);
    }

    public List<PrivateFTPServer> getPrivateFTPServers() {
	return table.connector.privateFTPServers.getPrivateFTPServers(this);
    }

    public int getRack() {
	return rack;
    }

    public int getRAM() {
	return ram;
    }

    public Server getServer() {
        Server se=table.connector.servers.get(pkey);
        if(se==null) throw new WrappedException(new SQLException("Unable to find Server: "+pkey));
        return se;
    }

    public int getServerReportDelay() {
        return server_report_delay;
    }
    
    public int getServerReportInterval() {
        return server_report_interval;
    }
    
    public List<SystemEmailAlias> getSystemEmailAliases() {
	return table.connector.systemEmailAliases.getSystemEmailAliases(this);
    }
    
    protected int getTableIDImpl() {
	return SchemaTable.AO_SERVERS;
    }

    public String getXeroscapeName() {
        return xeroscape_name;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getWildcardHTTPS() {
	return wildcard_https;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
	num_cpu=result.getInt(2);
	cpu_speed=result.getInt(3);
	ram = result.getInt(4);
	rack = result.getInt(5);
        if(result.wasNull()) rack=-1;
	disk = result.getInt(6);
	wildcard_https = result.getString(7);
        is_interbase=result.getBoolean(8);
	is_dns=result.getBoolean(9);
	is_router=result.getBoolean(10);
	iptables_name=result.getString(11);
	daemon_bind=result.getInt(12);
	if(result.wasNull()) daemon_bind=-1;
	daemon_key=result.getString(13);
	pool_size=result.getInt(14);
        distro_hour=result.getInt(15);
        Timestamp T=result.getTimestamp(16);
        last_distro_time=T==null?-1:T.getTime();
        failover_server=result.getInt(17);
        if(result.wasNull()) failover_server=-1;
        server_report_delay=result.getInt(18);
        server_report_interval=result.getInt(19);
        is_qmail=result.getBoolean(20);
        daemon_device_id=result.getString(21);
        xeroscape_name=result.getString(22);
        value=SQLUtility.getPennies(result.getString(23));
        monitoring_enabled=result.getBoolean(24);
        emailmon_password=result.getString(25);
        ftpmon_password=result.getString(26);
        daemon_connect_bind=result.getInt(27);
        time_zone=result.getString(28);
        jilter_bind=result.getInt(29);
        if(result.wasNull()) jilter_bind=-1;
    }

    public boolean isDNS() {
	return is_dns;
    }

    public boolean isEmailDomainAvailable(String domain) {
        return table.connector.emailDomains.isEmailDomainAvailable(this, domain);
    }

    public boolean isHomeUsed(String directory) {
	return table.connector.linuxServerAccounts.isHomeUsed(this, directory);
    }

    public boolean isInterBase() {
        return is_interbase;
    }

    public boolean isInterBaseDBGroupNameAvailable(String name) {
        return table.connector.interBaseDBGroups.isInterBaseDBGroupNameAvailable(this, name);
    }

    public boolean isMonitoringEnabled() {
        return monitoring_enabled;
    }
    
    public String getEmailmonPassword() {
        return emailmon_password;
    }
    
    public String getFtpmonPassword() {
        return ftpmon_password;
    }

    public boolean isMySQLServerNameAvailable(String name) {
	return table.connector.mysqlServers.isMySQLServerNameAvailable(name, this);
    }

    public boolean isPostgresServerNameAvailable(String name) {
	return table.connector.postgresServers.isPostgresServerNameAvailable(name, this);
    }

    public boolean isQmail() {
        return is_qmail;
    }

    public boolean isRouter() {
	return is_router;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
	num_cpu=in.readCompressedInt();
	cpu_speed=in.readCompressedInt();
	ram=in.readCompressedInt();
	rack=in.readCompressedInt();
	disk=in.readCompressedInt();
	wildcard_https=readNullUTF(in);
        is_interbase=in.readBoolean();
	is_dns=in.readBoolean();
	is_router=in.readBoolean();
	iptables_name=in.readUTF();
	daemon_bind=in.readCompressedInt();
	daemon_key=in.readUTF();
	pool_size=in.readCompressedInt();
        distro_hour=in.readCompressedInt();
        last_distro_time=in.readLong();
        failover_server=in.readCompressedInt();
        server_report_delay=in.readCompressedInt();
        server_report_interval=in.readCompressedInt();
        is_qmail=in.readBoolean();
        daemon_device_id=readNullUTF(in);
        xeroscape_name=readNullUTF(in);
        value=in.readCompressedInt();
        monitoring_enabled=in.readBoolean();
        emailmon_password=readNullUTF(in);
        ftpmon_password=readNullUTF(in);
        daemon_connect_bind=in.readCompressedInt();
        time_zone=in.readUTF();
        jilter_bind=in.readCompressedInt();
    }

    public void removeExpiredInterBaseBackups() {
        table.connector.interBaseBackups.removeExpiredInterBaseBackups(this);
    }

    public void removeExpiredMySQLBackups() {
        table.connector.mysqlBackups.removeExpiredMySQLBackups(this);
    }

    public void removeExpiredPostgresBackups() {
        table.connector.postgresBackups.removeExpiredPostgresBackups(this);
    }

    public void restartApache() {
        table.connector.requestUpdate(AOServProtocol.RESTART_APACHE, pkey);
    }

    public void restartCron() {
        table.connector.requestUpdate(AOServProtocol.RESTART_CRON, pkey);
    }

    public void restartInterBase() {
        table.connector.requestUpdate(AOServProtocol.RESTART_INTERBASE, pkey);
    }

    public void restartXfs() {
        table.connector.requestUpdate(AOServProtocol.RESTART_XFS, pkey);
    }

    public void restartXvfb() {
        table.connector.requestUpdate(AOServProtocol.RESTART_XVFB, pkey);
    }

    public long requestDaemonAccess(int daemonCommandCode, int param1) {
        return table.connector.requestLongQuery(
            AOServProtocol.REQUEST_DAEMON_ACCESS,
            pkey,
            daemonCommandCode,
            param1
        );
    }

    public void setLastDistroTime(long distroTime) {
        table.connector.requestUpdateIL(AOServProtocol.SET_LAST_DISTRO_TIME, pkey, distroTime);
    }

    public void startApache() {
        table.connector.requestUpdate(AOServProtocol.START_APACHE, pkey);
    }

    public void startCron() {
        table.connector.requestUpdate(AOServProtocol.START_CRON, pkey);
    }

    public void startDistro(boolean includeUser) {
        table.connector.distroFiles.startDistro(this, includeUser);
    }

    public void startInterBase() {
        table.connector.requestUpdate(AOServProtocol.START_INTERBASE, pkey);
    }

    public void startXfs() {
        table.connector.requestUpdate(AOServProtocol.START_XFS, pkey);
    }

    public void startXvfb() {
        table.connector.requestUpdate(AOServProtocol.START_XVFB, pkey);
    }

    public void stopApache() {
        table.connector.requestUpdate(AOServProtocol.STOP_APACHE, pkey);
    }

    public void stopCron() {
        table.connector.requestUpdate(AOServProtocol.STOP_CRON, pkey);
    }

    public void stopInterBase() {
        table.connector.requestUpdate(AOServProtocol.STOP_INTERBASE, pkey);
    }

    public void stopXfs() {
        table.connector.requestUpdate(AOServProtocol.STOP_XFS, pkey);
    }

    public void stopXvfb() {
        table.connector.requestUpdate(AOServProtocol.STOP_XVFB, pkey);
    }

    protected String toStringImpl() {
        return getServer().getHostname();
    }

    public void waitForHttpdSiteRebuild() {
	table.connector.httpdSites.waitForRebuild(this);
    }

    public void waitForInterBaseRebuild() {
	table.connector.interBaseUsers.waitForRebuild(this);
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
	out.writeCompressedInt(num_cpu);
	out.writeCompressedInt(cpu_speed);
	out.writeCompressedInt(ram);
	out.writeCompressedInt(rack);
	out.writeCompressedInt(disk);
	writeNullUTF(out, wildcard_https);
        out.writeBoolean(is_interbase);
	out.writeBoolean(is_dns);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_4)<0) out.writeBoolean(true);
	out.writeBoolean(is_router);
	out.writeUTF(iptables_name);
	out.writeCompressedInt(daemon_bind);
	out.writeUTF(daemon_key);
	out.writeCompressedInt(pool_size);
        out.writeCompressedInt(distro_hour);
        out.writeLong(last_distro_time);
        out.writeCompressedInt(failover_server);
        out.writeCompressedInt(server_report_delay);
        out.writeCompressedInt(server_report_interval);
        out.writeBoolean(is_qmail);
        writeNullUTF(out, daemon_device_id);
        writeNullUTF(out, xeroscape_name);
        out.writeCompressedInt(value);
        out.writeBoolean(monitoring_enabled);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_108)>=0) {
            writeNullUTF(out, emailmon_password);
            writeNullUTF(out, ftpmon_password);
        } else if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_104)>=0) {
            out.writeUTF(emailmon_password==null?AOServProtocol.FILTERED:emailmon_password);
            out.writeUTF(ftpmon_password==null?AOServProtocol.FILTERED:ftpmon_password);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_119)>=0) out.writeCompressedInt(daemon_connect_bind);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_2)>=0) out.writeUTF(time_zone);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_7)>=0) out.writeCompressedInt(jilter_bind);
    }
}