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
final public class RequestVncConsoleAccessCommand extends RemoteCommand<AOServer.DaemonAccess> {

    private static final long serialVersionUID = 3743410056004710207L;

    final private int virtualServer;

    public RequestVncConsoleAccessCommand(
        @Param(name="virtualServer") VirtualServer virtualServer
    ) {
        this.virtualServer = virtualServer.getKey();
    }

    public int getVirtualServer() {
        return virtualServer;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
