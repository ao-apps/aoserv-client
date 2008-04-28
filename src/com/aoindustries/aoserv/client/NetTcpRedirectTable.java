package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
        new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
        new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_PORT_name, ASCENDING),
        new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_NET_PROTOCOL_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public NetTcpRedirect get(Object pkey) {
	return getUniqueRow(NetTcpRedirect.COLUMN_NET_BIND, pkey);
    }

    public NetTcpRedirect get(int pkey) {
	return getUniqueRow(NetTcpRedirect.COLUMN_NET_BIND, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NET_TCP_REDIRECTS;
    }
}