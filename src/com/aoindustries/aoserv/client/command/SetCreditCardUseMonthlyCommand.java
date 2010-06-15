package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetCreditCardUseMonthlyCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private AccountingCode accounting;
    final private Integer creditCard;

    public SetCreditCardUseMonthlyCommand(
        @Param(name="accounting") AccountingCode accounting,
        @Param(name="creditCard", nullable=true) Integer creditCard
    ) {
        this.accounting = accounting;
        this.creditCard = creditCard;
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public Integer getCreditCard() {
        return creditCard;
    }

    @Override
    public boolean isReadOnlyCommand() {
        return false;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
