package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  NetDeviceID
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.net_device_ids)
public interface NetDeviceIDService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceStringKey<C,F,NetDeviceID> {
}
