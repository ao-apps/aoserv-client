package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

/**
 * @see  LinuxGroupType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupTypeTable extends GlobalTableStringKey<LinuxGroupType> {

    LinuxGroupTypeTable(AOServConnector connector) {
	super(connector, LinuxGroupType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(LinuxGroupType.COLUMN_DESCRIPTION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public LinuxGroupType get(String name) throws IOException, SQLException {
        return getUniqueRow(LinuxGroupType.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_GROUP_TYPES;
    }
}