package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

/**
 * @see  LinuxAccountType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountTypeTable extends GlobalTableStringKey<LinuxAccountType> {

    LinuxAccountTypeTable(AOServConnector connector) {
	super(connector, LinuxAccountType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxAccountType.COLUMN_DESCRIPTION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public LinuxAccountType get(String name) throws IOException, SQLException {
        return getUniqueRow(LinuxAccountType.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_ACCOUNT_TYPES;
    }
}