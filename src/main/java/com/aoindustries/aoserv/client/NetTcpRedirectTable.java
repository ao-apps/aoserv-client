/*
 * Copyright 2004-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  NetTcpRedirect
 *
 * @author  AO Industries, Inc.
 */
public final class NetTcpRedirectTable extends CachedTableIntegerKey<NetTcpRedirect> {

	NetTcpRedirectTable(AOServConnector connector) {
		super(connector, NetTcpRedirect.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_PORT_name, ASCENDING),
		new OrderBy(NetTcpRedirect.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_NET_PROTOCOL_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public NetTcpRedirect get(int pkey) throws IOException, SQLException {
		return getUniqueRow(NetTcpRedirect.COLUMN_NET_BIND, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_TCP_REDIRECTS;
	}
}
