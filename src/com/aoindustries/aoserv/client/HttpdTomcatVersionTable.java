package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdTomcatVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatVersionTable extends GlobalTableIntegerKey<HttpdTomcatVersion> {

    HttpdTomcatVersionTable(AOServConnector connector) {
	super(connector, HttpdTomcatVersion.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdTomcatVersion.COLUMN_VERSION_name+'.'+TechnologyVersion.COLUMN_VERSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public HttpdTomcatVersion get(Object pkey) {
        try {
            return getUniqueRow(HttpdTomcatVersion.COLUMN_VERSION, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public HttpdTomcatVersion get(int pkey) throws IOException, SQLException {
	return getUniqueRow(HttpdTomcatVersion.COLUMN_VERSION, pkey);
    }

    public HttpdTomcatVersion getHttpdTomcatVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
	return get(
            connector.getTechnologyNames()
            .get(HttpdTomcatVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPkey()
	);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_VERSIONS;
    }
}