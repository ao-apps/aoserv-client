/*
 * Copyright 2001-2011 by AO Industries, Inc.,
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
 * Each server has multiple network devices, each listening on different
 * IP addresses.
 *
 * @author  AO Industries, Inc.
 */
final public class NetDevice extends AOServObjectIntegerKey implements Comparable<NetDevice>, DtoFactory<com.aoindustries.aoserv.client.dto.NetDevice> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int server;
    private String deviceId;
    private String description;
    private InetAddress gateway;
    private InetAddress network;
    private InetAddress broadcast;
    private MacAddress macAddress;
    final private Long maxBitRate;
    final private Long monitoringBitRateLow;
    final private Long monitoringBitRateMedium;
    final private Long monitoringBitRateHigh;
    final private Long monitoringBitRateCritical;

    public NetDevice(
        AOServConnector connector,
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
        super(connector, pkey);
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
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        deviceId = intern(deviceId);
        description = intern(description);
        gateway = intern(gateway);
        network = intern(network);
        broadcast = intern(broadcast);
        macAddress = intern(macAddress);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(NetDevice other) {
        try {
            int diff = server==other.server ? 0 : getServer().compareTo(other.getServer());
            if(diff!=0) return diff;
            return deviceId.compareTo(other.deviceId);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a unique, generated pkey")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_SERVER = "server";
    @DependencySingleton
    @SchemaColumn(order=1, name=COLUMN_SERVER, index=IndexType.INDEXED, description="the pkey of the server that contains the device")
    public Server getServer() throws RemoteException {
        return getConnector().getServers().get(server);
    }

    static final String COLUMN_DEVICE_ID = "device_id";
    @DependencySingleton
    @SchemaColumn(order=2, name=COLUMN_DEVICE_ID, index=IndexType.INDEXED, description="the name of the device")
    public NetDeviceID getNetDeviceID() throws RemoteException {
        return getConnector().getNetDeviceIDs().get(deviceId);
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

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public NetDevice(AOServConnector connector, com.aoindustries.aoserv.client.dto.NetDevice dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getServer(),
            dto.getDeviceId(),
            dto.getDescription(),
            getInetAddress(dto.getGateway()),
            getInetAddress(dto.getNetwork()),
            getInetAddress(dto.getBroadcast()),
            getMacAddress(dto.getMacAddress()),
            dto.getMaxBitRate(),
            dto.getMonitoringBitRateLow(),
            dto.getMonitoringBitRateMedium(),
            dto.getMonitoringBitRateHigh(),
            dto.getMonitoringBitRateCritical()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.NetDevice getDto() {
        return new com.aoindustries.aoserv.client.dto.NetDevice(
            key,
            server,
            deviceId,
            description,
            getDto(gateway),
            getDto(network),
            getDto(broadcast),
            getDto(macAddress),
            maxBitRate,
            monitoringBitRateLow,
            monitoringBitRateMedium,
            monitoringBitRateHigh,
            monitoringBitRateCritical
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return deviceId+"@"+getServer().toStringImpl();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public IPAddress getIPAddress(String ipAddress) throws IOException, SQLException {
        return getConnector().getIpAddresses().getIPAddress(this, ipAddress);
    } */

    @DependentObjectSet
    public IndexedSet<IPAddress> getIpAddresses() throws RemoteException {
        return getConnector().getIpAddresses().filterIndexed(IPAddress.COLUMN_NET_DEVICE, this);
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
//        return getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_NET_DEVICE_BONDING_REPORT, pkey);
//    }
//
//    /**
//     * Gets the report from <code>/sys/class/net/<i>device</i>/statistics/...</code>
//     * or <code>null</code> if not an AOServer.
//     */
//    public String getStatisticsReport() throws IOException, SQLException {
//        return getConnector().requestStringQuery(true, AOServProtocol.CommandID.GET_NET_DEVICE_STATISTICS_REPORT, pkey);
//    }
    // </editor-fold>
}
