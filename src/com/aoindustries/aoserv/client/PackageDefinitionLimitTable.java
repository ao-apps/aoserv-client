package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import com.aoindustries.util.sort.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  PackageDefinitionLimit
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionLimitTable extends CachedTableIntegerKey<PackageDefinitionLimit> {

    PackageDefinitionLimitTable(AOServConnector connector) {
	super(connector, PackageDefinitionLimit.class);
    }

    List<PackageDefinitionLimit> getPackageDefinitionLimits(PackageDefinition packageDefinition) {
        return getIndexedRows(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION, packageDefinition.pkey);
    }

    public PackageDefinitionLimit get(Object pkey) {
	return getUniqueRow(PackageDefinitionLimit.COLUMN_PKEY, pkey);
    }

    public PackageDefinitionLimit get(int pkey) {
	return getUniqueRow(PackageDefinitionLimit.COLUMN_PKEY, pkey);
    }

    PackageDefinitionLimit getPackageDefinitionLimit(PackageDefinition packageDefinition, Resource resource) {
        String resourceName=resource.pkey;
        // Use the index first
        for(PackageDefinitionLimit limit : getPackageDefinitionLimits(packageDefinition)) if(limit.resource.equals(resourceName)) return limit;
        return null;
    }

    int getTableID() {
	return SchemaTable.PACKAGE_DEFINITION_LIMITS;
    }
}