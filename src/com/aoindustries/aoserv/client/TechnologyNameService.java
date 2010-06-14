package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  TechnologyName
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.technology_names)
public interface TechnologyNameService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,TechnologyName> {
}
