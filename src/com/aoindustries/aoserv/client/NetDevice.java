package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
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
        COLUMN_AO_SERVER=1
    ;

    int ao_server;
    String device_id;
    private String description;
    private String delete_route;
    private String gateway;
    private String netmask;
    private String network;
    private String broadcast;
    private String mac_address;
    private long max_bit_rate;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 2: return device_id;
            case 3: return description;
            case 4: return delete_route;
            case 5: return gateway;
            case 6: return netmask;
            case 7: return network;
            case 8: return broadcast;
            case 9: return mac_address;
            case 10: return max_bit_rate==-1 ? null : Long.valueOf(max_bit_rate);
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
    
    public IPAddress getIPAddress(String ipAddress) {
	return table.connector.ipAddresses.getIPAddress(this, ipAddress);
    }

    public List<IPAddress> getIPAddresses() {
	return table.connector.ipAddresses.getIPAddresses(this);
    }

    public NetDeviceID getNetDeviceID() {
	NetDeviceID ndi=table.connector.netDeviceIDs.get(device_id);
	if(ndi==null) throw new WrappedException(new SQLException("Unable to find NetDeviceID: "+device_id));
	return ndi;
    }

    public String getNetMask() {
	return netmask;
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
    
    public long getMaxBitRate() {
        return max_bit_rate;
    }

    public IPAddress getPrimaryIPAddress() {
	List<IPAddress> ips=getIPAddresses();
        List<IPAddress> matches=new ArrayList<IPAddress>();
	for(int c=0;c<ips.size();c++) {
            IPAddress ip=ips.get(c);
            if(!ip.isAlias()) matches.add(ip);
	}
        if(matches.isEmpty()) throw new WrappedException(new SQLException("Unable to find primary IPAddress for NetDevice: "+device_id+" on "+ao_server));
        if(matches.size()>1) throw new WrappedException(new SQLException("Found more than one primary IPAddress for NetDevice: "+device_id+" on "+ao_server));
        return matches.get(0);
    }

    public AOServer getAOServer() {
	AOServer ao=table.connector.aoServers.get(ao_server);
	if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
	return ao;
    }

    protected int getTableIDImpl() {
	return SchemaTable.NET_DEVICES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	ao_server=result.getInt(2);
	device_id=result.getString(3);
	description=result.getString(4);
	delete_route=result.getString(5);
	gateway=result.getString(6);
	netmask=result.getString(7);
        network=result.getString(8);
        broadcast=result.getString(9);
        mac_address=result.getString(10);
        max_bit_rate=result.getLong(11);
        if(result.wasNull()) max_bit_rate=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	ao_server=in.readCompressedInt();
	device_id=in.readUTF();
	description=in.readUTF();
	delete_route=readNullUTF(in);
	gateway=readNullUTF(in);
	netmask=in.readUTF();
        network=readNullUTF(in);
        broadcast=readNullUTF(in);
        mac_address=readNullUTF(in);
        max_bit_rate=in.readLong();
    }

    String toStringImpl() {
        return getAOServer().getServer().hostname+'|'+device_id;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(ao_server);
	out.writeUTF(device_id);
	out.writeUTF(description);
	writeNullUTF(out, delete_route);
	writeNullUTF(out, gateway);
	out.writeUTF(netmask);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_112)>=0) {
            writeNullUTF(out, network);
            writeNullUTF(out, broadcast);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_128)>=0) {
            writeNullUTF(out, mac_address);
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_2)>=0) {
            out.writeLong(max_bit_rate);
        }
    }
}
