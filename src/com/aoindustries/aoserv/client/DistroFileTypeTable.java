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
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DistroFileTypeTable extends GlobalTableStringKey<DistroFileType> {

    DistroFileTypeTable(AOServConnector connector) {
	super(connector, DistroFileType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DistroFileType.COLUMN_TYPE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public DistroFileType get(Object type) {
	return getUniqueRow(DistroFileType.COLUMN_TYPE, type);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DISTRO_FILE_TYPES;
    }
}