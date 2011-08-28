/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class HashPasswordCommand extends AOServCommand<String> {

    private final String password;

    public HashPasswordCommand(
        @Param(name="password") String password
    ) {
        this.password = password;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public String getPassword() {
        return password;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }

    @Override
    public String execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        return HashedPassword.hash(password);
    }
}
