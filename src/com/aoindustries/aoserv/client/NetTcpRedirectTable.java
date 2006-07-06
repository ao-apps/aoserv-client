package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  NetTcpRedirect
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class NetTcpRedirectTable extends CachedTableIntegerKey<NetTcpRedirect> {

    NetTcpRedirectTable(AOServConnector connector) {
	super(connector, NetTcpRedirect.class);
    }

    public NetTcpRedirect get(Object pkey) {
	return getUniqueRow(NetTcpRedirect.COLUMN_NET_BIND, pkey);
    }

    public NetTcpRedirect get(int pkey) {
	return getUniqueRow(NetTcpRedirect.COLUMN_NET_BIND, pkey);
    }

    int getTableID() {
	return SchemaTable.NET_TCP_REDIRECTS;
    }
}