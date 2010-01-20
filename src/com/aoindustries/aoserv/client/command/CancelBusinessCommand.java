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
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class CancelBusinessCommand extends AOServCommand<Void> {

    private static final long serialVersionUID = 1L;

    public static final String
        PARAM_ACCOUNTING = "accounting",
        PARAM_CANCEL_REASON = "cancel_reason"
    ;

    final private AccountingCode accounting;
    final private String cancelReason;

    public CancelBusinessCommand(
        @Param(name=PARAM_ACCOUNTING) AccountingCode accounting,
        @Param(name=PARAM_CANCEL_REASON, nullable=true) String cancelReason
    ) {
        this.accounting = accounting;
        this.cancelReason = cancelReason;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO: Validate: return canceled==null && !isRootBusiness();

        // Automatically disable if not already disabled
        // TODO: if(disableLog==null) {
        // TODO:     new SimpleAOClient(service.connector).disableBusiness(pkey, "Account canceled");
        // TODO: }

        // Now cancel the account
        // TODO: if(cancelReason!=null && (cancelReason=cancelReason.trim()).length()==0) cancelReason=null;

        // TODO
        return Collections.emptyMap();
    }
}
