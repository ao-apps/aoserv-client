/*
 * Copyright 2005-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  PackageDefinitionLimit
 *
 * @author  AO Industries, Inc.
 */
public final class PackageDefinitionLimitTable extends CachedTableIntegerKey<PackageDefinitionLimit> {

	PackageDefinitionLimitTable(AOServConnector connector) {
		super(connector, PackageDefinitionLimit.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name+'.'+PackageDefinition.COLUMN_ACCOUNTING_name, ASCENDING),
		new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name+'.'+PackageDefinition.COLUMN_CATEGORY_name, ASCENDING),
		new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name+'.'+PackageDefinition.COLUMN_MONTHLY_RATE_name, ASCENDING),
		new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name+'.'+PackageDefinition.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION_name+'.'+PackageDefinition.COLUMN_VERSION_name, ASCENDING),
		new OrderBy(PackageDefinitionLimit.COLUMN_RESOURCE_name+'.'+Resource.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	List<PackageDefinitionLimit> getPackageDefinitionLimits(PackageDefinition packageDefinition) throws IOException, SQLException {
		return getIndexedRows(PackageDefinitionLimit.COLUMN_PACKAGE_DEFINITION, packageDefinition.pkey);
	}

	@Override
	public PackageDefinitionLimit get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PackageDefinitionLimit.COLUMN_PKEY, pkey);
	}

	PackageDefinitionLimit getPackageDefinitionLimit(PackageDefinition packageDefinition, Resource resource) throws IOException, SQLException {
		if(packageDefinition==null) throw new AssertionError("packageDefinition is null");
		if(resource==null) throw new AssertionError("resource is null");
		String resourceName=resource.pkey;
		// Use the index first
		for(PackageDefinitionLimit limit : getPackageDefinitionLimits(packageDefinition)) if(limit.resource.equals(resourceName)) return limit;
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PACKAGE_DEFINITION_LIMITS;
	}
}
