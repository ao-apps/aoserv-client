package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  PostgresVersion
 *
 * @version  1.0a
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

    public PostgresVersion get(Object pkey) {
	return getUniqueRow(PostgresVersion.COLUMN_VERSION, pkey);
    }

    public PostgresVersion get(int pkey) {
	return getUniqueRow(PostgresVersion.COLUMN_VERSION, pkey);
    }

    public PostgresVersion getPostgresVersion(String version, OperatingSystemVersion osv) {
	return get(
            connector
            .technologyNames
            .get(PostgresVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPkey()
	);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_VERSIONS;
    }
}