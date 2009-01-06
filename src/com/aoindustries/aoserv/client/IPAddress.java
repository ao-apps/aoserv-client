package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Each <code>IPAddress</code> represents a unique IPv4 address.  Two of the IP
 * addresses exist on every server, <code>WILDCARD_IP</code> and <code>LOOPBACK_IP</code>.
 * Every other IP address is assigned to a specific <code>Server</code>.  IP
 * addresses may be assigned to a specific <code>Package</code> and may have
 * a monthly rate associated with them.
 *
 * @see  Server
 * @see  Package
 * @see  NetBind
 * @see  PrivateFTPServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class IPAddress extends CachedObjectIntegerKey<IPAddress> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_NET_DEVICE=2,
        COLUMN_PACKAGE=5
    ;
    static final String COLUMN_IP_ADDRESS_name = "ip_address";
    static final String COLUMN_NET_DEVICE_name = "net_device";

    public static final String
        LOOPBACK_IP="127.0.0.1",
        WILDCARD_IP="0.0.0.0"
    ;

    private static final String[] privateNetworks={
        "10.",
        "172.16.",
        "192.168.",
        "127."
    };

    String ip_address;
    int net_device;
    boolean is_alias;
    private String hostname;
    String packageName;
    private long created;
    private boolean available;
    private boolean isOverflow;
    private boolean isDHCP;
    private boolean pingMonitorEnabled;
    private String externalIpAddress;
    private String netmask;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return ip_address;
            case COLUMN_NET_DEVICE: return net_device==-1?null:Integer.valueOf(net_device);
            case 3: return is_alias?Boolean.TRUE:Boolean.FALSE;
            case 4: return hostname;
            case COLUMN_PACKAGE: return packageName;
            case 6: return new java.sql.Date(created);
            case 7: return available?Boolean.TRUE:Boolean.FALSE;
            case 8: return isOverflow?Boolean.TRUE:Boolean.FALSE;
            case 9: return isDHCP?Boolean.TRUE:Boolean.FALSE;
            case 10: return pingMonitorEnabled ? Boolean.TRUE : Boolean.FALSE;
            case 11: return externalIpAddress;
            case 12: return netmask;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * Determines when this <code>IPAddress</code> was created.  The created time
     * is reset when the address is allocated to a different <code>Package</code>,
     * which allows the automated accounting to start the billing on the correct
     * day of the month.
     */
    public long getCreated() {
	return created;
    }

    public String getHostname() {
	return hostname;
    }

    public static int getIntForIPAddress(String ipAddress) {
        // There must be four octets with . between
        String[] octets=StringUtility.splitString(ipAddress, '.');
        if(octets.length!=4) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);

        // Each octet should be from 1 to 3 digits, all numbers
        // and should have a value between 0 and 255 inclusive
        for(int c=0;c<4;c++) {
            String tet=octets[c];
            int tetLen=tet.length();
            if(tetLen<1 || tetLen>3) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
            for(int d=0;d<tetLen;d++) {
                char ch=tet.charAt(d);
                if(ch<'0' || ch>'9') throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
            }
            int val=Integer.parseInt(tet);
            if(val<0 || val>255) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
        }
        return
            (Integer.parseInt(octets[0])<<24)
            | (Integer.parseInt(octets[1])<<16)
            | (Integer.parseInt(octets[2])<<8)
            | (Integer.parseInt(octets[3])&255)
        ;
    }

    public static String getIPAddressForInt(int i) {
        return
            ((i>>>24)&255)
            +"."
            +((i>>>16)&255)
            +"."
            +((i>>>8)&255)
            +"."
            +(i&255)
        ;
    }

    public String getIPAddress() {
	return ip_address;
    }

    public List<NetBind> getNetBinds() {
	return table.connector.netBinds.getNetBinds(this);
    }

    public NetDevice getNetDevice() {
        if(net_device==-1) return null;
	NetDevice nd = table.connector.netDevices.get(net_device);
	if (nd == null) throw new WrappedException(new SQLException("Unable to find NetDevice: " + net_device));
	return nd;
    }

    public Package getPackage() {
        // May be null when filtered
        return table.connector.packages.get(packageName);
    }

    public boolean isOverflow() {
        return isOverflow;
    }
    
    public boolean isDHCP() {
        return isDHCP;
    }

    public boolean isPingMonitorEnabled() {
        return pingMonitorEnabled;
    }

    /**
     * Gets the external IP address, if different than ip_address.
     */
    public String getExternalIpAddress() {
        return externalIpAddress;
    }

    public String getNetMask() {
	return netmask;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.IP_ADDRESSES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        ip_address = result.getString(2);
        net_device = result.getInt(3);
        if(result.wasNull()) net_device=-1;
        is_alias = result.getBoolean(4);
        hostname = result.getString(5);
        packageName = result.getString(6);
        created = result.getTimestamp(7).getTime();
        available = result.getBoolean(8);
        isOverflow = result.getBoolean(9);
        isDHCP = result.getBoolean(10);
        pingMonitorEnabled = result.getBoolean(11);
        externalIpAddress = result.getString(12);
	netmask = result.getString(13);
    }

    public boolean isAlias() {
        return is_alias;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isUsed() {
        return !getNetBinds().isEmpty();
    }

    public static boolean isValidIPAddress(String ip) {
        // There must be four octets with . between
        String[] octets=StringUtility.splitString(ip, '.');
        if(octets.length!=4) return false;

        // Each octet should be from 1 to 3 digits, all numbers
        // and should have a value between 0 and 255 inclusive
        for(int c=0;c<4;c++) {
            String tet=octets[c];
            int tetLen=tet.length();
            if(tetLen<1 || tetLen>3) return false;
            for(int d=0;d<tetLen;d++) {
                char ch=tet.charAt(d);
                if(ch<'0' || ch>'9') return false;
            }
            int val=Integer.parseInt(tet);
            if(val<0 || val>255) return false;
        }
        return true;
    }

    public static boolean isPrivate(String ip_address) {
        for(int c=0;c<privateNetworks.length;c++) {
            String pvt=privateNetworks[c];
            int len=pvt.length();
            if(ip_address.length()>len && ip_address.substring(0, len).equals(pvt)) return true;
        }
        return false;
    }

    public boolean isPrivate() {
        return isPrivate(ip_address);
    }

    public boolean isWildcard() {
        return WILDCARD_IP.equals(ip_address);
    }

    public void moveTo(Server server) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.MOVE_IP_ADDRESS, ip_address, server.pkey);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        ip_address=in.readUTF().intern();
        net_device=in.readCompressedInt();
        is_alias=in.readBoolean();
        hostname=in.readUTF();
        packageName=in.readUTF().intern();
        created=in.readLong();
        available=in.readBoolean();
        isOverflow=in.readBoolean();
        isDHCP=in.readBoolean();
        pingMonitorEnabled = in.readBoolean();
        externalIpAddress = in.readNullUTF();
	netmask = in.readUTF().intern();
    }

    /**
     * Sets the hostname for this <code>IPAddress</code>.
     */
    public void setHostname(String hostname) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_IP_ADDRESS_HOSTNAME, pkey, hostname);
    }

    /**
     * Sets the <code>Package</code>.  The package may only be set if the IP Address is not used
     * by other resources.
     */
    public void setPackage(Package pk) {
        if(isUsed()) throw new WrappedException(new SQLException("Unable to set Package, IPAddress in use: #"+pkey));

        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_IP_ADDRESS_PACKAGE, pkey, pk.name);
    }

    public void setDHCPAddress(String ipAddress) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_IP_ADDRESS_DHCP_ADDRESS, pkey, ipAddress);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(ip_address);
        out.writeCompressedInt(net_device);
        out.writeBoolean(is_alias);
        out.writeUTF(hostname);
        out.writeUTF(packageName);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) out.writeCompressedInt(0);
        out.writeLong(created);
        out.writeBoolean(available);
        out.writeBoolean(isOverflow);
        out.writeBoolean(isDHCP);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)>=0) out.writeBoolean(pingMonitorEnabled);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_34)>=0) out.writeNullUTF(externalIpAddress);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_38)>=0) out.writeUTF(netmask);
    }
}
