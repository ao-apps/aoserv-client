package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
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

    public DistroFileType get(Object type) {
	return getUniqueRow(DistroFileType.COLUMN_TYPE, type);
    }

    int getTableID() {
	return SchemaTable.DISTRO_FILE_TYPES;
    }
}