package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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

    int addDNSRecord(
	DNSZone zone,
	String domain,
	DNSType type,
	int mx_priority,
	String destination,
        int ttl
    ) {
	return connector.requestIntQueryIL(
            AOServProtocol.ADD,
            SchemaTable.DNS_RECORDS,
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

    int getTableID() {
	return SchemaTable.DNS_RECORDS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_DNS_RECORD)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_DNS_RECORD, args, 5, err)) {
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