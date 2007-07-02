package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSiteTable extends CachedTableIntegerKey<HttpdTomcatSite> {

    HttpdTomcatSiteTable(AOServConnector connector) {
	super(connector, HttpdTomcatSite.class);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_SITES;
    }

    public HttpdTomcatSite get(Object pkey) {
	return getUniqueRow(HttpdTomcatSite.COLUMN_HTTPD_SITE, pkey);
    }

    public HttpdTomcatSite get(int pkey) {
	return getUniqueRow(HttpdTomcatSite.COLUMN_HTTPD_SITE, pkey);
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.START_JVM)) {
            if(AOSH.checkParamCount(AOSHCommand.START_JVM, args, 2, err)) {
                String message=connector.simpleAOClient.startJVM(args[1], args[2]);
                if(message!=null) {
                    err.println("aosh: "+AOSHCommand.START_JVM+": "+message);
                    err.flush();
                }
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.STOP_JVM)) {
            if(AOSH.checkParamCount(AOSHCommand.STOP_JVM, args, 2, err)) {
                String message=connector.simpleAOClient.stopJVM(args[1], args[2]);
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