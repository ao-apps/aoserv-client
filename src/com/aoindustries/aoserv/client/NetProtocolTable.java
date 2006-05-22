package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  NetProtocol
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetProtocolTable extends GlobalTableStringKey<NetProtocol> {

    NetProtocolTable(AOServConnector connector) {
	super(connector, NetProtocol.class);
    }

    public NetProtocol get(Object pkey) {
	return getUniqueRow(NetProtocol.COLUMN_PROTOCOL, pkey);
    }

    int getTableID() {
	return SchemaTable.NET_PROTOCOLS;
    }
}