/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2009, 2016  AO Industries, Inc.
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdSiteAuthenticatedLocation
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteAuthenticatedLocationTable extends CachedTableIntegerKey<HttpdSiteAuthenticatedLocation> {

	HttpdSiteAuthenticatedLocationTable(AOServConnector connector) {
		super(connector, HttpdSiteAuthenticatedLocation.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdSiteAuthenticatedLocation(
		HttpdSite hs,
		String path,
		boolean isRegularExpression,
		String authName,
		String authGroupFile,
		String authUserFile,
		String require
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(true, AOServProtocol.CommandID.ADD, SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS, hs.getPkey(), path, isRegularExpression, authName, authGroupFile, authUserFile, require);
	}

	@Override
	public HttpdSiteAuthenticatedLocation get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdSiteAuthenticatedLocation.COLUMN_PKEY, pkey);
	}

	List<HttpdSiteAuthenticatedLocation> getHttpdSiteAuthenticatedLocations(HttpdSite site) throws IOException, SQLException {
		return getIndexedRows(HttpdSiteAuthenticatedLocation.COLUMN_HTTPD_SITE, site.pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_SITE_AUTHENTICATED_LOCATIONS;
	}
}
