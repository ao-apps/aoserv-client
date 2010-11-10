/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.PostgresUser;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetPostgresUserPredisablePasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private int postgresUser;
    final private String encryptedPassword;

    public SetPostgresUserPredisablePasswordCommand(
        @Param(name="postgresUser") PostgresUser postgresUser,
        @Param(name="encryptedPassword") String encryptedPassword
    ) {
        this.postgresUser = postgresUser.getKey();
        this.encryptedPassword = encryptedPassword;
    }

    public int getPostgresUser() {
        return postgresUser;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
