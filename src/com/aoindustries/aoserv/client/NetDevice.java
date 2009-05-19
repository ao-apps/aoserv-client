package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Each server has multiple network devices, each listening on different
 * IP addresses.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetDevice extends CachedObjectIntegerKey<NetDevice> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER=1
    ;
    static final String COLUMN_SERVER_name = "server";
    static final String COLUMN_DEVICE_ID_name = "device_id";

    int server;
    String device_id;
    private String description;
    private String delete_route;
    private String gateway;
    private String network;
    private String broadcast;
    private String mac_address;
    private long max_bit_rate;
    private long monitoring_bit_rate_low;
    private long monitoring_bit_rate_medium;
    private long monitoring_bit_rate_high;
    private long monitoring_bit_rate_critical;

    @Override
    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER: return Integer.valueOf(server);
            case 2: return device_id;
            case 3: return description;
            case 4: return delete_route;
            case 5: return gateway;
            case 6: return network;
            case 7: return broadcast;
            case 8: return mac_address;
            case 9: return max_bit_rate==-1 ? null : Long.valueOf(max_bit_rate);
            case 10: return monitoring_bit_rate_low==-1 ? null : Long.valueOf(monitoring_bit_rate_low);
            case 11: return monitoring_bit_rate_medium==-1 ? null : Long.valueOf(monitoring_bit_rate_medium);
            case 12: return monitoring_bit_rate_high==-1 ? null : Long.valueOf(monitoring_bit_rate_high);
            case 13: return monitoring_bit_rate_critical==-1 ? null : Long.valueOf(monitoring_bit_rate_critical);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDeleteRoute() {
	return delete_route;
    }

    public String getDescription() {
	return description;
    }

    public String getGateway() {
	return gateway;
    }
    
    public IPAddress getIPAddress(String ipAddress) throws IOException, SQLException {
	return table.connector.getIpAddresses().getIPAddress(this, ipAddress);
    }

    public List<IPAddress> getIPAddresses() throws IOException, SQLException {
	return table.connector.getIpAddresses().getIPAddresses(this);
    }

    public NetDeviceID getNetDeviceID() throws SQLException, IOException {
        NetDeviceID ndi=table.connector.getNetDeviceIDs().get(device_id);
        if(ndi==null) new SQLException("Unable to find NetDeviceID: "+device_id);
        return ndi;
    }

    public String getNetwork() {
        return network;
    }
    
    public String getBroadcast() {
        return broadcast;
    }
    
    public String getMacAddress() {
        return mac_address;
    }
    
    /**
     * Gets the maximum bit rate this interface can support or <code>-1</code>
     * if unknown.
     */
    public long getMaxBitRate() {
        return max_bit_rate;
    }

    /**
     * Gets the 5-minute average that is considered a low-priority alert or
     * <code>-1</code> if no alert allowed at this level.
     */
    public long getMonitoringBitRateLow() {
        return monitoring_bit_rate_low;
    }

    /**
     * Gets the 5-minute average that is considered a medium-priority alert or
     * <code>-1</code> if no alert allowed at this level.
     */
    public long getMonitoringBitRateMedium() {
        return monitoring_bit_rate_medium;
    }

    /**
     * Gets the 5-minute average that is considered a high-priority alert or
     * <code>-1</code> if no alert allowed at this level.
     */
    public long getMonitoringBitRateHigh() {
        return monitoring_bit_rate_high;
    }

    /**
     * Gets the 5-minute average that is considered a critical-priority alert or
     * <code>-1</code> if no alert allowed at this level.  This is the level
     * that will alert people 24x7.
     */
    public long getMonitoringBitRateCritical() {
        return monitoring_bit_rate_critical;
    }

    public IPAddress getPrimaryIPAddress() throws SQLException, IOException {
	List<IPAddress> ips=getIPAddresses();
        List<IPAddress> matches=new ArrayList<IPAddress>();
	for(int c=0;c<ips.size();c++) {
            IPAddress ip=ips.get(c);
            if(!ip.isAlias()) matches.add(ip);
	}
        if(matches.isEmpty()) throw new SQLException("Unable to find primary IPAddress for NetDevice: "+device_id+" on "+server);
        if(matches.size()>1) throw new SQLException("Found more than one primary IPAddress for NetDevice: "+device_id+" on "+server);
        return matches.get(0);
    }

    public Server getServer() throws SQLException, IOException {
	Server se=table.connector.getServers().get(server);
	if(se==null) throw new SQLException("Unable to find Server: "+server);
	return se;
    }

    @Override
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NET_DEVICES;
    }

    @Override
    public void init(ResultSet result) throws SQLException {
        int pos = 1;
	pkey=result.getInt(pos++);
	server=result.getInt(pos++);
	device_id=result.getString(pos++);
	description=result.getString(pos++);
	delete_route=result.getString(pos++);
	gateway=result.getString(pos++);
        network=result.getString(pos++);
        broadcast=result.getString(pos++);
        mac_address=result.getString(pos++);
        max_bit_rate=result.getLong(pos++);
        if(result.wasNull()) max_bit_rate=-1;
        monitoring_bit_rate_low = result.getLong(pos++);
        if(result.wasNull()) monitoring_bit_rate_low = -1;
        monitoring_bit_rate_medium = result.getLong(pos++);
        if(result.wasNull()) monitoring_bit_rate_medium = -1;
        monitoring_bit_rate_high = result.getLong(pos++);
        if(result.wasNull()) monitoring_bit_rate_high = -1;
        monitoring_bit_rate_critical = result.getLong(pos++);
        if(result.wasNull()) monitoring_bit_rate_critical = -1;
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	server=in.readCompressedInt();
	device_id=in.readUTF().intern();
	description=in.readUTF();
	delete_route=StringUtility.intern(in.readNullUTF());
	gateway=StringUtility.intern(in.readNullUTF());
        network=StringUtility.intern(in.readNullUTF());
        broadcast=StringUtility.intern(in.readNullUTF());
        mac_address=in.readNullUTF();
        max_bit_rate=in.readLong();
        monitoring_bit_rate_low = in.readLong();
        monitoring_bit_rate_medium = in.readLong();
        monitoring_bit_rate_high = in.readLong();
        monitoring_bit_rate_critical = in.readLong();
    }

    @Override
    String toStringImpl() throws SQLException, IOException {
        return getServer().toString()+'|'+device_id;
    }

    @Override
    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(server);
	out.writeUTF(device_id);
	out.writeUTF(description);
	out.writeNullUTF(delete_route);
	out.writeNullUTF(gateway);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_37)<=0) out.writeUTF("255.255.255.0");
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_112)>=0) {
            out.writeNullUTF(network);
            out.writeNullUTF(broadcast);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_128)>=0) {
            out.writeNullUTF(mac_address);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_2)>=0) {
            out.writeLong(max_bit_rate);
        }
        if(version.compareTo(AOServProtocol.Version.VERSION_1_35)>=0) {
            out.writeLong(monitoring_bit_rate_low);
            out.writeLong(monitoring_bit_rate_medium);
            out.writeLong(monitoring_bit_rate_high);
            out.writeLong(monitoring_bit_rate_critical);
        }
    }

    /**
     * Gets the bonding report from <code>/proc/net/bonding/[p]bond#</code>
     * or <code>null</code> if not a bonded device.
     */
    public String getBondingReport() throws IOException, SQLException {
        if(!device_id.startsWith("bond")) return null;
        return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_NET_DEVICE_BONDING_REPORT, pkey);
    }
    
    /**
     * Gets the report from <code>/sys/class/net/<i>device</i>/statistics/...</code>
     * or <code>null</code> if not an AOServer.
     */
    public String getStatisticsReport() throws IOException, SQLException {
        return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_NET_DEVICE_STATISTICS_REPORT, pkey);
    }
}
