/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class CheckLinuxAccountPasswordCommand extends AOServCommand<List<PasswordChecker.Result>> {

    public static final String PARAM_LINUX_ACCOUNT = "linuxAccount";

    private final int linuxAccount;
    private final String password;

    public CheckLinuxAccountPasswordCommand(
        @Param(name=PARAM_LINUX_ACCOUNT) LinuxAccount linuxAccount,
        @Param(name="password") String password
    ) {
        this.linuxAccount = linuxAccount.getKey();
        this.password = password;
    }

    public int getLinuxAccount() {
        return linuxAccount;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        LinuxAccount la = connectedUser.getService().getConnector().getLinuxAccounts().get(linuxAccount);
        if(!connectedUser.canAccessLinuxAccount(la)) {
            errors = addValidationError(errors, PARAM_LINUX_ACCOUNT, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // Enforce can't set password type
            LinuxAccountType lat = la.getLinuxAccountType();
            if(!lat.isSetPasswordAllowed()) errors = addValidationError(errors, PARAM_LINUX_ACCOUNT, ApplicationResources.accessor, "CheckLinuxAccountPasswordCommand.validate.typeNotAllowed");
        }
        return errors;
    }

    static List<PasswordChecker.Result> checkPassword(LinuxAccount la, String password) throws IOException {
        return la.getLinuxAccountType().checkPassword(la.getUserId(), password);
    }

    @Override
    public List<PasswordChecker.Result> execute(AOServConnector<?,?> connector, boolean isInteractive) throws RemoteException {
        try {
            return checkPassword(connector.getLinuxAccounts().get(linuxAccount), password);
        } catch(IOException err) {
            throw new RemoteException(err.getMessage(), err);
        }
    }
}
