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
 * @see  MasterHost
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHostTable extends CachedTableIntegerKey<MasterHost> {

    MasterHostTable(AOServConnector connector) {
	super(connector, MasterHost.class);
    }

    public MasterHost get(Object pkey) {
	return getUniqueRow(MasterHost.COLUMN_PKEY, pkey);
    }

    public MasterHost get(int pkey) {
	return getUniqueRow(MasterHost.COLUMN_PKEY, pkey);
    }

    int getTableID() {
	return SchemaTable.MASTER_HOSTS;
    }
}