package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  AOServProtocol
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOServProtocolTable extends GlobalTableStringKey<AOServProtocol> {

    AOServProtocolTable(AOServConnector connector) {
	super(connector, AOServProtocol.class);
    }

    public AOServProtocol get(Object version) {
        return getUniqueRow(AOServProtocol.COLUMN_VERSION, version);
    }

    int getTableID() {
        return SchemaTable.AOSERV_PROTOCOLS;
    }
}