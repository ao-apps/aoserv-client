package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>DNSZone</code> is one domain hosted in the name servers.  It can have
 * any number of <code>DNSRecord</code>s.  Please see <code>DNSTLD</code> for
 * domain restrictions.
 *
 * @see  DNSTLD
 * @see  DNSRecord
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSZone extends CachedObjectStringKey<DNSZone> implements Removable, Dumpable {

    static final int
        COLUMN_ZONE=0,
        COLUMN_PACKAGE=2
    ;
    
    public static final int DEFAULT_TTL = 43200;

    /**
     * The zone that is in charge of the API.
     */
    public static final String API_ZONE="aoindustries.com.";

    /**
     * The hostmaster that is placed in a newly created <code>DNSZone</code>.
     */
    public static final String DEFAULT_HOSTMASTER="hostmaster."+API_ZONE;

    private String file;
    String packageName;
    private String hostmaster;
    private long serial;
    private int ttl;

    public int addDNSRecord(
	String domain,
	DNSType type,
	int mx_priority,
	String destination,
        int ttl
    ) {
	return table.connector.dnsRecords.addDNSRecord(this, domain, type, mx_priority, destination, ttl);
    }

    public void dump(PrintWriter out) {
	printZoneFile(out);
    }

    public DNSType[] getAllowedDNSTypes() {
	DNSTypeTable tt=table.connector.dnsTypes;
	if(isArpa()) {
            DNSType[] types={
                tt.get(DNSType.NS),
                tt.get(DNSType.PTR)
            };
            return types;
	} else {
            DNSType[] types={
                tt.get(DNSType.A),
                tt.get(DNSType.CNAME),
                tt.get(DNSType.MX),
                tt.get(DNSType.NS)
            };
            return types;
	}
    }

    public static String getArpaZoneForIPAddress(String ip) throws IllegalArgumentException {
        if(!IPAddress.isValidIPAddress(ip)) throw new IllegalArgumentException("Invalid IP address: "+ip);
        int pos=ip.indexOf('.');
        String oct1=ip.substring(0, pos);
        int pos2=ip.indexOf('.', pos+1);
        String oct2=ip.substring(pos+1, pos2);
        pos=ip.indexOf('.', pos2+1);
        String oct3=ip.substring(pos2+1, pos);
        return oct3+'.'+oct2+'.'+oct1+".in-addr.arpa";
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_ZONE: return pkey;
            case 1: return file;
            case COLUMN_PACKAGE: return packageName;
            case 3: return hostmaster;
            case 4: return Long.valueOf(serial);
            case 5: return Integer.valueOf(ttl);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public static long getCurrentSerial() {
	Calendar cal=Calendar.getInstance();
	return
            cal.get(Calendar.YEAR)*1000000L
            + (cal.get(Calendar.MONTH)+1)*10000
            + cal.get(Calendar.DATE)*100
            + 01
	;
    }

    public List<DNSRecord> getDNSRecords() {
	return table.connector.dnsRecords.getDNSRecords(this);
    }

    public List<DNSRecord> getDNSRecords(String domain, DNSType type) {
        return table.connector.dnsRecords.getDNSRecords(this, domain, type);
    }

    public String getFile() {
	return file;
    }

    public String getHostmaster() {
	return hostmaster;
    }

    public Package getPackage() {
	Package obj=table.connector.packages.get(packageName);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageName));
	return obj;
    }

    public long getSerial() {
	return serial;
    }
    
    public int getTTL() {
        return ttl;
    }

    protected int getTableIDImpl() {
	return SchemaTable.DNS_ZONES;
    }

    public String getZone() {
	return pkey;
    }

    public String getZoneFile() {
	ByteArrayOutputStream bout=new ByteArrayOutputStream();
	PrintWriter out=new PrintWriter(bout);
	printZoneFile(out);
	out.flush();
	return new String(bout.toByteArray());
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getString(1);
	file=result.getString(2);
	packageName=result.getString(3);
	hostmaster=result.getString(4);
	serial=result.getLong(5);
        ttl=result.getInt(6);
    }

    public boolean isArpa() {
	return pkey.length()>13 && pkey.substring(pkey.length()-13).equals(".in-addr.arpa");
    }

    public void printZoneFile(PrintWriter out) {
	List<DNSRecord> records=getDNSRecords();
	out.print("$TTL    ");
        out.print(ttl);
        out.print('\n');
	if(!isArpa()) {
            out.print("$ORIGIN "); out.print(pkey); out.print("\n");
	}
	out.print("@                       ");
        out.print(ttl);
        out.print(" IN   SOA     ");
        // Find the first nameserver
        DNSRecord firstNS=null;
        for(DNSRecord record : records) {
            if(record.getType().getType().equals(DNSType.NS)) {
                firstNS=record;
                break;
            }
        }
        out.print(firstNS==null ? "ns1.aoindustries.com." : firstNS.getDestination());
        out.print("   "); out.print(hostmaster); out.print(" (\n"
                + "                                "); out.print(serial); out.print(" ; serial\n"
                + "                                3600    ; refresh\n"
                + "                                600     ; retry\n"
                + "                                1814400 ; expiry\n"
                + "                                1800    ; minimum\n"
                + "                                )\n");
	int len=records.size();
	for(int c=0;c<len;c++) {
            DNSRecord record=records.get(c);
            String domain=record.domain;
            out.print(domain);
            int count=Math.max(1, 24-domain.length());
            for(int d=0;d<count;d++) out.print(' ');
            if (record.ttl!=DNSRecord.NO_TTL) {
                String s=String.valueOf(record.ttl);
                out.print(s);
                count=Math.max(1, 24-s.length());
            } else {
                count=24;
            }
            for(int d=0;d<count;d++) out.print(' ');
            out.print("IN   ");
            out.print(record.type);
            count=Math.max(1, 8-record.type.length());
            for(int d=0;d<count;d++) out.print(' ');
            int mx=record.mx_priority;
            if(mx!=DNSRecord.NO_MX_PRIORITY) {
                out.print(mx);
                out.print(' ');
            }
            out.print(record.destination);
            out.print('\n');
	}
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	file=in.readUTF();
	packageName=in.readUTF();
	hostmaster=in.readUTF();
	serial=in.readLong();
        ttl=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(pkey.equals(API_ZONE)) reasons.add(new CannotRemoveReason<DNSZone>("Not allowed to remove the API Zone: "+API_ZONE));
        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.REMOVE, SchemaTable.DNS_ZONES, pkey);
    }
    
    public void setTTL(int ttl) {
        table.connector.requestUpdateIL(AOServProtocol.SET_DNS_ZONE_TTL, pkey, ttl);
        this.ttl=ttl;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(file);
	out.writeUTF(packageName);
	out.writeUTF(hostmaster);
	out.writeLong(serial);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_127)>=0) out.writeCompressedInt(ttl);
    }
}