/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class RequestReplicationDaemonAccessCommand extends RemoteCommand<AOServer.DaemonAccess> {

    // TODO: private static final long serialVersionUID = 1L;

    final private int replication;

    public RequestReplicationDaemonAccessCommand(
        @Param(name="replication") FailoverFileReplication replication
    ) {
        this.replication = replication.getPkey();
    }

    public int getReplication() {
        return replication;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
