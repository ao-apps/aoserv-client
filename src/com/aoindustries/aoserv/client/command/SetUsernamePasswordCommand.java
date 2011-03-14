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

    public UserId getUsername() {
        return username;
    }

    public String getPlaintext() {
        return plaintext;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        Username un = connectedUser.getConnector().getUsernames().get(username);
        if(!connectedUser.canAccessUsername(un)) {
            errors = addValidationError(errors, PARAM_USERNAME, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // Make sure not disabled
            if(un.getDisableLog()!=null) errors = addValidationError(errors, PARAM_USERNAME, ApplicationResources.accessor, "SetUsernamePasswordCommand.validate.disabled");
            else {
                // Make sure passes other command validations
                for(AOServObject<?> dependent : un.getDependentObjects()) {
                    if(dependent instanceof PasswordProtected) {
                        errors = addValidationErrors(
                            errors,
                            ((PasswordProtected)dependent).getSetPasswordCommand(plaintext).validate(connectedUser)
                        );
                    }
                }
            }
        }
        return errors;
    }
}
