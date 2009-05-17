package com.aoindustries.aoserv.client;

import com.aoindustries.util.WrappedException;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

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
        try {
            return getUniqueRow(PostgresVersion.COLUMN_VERSION, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public PostgresVersion get(int pkey) throws IOException, SQLException {
	return getUniqueRow(PostgresVersion.COLUMN_VERSION, pkey);
    }

    public PostgresVersion getPostgresVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
	return get(
            connector.getTechnologyNames()
            .get(PostgresVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPkey()
	);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.POSTGRES_VERSIONS;
    }
}