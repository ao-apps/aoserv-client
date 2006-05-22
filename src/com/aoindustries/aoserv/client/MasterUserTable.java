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
 * @see  MasterUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterUserTable extends CachedTableStringKey<MasterUser> {

    MasterUserTable(AOServConnector connector) {
	super(connector, MasterUser.class);
    }

    public MasterUser get(Object pkey) {
	return getUniqueRow(MasterUser.COLUMN_USERNAME, pkey);
    }

    int getTableID() {
	return SchemaTable.MASTER_USERS;
    }
}