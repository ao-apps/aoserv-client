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
 * @see  SystemEmailAlias
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SystemEmailAliasTable extends CachedTableIntegerKey<SystemEmailAlias> {

    SystemEmailAliasTable(AOServConnector connector) {
	super(connector, SystemEmailAlias.class);
    }

    List<SystemEmailAlias> getSystemEmailAliases(AOServer ao) {
        return getIndexedRows(SystemEmailAlias.COLUMN_AO_SERVER, ao.pkey);
    }

    public SystemEmailAlias get(Object pkey) {
	return getUniqueRow(SystemEmailAlias.COLUMN_PKEY, pkey);
    }

    public SystemEmailAlias get(int pkey) {
	return getUniqueRow(SystemEmailAlias.COLUMN_PKEY, pkey);
    }

    int getTableID() {
	return SchemaTable.SYSTEM_EMAIL_ALIASES;
    }
}