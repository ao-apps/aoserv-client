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
final public class HoldTransactionCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = -2305145353127738720L;

    final private int transid;
    final private int creditCardTransaction;

    public HoldTransactionCommand(
        @Param(name="transaction") Transaction transaction,
        @Param(name="creditCardTransaction") CreditCardTransaction creditCardTransaction
    ) {
        this.transid = transaction.getTransid();
        this.creditCardTransaction = creditCardTransaction.getPkey();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getTransid() {
        return transid;
    }

    public int getCreditCardTransaction() {
        return creditCardTransaction;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
