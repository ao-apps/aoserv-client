package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  DNSRecord
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSRecordTable extends CachedTableIntegerKey<DNSRecord> {

    DNSRecordTable(AOServConnector connector) {
	super(connector, DNSRecord.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DNSRecord.COLUMN_ZONE_name, ASCENDING),
        new OrderBy(DNSRecord.COLUMN_DOMAIN_name, ASCENDING),
        new OrderBy(DNSRecord.COLUMN_TYPE_name, ASCENDING),
        new OrderBy(DNSRecord.COLUMN_MX_PRIORITY_name, ASCENDING),
        new OrderBy(DNSRecord.COLUMN_DESTINATION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addDNSRecord(
	DNSZone zone,
	String domain,
	DNSType type,
	int mx_priority,
	String destination,
        int ttl
    ) {
	return connector.requestIntQueryIL(
            AOServProtocol.CommandID.ADD,
            SchemaTable.TableID.DNS_RECORDS,
            zone.pkey,
            domain,
            type.pkey,
            mx_priority,
            destination,
            ttl
	);
    }

    public DNSRecord get(Object pkey) {
	return getUniqueRow(DNSRecord.COLUMN_PKEY, pkey);
    }

    public DNSRecord get(int pkey) {
	return getUniqueRow(DNSRecord.COLUMN_PKEY, pkey);
    }

    List<DNSRecord> getDNSRecords(DNSZone dnsZone) {
        return getIndexedRows(DNSRecord.COLUMN_ZONE, dnsZone.pkey);
    }

    List<DNSRecord> getDNSRecords(DNSZone dnsZone, String domain, DNSType dnsType) {
        String type=dnsType.pkey;

        // Use the index first
	List<DNSRecord> cached=getDNSRecords(dnsZone);
	int size=cached.size();
	List<DNSRecord> matches=new ArrayList<DNSRecord>(size);
	for(int c=0;c<size;c++) {
            DNSRecord record=cached.get(c);
            if(
                record.type.equals(type)
                && record.domain.equals(domain)
            ) matches.add(record);
	}
        return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_RECORDS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_DNS_RECORD)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_DNS_RECORD, args, 6, err)) {
                int pkey=connector.simpleAOClient.addDNSRecord(
                    args[1],
                    args[2],
                    args[3],
                    args[4].length()==0?DNSRecord.NO_MX_PRIORITY:AOSH.parseInt(args[4], "mx_priority"),
                    args[5],
                    args[6].length()==0?DNSRecord.NO_TTL:AOSH.parseInt(args[6], "ttl")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_DNS_RECORD)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_DNS_RECORD, args, 1, err)) {
                connector.simpleAOClient.removeDNSRecord(
                    AOSH.parseInt(args[1], "pkey")
                );
            }
            return true;
	}
	return false;
    }
}