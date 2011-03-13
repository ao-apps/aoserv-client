/*
 * Copyright 2001-2011 by AO Industries, Inc.,
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
public interface HttpdJBossVersionService extends AOServService<Integer,HttpdJBossVersion> {

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