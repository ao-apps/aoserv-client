/*
 * Copyright 2002-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.command.AOServCommand;
import java.rmi.RemoteException;

/**
 * Classes that are <code>Disablable</code> can be disable and reenabled.
 * 
 * TODO: Remove this interface once everything switched to resources.
 *
 * @author  AO Industries, Inc.
 */
public interface Disablable {

    /**
     * Checks if this object is disabled.  This should execute very quickly (not
     * incur any round-trip to any database) and thus does not throw any checked
     * exceptions.
     */
    boolean isDisabled();

    DisableLog getDisableLog() throws RemoteException;

    AOServCommand<Void> getDisableCommand(DisableLog dl);

    AOServCommand<Void> getEnableCommand();
}