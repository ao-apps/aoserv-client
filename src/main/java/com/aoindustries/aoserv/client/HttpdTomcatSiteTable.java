/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018  AO Industries, Inc.
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

/**
 * @see  HttpdTomcatSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSiteTable extends CachedTableIntegerKey<HttpdTomcatSite> {

	HttpdTomcatSiteTable(AOServConnector connector) {
		super(connector, HttpdTomcatSite.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_NAME_name, ASCENDING),
		new OrderBy(HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.HTTPD_TOMCAT_SITES;
	}

	@Override
	public HttpdTomcatSite get(int pkey) throws IOException, SQLException {
		return getUniqueRow(HttpdTomcatSite.COLUMN_HTTPD_SITE, pkey);
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_TOMCAT_SITE_BLOCK_WEBINF)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_TOMCAT_SITE_BLOCK_WEBINF, args, 3, err)) {
				connector.getSimpleAOClient().setHttpdTomcatSiteBlockWebinf(
					args[1],
					args[2],
					AOSH.parseBoolean(args[3], "block_webinf")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.START_JVM)) {
			if(AOSH.checkParamCount(AOSHCommand.START_JVM, args, 2, err)) {
				String message=connector.getSimpleAOClient().startJVM(args[1], args[2]);
				if(message!=null) {
					err.println("aosh: "+AOSHCommand.START_JVM+": "+message);
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.STOP_JVM)) {
			if(AOSH.checkParamCount(AOSHCommand.STOP_JVM, args, 2, err)) {
				String message=connector.getSimpleAOClient().stopJVM(args[1], args[2]);
				if(message!=null) {
					err.println("aosh: "+AOSHCommand.STOP_JVM+": "+message);
					err.flush();
				}
			}
			return true;
		}
		return false;
	}
}
