/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.web;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdSiteAuthenticatedLocation
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteAuthenticatedLocationTable extends CachedTableIntegerKey<HttpdSiteAuthenticatedLocation> {

	public HttpdSiteAuthenticatedLocationTable(AOServConnector connector) {
		super(connector, HttpdSiteAuthenticatedLocation.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_NAME_name, ASCENDING),
		new OrderBy(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdSiteAuthenticatedLocation(
		HttpdSite hs,
		String path,
		boolean isRegularExpression,
		String authName,
		UnixPath authGroupFile,
		UnixPath authUserFile,
		String require,
		String handler
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS,
			hs.getPkey(),
			path,
			isRegularExpression,
			authName,
			authGroupFile==null ? "" : authGroupFile.toString(),
			authUserFile==null ? "" : authUserFile.toString(),
			require,
			handler==null ? "" : handler
		);
	}

	@Override
	public HttpdSiteAuthenticatedLocation get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdSiteAuthenticatedLocation.COLUMN_PKEY, pkey);
	}

	List<HttpdSiteAuthenticatedLocation> getHttpdSiteAuthenticatedLocations(HttpdSite site) throws IOException, SQLException {
		return getIndexedRows(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE, site.getPkey());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command = args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_SITE_AUTHENTICATED_LOCATION)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_SITE_AUTHENTICATED_LOCATION, args, 9, err)) {
				out.println(
					connector.getSimpleAOClient().addHttpdSiteAuthenticatedLocation(
						args[1],
						args[2],
						args[3],
						AOSH.parseBoolean(args[4], "is_regular_expression"),
						args[5],
						args[6].isEmpty() ? null : AOSH.parseUnixPath(args[6], "auth_group_file"),
						args[7].isEmpty() ? null : AOSH.parseUnixPath(args[7], "auth_user_file"),
						args[8],
						args[9].isEmpty() ? null : args[9]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_AUTHENTICATED_LOCATION_ATTRIBUTES, args, 9, err)) {
				connector.getSimpleAOClient().setHttpdSiteAuthenticatedLocationAttributes(
					args[1],
					args[2],
					args[3],
					AOSH.parseBoolean(args[4], "is_regular_expression"),
					args[5],
					args[6].isEmpty() ? null : AOSH.parseUnixPath(args[6], "auth_group_file"),
					args[7].isEmpty() ? null : AOSH.parseUnixPath(args[7], "auth_user_file"),
					args[8],
					args[9].isEmpty() ? null : args[9]
				);
			}
			return true;
		} else return false;
	}
}
