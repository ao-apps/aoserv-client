/*
 * Copyright 2002-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PostgresVersion
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.postgres_versions)
public interface PostgresVersionService extends AOServService<Integer,PostgresVersion> {

    /* TODO
    public PostgresVersion getPostgresVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
        return get(
            connector.getTechnologyNames()
            .get(PostgresVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPkey()
        );
    } */
}