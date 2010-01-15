/*
 * Copyright 2002-2009 by AO Industries, Inc.,
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
public interface PostgresVersionService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,PostgresVersion> {

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