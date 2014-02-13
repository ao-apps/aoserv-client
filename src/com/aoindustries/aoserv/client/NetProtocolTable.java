package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

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

    public NetProtocol get(String protocol) throws IOException, SQLException {
        return getUniqueRow(NetProtocol.COLUMN_PROTOCOL, protocol);
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.NET_PROTOCOLS;
    }
}