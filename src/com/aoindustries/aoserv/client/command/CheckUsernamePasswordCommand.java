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
final public class CheckUsernamePasswordCommand extends AOServCommand<List<PasswordChecker.Result>> {

    public static final String PARAM_USERNAME = "username";

    final private UserId username;
    private final String password;

    public CheckUsernamePasswordCommand(
        @Param(name=PARAM_USERNAME) Username username,
        @Param(name="password") String password
    ) {
        this.username = username.getUsername();
        this.password = password;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public UserId getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        Username un = rootConn.getUsernames().get(username);
        if(!rootUser.canAccessUsername(un)) {
            errors = addValidationError(errors, PARAM_USERNAME, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // Make sure passes other command validations
            for(AOServObject<?> dependent : un.getDependentObjects()) {
                if(dependent instanceof PasswordProtected) {
                    errors = addValidationErrors(
                        errors,
                        ((PasswordProtected)dependent).getCheckPasswordCommand(password).checkExecute(userConn, rootConn, rootUser)
                    );
                }
            }
        }
        return errors;
    }

    static List<PasswordChecker.Result> checkPassword(AOServConnector connector, boolean isInteractive, Username un, String password) throws IOException {
        for(AOServObject<?> dependent : un.getDependentObjects()) {
            if(dependent instanceof PasswordProtected) {
                List<PasswordChecker.Result> results = ((PasswordProtected)dependent).getCheckPasswordCommand(password).execute(connector, isInteractive);
                if(PasswordChecker.hasResults(results)) return results;
            }
        }
        return PasswordChecker.getAllGoodResults();
    }

    @Override
    public List<PasswordChecker.Result> execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        try {
            return checkPassword(connector, isInteractive, connector.getUsernames().get(username), password);
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
