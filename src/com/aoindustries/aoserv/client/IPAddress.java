/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * Each <code>IPAddress</code> represents a unique IPv6 or IPv4 address.
 * Every IP address is assigned to a specific <code>NetDevice</code>.
 *
 * @see  Server
 * @see  Business
 * @see  NetBind
 * @see  NetDevice
 * @see  PrivateFtpServer
 *
 * @author  AO Industries, Inc.
 */
final public class IPAddress extends AOServObjectIntegerKey<IPAddress> implements Comparable<IPAddress>, DtoFactory<com.aoindustries.aoserv.client.dto.IPAddress> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private InetAddress ipAddress;
    final private Integer netDevice;
    final private boolean isAlias;
    private DomainName hostname;
    final private boolean available;
    final private boolean isOverflow;
    final private boolean isDhcp;
    final private boolean pingMonitorEnabled;
    private InetAddress externalIpAddress;
    final private short netmask;

    public IPAddress(
        IPAddressService<?,?> service,
        int serverResource,
        InetAddress ipAddress,
        Integer netDevice,
        boolean isAlias,
        DomainName hostname,
        boolean available,
        boolean isOverflow,
        boolean isDhcp,
        boolean pingMonitorEnabled,
        InetAddress externalIpAddress,
        short netmask
    ) {
        super(service, serverResource);
        this.ipAddress = ipAddress;
        this.netDevice = netDevice;
        this.isAlias = isAlias;
        this.hostname = hostname;
        this.available = available;
        this.isOverflow = isOverflow;
        this.isDhcp = isDhcp;
        this.pingMonitorEnabled = pingMonitorEnabled;
        this.externalIpAddress = externalIpAddress;
        this.netmask = netmask;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        ipAddress = intern(ipAddress);
        hostname = intern(hostname);
        externalIpAddress = intern(externalIpAddress);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(IPAddress other) {
        try {
            if(key==other.key) return 0;
            int diff = ipAddress.compareTo(other.ipAddress);
            if(diff!=0) return diff;
            return StringUtility.equals(netDevice, other.netDevice) ? 0 : AOServObjectUtils.compare(getNetDevice(), other.getNetDevice());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="server_resource", index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public ServerResource getServerResource() throws RemoteException {
        return getService().getConnector().getServerResources().get(key);
    }

    @SchemaColumn(order=1, name="ip_address", description="the IP address")
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    static final String COLUMN_NET_DEVICE = "net_device";
    @SchemaColumn(order=2, name=COLUMN_NET_DEVICE, index=IndexType.INDEXED, description="the network_device that this IP address is routed through, is null when unassigned")
    public NetDevice getNetDevice() throws RemoteException {
        if(netDevice==null) return null;
        return getService().getConnector().getNetDevices().get(netDevice);
    }

    static final String COLUMN_IS_ALIAS = "is_alias";
    @SchemaColumn(order=3, name=COLUMN_IS_ALIAS, index=IndexType.INDEXED, description="indicates that the IP address is using IP aliasing on the network device")
    public boolean isAlias() {
        return isAlias;
    }

    @SchemaColumn(order=4, name="hostname", description="the reverse mapping for the hostname")
    public DomainName getHostname() {
        return hostname;
    }

    @SchemaColumn(order=5, name="available", description="a flag if the IP address is available")
    public boolean isAvailable() {
        return available;
    }

    @SchemaColumn(order=6, name="is_overflow", description="indicates that the IP address is shared by different accounts")
    public boolean isOverflow() {
        return isOverflow;
    }

    @SchemaColumn(order=7, name="is_dhcp", description="the IP address is obtained via DHCP")
    public boolean isDhcp() {
        return isDhcp;
    }

    @SchemaColumn(order=8, name="ping_monitor_enabled", description="indicates that ping (ICMP ECHO) is monitored")
    public boolean isPingMonitorEnabled() {
        return pingMonitorEnabled;
    }

    @SchemaColumn(order=9, name="external_ip_address", description="the external IP address, if different than ip_address")
    public InetAddress getExternalIpAddress() {
        return externalIpAddress;
    }

    @SchemaColumn(order=10, name="netmask", description="the netmask of the local network")
    public short getNetMask() {
        return netmask;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.IPAddress getDto() {
        return new com.aoindustries.aoserv.client.dto.IPAddress(
            key,
            getDto(ipAddress),
            netDevice,
            isAlias,
            getDto(hostname),
            available,
            isOverflow,
            isDhcp,
            pingMonitorEnabled,
            getDto(externalIpAddress),
            netmask
        );
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getServerResource());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetDevice());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getDhcpDnsRecords());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetBinds());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getIpAddress().toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<NetBind> getNetBinds() throws RemoteException {
        return getService().getConnector().getNetBinds().filterIndexed(NetBind.COLUMN_IP_ADDRESS, this);
    }

    /* TODO
    public IndexedSet<DnsRecord> getDhcpDnsRecords() throws IOException, SQLException {
        return getService().getConnector().getDnsRecords().getIndexedRows(DnsRecord.COLUMN_DHCP_ADDRESS, pkey);
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
//    public boolean isUsed() throws IOException, SQLException {
//        return !getNetBinds().isEmpty();
//    }
//
//    /**
//     * Sets the hostname for this <code>IPAddress</code>.
//     */
//    public void setHostname(String hostname) throws IOException, SQLException {
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_IP_ADDRESS_HOSTNAME, pkey, hostname);
//    }
//
//    /**
//     * Sets the <code>Business</code>.  The business may only be set if the IP Address is not used
//     * by other resources.
//     * The created time
//     * is reset when the address is allocated to a different <code>Business</code>,
//     * which allows the automated accounting to start the billing on the correct
//     * day of the month.
//     */
//    public void setBusiness(Business bu) throws IOException, SQLException {
//        if(isUsed()) throw new SQLException("Unable to set Business, IPAddress in use: #"+pkey);
//        getService().getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_IP_ADDRESS_BUSINESS, pkey, bu.pkey);
//    }
//

    //private static final ConcurrentMap<String,String> getReverseDnsQueryCache = new ConcurrentHashMap<String,String>();

    /**
     * Gets the arpa address to be used for reverse DNS queries.
     */
    /*public static String getReverseDnsQuery(String ip) {
        String arpa = getReverseDnsQueryCache.get(ip);
        if(arpa==null) {
            int bits = getIntForIPAddress(ip);
            arpa =
                new StringBuilder(29)
                .append(bits&255)
                .append('.')
                .append((bits>>>8)&255)
                .append('.')
                .append((bits>>>16)&255)
                .append('.')
                .append((bits>>>24)&255)
                .append(".in-addr.arpa.")
                .toString()
            ;
            String existingArpa = getReverseDnsQueryCache.putIfAbsent(ip, arpa);
            if(existingArpa!=null) arpa = existingArpa;
        }
        return arpa;
    }*/
    // </editor-fold>
}
