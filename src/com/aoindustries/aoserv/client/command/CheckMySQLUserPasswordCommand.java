/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.MySQLUserId;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class CheckMySQLUserPasswordCommand extends AOServCommand<List<PasswordChecker.Result>> {

    public static final String PARAM_MYSQL_USER = "mysqlUser";

    final private int mysqlUser;
    private final String password;

    public CheckMySQLUserPasswordCommand(
        @Param(name=PARAM_MYSQL_USER) MySQLUser mysqlUser,
        @Param(name="password") String password
    ) {
        this.mysqlUser = mysqlUser.getPkey();
        this.password = password;
    }

    public int getMysqlUser() {
        return mysqlUser;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        MySQLUser mu = connectedUser.getConnector().getMysqlUsers().get(mysqlUser);
        if(!connectedUser.canAccessMySQLUser(mu)) {
            errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // No setting root password
            MySQLUserId username = mu.getUserId();
            if(
                username==MySQLUser.ROOT // OK - interned
            ) errors = addValidationError(errors, PARAM_MYSQL_USER, ApplicationResources.accessor, "SetMySQLUserPasswordCommand.validate.noSetRoot");
        }
        return errors;
    }

    static List<PasswordChecker.Result> checkPassword(MySQLUser mu, String password) throws IOException {
        return PasswordChecker.checkPassword(mu.getUserId().getUserId(), password, PasswordChecker.PasswordStrength.STRICT);
    }

    @Override
    public List<PasswordChecker.Result> execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        try {
            return checkPassword(connector.getMysqlUsers().get(mysqlUser), password);
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
