package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxAccountType;
import com.aoindustries.aoserv.client.LinuxAccountTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedLinuxAccountTypeService extends CachedServiceStringKey<LinuxAccountType> implements LinuxAccountTypeService<CachedConnector,CachedConnectorFactory> {

    CachedLinuxAccountTypeService(CachedConnector connector, LinuxAccountTypeService<?,?> wrapped) {
        super(connector, LinuxAccountType.class, wrapped);
    }
}
