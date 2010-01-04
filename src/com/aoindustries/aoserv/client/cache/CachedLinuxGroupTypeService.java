package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.LinuxGroupType;
import com.aoindustries.aoserv.client.LinuxGroupTypeService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedLinuxGroupTypeService extends CachedServiceStringKey<LinuxGroupType> implements LinuxGroupTypeService<CachedConnector,CachedConnectorFactory> {

    CachedLinuxGroupTypeService(CachedConnector connector, LinuxGroupTypeService<?,?> wrapped) {
        super(connector, LinuxGroupType.class, wrapped);
    }
}
