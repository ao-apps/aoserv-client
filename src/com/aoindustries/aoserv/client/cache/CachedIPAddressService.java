package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.IPAddress;
import com.aoindustries.aoserv.client.IPAddressService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedIPAddressService extends CachedServiceIntegerKey<IPAddress> implements IPAddressService<CachedConnector,CachedConnectorFactory> {

    CachedIPAddressService(CachedConnector connector, IPAddressService<?,?> wrapped) {
        super(connector, IPAddress.class, wrapped);
    }
}
