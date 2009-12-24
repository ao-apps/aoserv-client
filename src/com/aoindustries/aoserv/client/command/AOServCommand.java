package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.AOServConnector;
import java.rmi.RemoteException;

/**
 * <p>
 * Except normal tabular data queries, all other data access and data manipulation occurs as commands.
 * Commands may run entirely client-side, or be serialized to the server for processing.
 * Commands are checked client-side for performance and then checked server-side for safety.  The client-side
 * checks may be used in interface design.
 * </p>
 * <p>
 * A command may have any number of constructors, the AOSH will make its best attempt to match
 * the input to the command parameters for each constructor before failing.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public interface AOServCommand<R> {

    /**
     * Executes the command and retrieves the result.  If the command return
     * value is void, returns <code>Void.TYPE</code>.
     */
    abstract public R execute(AOServConnector<?,?> connector) throws RemoteException;
}
