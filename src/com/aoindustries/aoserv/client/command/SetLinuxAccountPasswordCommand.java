package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.LinuxAccount;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetLinuxAccountPasswordCommand extends AOServCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_LINUX_ACCOUNT = "linux_account",
        PARAM_PLAINTEXT = "plaintext"
    ;

    final private int linuxAccount;
    final private String plaintext;

    public SetLinuxAccountPasswordCommand(
        @Param(name=PARAM_LINUX_ACCOUNT) int linuxAccount,
        @Param(name=PARAM_PLAINTEXT) String plaintext
    ) {
        this.linuxAccount = linuxAccount;
        this.plaintext = plaintext;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        LinuxAccount la = connectedUser.getService().getConnector().getLinuxAccounts().get(linuxAccount);
        if(la.getAoServerResource().getResource().getDisableLog()!=null) errors = addValidationError(errors, PARAM_LINUX_ACCOUNT, locale, "SetLinuxAccountPasswordCommand.validate.disabled");
        if(!la.getLinuxAccountType().isSetPasswordAllowed()) errors = addValidationError(errors, PARAM_LINUX_ACCOUNT, locale, "SetLinuxAccountPasswordCommand.validate.typeNotAllowed");
        // TODO: Check password strength
        return errors;
    }
}
