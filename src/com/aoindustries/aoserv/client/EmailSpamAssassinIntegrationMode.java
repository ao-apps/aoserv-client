/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.Locale;

/**
 * An <code>EmailSpamAssassinIntegrationMode</code> is a simple wrapper for the types
 * of SpamAssassin integration modes.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class EmailSpamAssassinIntegrationMode extends AOServObjectStringKey<EmailSpamAssassinIntegrationMode> implements BeanFactory<com.aoindustries.aoserv.client.beans.EmailSpamAssassinIntegrationMode> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        NONE="none",
        POP3="pop3",
        IMAP="imap"
    ;

    public static final String DEFAULT_SPAMASSASSIN_INTEGRATION_MODE=POP3;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public EmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationModeService<?,?> service, String name, short sortOrder) {
        super(service, name);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EmailSpamAssassinIntegrationMode.COLUMN_SORT_ORDER_name, ASCENDING)
    };

    public String getName() {
        return key;
    }

    public String getDisplay(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "EmailSpamAssassinIntegrationMode."+key+".display");
    }

    public short getSortOrder() {
        return sortOrder;
    }
}