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
final public class SetBusinessAdministratorPasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 5094314822859543681L;

    public static final String PARAM_BUSINESS_ADMINISTRATOR = "businessAdministrator";
    public static final String PARAM_PLAINTEXT = "plaintext";

    final private UserId username;
    final private String plaintext;

    public SetBusinessAdministratorPasswordCommand(
        @Param(name=PARAM_BUSINESS_ADMINISTRATOR) BusinessAdministrator businessAdministrator,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.username = businessAdministrator.getUserId();
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
        BusinessAdministrator other = rootConn.getBusinessAdministrators().get(username);
        if(!rootUser.canAccessBusinessAdministrator(other)) {
            errors = addValidationError(errors, PARAM_BUSINESS_ADMINISTRATOR, "Common.validate.accessDenied");
        } else {
            // Make sure not disabled
            if(other.isDisabled()) errors = addValidationError(errors, PARAM_BUSINESS_ADMINISTRATOR, "SetBusinessAdministratorPasswordCommand.validate.disabled");
            else {
                // Check password strength
                try {
                    if(plaintext!=null && plaintext.length()>0) errors = addValidationError(errors, PARAM_PLAINTEXT, CheckBusinessAdministratorPasswordCommand.checkPassword(username, plaintext));
                } catch(IOException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
        }
        return errors;
    }
}
