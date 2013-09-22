/*
 * Copyright 2003-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.HostAddress;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.InternUtils;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
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
import java.util.List;
import java.util.Map;

/**
 * An <code>AOServer</code> stores the details about a server that runs the AOServ distribution.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServer extends CachedObjectIntegerKey<AOServer>
    implements DtoFactory<com.aoindustries.aoserv.client.dto.AOServer> {

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

    public int addCvsRepository(
        String path,
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

    public int addEmailDomain(String domain, Package packageObject) throws SQLException, IOException {
	return table.connector.getEmailDomains().addEmailDomain(domain, this, packageObject);
    }

    public int addEmailPipe(String path, Package packageObject) throws IOException, SQLException {
	return table.connector.getEmailPipes().addEmailPipe(this, path, packageObject);
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
        String serverAdmin,
        boolean useApache,
        IPAddress ipAddress,
        String primaryHttpHostname,
        String[] altHttpHostnames,
        String sharedTomcatName,
        HttpdTomcatVersion version,
        String contentSrc
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
	String serverAdmin,
	boolean useApache,
	IPAddress ipAddress,
	String primaryHttpHostname,
	String[] altHttpHostnames,
	HttpdTomcatVersion tomcatVersion,
	String contentSrc
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

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_SERVER: return Integer.valueOf(pkey);
            case COLUMN_HOSTNAME: return hostname;
            case 2: return daemon_bind==-1?null:Integer.valueOf(daemon_bind);
            case 3: return daemon_key;
            case 4: return Integer.valueOf(pool_size);
            case 5: return Integer.valueOf(distro_hour);
            case 6: return getLastDistroTime();
            case 7: return failover_server==-1?null:Integer.valueOf(failover_server);
            case 8: return daemon_device_id;
            case 9: return daemon_connect_bind==-1?null:Integer.valueOf(daemon_connect_bind);
            case 10: return time_zone;
            case 11: return jilter_bind;
            case 12: return restrict_outbound_email;
            case 13: return daemon_connect_address;
            case 14: return failover_batch_size;
            case 15: return Float.isNaN(monitoring_load_low) ? null : Float.valueOf(monitoring_load_low);
            case 16: return Float.isNaN(monitoring_load_medium) ? null : Float.valueOf(monitoring_load_medium);
            case 17: return Float.isNaN(monitoring_load_high) ? null : Float.valueOf(monitoring_load_high);
            case 18: return Float.isNaN(monitoring_load_critical) ? null : Float.valueOf(monitoring_load_critical);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public CvsRepository getCvsRepository(String path) throws IOException, SQLException {
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
    public EmailList getEmailList(String path) throws IOException, SQLException {
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
	List<LinuxAccount> la=new ArrayList<LinuxAccount>(len);
	for(int c=0;c<len;c++) la.add(lsa.get(c).getLinuxAccount());
	return la;
    }

    public List<LinuxGroup> getLinuxGroups() throws SQLException, IOException {
	List<LinuxServerGroup> lsg=getLinuxServerGroups();
	int len=lsg.size();
	List<LinuxGroup> lg=new ArrayList<LinuxGroup>(len);
	for(int c=0;c<len;c++) lg.add(lsg.get(c).getLinuxGroup());
	return lg;
    }

    public LinuxServerAccount getLinuxServerAccount(String username) throws IOException, SQLException {
        return table.connector.getLinuxServerAccounts().getLinuxServerAccount(this, username);
    }

    public LinuxServerAccount getLinuxServerAccount(int uid) throws IOException, SQLException {
        return table.connector.getLinuxServerAccounts().getLinuxServerAccount(this, uid);
    }

    public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException, SQLException {
	return table.connector.getLinuxServerAccounts().getLinuxServerAccounts(this);
    }

    public LinuxServerGroup getLinuxServerGroup(int gid) throws IOException, SQLException {
        return table.connector.getLinuxServerGroups().getLinuxServerGroup(this, gid);
    }

    public LinuxServerGroup getLinuxServerGroup(String groupName) throws IOException, SQLException {
        return table.connector.getLinuxServerGroups().getLinuxServerGroup(this, groupName);
    }

    public List<LinuxServerGroup> getLinuxServerGroups() throws IOException, SQLException {
	return table.connector.getLinuxServerGroups().getLinuxServerGroups(this);
    }

    public List<MajordomoServer> getMajordomoServers() throws IOException, SQLException {
        return table.connector.getMajordomoServers().getMajordomoServers(this);
    }

    private static final Map<Integer,Object> mrtgLocks = new HashMap<Integer,Object>();

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
                new AOServConnector.UpdateRequest() {
                    public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
                        masterOut.writeCompressedInt(AOServProtocol.CommandID.GET_MRTG_FILE.ordinal());
                        masterOut.writeCompressedInt(pkey);
                        masterOut.writeUTF(filename);
                    }

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

    public MySQLServer getMySQLServer(String name) throws IOException, SQLException {
	return table.connector.getMysqlServers().getMySQLServer(name, this);
    }

    public List<MySQLServer> getMySQLServers() throws IOException, SQLException {
	return table.connector.getMysqlServers().getMySQLServers(this);
    }

    public MySQLServer getPreferredMySQLServer() throws IOException, SQLException {
        // Look for the most-preferred version that has an instance on the server
        List<MySQLServer> pss=getMySQLServers();
        for(String versionPrefix : MySQLServer.PREFERRED_VERSION_PREFIXES) {
            for(int d=0;d<pss.size();d++) {
                MySQLServer ps=pss.get(d);
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

    public PostgresServer getPostgresServer(String name) throws IOException, SQLException {
	return table.connector.getPostgresServers().getPostgresServer(name, this);
    }

    public List<PostgresServer> getPostgresServers() throws IOException, SQLException {
	return table.connector.getPostgresServers().getPostgresServers(this);
    }

    public PostgresServer getPreferredPostgresServer() throws SQLException, IOException {
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
    
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.AO_SERVERS;
    }

    public boolean isEmailDomainAvailable(String domain) throws SQLException, IOException {
        return table.connector.getEmailDomains().isEmailDomainAvailable(this, domain);
    }

    public boolean isHomeUsed(String directory) throws IOException, SQLException {
	return table.connector.getLinuxServerAccounts().isHomeUsed(this, directory);
    }

    public boolean isMySQLServerNameAvailable(String name) throws IOException, SQLException {
	return table.connector.getMysqlServers().isMySQLServerNameAvailable(name, this);
    }

    public boolean isPostgresServerNameAvailable(String name) throws IOException, SQLException {
	return table.connector.getPostgresServers().isPostgresServerNameAvailable(name, this);
    }

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
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

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
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
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
        private final int port;
        private final long key;

        public DaemonAccess(String protocol, HostAddress host, int port, long key) {
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

        public int getPort() {
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
    protected String toStringImpl() {
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

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeCompressedInt(1);
            out.writeCompressedInt(2000);
            out.writeCompressedInt(1024);
            out.writeCompressedInt(2);
            out.writeCompressedInt(240);
            out.writeNullUTF(null);
            out.writeBoolean(false);
            out.writeBoolean(false);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_4)<0) out.writeBoolean(true);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeBoolean(false);
            out.writeUTF("AOServer #"+pkey);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_31)>=0) {
            out.writeUTF(hostname.toString());
        }
	out.writeCompressedInt(daemon_bind);
	out.writeUTF(daemon_key.toString());
	out.writeCompressedInt(pool_size);
        out.writeCompressedInt(distro_hour);
        out.writeLong(last_distro_time);
        out.writeCompressedInt(failover_server);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeCompressedInt(60*1000);
            out.writeCompressedInt(5*60*1000);
            out.writeBoolean(false);
        }
        out.writeNullUTF(daemon_device_id);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeNullUTF(null);
            out.writeCompressedInt(1200*100);
            out.writeBoolean(true);
            if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_108)>=0) {
                out.writeNullUTF(null);
                out.writeNullUTF(null);
            } else if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_104)>=0) {
                out.writeUTF(AOServProtocol.FILTERED);
                out.writeUTF(AOServProtocol.FILTERED);
            }
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_119)>=0) out.writeCompressedInt(daemon_connect_bind);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_2)>=0) out.writeUTF(time_zone);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_7)>=0) out.writeCompressedInt(jilter_bind);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_8)>=0) out.writeBoolean(restrict_outbound_email);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_11)>=0) out.writeNullUTF(ObjectUtils.toString(daemon_connect_address));
        if(version.compareTo(AOServProtocol.Version.VERSION_1_12)>=0) out.writeCompressedInt(failover_batch_size);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_35)>=0) {
            out.writeFloat(monitoring_load_low);
            out.writeFloat(monitoring_load_medium);
            out.writeFloat(monitoring_load_high);
            out.writeFloat(monitoring_load_critical);
        }
    }

    /**
     * Gets the 3ware RAID report.
     */
    public String get3wareRaidReport() throws IOException, SQLException {
        return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_3WARE_RAID_REPORT, pkey);
    }

    /**
     * Gets the MD RAID report.
     */
    public String getMdRaidReport() throws IOException, SQLException {
        return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_MD_RAID_REPORT, pkey);
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

        DrbdReport(
            String device,
            String resourceHostname,
            String resourceDevice,
            ConnectionState connectionState,
            DiskState localDiskState,
            DiskState remoteDiskState,
            Role localRole,
            Role remoteRole
        ) {
            this.device = device;
            this.resourceHostname = resourceHostname;
            this.resourceDevice = resourceDevice;
            this.connectionState = connectionState;
            this.localDiskState = localDiskState;
            this.remoteDiskState = remoteDiskState;
            this.localRole = localRole;
            this.remoteRole = remoteRole;
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
        List<DrbdReport> reports = new ArrayList<DrbdReport>(lines.size());
        for(String line : lines) {
            lineNum++;
            String[] values = StringUtility.splitString(line, '\t');
            if(values.length!=5) {
                throw new ParseException(
                    accessor.getMessage(
                        "AOServer.DrbdReport.ParseException.badColumnCount",
                        line
                    ),
                    lineNum
                );
            }

            // Device
            String device = values[0];
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
            String resource = values[1];
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
            DrbdReport.ConnectionState connectionState = DrbdReport.ConnectionState.valueOf(values[2]);

            // Disk states
            String ds = values[3];
			DrbdReport.DiskState localDiskState;
			DrbdReport.DiskState remoteDiskState;
			if(DrbdReport.DiskState.Unconfigured.name().equals(ds)) {
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
            String state = values[4];
			DrbdReport.Role localRole;
			DrbdReport.Role remoteRole;
			if(DrbdReport.Role.Unconfigured.name().equals(state)) {
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

			reports.add(new DrbdReport(device, domUHostname, domUDevice, connectionState, localDiskState, remoteDiskState, localRole, remoteRole));
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
                Map<String,VolumeGroup> volumeGroups = new HashMap<String,VolumeGroup>(size*4/3+1);
                for(int c=0;c<size;c++) {
                    final int lineNum = c+1;
                    String line = lines.get(c);
                    String[] fields = StringUtility.splitString(line, '\t');
                    if(fields.length!=6) throw new ParseException(
                        accessor.getMessage(
                            "AOServer.LvmReport.VolumeGroup.parseVgsReport.badColumnCount",
                            6,
                            fields.length
                        ),
                        lineNum
                    );
                    String vgExtentSize = fields[1].trim();
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
                    String vgName = fields[0].trim();
                    if(
                        volumeGroups.put(
                            vgName,
                            new VolumeGroup(
                                vgName,
                                Integer.parseInt(vgExtentSize),
                                Long.parseLong(fields[2].trim()),
                                Long.parseLong(fields[3].trim()),
                                Integer.parseInt(fields[4].trim()),
                                Integer.parseInt(fields[5].trim())
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
            private final Map<String,LogicalVolume> logicalVolumes = new HashMap<String,LogicalVolume>();
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
                Map<String,PhysicalVolume> physicalVolumes = new HashMap<String,PhysicalVolume>(size*4/3+1);
                Map<String,Integer> vgPhysicalVolumeCounts = new HashMap<String,Integer>(volumeGroups.size()*4/3+1);
                Map<String,Long> vgExtentCountTotals = new HashMap<String,Long>(volumeGroups.size()*4/3+1);
                Map<String,Long> vgAllocCountTotals = new HashMap<String,Long>(volumeGroups.size()*4/3+1);
                for(int c=0;c<size;c++) {
                    final int lineNum = c+1;
                    String line = lines.get(c);
                    String[] fields = StringUtility.splitString(line, '\t');
                    if(fields.length!=5) throw new ParseException(
                        accessor.getMessage(
                            "AOServer.LvmReport.PhysicalVolume.parsePvsReport.badColumnCount",
                            5,
                            fields.length
                        ),
                        lineNum
                    );
                    String pvName = fields[0].trim();
                    String vgName = fields[4].trim();
                    long pvPeCount = Long.parseLong(fields[1].trim());
                    long pvPeAllocCount = Long.parseLong(fields[2].trim());
                    String pvSizeString = fields[3].trim();
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
                    int actualPvCount = actualPvCountI==null ? 0 : actualPvCountI.intValue();
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
                    long actualVgExtentCount = actualVgExtentCountL==null ? 0 : actualVgExtentCountL.longValue();
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
                    final String[] fields = StringUtility.splitString(line, '\t');
                    if(fields.length!=7) throw new ParseException(
                        accessor.getMessage(
                            "AOServer.LvmReport.LogicalVolume.parseLsvReport.badColumnCount",
                            7,
                            fields.length
                        ),
                        lineNum
                    );
                    final String vgName = fields[0].trim();
                    final String lvName = fields[1].trim();
                    final int segCount = Integer.parseInt(fields[2].trim());
                    final SegmentType segType = SegmentType.valueOf(fields[3].trim());
                    final int stripeCount = Integer.parseInt(fields[4].trim());
                    final long segStartPe = Long.parseLong(fields[5].trim());
                    final String[] segPeRanges = StringUtility.splitString(fields[6].trim(), ' ');

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
                    if(segPeRanges.length!=stripeCount) throw new ParseException(
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
            private final List<Segment> segments = new ArrayList<Segment>();
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
            private final List<Stripe> stripes = new ArrayList<Stripe>();
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
                new AOServConnector.ResultRequest<LvmReport>() {
                    String vgs;
                    String pvs;
                    String lvs;
                    public void writeRequest(CompressedDataOutputStream out) throws IOException {
                        out.writeCompressedInt(AOServProtocol.CommandID.GET_AO_SERVER_LVM_REPORT.ordinal());
                        out.writeCompressedInt(pkey);
                    }
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
        Map<String,String> results = new HashMap<String,String>(lines.size()*4/3+1);
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
     */
    public String getFilesystemsCsvReport() throws IOException, SQLException {
        return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_FILESYSTEMS_CSV_REPORT, pkey);
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
    public String checkPort(InetAddress ipAddress, int port, String netProtocol, String appProtocol, Map<String,String> monitoringParameters) throws IOException, SQLException {
        return table.connector.requestStringQuery(
            true,
            AOServProtocol.CommandID.AO_SERVER_CHECK_PORT,
            pkey,
            ipAddress.toString(),
            port,
            netProtocol,
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
    public String checkSmtpBlacklist(InetAddress sourceIp, String connectIp) throws IOException, SQLException {
        return table.connector.requestStringQuery(false, AOServProtocol.CommandID.AO_SERVER_CHECK_SMTP_BLACKLIST, pkey, sourceIp.toString(), connectIp);
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
            daemon_bind==-1 ? null : Integer.valueOf(daemon_bind),
            getDto(daemon_key),
            pool_size,
            distro_hour,
            last_distro_time==-1 ? null : Long.valueOf(last_distro_time),
            failover_server==-1 ? null : Integer.valueOf(failover_server),
            daemon_device_id,
            daemon_connect_bind==-1 ? null : Integer.valueOf(daemon_connect_bind),
            time_zone,
            jilter_bind==-1 ? null : Integer.valueOf(jilter_bind),
            restrict_outbound_email,
            getDto(daemon_connect_address),
            failover_batch_size,
            Float.isNaN(monitoring_load_low) ? null : Float.valueOf(monitoring_load_low),
            Float.isNaN(monitoring_load_medium) ? null : Float.valueOf(monitoring_load_medium),
            Float.isNaN(monitoring_load_high) ? null : Float.valueOf(monitoring_load_high),
            Float.isNaN(monitoring_load_critical) ? null : Float.valueOf(monitoring_load_critical)
        );
    }
    // </editor-fold>
}
