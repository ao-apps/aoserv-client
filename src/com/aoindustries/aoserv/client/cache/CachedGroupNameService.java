package com.aoindustries.aoserv.client.cache;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.GroupName;
import com.aoindustries.aoserv.client.GroupNameService;

/**
 * @author  AO Industries, Inc.
 */
final class CachedGroupNameService extends CachedServiceGroupIdKey<GroupName> implements GroupNameService<CachedConnector,CachedConnectorFactory> {

    CachedGroupNameService(CachedConnector connector, GroupNameService<?,?> wrapped) {
        super(connector, GroupName.class, wrapped);
    }
}
