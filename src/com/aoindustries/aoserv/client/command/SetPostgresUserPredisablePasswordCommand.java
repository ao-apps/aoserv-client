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
final public class SetPostgresUserPredisablePasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 202981909240092697L;

    final private int postgresUser;
    final private String encryptedPassword;

    public SetPostgresUserPredisablePasswordCommand(
        @Param(name="postgresUser") PostgresUser postgresUser,
        @Param(name="encryptedPassword") String encryptedPassword
    ) {
        this.postgresUser = postgresUser.getPkey();
        this.encryptedPassword = encryptedPassword;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getPostgresUser() {
        return postgresUser;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
