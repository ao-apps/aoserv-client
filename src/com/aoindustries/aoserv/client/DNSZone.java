package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    static final String COLUMN_ZONE_name= "zone";
    
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
    ) throws IOException, SQLException {
	return table.connector.getDnsRecords().addDNSRecord(this, domain, type, mx_priority, destination, ttl);
    }

    public void dump(PrintWriter out) throws SQLException, IOException {
	printZoneFile(out);
    }

    public DNSType[] getAllowedDNSTypes() throws IOException, SQLException {
	DNSTypeTable tt=table.connector.getDnsTypes();
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

    public static String getArpaZoneForIPAddress(String ip, String netmask) throws IllegalArgumentException {
        if(!IPAddress.isValidIPAddress(ip)) throw new IllegalArgumentException("Invalid IP address: "+ip);
        if(netmask.equals("255.255.255.0")) {
            int pos = ip.indexOf('.');
            int oct1 = Integer.parseInt(ip.substring(0, pos));
            int pos2 = ip.indexOf('.', pos+1);
            int oct2 = Integer.parseInt(ip.substring(pos+1, pos2));
            pos = ip.indexOf('.', pos2+1);
            int oct3 = Integer.parseInt(ip.substring(pos2+1, pos));
            return oct3+"."+oct2+"."+oct1+".in-addr.arpa";
        } else if(netmask.equals("255.255.255.128")) {
            // Hurricane Electric compatible
            int pos = ip.indexOf('.');
            int oct1 = Integer.parseInt(ip.substring(0, pos));
            int pos2 = ip.indexOf('.', pos+1);
            int oct2 = Integer.parseInt(ip.substring(pos+1, pos2));
            pos = ip.indexOf('.', pos2+1);
            int oct3 = Integer.parseInt(ip.substring(pos2+1, pos));
            int oct4 = Integer.parseInt(ip.substring(pos+1));
            return "subnet"+(oct4&128)+"."+oct3+"."+oct2+"."+oct1+".in-addr.arpa";
        } else throw new IllegalArgumentException("Unsupported netmask: "+netmask);
    }

    Object getColumnImpl(int i) {
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

    public List<DNSRecord> getDNSRecords() throws IOException, SQLException {
        return table.connector.getDnsRecords().getDNSRecords(this);
    }

    public List<DNSRecord> getDNSRecords(String domain, DNSType type) throws IOException, SQLException {
        return table.connector.getDnsRecords().getDNSRecords(this, domain, type);
    }

    public String getFile() {
	return file;
    }

    public String getHostmaster() {
	return hostmaster;
    }

    public Package getPackage() throws SQLException, IOException {
	Package obj=table.connector.getPackages().get(packageName);
	if(obj==null) throw new SQLException("Unable to find Package: "+packageName);
	return obj;
    }

    public long getSerial() {
	return serial;
    }
    
    public int getTTL() {
        return ttl;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_ZONES;
    }

    public String getZone() {
	return pkey;
    }

    public String getZoneFile() throws SQLException, IOException {
        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        PrintWriter out=new PrintWriter(bout);
        printZoneFile(out);
        out.flush();
        return new String(bout.toByteArray());
    }

    public void init(ResultSet result) throws SQLException {
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

    private static void printRecord(PrintWriter out, String domain, int ttl, int recordTtl, String type, int mx, String destination) {
        out.print(domain);
        int count=Math.max(1, 24-domain.length());
        for(int d=0;d<count;d++) out.print(' ');
        if (recordTtl!=DNSRecord.NO_TTL) {
            String s=String.valueOf(recordTtl);
            out.print(s);
            count=Math.max(1, 24-s.length());
        } else {
            String s=String.valueOf(ttl);
            out.print(s);
            count=Math.max(1, 24-s.length());
        }
        for(int d=0;d<count;d++) out.print(' ');
        out.print("IN   ");
        out.print(type);
        count=Math.max(1, 8-type.length());
        for(int d=0;d<count;d++) out.print(' ');
        if(mx!=DNSRecord.NO_MX_PRIORITY) {
            out.print(mx);
            out.print(' ');
        }
        if(type.equals(DNSType.TXT)) {
            // Double-quote TXT types and filter " and anything < (space) or >= (char)0x7f from the destination
            out.print('"');
            for(int d=0, dlen=destination.length(); d<dlen; d++) {
                char ch = destination.charAt(d);
                if(
                    ch!='"'
                    && ch>=' '
                    && ch<(char)0x7f
                ) out.print(ch);
            }
            out.print('"');
        } else {
            out.print(destination);
        }
    }

    public void printZoneFile(PrintWriter out) throws SQLException, IOException {
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
                + "                                300     ; minimum\n"
                + "                                )\n");
        if(firstNS==null) {
            // Add the default nameservers because named will refuse to start without them
            out.print("; No name servers configured, using the defaults\n");
            printRecord(out, "@", ttl, DNSRecord.NO_TTL, DNSType.NS, DNSRecord.NO_MX_PRIORITY, "ns1.aoindustries.com."); out.print('\n');
            printRecord(out, "@", ttl, DNSRecord.NO_TTL, DNSType.NS, DNSRecord.NO_MX_PRIORITY, "ns2.aoindustries.com."); out.print('\n');
            printRecord(out, "@", ttl, DNSRecord.NO_TTL, DNSType.NS, DNSRecord.NO_MX_PRIORITY, "ns3.aoindustries.com."); out.print('\n');
            printRecord(out, "@", ttl, DNSRecord.NO_TTL, DNSType.NS, DNSRecord.NO_MX_PRIORITY, "ns4.aoindustries.com."); out.print('\n');
        }
	int len=records.size();
	for(int c=0;c<len;c++) {
            DNSRecord record=records.get(c);
            boolean hasConflictAbove = false;
            for(int d=0;d<c;d++) {
                if(record.hasConflict(records.get(d))) {
                    hasConflictAbove = true;
                    break;
                }
            }
            if(hasConflictAbove) out.print("; Disabled due to conflict: ");
            printRecord(out, record.domain, ttl, record.ttl, record.type, record.mx_priority, record.destination);
            // Allow the first one when there is a conflict
            if(!hasConflictAbove) {
                boolean hasConflictBelow = false;
                for(int d=c+1;d<len;d++) {
                    if(record.hasConflict(records.get(d))) {
                        hasConflictBelow = true;
                        break;
                    }
                }
                if(hasConflictBelow) out.print(" ; Some records below have been disabled due to conflict");
            }
            out.print('\n');
	}
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	file=in.readUTF();
	packageName=in.readUTF().intern();
	hostmaster=in.readUTF().intern();
	serial=in.readLong();
        ttl=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(pkey.equals(API_ZONE)) reasons.add(new CannotRemoveReason<DNSZone>("Not allowed to remove the API Zone: "+API_ZONE));
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.DNS_ZONES, pkey);
    }
    
    public void setTTL(int ttl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_DNS_ZONE_TTL, pkey, ttl);
        this.ttl=ttl;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(file);
        out.writeUTF(packageName);
        out.writeUTF(hostmaster);
        out.writeLong(serial);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_127)>=0) out.writeCompressedInt(ttl);
    }
}