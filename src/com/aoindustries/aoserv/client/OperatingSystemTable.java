package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * All of the operating systems referenced from other tables.
 *
 * @see OperatingSystem
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class OperatingSystemTable extends GlobalTableStringKey<OperatingSystem> {

    OperatingSystemTable(AOServConnector connector) {
	super(connector, OperatingSystem.class);
    }

    public OperatingSystem get(Object pkey) {
	return getUniqueRow(OperatingSystem.COLUMN_NAME, pkey);
    }

    int getTableID() {
        return SchemaTable.OPERATING_SYSTEMS;
    }
}