package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

/**
 * @see  HttpdJBossVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJBossVersionTable extends GlobalTableIntegerKey<HttpdJBossVersion> {

    HttpdJBossVersionTable(AOServConnector connector) {
	super(connector, HttpdJBossVersion.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdJBossVersion.COLUMN_VERSION_name+'.'+TechnologyVersion.COLUMN_VERSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public HttpdJBossVersion get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(HttpdJBossVersion.COLUMN_VERSION, pkey);
    }

    public HttpdJBossVersion getHttpdJBossVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
	return get(
            connector.getTechnologyNames()
            .get(HttpdJBossVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPkey()
	);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_JBOSS_VERSIONS;
    }
}