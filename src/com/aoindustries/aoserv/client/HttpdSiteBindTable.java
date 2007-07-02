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
 * @see  HttpdSiteBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteBindTable extends CachedTableIntegerKey<HttpdSiteBind> {

    HttpdSiteBindTable(AOServConnector connector) {
	super(connector, HttpdSiteBind.class);
    }

    public HttpdSiteBind get(Object pkey) {
	return getUniqueRow(HttpdSiteBind.COLUMN_PKEY, pkey);
    }

    public HttpdSiteBind get(int pkey) {
	return getUniqueRow(HttpdSiteBind.COLUMN_PKEY, pkey);
    }

    List<HttpdSiteBind> getHttpdSiteBinds(HttpdSite site) {
        return getIndexedRows(HttpdSiteBind.COLUMN_HTTPD_SITE, site.pkey);
    }

    List<HttpdSiteBind> getHttpdSiteBinds(HttpdSite site, HttpdServer server) {
        int serverPKey=server.pkey;

        // Use the index first
        List<HttpdSiteBind> cached=getHttpdSiteBinds(site);
	int size=cached.size();
        List<HttpdSiteBind> matches=new ArrayList<HttpdSiteBind>(size);
	for(int c=0;c<size;c++) {
            HttpdSiteBind siteBind=cached.get(c);
            if(siteBind.getHttpdBind().httpd_server==serverPKey) matches.add(siteBind);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SITE_BINDS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.DISABLE_HTTPD_SITE_BIND)) {
            if(AOSH.checkParamCount(AOSHCommand.DISABLE_HTTPD_SITE_BIND, args, 2, err)) {
                out.println(
                    connector.simpleAOClient.disableHttpdSiteBind(
                        AOSH.parseInt(args[1], "pkey"),
                        args[2]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_HTTPD_SITE_BIND)) {
            if(AOSH.checkParamCount(AOSHCommand.ENABLE_HTTPD_SITE_BIND, args, 1, err)) {
                connector.simpleAOClient.enableHttpdSiteBind(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_BIND_IS_MANUAL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_BIND_IS_MANUAL, args, 2, err)) {
                connector.simpleAOClient.setHttpdSiteBindIsManual(
                    AOSH.parseInt(args[1], "pkey"),
                    AOSH.parseBoolean(args[2], "is_manual")
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, args, 2, err)) {
                connector.simpleAOClient.setHttpdSiteBindRedirectToPrimaryHostname(
                    AOSH.parseInt(args[1], "pkey"),
                    AOSH.parseBoolean(args[2], "redirect_to_primary_hostname")
                );
            }
            return true;
	} else return false;
    }
}