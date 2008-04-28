package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(PackageCategory.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public PackageCategory get(Object pkey) {
	return getUniqueRow(PackageCategory.COLUMN_NAME, pkey);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.PACKAGE_CATEGORIES;
    }
}