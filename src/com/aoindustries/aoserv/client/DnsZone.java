/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A <code>DnsZone</code> is one domain hosted in the name servers.  It can have
 * any number of <code>DnsRecord</code>s.  Please see <code>DnsTld</code> for
 * domain restrictions.
 *
 * @see  DnsTld
 * @see  DnsRecord
 *
 * @author  AO Industries, Inc.
 */
final public class DnsZone extends Resource implements Comparable<DnsZone>, DtoFactory<com.aoindustries.aoserv.client.dto.DnsZone> /*, TODO: Removable, Dumpable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_TTL = 43200;

    /**
     * The zone that is in charge of the API.
     */
    // TODO: public static final String API_ZONE="aoindustries.com.";

    /**
     * The hostmaster that is placed in a newly created <code>DnsZone</code>.
     */
    // TODO: public static final String DEFAULT_HOSTMASTER="hostmaster."+API_ZONE;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private DomainName zone;
    private String file;
    private DomainName hostmaster;
    final private long serial;
    final private int ttl;

    public DnsZone(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        DomainName zone,
        String file,
        DomainName hostmaster,
        long serial,
        int ttl
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled);
        this.zone = zone;
        this.file = file;
        this.hostmaster = hostmaster;
        this.serial = serial;
        this.ttl = ttl;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        zone = intern(zone);
        file = intern(file);
        hostmaster = intern(hostmaster);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(DnsZone other) {
        return zone.compareTo(other.zone);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=RESOURCE_LAST_COLUMN+1, name="zone", index=IndexType.UNIQUE, description="the zone (domain) that is hosted")
    public DomainName getZone() {
        return zone;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+2, name="file", index=IndexType.UNIQUE, description="the filename of the zone file")
    public String getFile() {
        return file;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+3, name="hostmaster", description="the email address of the person in charge of the domain")
    public DomainName getHostmaster() {
        return hostmaster;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+4, name="serial", description="the ever-incrementing serial number for the file")
    public long getSerial() {
        return serial;
    }

    @SchemaColumn(order=RESOURCE_LAST_COLUMN+5, name="ttl", description="the number of seconds before distributed caches are refreshed")
    public int getTtl() {
        return ttl;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.DnsZone getDto() {
        return new com.aoindustries.aoserv.client.dto.DnsZone(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            getDto(zone),
            file,
            getDto(hostmaster),
            serial,
            ttl
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDnsRecords());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return zone.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<DnsRecord> getDnsRecords() throws RemoteException {
        return getConnector().getDnsRecords().filterIndexed(DnsRecord.COLUMN_ZONE, this);
    }
    /* TODO
    public List<DnsRecord> getDnsRecords(String domain, DnsType type) throws IOException, SQLException {
        return getConnector().getDnsRecords().getDnsRecords(this, domain, type);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Zone File Creation">
    public boolean isArpa() {
        return zone.toString().endsWith(".in-addr.arpa");
    }

    private static void printRecord(Appendable out, String domain, int ttl, Integer recordTtl, String type, Integer mx, String destination) throws IOException {
        out.append(domain);
        int count=Math.max(1, 24-domain.length());
        for(int d=0;d<count;d++) out.append(' ');
        if(recordTtl!=null) {
            String s=recordTtl.toString();
            out.append(s);
            count=Math.max(1, 24-s.length());
        } else {
            String s=Integer.toString(ttl);
            out.append(s);
            count=Math.max(1, 24-s.length());
        }
        for(int d=0;d<count;d++) out.append(' ');
        out.append("IN   ");
        out.append(type);
        count=Math.max(1, 8-type.length());
        for(int d=0;d<count;d++) out.append(' ');
        if(mx!=null) {
            out.append(mx.toString());
            out.append(' ');
        }
        if(type.equals(DnsType.TXT)) {
            // Double-quote TXT types and filter " and anything < (space) or >= (char)0x7f from the destination
            out.append('"');
            for(int d=0, dlen=destination.length(); d<dlen; d++) {
                char ch = destination.charAt(d);
                if(
                    ch!='"'
                    && ch>=' '
                    && ch<(char)0x7f
                ) out.append(ch);
            }
            out.append('"');
        } else {
            out.append(destination);
        }
    }

    public String getZoneFile() throws IOException {
        StringBuilder SB = new StringBuilder();
        printZoneFile(SB);
        return SB.toString();
    }

    public void printZoneFile(Appendable out) throws IOException {
        Business bu = getBusiness();
        Brand brand = null;
        while(bu!=null && brand==null) {
            brand = bu.getBrand();
            bu = bu.getParentBusiness();
        }
    	List<DnsRecord> records = new ArrayList<DnsRecord>(getDnsRecords());
        Collections.sort(records);
    	out.append("$TTL    ");
        out.append(Integer.toString(ttl));
        out.append('\n');
    	if(!isArpa()) {
            out.append("$ORIGIN ");
            out.append(zone.toString());
            out.append(".\n");
    	}
    	out.append("@                       ");
        out.append(Integer.toString(ttl));
        out.append(" IN   SOA     ");
        // Find the first nameserver
        DnsRecord firstNS=null;
        for(DnsRecord record : records) {
            if(record.getType().getType()==DnsType.NS) { // OK - interned
                firstNS=record;
                break;
            }
        }
        if(firstNS==null) {
            if(brand==null) throw new RemoteException("Unable to find Brand");
            out.append(brand.getNameserver1().toString());
        } else {
            out.append(firstNS.getDataDomainName().toString());
        }
        out.append(".   "); out.append(hostmaster.toString()); out.append(". (\n"
                + "                                "); out.append(Long.toString(serial)); out.append(" ; serial\n"
                + "                                3600    ; refresh\n"
                + "                                600     ; retry\n"
                + "                                1814400 ; expiry\n"
                + "                                300     ; minimum\n"
                + "                                )\n");
        if(firstNS==null) {
            // Add the default nameservers because named will refuse to start without them
            out.append("; No name servers configured, using the defaults\n");
            if(brand==null) throw new RemoteException("Unable to find Brand");
            printRecord(out, "@", ttl, null, DnsType.NS, null, brand.getNameserver1().toString()+'.');
            out.append('\n');
            printRecord(out, "@", ttl, null, DnsType.NS, null, brand.getNameserver2().toString()+'.');
            out.append('\n');
            DomainName ns3 = brand.getNameserver3();
            if(ns3!=null) {
                printRecord(out, "@", ttl, null, DnsType.NS, null, ns3.toString()+'.');
                out.append('\n');
            }
            DomainName ns4 = brand.getNameserver3();
            if(ns4!=null) {
                printRecord(out, "@", ttl, null, DnsType.NS, null, ns4.toString()+'.');
                out.append('\n');
            }
        }
        int len=records.size();
        for(int c=0;c<len;c++) {
            DnsRecord record=records.get(c);
            boolean hasConflictAbove = false;
            for(int d=0;d<c;d++) {
                if(record.hasConflict(records.get(d))) {
                    hasConflictAbove = true;
                    break;
                }
            }
            if(hasConflictAbove) out.append("; Disabled due to conflict: ");
            // Each record type
            InetAddress dataIpAddress = record.getDataIpAddress();
            if(dataIpAddress!=null) printRecord(out, record.getDomain(), ttl, record.getTtl(), record.getType().getType(), record.getMxPriority(), dataIpAddress.toString());
            else {
                if(record.getDataDomainName()!=null) printRecord(out, record.getDomain(), ttl, record.getTtl(), record.getType().getType(), record.getMxPriority(), record.getRelativeDataDomainName());
                else {
                    String dataText = record.getDataText();
                    if(dataText!=null) printRecord(out, record.getDomain(), ttl, record.getTtl(), record.getType().getType(), record.getMxPriority(), dataText);
                    else throw new AssertionError();
                }
            }

            // Allow the first one when there is a conflict
            if(!hasConflictAbove) {
                boolean hasConflictBelow = false;
                for(int d=c+1;d<len;d++) {
                    if(record.hasConflict(records.get(d))) {
                        hasConflictBelow = true;
                        break;
                    }
                }
                if(hasConflictBelow) out.append(" ; Some records below have been disabled due to conflict");
            }
            out.append('\n');
    	}
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addDnsRecord(
        String domain,
        DnsType type,
        int mx_priority,
        String destination,
        int ttl
    ) throws IOException, SQLException {
    	return getConnector().getDnsRecords().addDnsRecord(this, domain, type, mx_priority, destination, ttl);
    }

    public void dump(PrintWriter out) throws SQLException, IOException {
        printZoneFile(out);
    }

    public DnsType[] getAllowedDnsTypes() throws IOException, SQLException {
	DnsTypeService tt=getConnector().getDnsTypes();
	if(isArpa()) {
            DnsType[] types={
                tt.get(DnsType.NS),
                tt.get(DnsType.PTR)
            };
            return types;
	} else {
            DnsType[] types={
                tt.get(DnsType.A),
                tt.get(DnsType.CNAME),
                tt.get(DnsType.MX),
                tt.get(DnsType.NS)
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

    public static long getCurrentSerial() {
        Calendar cal=Calendar.getInstance();
        return
            cal.get(Calendar.YEAR)*1000000L
            + (cal.get(Calendar.MONTH)+1)*10000
            + cal.get(Calendar.DATE)*100
            + 01
    	;
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(pkey==API_ZONE) reasons.add(new CannotRemoveReason<DnsZone>("Not allowed to remove the API Zone: "+API_ZONE)); // OK - interned
        return reasons;
    }

    public void remove() throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.DNS_ZONES, pkey);
    }
    
    public void setTTL(int ttl) throws IOException, SQLException {
        getConnector().requestUpdateIL(true, AOServProtocol.CommandID.SET_DNS_ZONE_TTL, pkey, ttl);
        this.ttl=ttl;
    }
    */
    // </editor-fold>
}