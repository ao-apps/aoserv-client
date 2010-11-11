/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.DomainLabel;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

/**
 * A <code>Server</code> stores the details about a single, physical server.
 *
 * @author  AO Industries, Inc.
 */
final public class Server extends AOServObjectIntegerKey<Server> implements DtoFactory<com.aoindustries.aoserv.client.dto.Server> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The daemon key is only available to <code>MasterUser</code>s.  This value is used
     * in place of the key when not accessible.
     */
    public static final String HIDDEN_PASSWORD="*";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private DomainLabel farm;
    private String description;
    final Integer operatingSystemVersion;
    private AccountingCode accounting;
    private String name;
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
        this.farm = farm;
        this.description = description;
        this.operatingSystemVersion = operatingSystemVersion;
        this.accounting = accounting;
        this.name = name;
        this.monitoringEnabled = monitoringEnabled;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        farm = intern(farm);
        description = intern(description);
        accounting = intern(accounting);
        name = intern(name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Server other) throws RemoteException {
        int diff = accounting==other.accounting ? 0 : getBusiness().compareToImpl(other.getBusiness()); // OK - interned
        if(diff!=0) return diff;
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(name, other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final String COLUMN_PKEY = "pkey";
    @SchemaColumn(order=0, name=COLUMN_PKEY, index=IndexType.PRIMARY_KEY, description="a generated, unique ID")
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
        return getService().getConnector().getOperatingSystemVersions().get(operatingSystemVersion);
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=4, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business accountable for resources used by the server")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
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

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.Server getDto() {
        return new com.aoindustries.aoserv.client.dto.Server(key, getDto(farm), description, operatingSystemVersion, getDto(accounting), name, monitoringEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getServerFarm());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getOperatingSystemVersion());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServer());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getPhysicalServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getVirtualServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessServers());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getFailoverFileReplications());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMasterServers());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetDevices());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getServerResources());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public AOServer getAoServer() throws RemoteException {
        return getService().getConnector().getAoServers().filterUnique(AOServer.COLUMN_SERVER, this);
    }

    /* TODO
    public PhysicalServer getPhysicalServer() throws IOException, SQLException {
        return getService().getConnector().getPhysicalServers().get(pkey);
    }
     */
    public VirtualServer getVirtualServer() throws RemoteException {
        return getService().getConnector().getVirtualServers().filterUnique(VirtualServer.COLUMN_SERVER, this);
    }

    public IndexedSet<BusinessServer> getBusinessServers() throws RemoteException {
        return getService().getConnector().getBusinessServers().filterIndexed(BusinessServer.COLUMN_SERVER, this);
    }

    /**
     * Gets the list of all replications coming from this server.
     */
    public IndexedSet<FailoverFileReplication> getFailoverFileReplications() throws RemoteException {
        return getService().getConnector().getFailoverFileReplications().filterIndexed(FailoverFileReplication.COLUMN_SERVER, this);
    }

    public IndexedSet<IPAddress> getIpAddresses() throws RemoteException {
        return getService().getConnector().getIpAddresses().filterIndexedSet(IPAddress.COLUMN_NET_DEVICE, getNetDevices());
    }

    public IndexedSet<NetDevice> getNetDevices() throws RemoteException {
    	return getService().getConnector().getNetDevices().filterIndexed(NetDevice.COLUMN_SERVER, this);
    }

    /**
     * Gets the net device with the provided ID.
     *
     * @throws java.util.NoSuchElementException if not found
     */
    public NetDevice getNetDevice(NetDeviceID deviceID) throws RemoteException, NoSuchElementException {
        NetDevice nd = getNetDevices().filterUnique(NetDevice.COLUMN_DEVICE_ID, deviceID);
        if(nd==null) throw new NoSuchElementException("Unable to find NetDevice "+deviceID+" on "+this);
        return nd;
    }

    public IndexedSet<ServerResource> getServerResources() throws RemoteException {
        return getService().getConnector().getServerResources().filterIndexed(ServerResource.COLUMN_SERVER, this);
    }

    public IndexedSet<MasterServer> getMasterServers() throws RemoteException {
        return getService().getConnector().getMasterServers().filterIndexed(MasterServer.COLUMN_SERVER, this);
    }

    public IndexedSet<NetBind> getNetBinds() throws RemoteException {
        return getService().getConnector().getNetBinds().filterIndexedSet(NetBind.COLUMN_BUSINESS_SERVER, getBusinessServers());
    }

    public IndexedSet<NetBind> getNetBinds(Protocol protocol) throws RemoteException {
        return getNetBinds().filterIndexed(NetBind.COLUMN_APP_PROTOCOL, protocol);
    }

    /* TODO
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
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        AOServer aoServer = getAoServer();
        if(aoServer!=null) return aoServer.toStringImpl();
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
