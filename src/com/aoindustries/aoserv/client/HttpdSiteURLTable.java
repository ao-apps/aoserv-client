package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdSiteURL
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteURLTable extends CachedTableIntegerKey<HttpdSiteURL> {

    HttpdSiteURLTable(AOServConnector connector) {
	super(connector, HttpdSiteURL.class);
    }

    int addHttpdSiteURL(HttpdSiteBind hsb, String hostname) {
        return connector.requestIntQueryIL(AOServProtocol.ADD, SchemaTable.HTTPD_SITE_URLS, hsb.pkey, hostname);
    }

    public HttpdSiteURL get(Object pkey) {
	return getUniqueRow(HttpdSiteURL.COLUMN_PKEY, pkey);
    }

    public HttpdSiteURL get(int pkey) {
	return getUniqueRow(HttpdSiteURL.COLUMN_PKEY, pkey);
    }

    List<HttpdSiteURL> getHttpdSiteURLs(HttpdSiteBind bind) {
        return getIndexedRows(HttpdSiteURL.COLUMN_HTTPD_SITE_BIND, bind.pkey);
    }

    HttpdSiteURL getPrimaryHttpdSiteURL(HttpdSiteBind bind) {
        // Use the index first
	List<HttpdSiteURL> cached=getHttpdSiteURLs(bind);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            HttpdSiteURL hsu=cached.get(c);
            if(hsu.isPrimary) return hsu;
	}
	throw new WrappedException(new SQLException("Unable to find primary HttpdSiteURL for HttpdSiteBind with pkey="+bind.pkey));
    }

    List<HttpdSiteURL> getAltHttpdSiteURLs(HttpdSiteBind bind) {
        // Use the index first
	List<HttpdSiteURL> cached=getHttpdSiteURLs(bind);
	int size=cached.size();
        List<HttpdSiteURL> matches=new ArrayList<HttpdSiteURL>(size-1);
        for(int c=0;c<size;c++) {
            HttpdSiteURL hsu=cached.get(c);
            if(!hsu.isPrimary) matches.add(hsu);
	}
	return matches;
    }

    int getTableID() {
	return SchemaTable.HTTPD_SITE_URLS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_HTTPD_SITE_URL)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_HTTPD_SITE_URL, args, 2, err)) {
                out.println(connector.simpleAOClient.addHttpdSiteURL(AOSH.parseInt(args[1], "httpd_site_bind_pkey"), args[2]));
                out.flush();
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_HTTPD_SITE_URL)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_HTTPD_SITE_URL, args, 1, err)) {
                connector.simpleAOClient.removeHttpdSiteURL(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_PRIMARY_HTTPD_SITE_URL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_PRIMARY_HTTPD_SITE_URL, args, 1, err)) {
                connector.simpleAOClient.setPrimaryHttpdSiteURL(AOSH.parseInt(args[1], "pkey"));
            }
            return true;
	} else return false;
    }
}