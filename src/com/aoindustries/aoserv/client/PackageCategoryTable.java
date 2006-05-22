package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  PackageCategory
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageCategoryTable extends GlobalTableStringKey<PackageCategory> {

    PackageCategoryTable(AOServConnector connector) {
	super(connector, PackageCategory.class);
    }

    public PackageCategory get(Object pkey) {
	return getUniqueRow(PackageCategory.COLUMN_NAME, pkey);
    }

    int getTableID() {
        return SchemaTable.PACKAGE_CATEGORIES;
    }
}