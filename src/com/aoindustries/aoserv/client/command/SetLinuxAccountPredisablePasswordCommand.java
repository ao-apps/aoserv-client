/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetLinuxAccountPredisablePasswordCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = -253120119915651742L;

    final private int linuxAccount;
    final private String encryptedPassword;

    public SetLinuxAccountPredisablePasswordCommand(
        @Param(name="linuxAccount") LinuxAccount linuxAccount,
        @Param(name="encryptedPassword") String encryptedPassword
    ) {
        this.linuxAccount = linuxAccount.getPkey();
        this.encryptedPassword = encryptedPassword;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getLinuxAccount() {
        return linuxAccount;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
