/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.dns;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Record
 *
 * @author  AO Industries, Inc.
 */
final public class RecordTable extends CachedTableIntegerKey<Record> {

	RecordTable(AOServConnector connector) {
		super(connector, Record.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Record.COLUMN_ZONE_name, ASCENDING),
		new OrderBy(Record.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(Record.COLUMN_TYPE_name, ASCENDING),
		new OrderBy(Record.COLUMN_PRIORITY_name, ASCENDING),
		new OrderBy(Record.COLUMN_WEIGHT_name, ASCENDING),
		new OrderBy(Record.COLUMN_DESTINATION_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addDNSRecord(
		Zone zone,
		String domain,
		RecordType type,
		int priority,
		int weight,
		int port,
		String destination,
		int ttl
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.DNS_RECORDS,
			zone.getZone(),
			domain,
			type.getType(),
			priority,
			weight,
			port,
			destination,
			ttl
		);
	}

	@Override
	public Record get(int id) throws IOException, SQLException {
		return getUniqueRow(Record.COLUMN_ID, id);
	}

	List<Record> getDNSRecords(Zone dnsZone) throws IOException, SQLException {
		return getIndexedRows(Record.COLUMN_ZONE, dnsZone.getZone());
	}

	List<Record> getDNSRecords(Zone dnsZone, String domain, RecordType dnsType) throws IOException, SQLException {
		String type=dnsType.getType();

		// Use the index first
		List<Record> cached=getDNSRecords(dnsZone);
		int size=cached.size();
		List<Record> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			Record record=cached.get(c);
			if(
				record.getType_type().equals(type)
				&& record.getDomain().equals(domain)
			) matches.add(record);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.DNS_RECORDS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_DNS_RECORD)) {
			if(AOSH.checkParamCount(Command.ADD_DNS_RECORD, args, 8, err)) {
				out.println(connector.getSimpleAOClient().addDNSRecord(args[1],
						args[2],
						args[3],
						args[4].length()==0?Record.NO_PRIORITY : AOSH.parseInt(args[4], "priority"),
						args[5].length()==0?Record.NO_WEIGHT   : AOSH.parseInt(args[5], "weight"),
						args[6].length()==0?Record.NO_PORT     : AOSH.parseInt(args[6], "port"),
						args[7],
						args[8].length()==0?Record.NO_TTL      : AOSH.parseInt(args[8], "ttl")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_DNS_RECORD)) {
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
				err.print(Command.REMOVE_DNS_RECORD);
				err.println(": must be either 1 or 4 parameters");
				err.flush();
				return false;
			}
		}
		return false;
	}
}
