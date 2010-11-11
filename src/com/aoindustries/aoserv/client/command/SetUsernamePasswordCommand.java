/*
 * Copyright 2010 by AO Industries, Inc.,
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

    private static final long serialVersionUID = 1L;

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
        if(!connectedUser.canAccessUsername(username)) {
            errors = addValidationError(errors, PARAM_USERNAME, "Common.validate.accessDenied");
        } else {
            // Check LinuxAccountType.Constant for no password set allowed
            Username un = connectedUser.getService().getConnector().getUsernames().get(username);
            // Make sure not disabled
            if(un.getDisableLog()!=null) errors = addValidationError(errors, PARAM_USERNAME, "SetUsernamePasswordCommand.validate.disabled");
            else {
                // Make sure passes other command validations
                BusinessAdministrator ba = un.getBusinessAdministrator();
                if(ba!=null) errors = addValidationErrors(errors, new SetBusinessAdministratorPasswordCommand(ba, plaintext).validate(connectedUser));
                for(LinuxAccount la : un.getLinuxAccounts()) errors = addValidationErrors(errors, new SetLinuxAccountPasswordCommand(la, plaintext).validate(connectedUser));
                for(MySQLUser mu : un.getMysqlUsers()) errors = addValidationErrors(errors, new SetMySQLUserPasswordCommand(mu, plaintext).validate(connectedUser));
                for(PostgresUser pu : un.getPostgresUsers()) errors = addValidationErrors(errors, new SetPostgresUserPasswordCommand(pu, plaintext).validate(connectedUser));
            }
        }
        return errors;
    }
}
