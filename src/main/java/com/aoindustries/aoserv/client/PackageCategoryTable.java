/*
 * Copyright 2005-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  PackageCategory
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

	@Override
	public PackageCategory get(String name) throws IOException, SQLException {
		return getUniqueRow(PackageCategory.COLUMN_NAME, name);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PACKAGE_CATEGORIES;
	}
}
