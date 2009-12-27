package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.FailoverFileReplication;
import com.aoindustries.aoserv.client.FailoverFileReplicationService;

/**
 * @author  AO Industries, Inc.
 */
final class NoSwingFailoverFileReplicationService extends NoSwingServiceIntegerKey<FailoverFileReplication> implements FailoverFileReplicationService<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingFailoverFileReplicationService(NoSwingConnector connector, FailoverFileReplicationService<?,?> wrapped) {
        super(connector, FailoverFileReplication.class, wrapped);
    }
}
