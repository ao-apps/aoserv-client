package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MasterServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerTable extends CachedTableIntegerKey<MasterServer> {

    MasterServerTable(AOServConnector connector) {
	super(connector, MasterServer.class);
    }

    public MasterServer get(Object pkey) {
	return getUniqueRow(MasterServer.COLUMN_PKEY, pkey);
    }

    public MasterServer get(int pkey) {
	return getUniqueRow(MasterServer.COLUMN_PKEY, pkey);
    }

    int getTableID() {
	return SchemaTable.MASTER_SERVERS;
    }
}