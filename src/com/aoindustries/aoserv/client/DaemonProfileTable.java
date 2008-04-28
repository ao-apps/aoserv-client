package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DaemonProfile.COLUMN_AO_SERVER_name, ASCENDING),
        new OrderBy(DaemonProfile.COLUMN_CLASSNAME_name, ASCENDING),
        new OrderBy(DaemonProfile.COLUMN_METHOD_NAME_name, ASCENDING),
        new OrderBy(DaemonProfile.COLUMN_PARAMETER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public List<DaemonProfile> getRows() {
        List<DaemonProfile> list=new ArrayList<DaemonProfile>();
        getObjects(list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.DAEMON_PROFILE);
        return list;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DAEMON_PROFILE;
    }

    public DaemonProfile get(Object key) {
        throw new UnsupportedOperationException();
    }

    protected DaemonProfile getUniqueRowImpl(int col, Object value) {
        throw new IllegalArgumentException("Not a unique column: "+col);
    }
}
