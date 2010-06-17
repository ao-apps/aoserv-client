/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PackageDefinitionLimit
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.package_definition_limits)
public interface PackageDefinitionLimitService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,PackageDefinitionLimit> {
}
