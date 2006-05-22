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
 * @see  NetDeviceID
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetDeviceIDTable extends GlobalTableStringKey<NetDeviceID> {

    NetDeviceIDTable(AOServConnector connector) {
	super(connector, NetDeviceID.class);
    }

    public NetDeviceID get(Object pkey) {
	return getUniqueRow(NetDeviceID.COLUMN_NAME, pkey);
    }

    int getTableID() {
	return SchemaTable.NET_DEVICE_IDS;
    }
}