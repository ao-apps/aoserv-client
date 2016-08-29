/*
 * Copyright 2006-2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

	HttpdTomcatParameterTable(AOServConnector connector) {
		super(connector, HttpdTomcatParameter.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_TOMCAT_SITE_name+'.'+HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT_name+'.'+HttpdTomcatContext.COLUMN_PATH_name, ASCENDING),
		new OrderBy(HttpdTomcatParameter.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
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
			htc.pkey,
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
		return getIndexedRows(HttpdTomcatParameter.COLUMN_TOMCAT_CONTEXT, htc.pkey);
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
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
