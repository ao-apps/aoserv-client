package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  ResourceType
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.resource_types)
public interface ResourceTypeService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,String,ResourceType> {
}
