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
final public class SetLinuxAccountPasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String PARAM_LINUX_ACCOUNT = "linuxAccount";
    public static final String PARAM_PLAINTEXT = "plaintext";

    final private int linuxAccount;
    final private String plaintext;

    public SetLinuxAccountPasswordCommand(
        @Param(name=PARAM_LINUX_ACCOUNT) LinuxAccount linuxAccount,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.linuxAccount = linuxAccount.getKey();
        this.plaintext = plaintext;
    }

    public int getLinuxAccount() {
        return linuxAccount;
    }

    public String getPlaintext() {
        return plaintext;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // Check access
        LinuxAccount la = connectedUser.getConnector().getLinuxAccounts().get(linuxAccount);
        if(!connectedUser.canAccessLinuxAccount(la)) {
            errors = addValidationError(errors, PARAM_LINUX_ACCOUNT, ApplicationResources.accessor, "Common.validate.accessDenied");
        } else {
            // Enforce can't set password type
            LinuxAccountType lat = la.getLinuxAccountType();
            if(!lat.isSetPasswordAllowed()) errors = addValidationError(errors, PARAM_LINUX_ACCOUNT, ApplicationResources.accessor, "SetLinuxAccountPasswordCommand.validate.typeNotAllowed");
            // Make sure not disabled
            if(la.isDisabled()) errors = addValidationError(errors, PARAM_LINUX_ACCOUNT, ApplicationResources.accessor, "SetLinuxAccountPasswordCommand.validate.disabled");
            else {
                // Check password strength
                try {
                    if(plaintext!=null && plaintext.length()>0) errors = addValidationError(errors, PARAM_PLAINTEXT, CheckLinuxAccountPasswordCommand.checkPassword(la, plaintext));
                } catch(IOException err) {
                    throw new RemoteException(err.getMessage(), err);
                }
            }
        }
        return errors;
    }
}
