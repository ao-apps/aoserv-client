/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.UserId;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class CheckBusinessAdministratorPasswordCommand extends AOServCommand<List<PasswordChecker.Result>> {

    public static final String PARAM_BUSINESS_ADMINISTRATOR = "businessAdministrator";

    final private UserId username;
    private final String password;

    public CheckBusinessAdministratorPasswordCommand(
        @Param(name=PARAM_BUSINESS_ADMINISTRATOR) BusinessAdministrator businessAdministrator,
        @Param(name="password") String password
    ) {
        this.username = businessAdministrator.getUserId();
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
        BusinessAdministrator other = connectedUser.getConnector().getBusinessAdministrators().get(username);
        if(!connectedUser.canAccessBusinessAdministrator(other)) {
            errors = addValidationError(errors, PARAM_BUSINESS_ADMINISTRATOR, ApplicationResources.accessor, "Common.validate.accessDenied");
        }
        return errors;
    }

    static List<PasswordChecker.Result> checkPassword(UserId username, String password) throws IOException {
        return PasswordChecker.checkPassword(username, password, PasswordChecker.PasswordStrength.STRICT);
    }

    @Override
    public List<PasswordChecker.Result> execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        try {
            return checkPassword(username, password);
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
