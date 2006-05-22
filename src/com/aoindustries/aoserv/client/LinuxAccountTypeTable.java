package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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

    int getTableID() {
	return SchemaTable.LINUX_ACCOUNT_TYPES;
    }
}