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
final public class DeclineTransactionCommand extends RemoteCommand<Void> {

    // TODO: private static final long serialVersionUID = 1L;

    final private int transid;
    final private int creditCardTransaction;

    public DeclineTransactionCommand(
        @Param(name="transaction") Transaction transaction,
        @Param(name="creditCardTransaction") CreditCardTransaction creditCardTransaction
    ) {
        this.transid = transaction.getTransid();
        this.creditCardTransaction = creditCardTransaction.getPkey();
    }

    public int getTransid() {
        return transid;
    }

    public int getCreditCardTransaction() {
        return creditCardTransaction;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        return Collections.emptyMap();
    }
}
