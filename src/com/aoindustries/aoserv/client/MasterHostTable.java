package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MasterHost.COLUMN_USERNAME_name, ASCENDING),
        new OrderBy(MasterHost.COLUMN_HOST_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public MasterHost get(Object pkey) {
	return getUniqueRow(MasterHost.COLUMN_PKEY, pkey);
    }

    public MasterHost get(int pkey) {
	return getUniqueRow(MasterHost.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_HOSTS;
    }
}