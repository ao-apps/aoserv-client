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
final public class CryptCommand extends AOServCommand<String> {

    private final String password;
    private final String salt;

    public CryptCommand(
        @Param(name="password") String password,
        @Param(name="salt", nullable=true) String salt
    ) {
        this.password = password;
        this.salt = nullIfEmpty(salt);
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }

    @Override
    @SuppressWarnings("deprecation")
    public String execute(AOServConnector connector, boolean isInteractive) throws RemoteException {
        if(salt==null) return com.aoindustries.util.UnixCrypt.crypt(password);
        return com.aoindustries.util.UnixCrypt.crypt(password, salt);
    }
}
