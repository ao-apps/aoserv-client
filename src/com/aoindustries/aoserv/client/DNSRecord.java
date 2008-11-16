package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
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

    public Object getColumn(int i) {
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
    
    public IPAddress getDHCPAddress() {
        if(dhcpAddress==-1) return null;
        IPAddress ia=table.connector.ipAddresses.get(dhcpAddress);
        if(ia==null) throw new WrappedException(new SQLException("Unable to find IPAddress: "+dhcpAddress));
        return ia;
    }
    
    public int getTTL() {
        return ttl;
    }

    public String getDomain() {
	return domain;
    }

    public int getMXPriority() {
	return mx_priority;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_RECORDS;
    }

    public DNSType getType() {
	DNSType obj=table.connector.dnsTypes.get(type);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find DNSType: "+type));
	return obj;
    }

    public DNSZone getZone() {
	DNSZone obj=table.connector.dnsZones.get(zone);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find DNSZone: "+zone));
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

    public void remove() {
	table.connector.requestUpdateIL(AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.DNS_RECORDS, pkey);
    }

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