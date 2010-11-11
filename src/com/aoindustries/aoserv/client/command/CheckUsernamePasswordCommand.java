/*
 * Copyright 2010 by AO Industries, Inc.,
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

    public UserId getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        Username un = connectedUser.getService().getConnector().getUsernames().get(username);
        if(!connectedUser.canAccessUsername(un)) {
            errors = addValidationError(errors, PARAM_USERNAME, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // Make sure passes other command validations
            BusinessAdministrator ba = un.getBusinessAdministrator();
            if(ba!=null) errors = addValidationErrors(errors, new CheckBusinessAdministratorPasswordCommand(ba, password).validate(connectedUser));
            for(LinuxAccount la : un.getLinuxAccounts()) errors = addValidationErrors(errors, new CheckLinuxAccountPasswordCommand(la, password).validate(connectedUser));
            for(MySQLUser mu : un.getMysqlUsers()) errors = addValidationErrors(errors, new CheckMySQLUserPasswordCommand(mu, password).validate(connectedUser));
            for(PostgresUser pu : un.getPostgresUsers()) errors = addValidationErrors(errors, new CheckPostgresUserPasswordCommand(pu, password).validate(connectedUser));
        }
        return errors;
    }

    @Override
    public List<PasswordChecker.Result> execute(AOServConnector<?,?> connector, boolean isInteractive) throws RemoteException {
        try {
            Username un = connector.getUsernames().get(username);
            BusinessAdministrator ba = un.getBusinessAdministrator();
            if(ba!=null) {
                List<PasswordChecker.Result> results = new CheckBusinessAdministratorPasswordCommand(ba, password).execute(connector, isInteractive);
                if(PasswordChecker.hasResults(results)) return results;
            }
            for(LinuxAccount la : un.getLinuxAccounts()) {
                List<PasswordChecker.Result> results = new CheckLinuxAccountPasswordCommand(la, password).execute(connector, isInteractive);
                if(PasswordChecker.hasResults(results)) return results;
            }
            for(MySQLUser mu : un.getMysqlUsers()) {
                List<PasswordChecker.Result> results = new CheckMySQLUserPasswordCommand(mu, password).execute(connector, isInteractive);
                if(PasswordChecker.hasResults(results)) return results;
            }
            for(PostgresUser pu : un.getPostgresUsers()) {
                List<PasswordChecker.Result> results = new CheckPostgresUserPasswordCommand(pu, password).execute(connector, isInteractive);
                if(PasswordChecker.hasResults(results)) return results;
            }
            return PasswordChecker.getAllGoodResults();
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
