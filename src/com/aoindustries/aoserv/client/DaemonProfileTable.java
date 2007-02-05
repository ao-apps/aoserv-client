package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  DaemonProfile
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DaemonProfileTable extends AOServTable<Object,DaemonProfile> {

    DaemonProfileTable(AOServConnector connector) {
	super(connector, DaemonProfile.class);
    }

    public List<DaemonProfile> getRows() {
        List<DaemonProfile> list=new ArrayList<DaemonProfile>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.DAEMON_PROFILE);
        return list;
    }

    int getTableID() {
	return SchemaTable.DAEMON_PROFILE;
    }

    public DaemonProfile get(Object key) {
        throw new UnsupportedOperationException();
    }

    protected DaemonProfile getUniqueRowImpl(int col, Object value) {
        throw new IllegalArgumentException("Not a unique column: "+col);
    }
}
