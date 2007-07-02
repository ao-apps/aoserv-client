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

    public LinuxAccountType get(Object pkey) {
	return getUniqueRow(LinuxAccountType.COLUMN_NAME, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_ACCOUNT_TYPES;
    }
}