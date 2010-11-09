/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.validator.UserId;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetBusinessAdministratorPasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PLAINTEXT = "plaintext";

    final private UserId username;
    final private String plaintext;

    public SetBusinessAdministratorPasswordCommand(
        @Param(name=PARAM_USERNAME) UserId username,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.username = username;
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
        if(!connectedUser.canAccessUsername(username)) {
            errors = addValidationError(errors, PARAM_USERNAME, "Common.validate.accessDenied");
        } else {
            // Make sure not disabled
            BusinessAdministrator other = connectedUser.getService().get(username);
            if(other.getDisableLog()!=null) errors = addValidationError(errors, PARAM_USERNAME, "SetBusinessAdministratorPasswordCommand.validate.disabled");
            else {
                // Check password strength
                try {
                    if(plaintext!=null && plaintext.length()>0) errors = addValidationError(errors, PARAM_PLAINTEXT, BusinessAdministrator.checkPassword(username, plaintext));
                } catch(IOException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
        }
        return errors;
    }
}
