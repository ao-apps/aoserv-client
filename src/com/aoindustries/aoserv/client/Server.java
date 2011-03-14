/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

/**
 * A <code>Server</code> stores the details about a single, physical or virtual server.
 *
 * @author  AO Industries, Inc.
 */
abstract public class Server extends Resource implements Comparable<Server> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -1755298381676239387L;

    final protected int farm;
    final protected String description;
    final protected Integer operatingSystemVersion;
    final protected String name;
    final protected boolean monitoringEnabled;

    public Server(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int farm,
        String description,
        Integer operatingSystemVersion,
        String name,
        boolean monitoringEnabled
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.farm = farm;
        this.description = description;
        this.operatingSystemVersion = operatingSystemVersion;
        this.name = name;
        this.monitoringEnabled = monitoringEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    final public int compareTo(Server other) {
        try {
            int diff = getAccounting()==other.getAccounting() ? 0 : getBusiness().compareTo(other.getBusiness()); // OK - interned
            if(diff!=0) return diff;
            return compareIgnoreCaseConsistentWithEquals(name, other.name);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_FARM = getMethodColumn(Server.class, "farm");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the farm the server is located in")
    public ServerFarm getFarm() throws RemoteException {
        return getConnector().getServerFarms().filterUnique(ServerFarm.COLUMN_PKEY, farm);
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, description="a description of the servers purpose")
    public String getDescription() {
        return description;
    }

    public static final MethodColumn COLUMN_OPERATING_SYSTEM_VERSION = getMethodColumn(Server.class, "operatingSystemVersion");
    @DependencySingleton
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+3, index=IndexType.INDEXED, description="the version of operating system running on the server, if known")
    public OperatingSystemVersion getOperatingSystemVersion() throws RemoteException {
        if(operatingSystemVersion==null) return null;
        return getConnector().getOperatingSystemVersions().get(operatingSystemVersion);
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+4, description="the per-package unique name of the server (no special formatting required)")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+5, description="enables/disables monitoring")
    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }
    protected static final int SERVER_LAST_COLUMN = RESOURCE_LAST_COLUMN + 5;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Server(AOServConnector connector, com.aoindustries.aoserv.client.dto.Server dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getFarm(),
            dto.getDescription(),
            dto.getOperatingSystemVersion(),
            dto.getName(),
            dto.isMonitoringEnabled()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSingleton
    public AOServer getAoServer() throws RemoteException {
        return getConnector().getAoServers().filterUnique(AOServer.COLUMN_SERVER, this);
    }

    @DependentObjectSet
    public IndexedSet<BusinessServer> getBusinessServers() throws RemoteException {
        return getConnector().getBusinessServers().filterIndexed(BusinessServer.COLUMN_SERVER, this);
    }

    /**
     * Gets the list of all replications coming from this server.
     */
    @DependentObjectSet
    public IndexedSet<FailoverFileReplication> getFailoverFileReplications() throws RemoteException {
        return getConnector().getFailoverFileReplications().filterIndexed(FailoverFileReplication.COLUMN_SERVER, this);
    }

    public IndexedSet<IPAddress> getIpAddresses() throws RemoteException {
        return getConnector().getIpAddresses().filterIndexedSet(IPAddress.COLUMN_NET_DEVICE, getNetDevices());
    }

    @DependentObjectSet
    public IndexedSet<NetDevice> getNetDevices() throws RemoteException {
    	return getConnector().getNetDevices().filterIndexed(NetDevice.COLUMN_SERVER, this);
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

    @DependentObjectSet
    public IndexedSet<ServerResource> getServerResources() throws RemoteException {
        return getConnector().getServerResources().filterIndexed(ServerResource.COLUMN_SERVER, this);
    }

    @DependentObjectSet
    public IndexedSet<MasterServer> getMasterServers() throws RemoteException {
        return getConnector().getMasterServers().filterIndexed(MasterServer.COLUMN_SERVER, this);
    }

    public IndexedSet<NetBind> getNetBinds() throws RemoteException {
        return getConnector().getNetBinds().filterIndexedSet(NetBind.COLUMN_BUSINESS_SERVER, getBusinessServers());
    }

    public IndexedSet<NetBind> getNetBinds(Protocol protocol) throws RemoteException {
        return getNetBinds().filterIndexed(NetBind.COLUMN_APP_PROTOCOL, protocol);
    }

    /* TODO
    public List<Business> getBusinesses() throws IOException, SQLException {
        return getConnector().getBusinessServers().getBusinesses(this);
    }

    public NetBind getNetBind(
        IPAddress ipAddress,
        NetPort port,
        NetProtocol netProtocol
    ) throws IOException, SQLException {
        return getConnector().getNetBinds().getNetBind(this, ipAddress, port, netProtocol);
    }

    public List<NetBind> getNetBinds(IPAddress ipAddress) throws IOException, SQLException {
        // Use the index first
        List<NetBind> cached = getConnector().getNetBinds().getIndexedRows(NetBind.COLUMN_IP_ADDRESS, ipAddress.pkey);
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
        return getAccounting().toString()+'/'+name;
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
	    getConnector().getBusinesses().addBusiness(
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
        return getConnector().getNetBinds().addNetBind(
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
