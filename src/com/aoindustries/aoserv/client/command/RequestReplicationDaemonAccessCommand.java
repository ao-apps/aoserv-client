package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServer;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class RequestReplicationDaemonAccessCommand extends RemoteCommand<AOServer.DaemonAccess> {

    private static final long serialVersionUID = 1L;

    final private int replication;

    public RequestReplicationDaemonAccessCommand(
        @Param(name="replication") int replication
    ) {
        this.replication = replication;
    }

    public int getReplication() {
        return replication;
    }

    @Override
    public boolean isReadOnlyCommand() {
        return true;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
