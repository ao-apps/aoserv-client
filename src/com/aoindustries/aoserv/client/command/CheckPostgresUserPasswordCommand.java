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
final public class CheckPostgresUserPasswordCommand extends AOServCommand<List<PasswordChecker.Result>> {

    public static final String PARAM_POSTGRES_USER = "postgresUser";

    final private int postgresUser;
    private final String password;

    public CheckPostgresUserPasswordCommand(
        @Param(name=PARAM_POSTGRES_USER) PostgresUser postgresUser,
        @Param(name="password") String password
    ) {
        this.postgresUser = postgresUser.getKey();
        this.password = password;
    }

    public int getPostgresUser() {
        return postgresUser;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        PostgresUser pu = connectedUser.getConnector().getPostgresUsers().get(postgresUser);
        if(!connectedUser.canAccessPostgresUser(pu)) {
            errors = addValidationError(errors, PARAM_POSTGRES_USER, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // No setting root password
            PostgresUserId username = pu.getUserId();
            if(
                username==PostgresUser.POSTGRES // OK - interned
            ) errors = addValidationError(errors, PARAM_POSTGRES_USER, ApplicationResources.accessor, "SetPostgresUserPasswordCommand.validate.noSetPostgres");
        }
        return errors;
    }

    static List<PasswordChecker.Result> checkPassword(PostgresUser pu, String password) throws IOException {
        return PasswordChecker.checkPassword(pu.getUserId().getUserId(), password, PasswordChecker.PasswordStrength.STRICT);
    }

    @Override
    public List<PasswordChecker.Result> execute(AOServConnector<?,?> connector, boolean isInteractive) throws RemoteException {
        try {
            return checkPassword(connector.getPostgresUsers().get(postgresUser), password);
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
