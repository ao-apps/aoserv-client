/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

	BusinessServerTable(AOServConnector connector) {
		super(connector, BusinessServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BusinessServer.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(BusinessServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(BusinessServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addBusinessServer(Business business, Server server) throws IOException, SQLException {
		return connector.requestIntQueryIL(true, AOServProtocol.CommandID.ADD, SchemaTable.TableID.BUSINESS_SERVERS, business.pkey.toString(), server.pkey);
	}

	public BusinessServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(BusinessServer.COLUMN_PKEY, pkey);
	}

	List<BusinessServer> getBusinessServers(Business bu) throws IOException, SQLException {
		return getIndexedRows(BusinessServer.COLUMN_ACCOUNTING, bu.pkey);
	}

	List<BusinessServer> getBusinessServers(Server server) throws IOException, SQLException {
		return getIndexedRows(BusinessServer.COLUMN_SERVER, server.pkey);
	}

	List<Business> getBusinesses(Server server) throws IOException, SQLException {
		// Use the cache and convert
		List<BusinessServer> cached=getBusinessServers(server);
		int size=cached.size();
		List<Business> businesses=new ArrayList<Business>(size);
		for(int c=0;c<size;c++) businesses.add(cached.get(c).getBusiness());
		return businesses;
	}

	BusinessServer getBusinessServer(Business bu, Server se) throws IOException, SQLException {
		int pkey=se.pkey;

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

	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESS_SERVERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_SERVER, args, 2, err)) {
				int pkey=connector.getSimpleAOClient().addBusinessServer(
					AOSH.parseAccountingCode(args[1], "business"),
					args[2]
				);
				out.println(pkey);
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