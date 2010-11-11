/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.trace;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.aoserv.client.wrapped.*;
import com.aoindustries.security.LoginException;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.ErrorPrinter;
import java.rmi.RemoteException;
import java.util.Locale;

/**
 * An implementation of <code>AOServConnectorFactory</code> that logs trace information.
 *
 * @author  AO Industries, Inc.
 */
final public class TraceConnectorFactory extends WrappedConnectorFactory<TraceConnector,TraceConnectorFactory> {

    public TraceConnectorFactory(AOServConnectorFactory<?,?> wrapped) {
        super(wrapped);
    }

    @Override
    protected TraceConnector newWrappedConnector(Locale locale, UserId connectAs, UserId authenticateAs, String password, DomainName daemonServer) throws LoginException, RemoteException {
        long startNanos = System.nanoTime();
        try {
            return new TraceConnector(this, locale, connectAs, authenticateAs, password, daemonServer);
        } finally {
            long nanos = System.nanoTime() - startNanos;
            ErrorPrinter.printStackTraces(new Throwable(SQLUtility.getMilliDecimal(nanos / 1000)+"ms"), System.err);
            System.err.flush();
        }
    }
}
