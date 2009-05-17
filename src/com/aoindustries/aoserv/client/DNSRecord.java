package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * A <code>DNSRecord</code> is one line of a <code>DNSZone</code>
 * (name server zone file).
 *
 * @see  DNSZone
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSRecord extends CachedObjectIntegerKey<DNSRecord> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_ZONE=1
    ;
    static final String COLUMN_ZONE_name = "zone";
    static final String COLUMN_DOMAIN_name = "domain";
    static final String COLUMN_TYPE_name = "type";
    static final String COLUMN_MX_PRIORITY_name = "mx_priority";
    static final String COLUMN_DESTINATION_name = "destination";

    public static final int NO_MX_PRIORITY=-1;
    public static final int NO_TTL=-1;

    String
        zone,
        domain,
        type
    ;
    int mx_priority;
    String destination;
    int dhcpAddress;
    int ttl;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_ZONE: return zone;
            case 2: return domain;
            case 3: return type;
            case 4: return mx_priority==NO_MX_PRIORITY?null:Integer.valueOf(mx_priority);
            case 5: return destination;
            case 6: return dhcpAddress==-1?null:Integer.valueOf(dhcpAddress);
            case 7: return ttl==NO_TTL?null:Integer.valueOf(ttl);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDestination() {
	return destination;
    }
    
    public IPAddress getDHCPAddress() throws SQLException, IOException {
        if(dhcpAddress==-1) return null;
        IPAddress ia=table.connector.getIpAddresses().get(dhcpAddress);
        if(ia==null) throw new SQLException("Unable to find IPAddress: "+dhcpAddress);
        return ia;
    }
    
    public int getTTL() {
        return ttl;
    }

    public String getDomain() {
	return domain;
    }

    /**
     * Gets the domain, but in fully-qualified, absolute path (with trailing period).
     */
    public String getAbsoluteDomain() {
        if(domain.equals("@")) return zone;
        if(domain.endsWith(".")) return domain;
        return domain+'.'+zone;
    }

    /**
     * Checks if this record conflicts with the provided record, meaning they may not both exist
     * in a zone file at the same time.  The current conflicts checked are:
     * <ol>
     *   <li>CNAME must exist by itself, and only one CNAME maximum, per domain</li>
     * </ol>
     *
     * @return <code>true</code> if there is a conflict, <code>false</code> if the records may coexist.
     */
    public boolean hasConflict(DNSRecord other) {
        String domain1 = getAbsoluteDomain();
        String domain2 = other.getAbsoluteDomain();
        
        // Look for CNAME conflict
        if(domain1.equals(domain2)) {
            // If either (or both) are CNAME, there is a conflict
            if(
                type.equals(DNSType.CNAME)
                || other.type.equals(DNSType.CNAME)
            ) {
                return true;
            }
        }
        return false;
    }

    public int getMXPriority() {
	return mx_priority;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_RECORDS;
    }

    public DNSType getType() throws SQLException, IOException {
	DNSType obj=table.connector.getDnsTypes().get(type);
	if(obj==null) throw new SQLException("Unable to find DNSType: "+type);
	return obj;
    }

    public DNSZone getZone() throws SQLException, IOException {
	DNSZone obj=table.connector.getDnsZones().get(zone);
	if(obj==null) throw new SQLException("Unable to find DNSZone: "+zone);
	return obj;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	zone=result.getString(2);
	domain=result.getString(3);
	type=result.getString(4);
	mx_priority=result.getInt(5);
	if(result.wasNull()) mx_priority=NO_MX_PRIORITY;
	destination=result.getString(6);
        dhcpAddress=result.getInt(7);
        if(result.wasNull()) dhcpAddress=-1;
        ttl=result.getInt(8);
        if(result.wasNull()) ttl=NO_TTL;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	zone=in.readUTF().intern();
	domain=in.readUTF().intern();
	type=in.readUTF().intern();
	mx_priority=in.readCompressedInt();
	destination=in.readUTF().intern();
        dhcpAddress=in.readCompressedInt();
        ttl=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.DNS_RECORDS, pkey);
    }

    @Override
    String toStringImpl() {
	StringBuilder SB=new StringBuilder();
	SB
            .append(zone)
            .append(": ")
            .append(domain)
            .append(" IN ")
            .append(type)
	;
	if(mx_priority!=NO_MX_PRIORITY) SB.append(' ').append(mx_priority);
	SB
            .append(' ').append(destination)
            .append(' ').append(ttl)
        ;
    	return SB.toString();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(zone);
	out.writeUTF(domain);
	out.writeUTF(type);
	out.writeCompressedInt(mx_priority);
	out.writeUTF(destination);
        out.writeCompressedInt(dhcpAddress);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_127)>=0) out.writeCompressedInt(ttl);
    }
}