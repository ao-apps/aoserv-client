/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2012, 2016  AO Industries, Inc.
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
 * @see  HttpdSiteBind
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteBindTable extends CachedTableIntegerKey<HttpdSiteBind> {

	HttpdSiteBindTable(AOServConnector connector) {
		super(connector, HttpdSiteBind.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdSiteBind.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdSiteBind.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(HttpdSiteBind.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(HttpdSiteBind.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(HttpdSiteBind.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_PORT_name, ASCENDING),
		new OrderBy(HttpdSiteBind.COLUMN_HTTPD_BIND_name+'.'+HttpdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_NET_PROTOCOL_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public HttpdSiteBind get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdSiteBind.COLUMN_PKEY, pkey);
	}

	List<HttpdSiteBind> getHttpdSiteBinds(HttpdSite site) throws IOException, SQLException {
		return getIndexedRows(HttpdSiteBind.COLUMN_HTTPD_SITE, site.pkey);
	}

	List<HttpdSiteBind> getHttpdSiteBinds(HttpdSite site, HttpdServer server) throws SQLException, IOException {
		int serverPKey=server.pkey;

		// Use the index first
		List<HttpdSiteBind> cached=getHttpdSiteBinds(site);
		int size=cached.size();
		List<HttpdSiteBind> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			HttpdSiteBind siteBind=cached.get(c);
			if(siteBind.getHttpdBind().httpd_server==serverPKey) matches.add(siteBind);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_BINDS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.DISABLE_HTTPD_SITE_BIND)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_HTTPD_SITE_BIND, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableHttpdSiteBind(
						AOSH.parseInt(args[1], "pkey"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_HTTPD_SITE_BIND)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_HTTPD_SITE_BIND, args, 1, err)) {
				connector.getSimpleAOClient().enableHttpdSiteBind(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_BIND_IS_MANUAL)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_BIND_IS_MANUAL, args, 2, err)) {
				connector.getSimpleAOClient().setHttpdSiteBindIsManual(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseBoolean(args[2], "is_manual")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, args, 2, err)) {
				connector.getSimpleAOClient().setHttpdSiteBindRedirectToPrimaryHostname(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseBoolean(args[2], "redirect_to_primary_hostname")
				);
			}
			return true;
		} else return false;
	}
}
