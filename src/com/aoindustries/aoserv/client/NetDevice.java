/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.MacAddress;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Each server has multiple network devices, each listening on different
 * IP addresses.
 *
 * @author  AO Industries, Inc.
 */
final public class NetDevice extends AOServObjectIntegerKey<NetDevice> implements BeanFactory<com.aoindustries.aoserv.client.beans.NetDevice> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int server;
    final private String deviceId;
    final private String description;
    final private InetAddress gateway;
    final private InetAddress network;
    final private InetAddress broadcast;
    final private MacAddress macAddress;
    final private Long maxBitRate;
    final private Long monitoringBitRateLow;
    final private Long monitoringBitRateMedium;
    final private Long monitoringBitRateHigh;
    final private Long monitoringBitRateCritical;

    public NetDevice(
        NetDeviceService<?,?> service,
        int pkey,
        int server,
        String deviceId,
        String description,
        InetAddress gateway,
        InetAddress network,
        InetAddress broadcast,
        MacAddress macAddress,
        Long maxBitRate,
        Long monitoringBitRateLow,
        Long monitoringBitRateMedium,
        Long monitoringBitRateHigh,
        Long monitoringBitRateCritical
    ) {
        super(service, pkey);
        this.server = server;
        this.deviceId = deviceId;
        this.description = description;
        this.gateway = gateway;
        this.network = network;
        this.broadcast = broadcast;
        this.macAddress = macAddress;
        this.maxBitRate = maxBitRate;
        this.monitoringBitRateLow = monitoringBitRateLow;
        this.monitoringBitRateMedium = monitoringBitRateMedium;
        this.monitoringBitRateHigh = monitoringBitRateHigh;
        this.monitoringBitRateCritical = monitoringBitRateCritical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(NetDevice other) throws RemoteException {
        int diff = server==other.server ? 0 : getServer().compareTo(other.getServer());
        if(diff!=0) return diff;
        return deviceId.compareTo(other.deviceId);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a unique, generated pkey")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_SERVER = "server";
    @SchemaColumn(order=1, name=COLUMN_SERVER, index=IndexType.INDEXED, description="the pkey of the server that contains the device")
    public Server getServer() throws RemoteException {
        return getService().getConnector().getServers().get(server);
    }

    static final String COLUMN_DEVICE_ID = "device_id";
    @SchemaColumn(order=2, name=COLUMN_DEVICE_ID, index=IndexType.INDEXED, description="the name of the device")
    public NetDeviceID getNetDeviceID() throws RemoteException {
        return getService().getConnector().getNetDeviceIDs().get(deviceId);
    }

    @SchemaColumn(order=3, name="description", description="a description of the device")
    public String getDescription() {
        return description;
    }

    @SchemaColumn(order=4, name="gateway", description="the gateway IP address")
    public InetAddress getGateway() {
        return gateway;
    }

    @SchemaColumn(order=5, name="network", description="the local network")
    public InetAddress getNetwork() {
        return network;
    }

    @SchemaColumn(order=6, name="broadcast", description="the broadcast IP")
    public InetAddress getBroadcast() {
        return broadcast;
    }

    @SchemaColumn(order=7, name="mac_address", index=IndexType.UNIQUE, description="the MAC address to be used on the device")
    public MacAddress getMacAddress() {
        return macAddress;
    }

    @SchemaColumn(order=8, name="max_bit_rate", description="the maximum bits per second for this network device")
    public Long getMaxBitRate() {
        return maxBitRate;
    }

    @SchemaColumn(order=9, name="monitoring_bit_rate_low", description="the 5-minute average that will trigger a low-level alert")
    public Long getMonitoringBitRateLow() {
        return monitoringBitRateLow;
    }

    @SchemaColumn(order=10, name="monitoring_bit_rate_medium", description="the 5-minute average that will trigger a medium-level alert")
    public Long getMonitoringBitRateMedium() {
        return monitoringBitRateMedium;
    }

    @SchemaColumn(order=11, name="monitoring_bit_rate_high", description="the 5-minute average that will trigger a high-level alert")
    public Long getMonitoringBitRateHigh() {
        return monitoringBitRateHigh;
    }

    @SchemaColumn(order=12, name="monitoring_bit_rate_critical", description="the 5-minute average that will trigger a critical-level alert")
    public Long getMonitoringBitRateCritical() {
        return monitoringBitRateCritical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.NetDevice getBean() {
        return new com.aoindustries.aoserv.client.beans.NetDevice(
            key,
            server,
            deviceId,
            description,
            gateway==null ? null : gateway.getBean(),
            network==null ? null : network.getBean(),
            broadcast==null ? null : broadcast.getBean(),
            macAddress==null ? null : macAddress.getBean(),
            maxBitRate,
            monitoringBitRateLow,
            monitoringBitRateMedium,
            monitoringBitRateHigh,
            monitoringBitRateCritical
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getServer(),
            getNetDeviceID()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getIpAddresses()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return deviceId+"@"+getServer().toStringImpl(userLocale);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public IPAddress getIPAddress(String ipAddress) throws IOException, SQLException {
        return getService().getConnector().getIpAddresses().getIPAddress(this, ipAddress);
    } */

    public IndexedSet<IPAddress> getIpAddresses() throws RemoteException {
        return getService().getConnector().getIpAddresses().filterIndexed(IPAddress.COLUMN_NET_DEVICE, this);
    }

    public IPAddress getPrimaryIPAddress() throws RemoteException, NoSuchElementException {
        IPAddress primaryIp = getIpAddresses().filterUnique(IPAddress.COLUMN_IS_ALIAS, false);
        if(primaryIp==null) throw new NoSuchElementException("Unable to find primary IP address for NetDevice: "+this);
        return primaryIp;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
//    /**
//     * Gets the bonding report from <code>/proc/net/bonding/[p]bond#</code>
//     * or <code>null</code> if not a bonded device.
//     */
//    public String getBondingReport() throws IOException, SQLException {
//        if(!deviceId.startsWith("bond")) return null;
//        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_NET_DEVICE_BONDING_REPORT, pkey);
//    }
//
//    /**
//     * Gets the report from <code>/sys/class/net/<i>device</i>/statistics/...</code>
//     * or <code>null</code> if not an AOServer.
//     */
//    public String getStatisticsReport() throws IOException, SQLException {
//        return getService().getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_NET_DEVICE_STATISTICS_REPORT, pkey);
//    }
    // </editor-fold>
}
