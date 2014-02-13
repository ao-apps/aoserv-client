package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Architecture.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Architecture get(String name) throws IOException, SQLException {
        return getUniqueRow(Architecture.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.ARCHITECTURES;
    }
}