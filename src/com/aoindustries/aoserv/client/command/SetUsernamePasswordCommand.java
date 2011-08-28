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
final public class SetUsernamePasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 6074637276024727093L;

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PLAINTEXT = "plaintext";

    final private UserId username;
    final private String plaintext;

    public SetUsernamePasswordCommand(
        @Param(name=PARAM_USERNAME) Username username,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.username = username.getUsername();
        this.plaintext = plaintext;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public UserId getUsername() {
        return username;
    }

    public String getPlaintext() {
        return plaintext;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        Username rootUn = rootConn.getUsernames().get(username);
        if(!rootUser.canAccessUsername(rootUn)) {
            errors = addValidationError(errors, PARAM_USERNAME, "Common.validate.accessDenied");
        } else {
            // Make sure not disabled
            if(rootUn.isDisabled()) errors = addValidationError(errors, PARAM_USERNAME, "SetUsernamePasswordCommand.validate.disabled");
            else {
                // Make sure passes other command validations
                for(AOServObject<?> rootDependent : rootUn.getDependentObjects()) {
                    if(rootDependent instanceof PasswordProtected) {
                        errors = addValidationErrors(
                            errors,
                            ((PasswordProtected)rootDependent).getSetPasswordCommand(plaintext).checkExecute(userConn, rootConn, rootUser)
                        );
                    }
                }
            }
        }
        return errors;
    }
}
