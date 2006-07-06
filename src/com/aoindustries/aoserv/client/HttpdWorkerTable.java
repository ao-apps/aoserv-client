package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdWorker
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdWorkerTable extends CachedTableIntegerKey<HttpdWorker> {

    HttpdWorkerTable(AOServConnector connector) {
	super(connector, HttpdWorker.class);
    }

    public HttpdWorker get(Object pkey) {
	return getUniqueRow(HttpdWorker.COLUMN_PKEY, pkey);
    }

    public HttpdWorker get(int pkey) {
	return getUniqueRow(HttpdWorker.COLUMN_PKEY, pkey);
    }

    List<HttpdWorker> getHttpdWorkers(HttpdServer server) {
        int serverPKey=server.pkey;
	List<HttpdWorker> cached=getRows();
	int size=cached.size();
        List<HttpdWorker> matches=new ArrayList<HttpdWorker>(size);
    Loop:
	for(int c=0;c<size;c++) {
            HttpdWorker worker=cached.get(c);
            HttpdTomcatSite hts=worker.getHttpdTomcatSite();
            if(hts!=null) {
                List<HttpdSiteBind> binds=hts.getHttpdSite().getHttpdSiteBinds();
                // If one of the binds is this server, then count as a match
                for(int d=0;d<binds.size();d++) {
                    if(binds.get(d).getHttpdBind().httpd_server==serverPKey) {
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
                        for(int e=0;e<binds.size();e++) {
                            if(binds.get(e).getHttpdBind().httpd_server==serverPKey) {
                                matches.add(worker);
                                continue Loop;
                            }
                        }
                    }
                } else {
                    connector.errorHandler.reportWarning(
                            new SQLException("HttpdWorker doesn't have either HttpdTomcatSite or HttpdSharedTomcat"),
                            new Object[] {"pkey="+worker.pkey}
                    );
                }
            }
	}
	return matches;
    }

    List<HttpdWorker> getHttpdWorkers(HttpdTomcatSite tomcatSite) {
        return getIndexedRows(HttpdWorker.COLUMN_TOMCAT_SITE, tomcatSite.pkey);
    }

    HttpdWorker getHttpdWorker(NetBind nb) {
	return getUniqueRow(HttpdWorker.COLUMN_NET_BIND, nb.pkey);
    }

    int getTableID() {
	return SchemaTable.HTTPD_WORKERS;
    }
}