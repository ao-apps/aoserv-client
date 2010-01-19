package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.wrapped.WrappedConnector;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * @see NoSwingConnectorFactory
 *
 * @author  AO Industries, Inc.
 */
final public class NoSwingConnector extends WrappedConnector<NoSwingConnector,NoSwingConnectorFactory> {

    NoSwingConnector(NoSwingConnectorFactory factory, Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws RemoteException, LoginException {
        super(factory, locale, connectAs, authenticateAs, password, daemonServer);
    }

    @Override
    protected AOServConnector<?,?> getWrapped() throws RemoteException {
        NoSwingConnectorFactory.checkNotSwing();
        return super.getWrapped();
    }

    protected boolean isAoServObjectServiceSettable() {
        return true;
    }
}
