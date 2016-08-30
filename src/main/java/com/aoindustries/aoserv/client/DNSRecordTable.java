/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2012, 2014, 2015, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  DNSRecord
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
		new OrderBy(DNSRecord.COLUMN_PRIORITY_name, ASCENDING),
		new OrderBy(DNSRecord.COLUMN_WEIGHT_name, ASCENDING),
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
		int priority,
		int weight,
		int port,
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
			priority,
			weight,
			port,
			destination,
			ttl
		);
	}

	@Override
	public DNSRecord get(int pkey) throws IOException, SQLException {
		return getUniqueRow(DNSRecord.COLUMN_PKEY, pkey);
	}

	List<DNSRecord> getDNSRecords(DNSZone dnsZone) throws IOException, SQLException {
		return getIndexedRows(DNSRecord.COLUMN_ZONE, dnsZone.pkey);
	}

	List<DNSRecord> getDNSRecords(DNSZone dnsZone, String domain, DNSType dnsType) throws IOException, SQLException {
		String type=dnsType.pkey;

		// Use the index first
		List<DNSRecord> cached=getDNSRecords(dnsZone);
		int size=cached.size();
		List<DNSRecord> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			DNSRecord record=cached.get(c);
			if(
				record.type.equals(type)
				&& record.domain.equals(domain)
			) matches.add(record);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_RECORDS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_DNS_RECORD)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_DNS_RECORD, args, 8, err)) {
				int pkey=connector.getSimpleAOClient().addDNSRecord(
					args[1],
					args[2],
					args[3],
					args[4].length()==0?DNSRecord.NO_PRIORITY : AOSH.parseInt(args[4], "priority"),
					args[5].length()==0?DNSRecord.NO_WEIGHT   : AOSH.parseInt(args[5], "weight"),
					args[6].length()==0?DNSRecord.NO_PORT     : AOSH.parseInt(args[6], "port"),
					args[7],
					args[8].length()==0?DNSRecord.NO_TTL      : AOSH.parseInt(args[8], "ttl")
				);
				out.println(pkey);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_DNS_RECORD)) {
			if(args.length == 2) {
				connector.getSimpleAOClient().removeDNSRecord(
					AOSH.parseInt(args[1], "pkey")
				);
				return true;
			} else if(args.length == 5) {
				connector.getSimpleAOClient().removeDNSRecord(
					args[1],
					args[2],
					args[3],
					args[4]
				);
				return true;
			} else {
				err.print("aosh: ");
				err.print(AOSHCommand.REMOVE_DNS_RECORD);
				err.println(": must be either 1 or 4 parameters");
				err.flush();
				return false;
			}
		}
		return false;
	}
}
