/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetMySQLUserPasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = -1007319692371973247L;

    public static final String PARAM_MYSQL_USER = "mysqlUser";
    public static final String PARAM_PLAINTEXT = "plaintext";

    final private int mysqlUser;
    final private String plaintext;

    public SetMySQLUserPasswordCommand(
        @Param(name=PARAM_MYSQL_USER) MySQLUser mysqlUser,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.mysqlUser = mysqlUser.getPkey();
        this.plaintext = plaintext;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getMysqlUser() {
        return mysqlUser;
    }

    public String getPlaintext() {
        return plaintext;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        MySQLUser rootMu = rootConn.getMysqlUsers().get(mysqlUser);
        if(!rootUser.canAccessMySQLUser(rootMu)) {
            errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // No setting root password
            MySQLUserId username = rootMu.getUserId();
            if(
                username==MySQLUser.ROOT // OK - interned
            ) errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "SetMySQLUserPasswordCommand.validate.noSetRoot");
            else {
                // Make sure not disabled
                if(rootMu.isDisabled()) errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "SetMySQLUserPasswordCommand.validate.disabled");
                else {
                    // Check password strength
                    try {
                        if(plaintext!=null && plaintext.length()>0) errors = addValidationError(errors, PARAM_PLAINTEXT, CheckMySQLUserPasswordCommand.checkPassword(rootMu, plaintext));
                    } catch(IOException err) {
                        throw new RemoteException(err.getMessage(), err);
                    }
                }
            }
        }
        return errors;
    }
}
