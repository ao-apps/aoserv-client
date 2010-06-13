package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;

/**
 * Thrown when a read-write command is executed on a read-only connector.
 *
 * @author  AO Industries, Inc.
 */
public class ReadOnlyException extends RemoteException {

    private static final long serialVersionUID = 1L;

    public ReadOnlyException() {
        super();
    }
}
