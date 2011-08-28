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

    private static final long serialVersionUID = -4054760416937318142L;

    final private int replication;

    public RequestReplicationDaemonAccessCommand(
        @Param(name="replication") FailoverFileReplication replication
    ) {
        this.replication = replication.getPkey();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public int getReplication() {
        return replication;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
