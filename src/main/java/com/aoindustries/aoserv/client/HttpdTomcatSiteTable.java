/*
 * Copyright 2001-2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
		new OrderBy(HttpdTomcatSite.COLUMN_HTTPD_SITE_name+'.'+HttpdSite.COLUMN_SITE_NAME_name, ASCENDING),
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
		if(command.equalsIgnoreCase(AOSHCommand.START_JVM)) {
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
