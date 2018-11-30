/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.account;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Server;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  BusinessServerTable
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessServerTable extends CachedTableIntegerKey<BusinessServer> {

	public BusinessServerTable(AOServConnector connector) {
		super(connector, BusinessServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BusinessServer.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(BusinessServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BusinessServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addBusinessServer(Business business, Server server) throws IOException, SQLException {
		return connector.requestIntQueryIL(true, AOServProtocol.CommandID.ADD, SchemaTable.TableID.BUSINESS_SERVERS, business.getAccounting().toString(), server.getPkey());
	}

	@Override
	public BusinessServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(BusinessServer.COLUMN_PKEY, pkey);
	}

	List<BusinessServer> getBusinessServers(Business bu) throws IOException, SQLException {
		return getIndexedRows(BusinessServer.COLUMN_ACCOUNTING, bu.getAccounting());
	}

	List<BusinessServer> getBusinessServers(Server server) throws IOException, SQLException {
		return getIndexedRows(BusinessServer.COLUMN_SERVER, server.getPkey());
	}

	public List<Business> getBusinesses(Server server) throws IOException, SQLException {
		// Use the cache and convert
		List<BusinessServer> cached=getBusinessServers(server);
		int size=cached.size();
		List<Business> businesses=new ArrayList<>(size);
		for(int c=0;c<size;c++) businesses.add(cached.get(c).getBusiness());
		return businesses;
	}

	BusinessServer getBusinessServer(Business bu, Server se) throws IOException, SQLException {
		int pkey=se.getPkey();

		// Use the index first
		List<BusinessServer> cached=getBusinessServers(bu);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			BusinessServer bs=cached.get(c);
			if(bs.server==pkey) return bs;
		}
		return null;
	}

	Server getDefaultServer(Business business) throws IOException, SQLException {
		// Use index first
		List<BusinessServer> cached=getBusinessServers(business);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			BusinessServer bs=cached.get(c);
			if(bs.is_default) return bs.getServer();
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESS_SERVERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_SERVER, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().addBusinessServer(
						AOSH.parseAccountingCode(args[1], "business"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_BUSINESS_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_BUSINESS_SERVER, args, 2, err)) {
				connector.getSimpleAOClient().removeBusinessServer(
					AOSH.parseAccountingCode(args[1], "business"),
					args[2]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_DEFAULT_BUSINESS_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_DEFAULT_BUSINESS_SERVER, args, 2, err)) {
				connector.getSimpleAOClient().setDefaultBusinessServer(
					AOSH.parseAccountingCode(args[1], "business"),
					args[2]
				);
			}
			return true;
		} else return false;
	}
}
