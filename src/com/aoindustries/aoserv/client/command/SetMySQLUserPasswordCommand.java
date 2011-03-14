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

    // TODO: private static final long serialVersionUID = 1L;

    public static final String PARAM_MYSQL_USER = "mysqlUser";
    public static final String PARAM_PLAINTEXT = "plaintext";

    final private int mysqlUser;
    final private String plaintext;

    public SetMySQLUserPasswordCommand(
        @Param(name=PARAM_MYSQL_USER) MySQLUser mysqlUser,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.mysqlUser = mysqlUser.getKey();
        this.plaintext = plaintext;
    }

    public int getMysqlUser() {
        return mysqlUser;
    }

    public String getPlaintext() {
        return plaintext;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        MySQLUser mu = connectedUser.getConnector().getMysqlUsers().get(mysqlUser);
        if(!connectedUser.canAccessMySQLUser(mu)) {
            errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // No setting root password
            MySQLUserId username = mu.getUserId();
            if(
                username==MySQLUser.ROOT // OK - interned
            ) errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "SetMySQLUserPasswordCommand.validate.noSetRoot");
            else {
                // Make sure not disabled
                if(mu.isDisabled()) errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "SetMySQLUserPasswordCommand.validate.disabled");
                else {
                    // Check password strength
                    try {
                        if(plaintext!=null && plaintext.length()>0) errors = addValidationError(errors, PARAM_PLAINTEXT, CheckMySQLUserPasswordCommand.checkPassword(mu, plaintext));
                    } catch(IOException err) {
                        throw new RemoteException(err.getMessage(), err);
                    }
                }
            }
        }
        return errors;
    }
}
