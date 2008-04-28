package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  USState
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class USStateTable extends GlobalTableStringKey<USState> {

    USStateTable(AOServConnector connector) {
	super(connector, USState.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(USState.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.US_STATES;
    }

    public USState get(Object pkey) {
	return getUniqueRow(USState.COLUMN_CODE, pkey);
    }
}