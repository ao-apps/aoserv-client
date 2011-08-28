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
final public class GetTransactionDescriptionCommand extends RemoteCommand<String> {

    private static final long serialVersionUID = 4342119144411846371L;

    final private int transid;

    public GetTransactionDescriptionCommand(
        @Param(name="transaction") Transaction transaction
    ) {
        this.transid = transaction.getTransid();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public int getTransid() {
        return transid;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
