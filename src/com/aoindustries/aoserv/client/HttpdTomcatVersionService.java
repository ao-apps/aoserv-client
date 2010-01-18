/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  HttpdTomcatVersion
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.httpd_tomcat_versions)
public interface HttpdTomcatVersionService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,HttpdTomcatVersion> {

    /* TODO
    public HttpdTomcatVersion getHttpdTomcatVersion(String version, OperatingSystemVersion osv) throws IOException, SQLException {
	return get(
            connector.getTechnologyNames()
            .get(HttpdTomcatVersion.TECHNOLOGY_NAME)
            .getTechnologyVersion(connector, version, osv)
            .getPkey()
	);
    }
     */
}