/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  NetDeviceID
 *
 * @author  AO Industries, Inc.
 */
final public class NetDeviceIDTable extends GlobalTableStringKey<NetDeviceID> {

	NetDeviceIDTable(AOServConnector connector) {
		super(connector, NetDeviceID.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NetDeviceID.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public NetDeviceID get(String name) throws IOException, SQLException {
		return getUniqueRow(NetDeviceID.COLUMN_NAME, name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_DEVICE_IDS;
	}
}
