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
 * @author  AO Industries, Inc.
 */
final public class CancelBusinessCommand extends RemoteCommand<Void> {

    // TODO: private static final long serialVersionUID = 1L;

    final private AccountingCode accounting;
    final private String cancelReason;

    public CancelBusinessCommand(
        @Param(name="business") Business business,
        @Param(name="cancelReason", nullable=true) String cancelReason
    ) {
        this.accounting = business.getAccounting();
        this.cancelReason = nullIfEmpty(cancelReason);
    }

    public AccountingCode getAccounting() {
        return accounting;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO: Validate: return canceled==null && !isRootBusiness();

        // Automatically disable if not already disabled
        // TODO: if(disableLog==null) {
        // TODO:     new SimpleAOClient(service.connector).disableBusiness(pkey, "Account canceled");
        // TODO: }

        // Now cancel the account
        // TODO: if(cancelReason!=null && (cancelReason=cancelReason.trim()).length()==0) cancelReason=null;

        // TODO
        // Check access to business
        // checkAccessBusiness(conn, source, "cancelBusiness", accounting);

        // if(accounting.equals(getRootBusiness())) throw new SQLException("Not allowed to cancel the root business: "+accounting);

        // Business must be disabled
        // if(!isBusinessDisabled(conn, accounting)) throw new SQLException("Unable to cancel Business, Business not disabled: "+accounting);

        // Business must not already be canceled
        // if(isBusinessCanceled(conn, accounting)) throw new SQLException("Unable to cancel Business, Business already canceled: "+accounting);

        // All children must be canceled
        
        return Collections.emptyMap();
    }
}
