/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    // TODO: Should have an upper bound to this cache to avoid memory leak
    private static final ConcurrentMap<String,Integer> intForIPAddressCache = new ConcurrentHashMap<>();

    public static Integer getIntForIPAddress(String ipAddress) throws IllegalArgumentException {
        Integer result = intForIPAddressCache.get(ipAddress);
        if(result==null) {
            // There must be four octets with . between
            List<String> octets=StringUtility.splitString(ipAddress, '.');
            if(octets.size()!=4) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);

            // Each octet should be from 1 to 3 digits, all numbers
            // and should have a value between 0 and 255 inclusive
            for(int c=0;c<4;c++) {
                String tet=octets.get(c);
                int tetLen=tet.length();
                if(tetLen<1 || tetLen>3) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
                for(int d=0;d<tetLen;d++) {
                    char ch=tet.charAt(d);
                    if(ch<'0' || ch>'9') throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
                }
                int val=Integer.parseInt(tet);
                if(val<0 || val>255) throw new IllegalArgumentException("Invalid IP address: "+ipAddress);
            }
            result =
                (Integer.parseInt(octets.get(0))<<24)
                | (Integer.parseInt(octets.get(1))<<16)
                | (Integer.parseInt(octets.get(2))<<8)
                | (Integer.parseInt(octets.get(3))&255)
            ;
            Integer existing = intForIPAddressCache.putIfAbsent(ipAddress, result);
            if(existing!=null) result = existing;
        }
        return result;
    }

    public static String getIPAddressForInt(int i) {
        return
            new StringBuilder(15)
            .append((i>>>24)&255)
            .append('.')
            .append((i>>>16)&255)
            .append('.')
            .append((i>>>8)&255)
            .append('.')
            .append(i&255)
            .toString()
        ;
    }

    public static boolean isValidIPAddress(String ip) {
        // There must be four octets with . between
        List<String> octets=StringUtility.splitString(ip, '.');
        if(octets.size()!=4) return false;

        // Each octet should be from 1 to 3 digits, all numbers
        // and should have a value between 0 and 255 inclusive
        for(int c=0;c<4;c++) {
            String tet=octets.get(c);
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

    InetAddress ip_address;
    int net_device;
    boolean is_alias;
    private DomainName hostname;
    String packageName;
    private long created;
    private boolean available;
    private boolean isOverflow;
    private boolean isDHCP;
    private boolean pingMonitorEnabled;
    private InetAddress externalIpAddress;
    private String netmask;

	@Override
    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return ip_address;
            case COLUMN_NET_DEVICE: return net_device==-1?null:Integer.valueOf(net_device);
            case 3: return is_alias?Boolean.TRUE:Boolean.FALSE;
            case 4: return hostname;
            case COLUMN_PACKAGE: return packageName;
            case 6: return getCreated();
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
    public Timestamp getCreated() {
        return new Timestamp(created);
    }

    public DomainName getHostname() {
        return hostname;
    }

    public InetAddress getInetAddress() {
        return ip_address;
    }

    public List<NetBind> getNetBinds() throws IOException, SQLException {
        return table.connector.getNetBinds().getNetBinds(this);
    }

    public NetDevice getNetDevice() throws SQLException, IOException {
        if(net_device==-1) return null;
	NetDevice nd = table.connector.getNetDevices().get(net_device);
	if (nd == null) throw new SQLException("Unable to find NetDevice: " + net_device);
	return nd;
    }

    public Package getPackage() throws IOException, SQLException {
        // May be null when filtered
        return table.connector.getPackages().get(packageName);
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
    public InetAddress getExternalIpAddress() {
        return externalIpAddress;
    }

    public String getNetMask() {
        return netmask;
    }

	@Override
    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.IP_ADDRESSES;
    }

	@Override
    public void init(ResultSet result) throws SQLException {
        try {
            pkey = result.getInt(1);
            ip_address = InetAddress.valueOf(result.getString(2));
            net_device = result.getInt(3);
            if(result.wasNull()) net_device=-1;
            is_alias = result.getBoolean(4);
            hostname = DomainName.valueOf(result.getString(5));
            packageName = result.getString(6);
            created = result.getTimestamp(7).getTime();
            available = result.getBoolean(8);
            isOverflow = result.getBoolean(9);
            isDHCP = result.getBoolean(10);
            pingMonitorEnabled = result.getBoolean(11);
            externalIpAddress = InetAddress.valueOf(result.getString(12));
            netmask = result.getString(13);
        } catch(ValidationException e) {
            throw new SQLException(e);
        }
    }

    public boolean isAlias() {
        return is_alias;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isUsed() throws IOException, SQLException {
        return !getNetBinds().isEmpty();
    }

    /**
     * @deprecated  Replace with getInetAddress().isUnspecified()
     */
    @Deprecated
    public boolean isWildcard() {
        return ip_address.isUnspecified();
    }

    public void moveTo(Server server) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.MOVE_IP_ADDRESS, ip_address.toString(), server.pkey);
    }

	@Override
    public void read(CompressedDataInputStream in) throws IOException {
        try {
            pkey=in.readCompressedInt();
            ip_address=InetAddress.valueOf(in.readUTF()).intern();
            net_device=in.readCompressedInt();
            is_alias=in.readBoolean();
            hostname=DomainName.valueOf(in.readNullUTF());
            packageName=in.readUTF().intern();
            created=in.readLong();
            available=in.readBoolean();
            isOverflow=in.readBoolean();
            isDHCP=in.readBoolean();
            pingMonitorEnabled = in.readBoolean();
            externalIpAddress = InetAddress.valueOf(in.readNullUTF());
            netmask = in.readUTF().intern();
        } catch(ValidationException e) {
            throw new IOException(e);
        }
    }

    /**
     * Sets the hostname for this <code>IPAddress</code>.
     */
    public void setHostname(DomainName hostname) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_IP_ADDRESS_HOSTNAME, pkey, hostname.toString());
    }

    /**
     * Sets the <code>Package</code>.  The package may only be set if the IP Address is not used
     * by other resources.
     */
    public void setPackage(Package pk) throws IOException, SQLException {
        if(isUsed()) throw new SQLException("Unable to set Package, IPAddress in use: #"+pkey);

        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_IP_ADDRESS_PACKAGE, pkey, pk.name);
    }

    public void setDHCPAddress(InetAddress ipAddress) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_IP_ADDRESS_DHCP_ADDRESS, pkey, ipAddress.toString());
    }

	@Override
    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_68)<=0) out.writeUTF(ip_address.isUnspecified() ? "0.0.0.0" : ip_address.toString());
        else out.writeUTF(ip_address.toString());
        out.writeCompressedInt(net_device);
        out.writeBoolean(is_alias);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_68)<=0) {
            out.writeUTF(hostname==null ? "*" : hostname.toString());
        } else {
            out.writeNullUTF(ObjectUtils.toString(hostname));
        }
        out.writeUTF(packageName);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) out.writeCompressedInt(0);
        out.writeLong(created);
        out.writeBoolean(available);
        out.writeBoolean(isOverflow);
        out.writeBoolean(isDHCP);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)>=0) out.writeBoolean(pingMonitorEnabled);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_34)>=0) out.writeNullUTF(ObjectUtils.toString(externalIpAddress));
        if(version.compareTo(AOServProtocol.Version.VERSION_1_38)>=0) out.writeUTF(netmask);
    }
}
