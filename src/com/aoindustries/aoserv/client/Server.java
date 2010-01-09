package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.DomainLabel;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>Server</code> stores the details about a single, physical server.
 *
 * @author  AO Industries, Inc.
 */
final public class Server extends AOServObjectIntegerKey<Server> implements BeanFactory<com.aoindustries.aoserv.client.beans.Server> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The daemon key is only available to <code>MasterUser</code>s.  This value is used
     * in place of the key when not accessible.
     */
    public static final String HIDDEN_PASSWORD="*";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private DomainLabel farm;
    final private String description;
    final Integer operatingSystemVersion;
    final private AccountingCode accounting;
    final private String name;
    final private boolean monitoringEnabled;

    public Server(
        ServerService<?,?> service,
        int pkey,
        DomainLabel farm,
        String description,
        Integer operatingSystemVersion,
        AccountingCode accounting,
        String name,
        boolean monitoringEnabled
    ) {
        super(service, pkey);
        this.farm = farm.intern();
        this.description = description;
        this.operatingSystemVersion = operatingSystemVersion;
        this.accounting = accounting.intern();
        this.name = name;
        this.monitoringEnabled = monitoringEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Server other) throws RemoteException {
        int diff = accounting.equals(other.accounting) ? 0 : getBusiness().compareTo(other.getBusiness());
        if(diff!=0) return diff;
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(name, other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated, unique ID")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_FARM = "farm";
    @SchemaColumn(order=1, name=COLUMN_FARM, index=IndexType.INDEXED, description="the name of the farm the server is located in")
    public ServerFarm getServerFarm() throws RemoteException {
        return getService().getConnector().getServerFarms().get(farm);
    }

    @SchemaColumn(order=2, name="description", description="a description of the servers purpose")
    public String getDescription() {
        return description;
    }

    static final String COLUMN_OPERATING_SYSTEM_VERSION="operating_system_version";
    @SchemaColumn(order=3, name=COLUMN_OPERATING_SYSTEM_VERSION, index=IndexType.INDEXED, description="the version of operating system running on the server, if known")
    public OperatingSystemVersion getOperatingSystemVersion() throws RemoteException {
        if(operatingSystemVersion==null) return null;
        OperatingSystemVersion osv=getService().getConnector().getOperatingSystemVersions().get(operatingSystemVersion);
        if(osv==null) new RemoteException("Unable to find OperatingSystemVersion: "+operatingSystemVersion);
        return osv;
    }

    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=4, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business accountable for resources used by the server")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(accounting);
    }

    @SchemaColumn(order=5, name="name", description="the per-package unique name of the server (no special formatting required)")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=6, name="monitoring_enabled", description="enables/disables monitoring")
    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Server getBean() {
        return new com.aoindustries.aoserv.client.beans.Server(key, farm.getBean(), description, operatingSystemVersion, accounting.getBean(), name, monitoringEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getServerFarm(),
            getOperatingSystemVersion(),
            getBusiness()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getAOServer()
                // TODO: getPhysicalServer(),
                // TODO: getVirtualServer()
            ),
            // TODO: getBusinessServers(),
            // TODO: getNetDevices(),
            getFailoverFileReplications()
            // TODO: getMasterServers()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public AOServer getAOServer() throws RemoteException {
        return getService().getConnector().getAoServers().get(key);
    }

    /**
     * Gets the list of all replications coming from this server.
     */
    public Set<FailoverFileReplication> getFailoverFileReplications() throws RemoteException {
        return getService().getConnector().getFailoverFileReplications().getIndexed(FailoverFileReplication.COLUMN_SERVER, this);
    }

    /* TODO
    public PhysicalServer getPhysicalServer() throws IOException, SQLException {
        return getService().getConnector().getPhysicalServers().get(pkey);
    }

    public VirtualServer getVirtualServer() throws IOException, SQLException {
        return getService().getConnector().getVirtualServers().get(pkey);
    }

    public List<Business> getBusinesses() throws IOException, SQLException {
        return getService().getConnector().getBusinessServers().getBusinesses(this);
    }

    public NetBind getNetBind(
        IPAddress ipAddress,
        NetPort port,
        NetProtocol netProtocol
    ) throws IOException, SQLException {
        return getService().getConnector().getNetBinds().getNetBind(this, ipAddress, port, netProtocol);
    }

    public List<NetBind> getNetBinds() throws IOException, SQLException {
        List<NetBind> nbs = getService().getConnector().getNetBinds().getRows();
        List<NetBind> matches = new ArrayList<NetBind>(nbs.size());
        for(NetBind nb : nbs) {
            if(nb.getBusinessServer().server==pkey) matches.add(nb);
        }
        return Collections.unmodifiableList(nbs);
    }

    public List<NetBind> getNetBinds(IPAddress ipAddress) throws IOException, SQLException {
        // Use the index first
        List<NetBind> cached = getService().getConnector().getNetBinds().getIndexedRows(NetBind.COLUMN_IP_ADDRESS, ipAddress.pkey);
        int size=cached.size();
        List<NetBind> matches=new ArrayList<NetBind>(size);
        for(NetBind nb : cached) {
            if(nb.getBusinessServer().server==pkey) matches.add(nb);
        }
        return Collections.unmodifiableList(matches);
    }

    public List<NetBind> getNetBinds(Protocol protocol) throws IOException, SQLException {
        // Use the index first
        List<NetBind> cached = getService().getConnector().getNetBinds().getIndexedRows(NetBind.COLUMN_APP_PROTOCOL, protocol.pkey);
        int size=cached.size();
        List<NetBind> matches=new ArrayList<NetBind>(size);
        for(NetBind nb : cached) {
            if(nb.getBusinessServer().server==pkey) matches.add(nb);
        }
        return Collections.unmodifiableList(matches);
    }

    public NetDevice getNetDevice(String deviceID) throws IOException, SQLException {
    	return getService().getConnector().getNetDevices().getNetDevice(this, deviceID);
    }

    public List<NetDevice> getNetDevices() throws IOException, SQLException {
    	return getService().getConnector().getNetDevices().getNetDevices(this);
    }

    public List<IPAddress> getIPAddresses() throws IOException, SQLException {
        return getService().getConnector().getIpAddresses().getIPAddresses(this);
    }

    public IPAddress getAvailableIPAddress() throws SQLException, IOException {
        for(IPAddress ip : getIPAddresses()) {
            if(
                ip.isAvailable()
                && ip.isAlias()
                && !ip.getNetDevice().getNetDeviceID().isLoopback()
            ) return ip;
        }
        return null;
    }

    public List<BusinessServer> getBusinessServers() throws IOException, SQLException {
        return getService().getConnector().getBusinessServers().getIndexedRows(BusinessServer.COLUMN_SERVER, pkey);
    }

    public List<MasterServer> getMasterServers() throws IOException, SQLException {
        return getService().getConnector().getMasterServers().getIndexedRows(MasterServer.COLUMN_SERVER, pkey);
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        // TODO: AOServer aoServer = getAOServer();
        // TODO: if(aoServer!=null) return aoServer.toStringImpl(userLocale);
        return accounting.toString()+'/'+name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void addBusiness(
        String accounting,
        String contractVersion,
        Business parent,
        boolean can_add_backup_servers,
        boolean can_add_businesses,
        boolean can_see_prices,
        boolean billParent,
        PackageDefinition packageDefinition
    ) throws IOException, SQLException {
	    getService().getConnector().getBusinesses().addBusiness(
            accounting,
            contractVersion,
            this,
            parent.pkey,
            can_add_backup_servers,
            can_add_businesses,
            can_see_prices,
            billParent,
            packageDefinition
        );
    }

    public int addNetBind(
        Business bu,
        IPAddress ia,
        NetPort netPort,
        NetProtocol netProtocol,
        Protocol appProtocol,
        boolean openFirewall,
        boolean monitoringEnabled
    ) throws IOException, SQLException {
        return getService().getConnector().getNetBinds().addNetBind(
            this,
            bu,
            ia,
            netPort,
            netProtocol,
            appProtocol,
            openFirewall,
            monitoringEnabled
        );
    }
    */

    /**
     * Gets the accounting code, will not be filtered.
     *
     * @see #getBusiness()
     */
    /* TODO
    public AccountingCode getAccounting() {
        return accounting;
    }
     */
    // </editor-fold>
}
