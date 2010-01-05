package com.aoindustries.aoserv.client.noswing;

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
final class NoSwingGroupNameService extends NoSwingServiceStringKey<GroupName> implements GroupNameService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingGroupNameService(NoSwingConnector connector, GroupNameService<?,?> wrapped) {
        super(connector, GroupName.class, wrapped);
    }
}
