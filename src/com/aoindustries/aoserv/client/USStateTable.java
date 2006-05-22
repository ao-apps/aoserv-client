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

    int getTableID() {
	return SchemaTable.US_STATES;
    }

    public USState get(Object pkey) {
	return getUniqueRow(USState.COLUMN_CODE, pkey);
    }
}