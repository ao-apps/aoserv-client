/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
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
final public class IPAddress extends ServerResource implements Comparable<IPAddress>, DtoFactory<com.aoindustries.aoserv.client.dto.IPAddress> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 5500928104479497136L;

    private InetAddress inetAddress;
    final private Integer netDevice;
    final private boolean alias;
    private DomainName hostname;
    final private boolean available;
    final private boolean overflow;
    final private boolean dhcp;
    final private boolean pingMonitorEnabled;
    private InetAddress externalInetAddress;
    final private short netmask;

    public IPAddress(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int server,
        int businessServer,
        InetAddress inetAddress,
        Integer netDevice,
        boolean alias,
        DomainName hostname,
        boolean available,
        boolean overflow,
        boolean dhcp,
        boolean pingMonitorEnabled,
        InetAddress externalInetAddress,
        short netmask
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, server, businessServer);
        this.inetAddress = inetAddress;
        this.netDevice = netDevice;
        this.alias = alias;
        this.hostname = hostname;
        this.available = available;
        this.overflow = overflow;
        this.dhcp = dhcp;
        this.pingMonitorEnabled = pingMonitorEnabled;
        this.externalInetAddress = externalInetAddress;
        this.netmask = netmask;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        inetAddress = intern(inetAddress);
        hostname = intern(hostname);
        externalInetAddress = intern(externalInetAddress);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(IPAddress other) {
        try {
            if(getKeyInt()==other.getKeyInt()) return 0;
            int diff = inetAddress.compareTo(other.inetAddress);
            if(diff!=0) return diff;
            return StringUtility.equals(netDevice, other.netDevice) ? 0 : compare(getNetDevice(), other.getNetDevice());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+1, description="the IP address")
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public static final MethodColumn COLUMN_NET_DEVICE = getMethodColumn(IPAddress.class, "netDevice");
    @DependencySingleton
    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+2, index=IndexType.INDEXED, description="the network_device that this IP address is routed through, is null when unassigned")
    public NetDevice getNetDevice() throws RemoteException {
        if(netDevice==null) return null;
        return getConnector().getNetDevices().get(netDevice);
    }

    public static final MethodColumn COLUMN_ALIAS = getMethodColumn(IPAddress.class, "alias");
    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+3, index=IndexType.INDEXED, description="indicates that the IP address is using IP aliasing on the network device")
    public boolean isAlias() {
        return alias;
    }

    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+4, description="the reverse mapping for the hostname")
    public DomainName getHostname() {
        return hostname;
    }

    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+5, description="a flag if the IP address is available")
    public boolean isAvailable() {
        return available;
    }

    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+6, description="indicates that the IP address is shared by different accounts")
    public boolean isOverflow() {
        return overflow;
    }

    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+7, description="the IP address is obtained via DHCP")
    public boolean isDhcp() {
        return dhcp;
    }

    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+8, description="indicates that ping (ICMP ECHO) is monitored")
    public boolean isPingMonitorEnabled() {
        return pingMonitorEnabled;
    }

    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+9, description="the external IP address, if different than inetAddress")
    public InetAddress getExternalInetAddress() {
        return externalInetAddress;
    }

    @SchemaColumn(order=SERVER_RESOURCE_LAST_COLUMN+10, description="the netmask of the local network")
    public short getNetmask() {
        return netmask;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public IPAddress(AOServConnector connector, com.aoindustries.aoserv.client.dto.IPAddress dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getServer(),
            dto.getBusinessServer(),
            getInetAddress(dto.getInetAddress()),
            dto.getNetDevice(),
            dto.isAlias(),
            getDomainName(dto.getHostname()),
            dto.isAvailable(),
            dto.isOverflow(),
            dto.isDhcp(),
            dto.isPingMonitorEnabled(),
            getInetAddress(dto.getExternalInetAddress()),
            dto.getNetmask()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.IPAddress getDto() {
        return new com.aoindustries.aoserv.client.dto.IPAddress(
            getKeyInt(),
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            server,
            businessServer,
            getDto(inetAddress),
            netDevice,
            alias,
            getDto(hostname),
            available,
            overflow,
            dhcp,
            pingMonitorEnabled,
            getDto(externalInetAddress),
            netmask
        );
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getInetAddress().toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<NetBind> getNetBinds() throws RemoteException {
        return getConnector().getNetBinds().filterIndexed(NetBind.COLUMN_IP_ADDRESS, this);
    }

    @DependentObjectSet
    public IndexedSet<DnsRecord> getDhcpDnsRecords() throws RemoteException {
        return getConnector().getDnsRecords().filterIndexed(DnsRecord.COLUMN_DHCP_ADDRESS, this);
    }
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
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_IP_ADDRESS_HOSTNAME, pkey, hostname);
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
//        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_IP_ADDRESS_BUSINESS, pkey, bu.pkey);
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
