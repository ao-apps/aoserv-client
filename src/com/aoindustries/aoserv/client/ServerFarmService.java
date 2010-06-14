package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainLabel;

/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * For AO Industries use only.
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.server_farms)
public interface ServerFarmService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,DomainLabel,ServerFarm> {
}
