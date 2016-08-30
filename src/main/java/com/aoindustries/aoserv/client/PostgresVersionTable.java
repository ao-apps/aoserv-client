/*
 * Copyright 2002-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  PostgresVersion
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresVersionTable extends GlobalTableIntegerKey<PostgresVersion> {

	PostgresVersionTable(AOServConnector connector) {
		super(connector, PostgresVersion.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PostgresVersion.COLUMN_VERSION_name+'.'+TechnologyVersion.COLUMN_VERSION_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public PostgresVersion get(int version) throws IOException, SQLException {
		return getUniqueRow(PostgresVersion.COLUMN_VERSION, version);
	}

	public PostgresVersion getPostgresVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
		return get(
			connector.getTechnologyNames()
			.get(PostgresVersion.TECHNOLOGY_NAME)
			.getTechnologyVersion(connector, version, osv)
			.getPkey()
		);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.POSTGRES_VERSIONS;
	}
}