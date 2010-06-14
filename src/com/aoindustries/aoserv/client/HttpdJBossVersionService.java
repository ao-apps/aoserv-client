/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  HttpdJBossVersion
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.httpd_jboss_versions)
public interface HttpdJBossVersionService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,HttpdJBossVersion> {

    /* TODO
    public HttpdJBossVersion getHttpdJBossVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
	return get(
            connector.getTechnologyNames()
            .get(HttpdJBossVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPkey()
	);
    }*/
}