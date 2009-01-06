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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MasterUser.COLUMN_USERNAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public MasterUser get(Object pkey) {
	return getUniqueRow(MasterUser.COLUMN_USERNAME, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_USERS;
    }
}