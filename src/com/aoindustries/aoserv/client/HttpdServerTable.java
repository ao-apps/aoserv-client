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
 * @see  HttpdServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdServerTable extends CachedTableIntegerKey<HttpdServer> {

    HttpdServerTable(AOServConnector connector) {
	super(connector, HttpdServer.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(HttpdServer.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(HttpdServer.COLUMN_NUMBER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public HttpdServer get(Object pkey) {
        try {
            return getUniqueRow(HttpdServer.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public HttpdServer get(int pkey) throws IOException, SQLException {
	return getUniqueRow(HttpdServer.COLUMN_PKEY, pkey);
    }

    List<HttpdServer> getHttpdServers(AOServer ao) throws IOException, SQLException {
        return getIndexedRows(HttpdServer.COLUMN_AO_SERVER, ao.pkey);
    }

    List<HttpdServer> getHttpdServers(Package pk) throws IOException, SQLException {
        return getIndexedRows(HttpdServer.COLUMN_PACKAGE, pk.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SERVERS;
    }
}