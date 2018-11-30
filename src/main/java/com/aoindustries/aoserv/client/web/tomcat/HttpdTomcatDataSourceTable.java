/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2012, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.web.HttpdSite;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  HttpdTomcatDataSource
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatDataSourceTable extends CachedTableIntegerKey<HttpdTomcatDataSource> {

	public HttpdTomcatDataSourceTable(AOServConnector connector) {
		super(connector, HttpdTomcatDataSource.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_PATH_name, ASCENDING),
		new OrderBy(HttpdTomcatDataSource.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatDataSource(
		HttpdTomcatContext htc,
		String name,
		String driverClassName,
		String url,
		String username,
		String password,
		int maxActive,
		int maxIdle,
		int maxWait,
		String validationQuery
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES,
			htc.getPkey(),
			name,
			driverClassName,
			url,
			username,
			password,
			maxActive,
			maxIdle,
			maxWait,
			validationQuery==null ? "" : validationQuery
		);
	}

	@Override
	public HttpdTomcatDataSource get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdTomcatDataSource.COLUMN_PKEY, pkey);
	}

	List<HttpdTomcatDataSource> getHttpdTomcatDataSources(HttpdTomcatContext htc) throws IOException, SQLException {
		return getIndexedRows(HttpdTomcatDataSource.COLUMN_TOMCAT_CONTEXT, htc.getPkey());
	}

	HttpdTomcatDataSource getHttpdTomcatDataSource(HttpdTomcatContext htc, String name) throws IOException, SQLException {
		// Use index first
		List<HttpdTomcatDataSource> dataSources=getHttpdTomcatDataSources(htc);
		for(HttpdTomcatDataSource dataSource : dataSources) {
			if(dataSource.name.equals(name)) return dataSource;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_DATA_SOURCE)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_DATA_SOURCE, args, 12, err)) {
				out.println(
					connector.getSimpleAOClient().addHttpdTomcatDataSource(
						args[1],
						args[2],
						args[3],
						args[4],
						args[5],
						args[6],
						args[7],
						args[8],
						AOSH.parseInt(args[9], "max_active"),
						AOSH.parseInt(args[10], "max_idle"),
						AOSH.parseInt(args[11], "max_wait"),
						args[12]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_TOMCAT_DATA_SOURCE)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_TOMCAT_DATA_SOURCE, args, 1, err)) {
				connector.getSimpleAOClient().removeHttpdTomcatDataSource(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.UPDATE_HTTPD_TOMCAT_DATA_SOURCE)) {
			if(AOSH.checkParamCount(AOSHCommand.UPDATE_HTTPD_TOMCAT_DATA_SOURCE, args, 13, err)) {
				connector.getSimpleAOClient().updateHttpdTomcatDataSource(
					args[1],
					args[2],
					args[3],
					args[4],
					args[5],
					args[6],
					args[7],
					args[8],
					args[9],
					AOSH.parseInt(args[10], "max_active"),
					AOSH.parseInt(args[11], "max_idle"),
					AOSH.parseInt(args[12], "max_wait"),
					args[13]
				);
			}
			return true;
		} else return false;
	}
}
