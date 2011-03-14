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
final public class SetMySQLUserPredisablePasswordCommand extends RemoteCommand<Void> {

    // TODO: private static final long serialVersionUID = 1L;

    final private int mysqlUser;
    final private String encryptedPassword;

    public SetMySQLUserPredisablePasswordCommand(
        @Param(name="mysqlUser") MySQLUser mysqlUser,
        @Param(name="encryptedPassword") String encryptedPassword
    ) {
        this.mysqlUser = mysqlUser.getKey();
        this.encryptedPassword = encryptedPassword;
    }

    public int getMysqlUser() {
        return mysqlUser;
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
