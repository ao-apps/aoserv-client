package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  Architecture
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ArchitectureTable extends GlobalTableStringKey<Architecture> {

    ArchitectureTable(AOServConnector connector) {
	super(connector, Architecture.class);
    }

    public Architecture get(Object name) {
        return getUniqueRow(Architecture.COLUMN_NAME, name);
    }

    int getTableID() {
        return SchemaTable.ARCHITECTURES;
    }
}