package com.aoindustries.aoserv.client.noswing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnectorFactory;
import com.aoindustries.security.LoginException;
import java.rmi.RemoteException;
import java.util.Locale;
import javax.swing.SwingUtilities;

/**
 * An implementation of <code>AOServConnectorFactory</code> that immediately
 * fails when any call is made on the Swing event thread.  Because network
 * delays can be unpredictable, this can lead to much more responsive and
 * resilient Swing interfaces.
 *
 * @see SwingUtilities#isEventDispatchThread()
 *
 * @author  AO Industries, Inc.
 */
final public class NoSwingConnectorFactory implements AOServConnectorFactory<NoSwingConnector,NoSwingConnectorFactory> {

    static void checkNotSwing() throws RemoteException {
        if(SwingUtilities.isEventDispatchThread()) throw new RemoteException("Refusing to place AOServ call from Swing event dispatch thread");
    }

    final AOServConnectorFactory<?,?> wrapped;

    public NoSwingConnectorFactory(AOServConnectorFactory<?,?> wrapped) {
        this.wrapped = wrapped;
    }

    public NoSwingConnector newConnector(Locale locale, String connectAs, String authenticateAs, String password, String daemonServer) throws LoginException, RemoteException {
        checkNotSwing();
        return new NoSwingConnector(this, wrapped.newConnector(locale, connectAs, authenticateAs, password, daemonServer));
    }
}
