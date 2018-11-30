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
package com.aoindustries.aoserv.client.web.tomcat;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.NetBind;
import com.aoindustries.aoserv.client.net.Server;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.web.HttpdServer;
import com.aoindustries.aoserv.client.web.HttpdSiteBind;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @see  HttpdWorker
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdWorkerTable extends CachedTableIntegerKey<HttpdWorker> {

	public HttpdWorkerTable(AOServConnector connector) {
		super(connector, HttpdWorker.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdWorker.COLUMN_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(HttpdWorker.COLUMN_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(HttpdWorker.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public HttpdWorker get(int bind) throws IOException, SQLException {
		return getUniqueRow(HttpdWorker.COLUMN_BIND, bind);
	}

	public List<HttpdWorker> getHttpdWorkers(HttpdServer server) throws IOException, SQLException {
		int serverPKey=server.getPkey();
		List<HttpdWorker> cached=getRows();
		int size=cached.size();
		List<HttpdWorker> matches=new ArrayList<>(size);
	Loop:
		for(int c=0;c<size;c++) {
			HttpdWorker worker=cached.get(c);
			HttpdTomcatSite hts=worker.getTomcatSite();
			if(hts!=null) {
				List<HttpdSiteBind> binds=hts.getHttpdSite().getHttpdSiteBinds();
				// If one of the binds is this server, then count as a match
				for (HttpdSiteBind bind : binds) {
					if (bind.getHttpdBind().getHttpdServer_pkey() == serverPKey) {
						matches.add(worker);
						continue Loop;
					}
				}
			} else {
				HttpdSharedTomcat hst=worker.getHttpdSharedTomcat();
				if(hst!=null) {
					// If one of the binds is this server, then count as a match
					for(HttpdTomcatSharedSite htss : hst.getHttpdTomcatSharedSites()) {
						List<HttpdSiteBind> binds=htss.getHttpdTomcatSite().getHttpdSite().getHttpdSiteBinds();
						for (HttpdSiteBind bind : binds) {
							if (bind.getHttpdBind().getHttpdServer_pkey() == serverPKey) {
								matches.add(worker);
								continue Loop;
							}
						}
					}
				} else {
					connector.getLogger().log(Level.WARNING, "pkey="+worker.getPkey(), new SQLException("HttpdWorker doesn't have either HttpdTomcatSite or HttpdSharedTomcat"));
				}
			}
		}
		return matches;
	}

	List<HttpdWorker> getHttpdWorkers(HttpdTomcatSite tomcatSite) throws IOException, SQLException {
		return getIndexedRows(HttpdWorker.COLUMN_TOMCAT_SITE, tomcatSite.getPkey());
	}

	public HttpdWorker getHttpdWorker(NetBind nb) throws IOException, SQLException {
		return getUniqueRow(HttpdWorker.COLUMN_BIND, nb.getId());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_WORKERS;
	}
}
