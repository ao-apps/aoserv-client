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
 * @see  HttpdTomcatParameter
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatParameterTable extends CachedTableIntegerKey<HttpdTomcatParameter> {

	public HttpdTomcatParameterTable(AOServConnector connector) {
		super(connector, HttpdTomcatParameter.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_PATH_name, ASCENDING),
		new OrderBy(HttpdTomcatParameter.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addHttpdTomcatParameter(
		HttpdTomcatContext htc,
		String name,
		String value,
		boolean override,
		String description
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.HTTPD_TOMCAT_PARAMETERS,
			htc.getPkey(),
			name,
			value,
			override,
			description==null ? "" : description
		);
	}

	@Override
	public HttpdTomcatParameter get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdTomcatParameter.COLUMN_PKEY, pkey);
	}

	List<HttpdTomcatParameter> getHttpdTomcatParameters(HttpdTomcatContext htc) throws IOException, SQLException {
		return getIndexedRows(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT, htc.getPkey());
	}

	HttpdTomcatParameter getHttpdTomcatParameter(HttpdTomcatContext htc, String name) throws IOException, SQLException {
		// Use index first
		List<HttpdTomcatParameter> parameters=getHttpdTomcatParameters(htc);
		for(HttpdTomcatParameter parameter : parameters) {
			if(parameter.name.equals(name)) return parameter;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_PARAMETERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_TOMCAT_PARAMETER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_TOMCAT_PARAMETER, args, 7, err)) {
				out.println(
					connector.getSimpleAOClient().addHttpdTomcatParameter(
						args[1],
						args[2],
						args[3],
						args[4],
						args[5],
						AOSH.parseBoolean(args[6], "override"),
						args[7]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_TOMCAT_PARAMETER)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_TOMCAT_PARAMETER, args, 1, err)) {
				connector.getSimpleAOClient().removeHttpdTomcatParameter(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.UPDATE_HTTPD_TOMCAT_PARAMETER)) {
			if(AOSH.checkParamCount(AOSHCommand.UPDATE_HTTPD_TOMCAT_PARAMETER, args, 8, err)) {
				connector.getSimpleAOClient().updateHttpdTomcatParameter(
					args[1],
					args[2],
					args[3],
					args[4],
					args[5],
					args[6],
					AOSH.parseBoolean(args[7], "override"),
					args[8]
				);
			}
			return true;
		} else return false;
	}
}
