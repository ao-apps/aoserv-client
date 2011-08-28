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
final public class SetPostgresUserPasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = -634430116390382028L;

    public static final String PARAM_POSTGRES_USER = "postgresUser";
    public static final String PARAM_PLAINTEXT = "plaintext";

    final private int postgresUser;
    final private String plaintext;

    public SetPostgresUserPasswordCommand(
        @Param(name=PARAM_POSTGRES_USER) PostgresUser postgresUser,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.postgresUser = postgresUser.getPkey();
        this.plaintext = plaintext;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getPostgresUser() {
        return postgresUser;
    }

    public String getPlaintext() {
        return plaintext;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        PostgresUser rootPu = rootConn.getPostgresUsers().get(postgresUser);
        if(!rootUser.canAccessPostgresUser(rootPu)) {
            errors = addValidationError(errors, PARAM_POSTGRES_USER, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // No setting root password
            PostgresUserId username = rootPu.getUserId();
            if(
                username==PostgresUser.POSTGRES // OK - interned
            ) errors = addValidationError(errors, PARAM_POSTGRES_USER, ApplicationResources.accessor, "SetPostgresUserPasswordCommand.validate.noSetPostgres");
            else {
                // Make sure not disabled
                if(rootPu.isDisabled()) errors = addValidationError(errors, PARAM_POSTGRES_USER, ApplicationResources.accessor, "SetPostgresUserPasswordCommand.validate.disabled");
                else {
                    // Check password strength
                    try {
                        if(plaintext!=null && plaintext.length()>0) errors = addValidationError(errors, PARAM_PLAINTEXT, CheckPostgresUserPasswordCommand.checkPassword(rootPu, plaintext));
                    } catch(IOException err) {
                        throw new RemoteException(err.getMessage(), err);
                    }
                }
            }
        }
        return errors;
    }
}
