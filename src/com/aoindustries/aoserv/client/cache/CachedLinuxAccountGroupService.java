package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxAccountGroup;
import com.aoindustries.aoserv.client.LinuxAccountGroupService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedLinuxAccountGroupService extends CachedServiceIntegerKey<LinuxAccountGroup> implements LinuxAccountGroupService<CachedConnector,CachedConnectorFactory> {

    CachedLinuxAccountGroupService(CachedConnector connector, LinuxAccountGroupService<?,?> wrapped) {
        super(connector, LinuxAccountGroup.class, wrapped);
    }
}
