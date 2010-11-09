/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.GroupId;
import com.aoindustries.aoserv.client.validator.HashedPassword;
import com.aoindustries.aoserv.client.validator.Hostname;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.LinuxID;
import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.aoserv.client.validator.NetPort;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

/**
 * An <code>AOServer</code> stores the details about a server that runs the AOServ distribution.
 *
 * @author  AO Industries, Inc.
 */
final public class AOServer extends AOServObjectIntegerKey<AOServer> implements BeanFactory<com.aoindustries.aoserv.client.beans.AOServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private DomainName hostname;
    final private Integer daemonBind;
    final private HashedPassword daemonKey;
    final private int poolSize;
    final private int distroHour;
    final private Long lastDistroTime;
    final private Integer failoverServer;
    private String daemonDeviceId;
    final private Integer daemonConnectBind;
    private String timeZone;
    final private Integer jilterBind;
    final private boolean restrictOutboundEmail;
    private InetAddress daemonConnectAddress;
    final private int failoverBatchSize;
    final private Float monitoringLoadLow;
    final private Float monitoringLoadMedium;
    final private Float monitoringLoadHigh;
    final private Float monitoringLoadCritical;

    public AOServer(
        AOServerService<?,?> service,
        int server,
        DomainName hostname,
        Integer daemonBind,
        HashedPassword daemonKey,
        int poolSize,
        int distroHour,
        Long lastDistroTime,
        Integer failoverServer,
        String daemonDeviceId,
        Integer daemonConnectBind,
        String timeZone,
        Integer jilterBind,
        boolean restrictOutboundEmail,
        InetAddress daemonConnectAddress,
        int failoverBatchSize,
        Float monitoringLoadLow,
        Float monitoringLoadMedium,
        Float monitoringLoadHigh,
        Float monitoringLoadCritical
    ) {
        super(service, server);
        this.hostname = hostname;
        this.daemonBind = daemonBind;
        this.daemonKey = daemonKey;
        this.poolSize = poolSize;
        this.distroHour = distroHour;
        this.lastDistroTime = lastDistroTime;
        this.failoverServer = failoverServer;
        this.daemonDeviceId = daemonDeviceId;
        this.daemonConnectBind = daemonConnectBind;
        this.timeZone = timeZone;
        this.jilterBind = jilterBind;
        this.restrictOutboundEmail = restrictOutboundEmail;
        this.daemonConnectAddress = daemonConnectAddress;
        this.failoverBatchSize = failoverBatchSize;
        this.monitoringLoadLow = monitoringLoadLow;
        this.monitoringLoadMedium = monitoringLoadMedium;
        this.monitoringLoadHigh = monitoringLoadHigh;
        this.monitoringLoadCritical = monitoringLoadCritical;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        hostname = intern(hostname);
        daemonDeviceId = intern(daemonDeviceId);
        timeZone = intern(timeZone);
        daemonConnectAddress = intern(daemonConnectAddress);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(AOServer other) {
        return hostname.compareTo(other.hostname);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final String COLUMN_SERVER = "server";
    @SchemaColumn(order=0, name=COLUMN_SERVER, index=IndexType.PRIMARY_KEY, description="a reference to servers")
    public Server getServer() throws RemoteException {
        return getService().getConnector().getServers().get(key);
    }

    public static final String COLUMN_HOSTNAME = "hostname";
    /**
     * Gets the unique hostname for this server.  Should be resolvable in DNS to ease maintenance.
     */
    @SchemaColumn(order=1, name=COLUMN_HOSTNAME, index=IndexType.UNIQUE, description="the unique hostname of the server")
    public DomainName getHostname() {
        return hostname;
    }

    /**
     * Gets the port information to bind to.
     */
    static final String COLUMN_DAEMON_BIND = "daemon_bind";
    @SchemaColumn(order=2, name=COLUMN_DAEMON_BIND, index=IndexType.UNIQUE, description="the network bind info for the AOServ Daemon")
    public NetBind getDaemonBind() throws RemoteException {
    	if(daemonBind==null) return null;
        return getService().getConnector().getNetBinds().get(daemonBind);
    }

    @SchemaColumn(order=3, name="daemon_key", description="the hashed key used to connect to this server")
    public HashedPassword getDaemonKey() {
        return daemonKey;
    }

    @SchemaColumn(order=4, name="pool_size", description="the recommended connection pool size for the AOServ Master")
    public int getPoolSize() {
        return poolSize;
    }

    @SchemaColumn(order=5, name="distro_hour", description="the hour the distribution will occur, in server time zone")
    public int getDistroHour() {
        return distroHour;
    }

    @SchemaColumn(order=6, name="last_distro_time", description="the time the last distro check was started")
    public Timestamp getLastDistroTime() {
        return lastDistroTime==null ? null : new Timestamp(lastDistroTime);
    }

    static final String COLUMN_FAILOVER_SERVER = "failover_server";
    @SchemaColumn(order=7, name=COLUMN_FAILOVER_SERVER, index=IndexType.INDEXED, description="the server that is currently running this server")
    public AOServer getFailoverServer() throws RemoteException {
        if(failoverServer==null) return null;
        return getService().getConnector().getAoServers().get(failoverServer);
    }

    static final String COLUMN_DAEMON_DEVICE_ID = "daemon_device_id";
    @SchemaColumn(order=8, name=COLUMN_DAEMON_DEVICE_ID, index=IndexType.INDEXED, description="the device name the master connects to")
    public NetDeviceID getDaemonDeviceID() throws RemoteException {
        return getService().getConnector().getNetDeviceIDs().get(daemonDeviceId);
    }

    static final String COLUMN_DAEMON_CONNECT_BIND = "daemon_connect_bind";
    @SchemaColumn(order=9, name=COLUMN_DAEMON_CONNECT_BIND, index=IndexType.UNIQUE, description="the bind to connect to")
    public NetBind getDaemonConnectBind() throws RemoteException {
        if(daemonConnectBind==null) return null;
        return getService().getConnector().getNetBinds().get(daemonConnectBind);
    }

    static final String COLUMN_TIME_ZONE = "time_zone";
    @SchemaColumn(order=10, name=COLUMN_TIME_ZONE, index=IndexType.INDEXED, description="the time zone setting for the server")
    public TimeZone getTimeZone() throws RemoteException {
        return getService().getConnector().getTimeZones().get(timeZone);
    }

    static final String COLUMN_JILTER_BIND = "jilter_bind";
    @SchemaColumn(order=11, name=COLUMN_JILTER_BIND, index=IndexType.UNIQUE, description="the bind that sendmail uses to connect to jilter")
    public NetBind getJilterBind() throws RemoteException {
    	if(jilterBind==null) return null;
        return getService().getConnector().getNetBinds().get(jilterBind);
    }

    @SchemaColumn(order=12, name="restrict_outbound_email", description="controls if outbound email may only come from address hosted on this machine")
    public boolean getRestrictOutboundEmail() {
        return restrictOutboundEmail;
    }

    /**
     * Gets the address that should be connected to in order to reach this server.
     * This overrides both getDaemonConnectBind and getDaemonBind.
     *
     * @see  #getDaemonConnectBind
     * @see  #getDaemonBind
     */
    @SchemaColumn(order=13, name="daemon_connect_address", description="provides a specific address to use for connecting to AOServDaemon")
    public InetAddress getDaemonConnectAddress() {
        return daemonConnectAddress;
    }

    /**
     * Gets the number of filesystem entries sent per batch during failover replications.
     */
    @SchemaColumn(order=14, name="failover_batch_size", description="the batch size used for failover replications coming from this server")
    public int getFailoverBatchSize() {
        return failoverBatchSize;
    }

    /**
     * Gets the 5-minute load average that is considered a low-priority alert or
     * <code>NaN</code> if no alert allowed at this level.
     */
    @SchemaColumn(order=15, name="monitoring_load_low", description="the 5-minute load average that will trigger a low-level alert")
    public Float getMonitoringLoadLow() {
        return monitoringLoadLow;
    }

    /**
     * Gets the 5-minute load average that is considered a medium-priority alert or
     * <code>NaN</code> if no alert allowed at this level.
     */
    @SchemaColumn(order=16, name="monitoring_load_medium", description="the 5-minute load average that will trigger a medium-level alert")
    public Float getMonitoringLoadMedium() {
        return monitoringLoadMedium;
    }

    /**
     * Gets the 5-minute load average that is considered a high-priority alert or
     * <code>NaN</code> if no alert allowed at this level.
     */
    @SchemaColumn(order=17, name="monitoring_load_high", description="the 5-minute load average that will trigger a high-level alert")
    public Float getMonitoringLoadHigh() {
        return monitoringLoadHigh;
    }

    /**
     * Gets the 5-minute load average that is considered a critical-priority alert or
     * <code>NaN</code> if no alert allowed at this level.  This is the level
     * that will alert people 24x7.
     */
    @SchemaColumn(order=18, name="monitoring_load_critical", description="the 5-minute load average that will trigger a critical-level alert")
    public Float getMonitoringLoadCritical() {
        return monitoringLoadCritical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.AOServer getBean() {
        return new com.aoindustries.aoserv.client.beans.AOServer(key, getBean(hostname), daemonBind, getBean(daemonKey), poolSize, distroHour, getLastDistroTime(), failoverServer, daemonDeviceId, daemonConnectBind, timeZone, jilterBind, restrictOutboundEmail, getBean(daemonConnectAddress), failoverBatchSize, monitoringLoadLow, monitoringLoadMedium, monitoringLoadHigh, monitoringLoadCritical);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDaemonBind());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getFailoverServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDaemonDeviceID());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDaemonConnectBind());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTimeZone());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getJilterBind());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServerDaemonHosts());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServerResources());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBackupPartitions());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNestedAoServers());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxServerAccounts());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailDomains());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxServerGroups());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailPipes());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getSystemEmailAliases());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailSmtpRelays());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getFailoverMySQLReplications());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdSharedTomcats());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<AOServerResource> getAoServerResources() throws RemoteException {
        return getService().getConnector().getAoServerResources().filterIndexed(AOServerResource.COLUMN_AO_SERVER, this);
    }

    public IndexedSet<BackupPartition> getBackupPartitions() throws RemoteException {
        return getService().getConnector().getBackupPartitions().filterIndexed(BackupPartition.COLUMN_AO_SERVER, this);
    }

    public IndexedSet<CvsRepository> getCvsRepositories() throws RemoteException {
        return getService().getConnector().getCvsRepositories().filterUniqueSet(CvsRepository.COLUMN_AO_SERVER_RESOURCE, getAoServerResources());
    }

    public IndexedSet<FtpGuestUser> getFtpGuestUsers() throws RemoteException {
        return getService().getConnector().getFtpGuestUsers().filterUniqueSet(FtpGuestUser.COLUMN_LINUX_ACCOUNT, getLinuxAccounts());
    }

    public IndexedSet<HttpdServer> getHttpdServers() throws RemoteException {
        return getService().getConnector().getHttpdServers().filterUniqueSet(HttpdServer.COLUMN_AO_SERVER_RESOURCE, getAoServerResources());
    }

    /**
     * Gets the set of servers that are currently failed-over to this server.
     */
    public IndexedSet<AOServer> getNestedAoServers() throws RemoteException {
        return getService().getConnector().getAoServers().filterIndexed(AOServer.COLUMN_FAILOVER_SERVER, this);
    }

    public IndexedSet<HttpdSite> getHttpdSites() throws RemoteException {
        return getService().getConnector().getHttpdSites().filterUniqueSet(HttpdSite.COLUMN_AO_SERVER_RESOURCE, getAoServerResources());
    }

    /**
     * Gets all of the linux accounts on this server.
     */
    public IndexedSet<LinuxAccount> getLinuxAccounts() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().filterUniqueSet(LinuxAccount.COLUMN_AO_SERVER_RESOURCE, getAoServerResources());
    }

    /**
     * Gets the linux account with the given name.
     *
     * @throws NoSuchElementException if account not found.
     */
    public LinuxAccount getLinuxAccount(UserId username) throws RemoteException, NoSuchElementException {
        LinuxAccount la = getLinuxAccounts().filterUnique(LinuxAccount.COLUMN_USERNAME, getService().getConnector().getUsernames().get(username));
        if(la==null) throw new NoSuchElementException("this="+this+", username="+username);
        return la;
    }

    /**
     * Gets the linux accounts with the given UID.
     *
     * @throws NoSuchElementException if no accounts found.
     */
    public IndexedSet<LinuxAccount> getLinuxAccounts(LinuxID uid) throws RemoteException, NoSuchElementException {
        IndexedSet<LinuxAccount> las = getLinuxAccounts().filterIndexed(LinuxAccount.COLUMN_UID, uid);
        if(las.isEmpty()) throw new NoSuchElementException("this="+this+", uid="+uid);
        return las;
    }

    public IndexedSet<LinuxGroup> getLinuxGroups() throws RemoteException {
        return getService().getConnector().getLinuxGroups().filterUniqueSet(LinuxGroup.COLUMN_AO_SERVER_RESOURCE, getAoServerResources());
    }

    /**
     * Gets the linux group with the given name.
     *
     * @throws NoSuchElementException if group not found.
     */
    public LinuxGroup getLinuxGroup(GroupId groupName) throws RemoteException, NoSuchElementException {
        LinuxGroup lg = getLinuxGroups().filterUnique(LinuxGroup.COLUMN_GROUP_NAME, getService().getConnector().getGroupNames().get(groupName));
        if(lg==null) throw new NoSuchElementException("this="+this+", groupName="+groupName);
        return lg;
    }

    public IndexedSet<MySQLServer> getMysqlServers() throws RemoteException {
        return getService().getConnector().getMysqlServers().filterUniqueSet(MySQLServer.COLUMN_AO_SERVER_RESOURCE, getAoServerResources());
    }

    /**
     * Gets the MySQL server with the given server name.
     *
     * @throws NoSuchElementException if group not found.
     */
    public MySQLServer getMysqlServer(MySQLServerName name) throws RemoteException {
        MySQLServer ms = getMysqlServers().filterUnique(MySQLServer.COLUMN_NAME, name);
        if(ms==null) throw new NoSuchElementException("this="+this+", name="+name);
        return ms;
    }

    public IndexedSet<PostgresServer> getPostgresServers() throws RemoteException {
        return getService().getConnector().getPostgresServers().filterUniqueSet(PostgresServer.COLUMN_AO_SERVER_RESOURCE, getAoServerResources());
    }

    /**
     * Gets the PostgresSQL server with the given server name.
     *
     * @throws NoSuchElementException if group not found.
     */
    public PostgresServer getPostgresServer(String name) throws RemoteException {
        PostgresServer ps = getPostgresServers().filterUnique(PostgresServer.COLUMN_NAME, name);
        if(ps==null) throw new NoSuchElementException("this="+this+", name="+name);
        return ps;
    }

    public IndexedSet<AOServerDaemonHost> getAoServerDaemonHosts() throws RemoteException {
    	return getService().getConnector().getAoServerDaemonHosts().filterIndexed(AOServerDaemonHost.COLUMN_AO_SERVER, this);
    }

    public IPAddress getPrimaryIPAddress() throws RemoteException {
        NetDeviceID deviceId = getDaemonDeviceID();
        NetDevice nd=getServer().getNetDevice(deviceId);
        if(nd==null) throw new NoSuchElementException("Unable to find NetDevice: "+deviceId+" on "+this);
        return nd.getPrimaryIPAddress();
    }

    public InetAddress getDaemonConnectorIP() throws RemoteException {
        InetAddress address = getDaemonConnectAddress();
        if(address!=null) return address;
        address = getDaemonConnectBind().getIpAddress().getIpAddress();
        if(address.isUnspecified()) address = getPrimaryIPAddress().getIpAddress();
        return address;
    }

    public IndexedSet<FailoverMySQLReplication> getFailoverMySQLReplications() throws RemoteException {
        return getService().getConnector().getFailoverMySQLReplications().filterIndexed(FailoverMySQLReplication.COLUMN_AO_SERVER, this);
    }

    /* TODO
    public IPAddress getDaemonIPAddress() throws IOException {
        NetBind nb=getDaemonBind();
        if(nb==null) throw new AssertionError("Unable to find daemon NetBind for AOServer: "+pkey);
        IPAddress ia=nb.getIPAddress();
        String ip=ia.getIPAddress();
        if(ip.equals(IPAddress.WILDCARD_IP)) {
            NetDeviceID ndi=getDaemonDeviceID();
            NetDevice nd=getServer().getNetDevice(ndi.getName());
            if(nd==null) throw new AssertionError("Unable to find NetDevice: "+ndi.getName()+" on "+pkey);
            ia=nd.getPrimaryIPAddress();
            if(ia==null) throw new AssertionError("Unable to find primary IPAddress: "+ndi.getName()+" on "+pkey);
        }
        return ia;
    }

    public List<EmailAddress> getEmailAddresses() throws IOException {
        return getService().getConnector().getEmailAddresses().getEmailAddresses(this);
    }

    public List<EmailDomain> getEmailDomains() throws IOException {
    	return getService().getConnector().getEmailDomains().getEmailDomains(this);
    }

    public List<EmailForwarding> getEmailForwarding() throws IOException {
        return getService().getConnector().getEmailForwardings().getEmailForwarding(this);
    }

    public List<EmailListAddress> getEmailListAddresses() throws IOException {
        return getService().getConnector().getEmailListAddresses().getEmailListAddresses(this);
    }

    public List<EmailPipeAddress> getEmailPipeAddresses() throws IOException {
        return getService().getConnector().getEmailPipeAddresses().getEmailPipeAddresses(this);
    }

    public List<EmailPipe> getEmailPipes() throws IOException {
        return getService().getConnector().getEmailPipes().getEmailPipes(this);
    }
    */
    /**
     * Gets all of the smtp relays settings that apply to either all servers or this server specifically.
     */
    /* TODO
    public List<EmailSmtpRelay> getEmailSmtpRelays() throws IOException {
        return getService().getConnector().getEmailSmtpRelays().getEmailSmtpRelays(this);
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() throws IOException {
        return getService().getConnector().getHttpdSharedTomcats().getHttpdSharedTomcats(this);
    }

    public List<LinuxAccAddress> getLinuxAccAddresses() throws IOException {
        return getService().getConnector().getLinuxAccAddresses().getLinuxAccAddresses(this);
    }

    public List<LinuxAccount> getLinuxAccounts() throws IOException {
        List<LinuxServerAccount> lsa=getLinuxServerAccounts();
        int len=lsa.size();
        List<LinuxAccount> la=new ArrayList<LinuxAccount>(len);
        for(int c=0;c<len;c++) la.add(lsa.get(c).getLinuxAccount());
        return la;
    }

    public List<LinuxServerAccount> getLinuxServerAccounts() throws IOException {
    	return getService().getConnector().getLinuxServerAccounts().getLinuxServerAccounts(this);
    }

    public List<LinuxServerGroup> getLinuxServerGroups() throws IOException {
    	return getService().getConnector().getLinuxServerGroups().getLinuxServerGroups(this);
    }

    public List<MajordomoServer> getMajordomoServers() throws IOException {
        return getService().getConnector().getMajordomoServers().getMajordomoServers(this);
    }

    public List<PrivateFtpServer> getPrivateFtpServers() throws IOException {
    	return getService().getConnector().getPrivateFtpServers().getPrivateFtpServers(this);
    }

    public List<SystemEmailAlias> getSystemEmailAliases() throws IOException {
        return getService().getConnector().getSystemEmailAliases().getSystemEmailAliases(this);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    protected String toStringImpl() {
        return hostname.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Daemon Access">
    public static class DaemonAccess implements Serializable, BeanFactory<com.aoindustries.aoserv.client.beans.DaemonAccess> {

        private static final long serialVersionUID = 1L;

        private final String protocol;
        private final Hostname host;
        private final NetPort port;
        private final long key;

        public DaemonAccess(String protocol, Hostname host, NetPort port, long key) {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            this.key = key;
        }

        public String getProtocol() {
            return protocol;
        }

        public Hostname getHost() {
            return host;
        }

        public NetPort getPort() {
            return port;
        }

        public long getKey() {
            return key;
        }

        @Override
        public com.aoindustries.aoserv.client.beans.DaemonAccess getBean() {
            return new com.aoindustries.aoserv.client.beans.DaemonAccess(protocol, AOServObject.getBean(host), AOServObject.getBean(port), key);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addCvsRepository(
        String path,
        LinuxServerAccount lsa,
        LinuxServerGroup lsg,
        long mode
    ) throws IOException {
    	return getService().getConnector().getCvsRepositories().addCvsRepository(
            this,
            path,
            lsa,
            lsg,
            mode
    	);
    }

    public int addEmailDomain(String domain, Business business) throws IOException {
        return getService().getConnector().getEmailDomains().addEmailDomain(domain, this, business);
    }

    public int addEmailPipe(String path, Business bu) throws IOException {
        return getService().getConnector().getEmailPipes().addEmailPipe(this, path, bu);
    }

    public int addHttpdJBossSite(
        String siteName,
        Business business,
        LinuxServerAccount siteUser,
        LinuxServerGroup siteGroup,
        String serverAdmin,
        boolean useApache,
        IPAddress ipAddress,
        DomainName primaryHttpHostname,
        DomainName[] altHttpHostnames,
        int jBossVersion
    ) throws IOException {
        return getService().getConnector().getHttpdJBossSites().addHttpdJBossSite(
            this,
            siteName,
            business,
            siteUser,
            siteGroup,
            serverAdmin,
            useApache,
            ipAddress,
            primaryHttpHostname,
            altHttpHostnames,
            jBossVersion
        );
    }

    public int addHttpdSharedTomcat(
    	String name,
        HttpdTomcatVersion version,
        LinuxServerAccount lsa,
        LinuxServerGroup lsg,
        boolean isSecure,
        boolean isOverflow
    ) throws IOException {
        return getService().getConnector().getHttpdSharedTomcats().addHttpdSharedTomcat(
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
        Business business,
        LinuxServerAccount siteUser,
        LinuxServerGroup siteGroup,
        String serverAdmin,
        boolean useApache,
        IPAddress ipAddress,
        DomainName primaryHttpHostname,
        DomainName[] altHttpHostnames,
        String sharedTomcatName
    ) throws IOException {
        return getService().getConnector().getHttpdTomcatSharedSites().addHttpdTomcatSharedSite(
            this,
            siteName,
            business,
            siteUser,
            siteGroup,
            serverAdmin,
            useApache,
            ipAddress,
            primaryHttpHostname,
            altHttpHostnames,
            sharedTomcatName
        );
    }

    public int addHttpdTomcatStdSite(
        String siteName,
        Business business,
        LinuxServerAccount jvmUser,
        LinuxServerGroup jvmGroup,
        String serverAdmin,
        boolean useApache,
        IPAddress ipAddress,
        DomainName primaryHttpHostname,
        DomainName[] altHttpHostnames,
        HttpdTomcatVersion tomcatVersion
    ) throws IOException {
        return getService().getConnector().getHttpdTomcatStdSites().addHttpdTomcatStdSite(
            this,
            siteName,
            business,
            jvmUser,
            jvmGroup,
            serverAdmin,
            useApache,
            ipAddress,
            primaryHttpHostname,
            altHttpHostnames,
            tomcatVersion
        );
    }

    public BackupPartition getBackupPartitionForPath(String path) throws IOException {
        return getService().getConnector().getBackupPartitions().getBackupPartitionForPath(this, path);
    }

    public CvsRepository getCvsRepository(String path) throws IOException {
        return getService().getConnector().getCvsRepositories().getCvsRepository(this, path);
    }

    public EmailDomain getEmailDomain(String domain) throws IOException {
        return getService().getConnector().getEmailDomains().getEmailDomain(this, domain);
    }
    */
    /**
     * Rename to getEmailList when all uses updated.
     */
    /* TODO
    public EmailList getEmailList(String path) throws IOException {
        return getService().getConnector().getEmailLists().getEmailList(this, path);
    }

    public EmailSmtpRelay getEmailSmtpRelay(Business bu, String host) throws IOException {
    	return getService().getConnector().getEmailSmtpRelays().getEmailSmtpRelay(bu, this, host);
    }

    public HttpdSharedTomcat getHttpdSharedTomcat(String jvmName) throws IOException {
        return getService().getConnector().getHttpdSharedTomcats().getHttpdSharedTomcat(jvmName, this);
    }

    public HttpdSite getHttpdSite(String siteName) throws IOException {
    	return getService().getConnector().getHttpdSites().getHttpdSite(siteName, this);
    }

    public LinuxServerAccount getLinuxServerAccount(String username) throws IOException {
        return getService().getConnector().getLinuxServerAccounts().getLinuxServerAccount(this, username);
    }

    public LinuxServerAccount getLinuxServerAccount(int uid) throws IOException {
        return getService().getConnector().getLinuxServerAccounts().getLinuxServerAccount(this, uid);
    }

    public LinuxServerGroup getLinuxServerGroup(int gid) throws IOException {
        return getService().getConnector().getLinuxServerGroups().getLinuxServerGroup(this, gid);
    }

    public LinuxServerGroup getLinuxServerGroup(String groupName) throws IOException {
        return getService().getConnector().getLinuxServerGroups().getLinuxServerGroup(this, groupName);
    }

    private static final Map<Integer,Object> mrtgLocks = new HashMap<Integer,Object>();

    public void getMrtgFile(final String filename, final OutputStream out) throws IOException {
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
            getService().getConnector().requestUpdate(
                false,
                new AOServConnector.UpdateRequest() {
                    public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
                        masterOut.writeCompressedInt(AOServProtocol.CommandID.GET_MRTG_FILE.ordinal());
                        masterOut.writeCompressedInt(pkey);
                        masterOut.writeUTF(filename);
                    }

                    public void readResponse(CompressedDataInputStream in) throws IOException {
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

    public MySQLServer getPreferredMySQLServer() throws IOException {
        // Look for the most-preferred version that has an instance on the server
        List<MySQLServer> pss=getMySQLServers();
        String[] preferredVersionPrefixes=MySQLServer.getPreferredVersionPrefixes();
        for(int c=0;c<preferredVersionPrefixes.length;c++) {
            String versionPrefix=preferredVersionPrefixes[c];
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

    public PostgresServer getPreferredPostgresServer() throws IOException {
        // Look for the most-preferred version that has an instance on the server
        List<PostgresServer> pss=getPostgresServers();
        String[] preferredMinorVersions=PostgresVersion.getPreferredMinorVersions();
        for(int c=0;c<preferredMinorVersions.length;c++) {
            String version=preferredMinorVersions[c];
            for(int d=0;d<pss.size();d++) {
                PostgresServer ps=pss.get(d);
                if(ps.getPostgresVersion().getMinorVersion()==version) {
                    return ps;
                }
            }
        }

        // Default to first available server if no preferred ones round
        return pss.isEmpty()?null:pss.get(0);
    }

    public PrivateFtpServer getPrivateFtpServer(String path) {
    	return getService().getConnector().privateFtpServers.getPrivateFtpServer(this, path);
    }

    public boolean isEmailDomainAvailable(String domain) throws SQLException, IOException {
        return getService().getConnector().getEmailDomains().isEmailDomainAvailable(this, domain);
    }

    public boolean isHomeUsed(String directory) throws IOException, SQLException {
    	return getService().getConnector().getLinuxServerAccounts().isHomeUsed(this, directory);
    }

    public boolean isMySQLServerNameAvailable(String name) throws IOException, SQLException {
        return getService().getConnector().getMysqlServers().isMySQLServerNameAvailable(name, this);
    }

    public boolean isPostgresServerNameAvailable(String name) throws IOException, SQLException {
    	return getService().getConnector().getPostgresServers().isPostgresServerNameAvailable(name, this);
    }

    public void restartApache() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.RESTART_APACHE, pkey);
    }

    public void restartCron() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.RESTART_CRON, pkey);
    }

    public void restartXfs() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.RESTART_XFS, pkey);
    }

    public void restartXvfb() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.RESTART_XVFB, pkey);
    }

    public void setLastDistroTime(long distroTime) throws IOException, SQLException {
        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_LAST_DISTRO_TIME, pkey, distroTime);
    }

    public void startApache() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.START_APACHE, pkey);
    }

    public void startCron() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.START_CRON, pkey);
    }

    public void startDistro(boolean includeUser) throws IOException, SQLException {
        getService().getConnector().getDistroFiles().startDistro(this, includeUser);
    }

    public void startXfs() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false,AOServProtocol.CommandID.START_XFS, pkey);
    }

    public void startXvfb() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.START_XVFB, pkey);
    }

    public void stopApache() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.STOP_APACHE, pkey);
    }

    public void stopCron() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.STOP_CRON, pkey);
    }

    public void stopXfs() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.STOP_XFS, pkey);
    }

    public void stopXvfb() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.STOP_XVFB, pkey);
    }

    public void waitForHttpdSiteRebuild() throws IOException, SQLException {
	    getService().getConnector().getHttpdSites().waitForRebuild(this);
    }

    public void waitForLinuxAccountRebuild() throws IOException, SQLException {
	    getService().getConnector().getLinuxAccounts().waitForRebuild(this);
    }

    public void waitForMySQLDatabaseRebuild() throws IOException, SQLException {
	    getService().getConnector().getMysqlDatabases().waitForRebuild(this);
    }

    public void waitForMySQLDBUserRebuild() throws IOException, SQLException {
	    getService().getConnector().getMysqlDBUsers().waitForRebuild(this);
    }

    public void waitForMySQLServerRebuild() throws IOException, SQLException {
	    getService().getConnector().getMysqlServers().waitForRebuild(this);
    }

    public void waitForMySQLUserRebuild() throws IOException, SQLException {
	    getService().getConnector().getMysqlUsers().waitForRebuild(this);
    }

    public void waitForPostgresDatabaseRebuild() throws IOException, SQLException {
	    getService().getConnector().getPostgresDatabases().waitForRebuild(this);
    }

    public void waitForPostgresServerRebuild() throws IOException, SQLException {
	    getService().getConnector().getPostgresServers().waitForRebuild(this);
    }

    public void waitForPostgresUserRebuild() throws IOException, SQLException {
	    getService().getConnector().getPostgresUsers().waitForRebuild(this);
    }
    */
    /**
     * Gets the 3ware RAID report.
     */
    /* TODO
    public String get3wareRaidReport() throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_3WARE_RAID_REPORT, pkey);
    }
    */
    /**
     * Gets the MD RAID report.
     */
    /* TODO
    public String getMdRaidReport() throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_MD_RAID_REPORT, pkey);
    }

    public static class DrbdReport {
    */
        /**
         * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-connection-states
         */
    /* TODO
        public enum ConnectionState {
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
            VerifyT,
            Unconfigured
        }*/

        /**
         * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-roles
         */
    /* TODO
        public enum Role {
            Primary,
            Secondary,
            Unknown
        }
*/
        /**
         * Obtained from http://www.drbd.org/users-guide/ch-admin.html#s-disk-states
         */
    /* TODO
        public enum DiskState {
            Diskless,
            Attaching,
            Failed,
            Negotiating,
            Inconsistent,
            Outdated,
            DUnknown,
            Consistent,
            UpToDate
        }

        final private String device;
        final private DomainName resourceHostname;
        final private String resourceDevice;
        final private ConnectionState connectionState;
        final private DiskState localDiskState;
        final private DiskState remoteDiskState;
        final private Role localRole;
        final private Role remoteRole;

        DrbdReport(
            String device,
            DomainName resourceHostname,
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

        public DomainName getResourceHostname() {
            return resourceHostname;
        }
    }
    */
    /**
     * Gets the DRBD report.
     */
    /* TODO
    public List<DrbdReport> getDrbdReport() throws IOException, SQLException, ParseException {
        return parseDrbdReport(getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_DRBD_REPORT, pkey));
    }
    */
    /**
     * Parses a DRBD report.
     */
    /* TODO
    public static List<DrbdReport> parseDrbdReport(String drbdReport) throws ParseException {
        List<String> lines = StringUtility.splitLines(drbdReport);
        int lineNum = 0;
        List<DrbdReport> reports = new ArrayList<DrbdReport>(lines.size());
        for(String line : lines) {
            lineNum++;
            String[] values = StringUtility.splitString(line, '\t');
            if(values.length!=5) {
                throw new ParseException(
                    ApplicationResources.accessor.getMessage(
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
                    ApplicationResources.accessor.getMessage(
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
                ApplicationResources.accessor.getMessage(
                    "AOServer.DrbdReport.ParseException.noDash",
                    resource
                ),
                lineNum
            );
            DomainName domUHostname = resource.substring(0, dashPos);
            String domUDevice = resource.substring(dashPos+1);
            if(
                domUDevice.length()!=4
                || domUDevice.charAt(0)!='x'
                || domUDevice.charAt(1)!='v'
                || domUDevice.charAt(2)!='d'
                || domUDevice.charAt(3)<'a'
                || domUDevice.charAt(3)>'z'
            ) throw new ParseException(
                ApplicationResources.accessor.getMessage(
                    "AOServer.DrbdReport.ParseException.unexpectedResourceEnding",
                    domUDevice
                ),
                lineNum
            );

            // Connection State
            DrbdReport.ConnectionState connectionState = DrbdReport.ConnectionState.valueOf(values[2]);

            // Disk states
            String ds = values[3];
            int dsSlashPos = ds.indexOf('/');
            if(dsSlashPos==-1) throw new ParseException(
                ApplicationResources.accessor.getMessage(
                    "AOServer.DrbdReport.ParseException.noSlashInDiskStates",
                    ds
                ),
                lineNum
            );
            DrbdReport.DiskState localDiskState = DrbdReport.DiskState.valueOf(ds.substring(0, dsSlashPos));
            DrbdReport.DiskState remoteDiskState = DrbdReport.DiskState.valueOf(ds.substring(dsSlashPos+1));

            // Roles
            String state = values[4];
            int slashPos = state.indexOf('/');
            if(slashPos==-1) throw new ParseException(
                ApplicationResources.accessor.getMessage(
                    "AOServer.DrbdReport.ParseException.noSlashInState",
                    state
                ),
                lineNum
            );
            DrbdReport.Role localRole = DrbdReport.Role.valueOf(state.substring(0, slashPos));
            DrbdReport.Role remoteRole = DrbdReport.Role.valueOf(state.substring(slashPos+1));

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
*/
            /**
             * Parses the output of vgs --noheadings --separator=$'\t' --units=b -o vg_name,vg_extent_size,vg_extent_count,vg_free_count,pv_count,lv_count
             */
    /* TODO
            private static Map<String,VolumeGroup> parseVgsReport(String vgs) throws ParseException {
                List<String> lines = StringUtility.splitLines(vgs);
                int size = lines.size();
                Map<String,VolumeGroup> volumeGroups = new HashMap<String,VolumeGroup>(size*4/3+1);
                for(int c=0;c<size;c++) {
                    final int lineNum = c+1;
                    String line = lines.get(c);
                    String[] fields = StringUtility.splitString(line, '\t');
                    if(fields.length!=6) throw new ParseException(
                        ApplicationResources.accessor.getMessage(
                            "AOServer.LvmReport.VolumeGroup.parseVgsReport.badColumnCount",
                            6,
                            fields.length
                        ),
                        lineNum
                    );
                    String vgExtentSize = fields[1].trim();
                    if(!vgExtentSize.endsWith("B")) {
                        throw new ParseException(
                            ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
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
*/
            /**
             * Sorts ascending by:
             * <ol>
             *   <li>vgName</li>
             * </ol>
             */
    /* TODO
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
*/
            /**
             * Parses the output of pvs --noheadings --separator=$'\t' --units=b -o pv_name,pv_pe_count,pv_pe_alloc_count,pv_size,vg_name
             */
    /* TODO
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
                        ApplicationResources.accessor.getMessage(
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
                            ApplicationResources.accessor.getMessage(
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
                            ApplicationResources.accessor.getMessage(
                                "AOServer.LvmReport.PhysicalVolume.parsePvsReport.invalidValues",
                                pvPeCount,
                                pvPeAllocCount,
                                vgName
                            ),
                            lineNum
                        );
                        volumeGroup = volumeGroups.get(vgName);
                        if(volumeGroup==null) throw new ParseException(
                            ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
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
*/
            /**
             * Sorts ascending by:
             * <ol>
             *   <li>pvName</li>
             * </ol>
             */
    /* TODO
            public int compareTo(PhysicalVolume other) {
                return pvName.compareTo(other.pvName);
            }

            public String getPvName() {
                return pvName;
            }
*/
            /**
             * The number of extents allocated, this is 0 when not allocated.
             */
    /* TODO
            public long getPvPeAllocCount() {
                return pvPeAllocCount;
            }
*/
            /**
             * The total number of extents, this is 0 when not allocated.
             */
    /* TODO
            public long getPvPeCount() {
                return pvPeCount;
            }
*/
            /**
             * The size of the physical volume in bytes.  This is always available,
             * even when not allocated.
             */
    /* TODO
            public long getPvSize() {
                return pvSize;
            }

            public VolumeGroup getVolumeGroup() {
                return volumeGroup;
            }
        }

        public static class LogicalVolume implements Comparable<LogicalVolume> {
*/
            /**
             * Parses the output from lvs --noheadings --separator=$'\t' -o vg_name,lv_name,seg_count,segtype,stripes,seg_start_pe,seg_pe_ranges
             */
    /* TODO
            private static void parseLvsReport(String lvs, Map<String,VolumeGroup> volumeGroups, Map<String,PhysicalVolume> physicalVolumes) throws ParseException {
                final List<String> lines = StringUtility.splitLines(lvs);
                final int size = lines.size();
                for(int c=0;c<size;c++) {
                    final int lineNum = c+1;
                    final String line = lines.get(c);
                    final String[] fields = StringUtility.splitString(line, '\t');
                    if(fields.length!=7) throw new ParseException(
                        ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
                            "AOServer.LvmReport.LogicalVolume.parseLsvReport.volumeGroupNotFound",
                            vgName
                        ),
                        lineNum
                    );

                    // Find or add the logical volume
                    if(segCount<1) throw new ParseException(
                        ApplicationResources.accessor.getMessage(
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
                            ApplicationResources.accessor.getMessage(
                                "AOServer.LvmReport.LogicalVolume.parseLsvReport.segCountChanged",
                                logicalVolume.segCount,
                                segCount
                            ),
                            lineNum
                        );
                    }

                    // Add the segment
                    if(stripeCount<1) throw new ParseException(
                        ApplicationResources.accessor.getMessage(
                            "AOServer.LvmReport.LogicalVolume.parseLsvReport.badStripeCount",
                            stripeCount
                        ),
                        lineNum
                    );
                    if(segPeRanges.length!=stripeCount) throw new ParseException(
                        ApplicationResources.accessor.getMessage(
                            "AOServer.LvmReport.LogicalVolume.parseLsvReport.mismatchStripeCount"
                        ),
                        lineNum
                    );
                    Segment newSegment = new Segment(logicalVolume, segType, stripeCount, segStartPe);
                    // Check no overlap in segments
                    for(Segment existingSegment : logicalVolume.segments) {
                        if(newSegment.overlaps(existingSegment)) throw new ParseException(
                            ApplicationResources.accessor.getMessage(
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
                            ApplicationResources.accessor.getMessage(
                                "AOServer.LvmReport.LogicalVolume.parseLsvReport.segPeRangeNoColon",
                                segPeRange
                            ),
                            lineNum
                        );
                        int dashPos = segPeRange.indexOf('-', colonPos+1);
                        if(dashPos==-1) throw new ParseException(
                            ApplicationResources.accessor.getMessage(
                                "AOServer.LvmReport.LogicalVolume.parseLsvReport.segPeRangeNoDash",
                                segPeRange
                            ),
                            lineNum
                        );
                        String stripeDevice = segPeRange.substring(0, colonPos).trim();
                        PhysicalVolume stripePv = physicalVolumes.get(stripeDevice);
                        if(stripePv==null) throw new ParseException(
                            ApplicationResources.accessor.getMessage(
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
                                            ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
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
                        ApplicationResources.accessor.getMessage(
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
*/
            /**
             * Sorts ascending by:
             * <ol>
             *   <li>volumeGroup</li>
             *   <li>lvName</li>
             * </ol>
             */
    /* TODO
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
*/
            /**
             * Sorts ascending by:
             * <ol>
             *   <li>logicalVolume</li>
             *   <li>segStartPe</li>
             * </ol>
             */
    /* TODO
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
*/
            /**
             * Gets the last logical physical extent as determined by counting
             * the total size of the stripes and using the following function:
             * <pre>segStartPe + totalStripePE - 1</pre>
             */
    /* TODO
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
*/
            /**
             * Sorts ascending by:
             * <ol>
             *   <li>segment</li>
             *   <li>firstPe</li>
             * </ol>
             */
    /* TODO
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
*/
    /**
     * Gets the LVM report.
     */
    /* TODO
    public LvmReport getLvmReport() throws IOException, SQLException, ParseException {
        try {
            return getService().getConnector().requestResult(
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
*/
    /**
     * Gets the hard drive temperature report.
     */
    /* TODO
    public String getHddTempReport() throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_HDD_TEMP_REPORT, pkey);
    }
*/
    /**
     * Gets the model of each hard drive on the server.  The key
     * is the device name and the value is the model name.
     */
    /* TODO
    public Map<String,String> getHddModelReport() throws IOException, SQLException, ParseException {
        String report = getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_HDD_MODEL_REPORT, pkey);
        List<String> lines = StringUtility.splitLines(report);
        int lineNum = 0;
        Map<String,String> results = new HashMap<String,String>(lines.size()*4/3+1);
        for(String line : lines) {
            lineNum++;
            int colonPos = line.indexOf(':');
            if(colonPos==-1) throw new ParseException(
                ApplicationResources.accessor.getMessage(
                    "AOServer.getHddModelReport.ParseException.noColon",
                    line
                ),
                lineNum
            );
            String device = line.substring(0, colonPos).trim();
            String model = line.substring(colonPos+1).trim();
            if(results.put(device, model)!=null) throw new ParseException(
                ApplicationResources.accessor.getMessage(
                    "AOServer.getHddModelReport.ParseException.duplicateDevice",
                    device
                ),
                lineNum
            );
        }
        return results;
    }
*/
    /**
     * Gets the filesystem states report.
     */
    /* TODO
    public String getFilesystemsCsvReport() throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_FILESYSTEMS_CSV_REPORT, pkey);
    }
*/
    /**
     * Gets the output of /proc/loadavg
     */
    /* TODO
    public String getLoadAvgReport() throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_LOADAVG_REPORT, pkey);
    }
    */
    /**
     * Gets the output of /proc/meminfo
     */
    /* TODO
    public String getMemInfoReport() throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_MEMINFO_REPORT, pkey);
    }
*/
    /**
     * Checks a port from the daemon's point of view.  This is required for monitoring of private and loopback IPs.
     */
    /* TODO
    public String checkPort(String ipAddress, int port, String netProtocol, String appProtocol, Map<String,String> monitoringParameters) throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(
            true,
            AOServProtocol.CommandID.AO_SERVER_CHECK_PORT,
            pkey,
            ipAddress,
            port,
            netProtocol,
            appProtocol,
            NetBind.encodeParameters(monitoringParameters)
        );
    }
*/
    /**
     * Gets the current system time in milliseconds.
     */
    /* TODO
    public long getSystemTimeMillis() throws IOException, SQLException {
        return getService().getConnector().requestLongQuery(true, AOServProtocol.CommandID.GET_AO_SERVER_SYSTEM_TIME_MILLIS, pkey);
    }
*/
    /**
     * Gets the status line of a SMTP server from the server from the provided source IP.
     */
    /* TODO
    public String checkSmtpBlacklist(String sourceIp, String connectIp) throws IOException, SQLException {
        return getService().getConnector().requestStringQuery(false, AOServProtocol.CommandID.AO_SERVER_CHECK_SMTP_BLACKLIST, pkey, sourceIp, connectIp);
    }
     */
    // </editor-fold>
}
