/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  DnsRecord
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.dns_records)
public interface DnsRecordService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,DnsRecord> {

    /* TODO
    int addDnsRecord(
	DnsZone zone,
	String domain,
	DnsType type,
	int mx_priority,
	String destination,
        int ttl
    ) throws IOException, SQLException {
    	return connector.requestIntQueryIL(
            true,
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

    List<DnsRecord> getDnsRecords(DnsZone dnsZone, String domain, DnsType dnsType) throws IOException, SQLException {
        String type=dnsType.pkey;

        // Use the index first
	List<DnsRecord> cached=getDnsRecords(dnsZone);
	int size=cached.size();
	List<DnsRecord> matches=new ArrayList<DnsRecord>(size);
	for(int c=0;c<size;c++) {
            DnsRecord record=cached.get(c);
            if(
                record.type.equals(type)
                && record.domain.equals(domain)
            ) matches.add(record);
	}
        return matches;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_DNS_RECORD)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_DNS_RECORD, args, 6, err)) {
                int pkey=connector.getSimpleAOClient().addDnsRecord(
                    args[1],
                    args[2],
                    args[3],
                    args[4].length()==0?DnsRecord.NO_MX_PRIORITY:AOSH.parseInt(args[4], "mx_priority"),
                    args[5],
                    args[6].length()==0?DnsRecord.NO_TTL:AOSH.parseInt(args[6], "ttl")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_DNS_RECORD)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_DNS_RECORD, args, 1, err)) {
                connector.getSimpleAOClient().removeDnsRecord(
                    AOSH.parseInt(args[1], "pkey")
                );
            }
            return true;
	}
	return false;
    }
     */
}