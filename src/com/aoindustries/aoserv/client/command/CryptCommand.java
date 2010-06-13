package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
        this.salt = salt==null || salt.length()==0 ? null : salt;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }

    @Override
    @SuppressWarnings("deprecation")
    public String execute(AOServConnector<?,?> connector, boolean isInteractive) throws RemoteException {
        if(salt==null) return com.aoindustries.util.UnixCrypt.crypt(password);
        return com.aoindustries.util.UnixCrypt.crypt(password, salt);
    }
}
