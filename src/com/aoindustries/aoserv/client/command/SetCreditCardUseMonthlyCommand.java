/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Sets the credit card that will be used monthly.  Any other selected card will
 * be deselected.  If <code>creditCard</code> is null, none will be used automatically.
 *
 * @author  AO Industries, Inc.
 */
final public class SetCreditCardUseMonthlyCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 4153190954855834499L;

    final private AccountingCode accounting;
    final private Integer creditCard;

    public SetCreditCardUseMonthlyCommand(
        @Param(name="business") Business business,
        @Param(name="creditCard", nullable=true) CreditCard creditCard
    ) {
        this.accounting = business.getAccounting();
        this.creditCard = creditCard==null ? null : creditCard.getKey();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public Integer getCreditCard() {
        return creditCard;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
