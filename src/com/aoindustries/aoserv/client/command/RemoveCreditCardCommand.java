/*
 * Copyright 2011 by AO Industries, Inc.,
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
final public class RemoveCreditCardCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 613163094634400715L;

    final private int creditCard;

    public RemoveCreditCardCommand(
        @Param(name="creditCard") CreditCard creditCard
    ) {
        this.creditCard = creditCard.getPkey();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getCreditCard() {
        return creditCard;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
    	// getConnector().requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.CREDIT_CARDS, pkey);
        return errors;
    }
}
