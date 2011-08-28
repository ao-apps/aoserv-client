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
final public class ReactivateCreditCardCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = -8688628450495707236L;

    final private int pkey;

    public ReactivateCreditCardCommand(
        @Param(name="creditCard") CreditCard creditCard
    ) {
        this.pkey = creditCard.getPkey();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getPkey() {
        return pkey;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
