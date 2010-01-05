package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxGroup;
import com.aoindustries.aoserv.client.LinuxGroupService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedLinuxGroupService extends CachedServiceIntegerKey<LinuxGroup> implements LinuxGroupService<CachedConnector,CachedConnectorFactory> {

    CachedLinuxGroupService(CachedConnector connector, LinuxGroupService<?,?> wrapped) {
        super(connector, LinuxGroup.class, wrapped);
    }
}
