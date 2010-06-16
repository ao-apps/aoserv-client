/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class HoldTransactionCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private int transid;
    final private int creditCardTransaction;

    public HoldTransactionCommand(
        @Param(name="transid") int transid,
        @Param(name="creditCardTransaction") int creditCardTransaction
    ) {
        this.transid = transid;
        this.creditCardTransaction = creditCardTransaction;
    }

    public int getTransid() {
        return transid;
    }

    public int getCreditCardTransaction() {
        return creditCardTransaction;
    }

    @Override
    public boolean isReadOnlyCommand() {
        return false;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        return Collections.emptyMap();
    }
}
