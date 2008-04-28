package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(NetProtocol.COLUMN_PROTOCOL_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public NetProtocol get(Object pkey) {
	return getUniqueRow(NetProtocol.COLUMN_PROTOCOL, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NET_PROTOCOLS;
    }
}