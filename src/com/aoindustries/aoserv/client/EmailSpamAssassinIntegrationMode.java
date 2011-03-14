/*
 * Copyright 2005-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;

/**
 * An <code>EmailSpamAssassinIntegrationMode</code> is a simple wrapper for the types
 * of SpamAssassin integration modes.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class EmailSpamAssassinIntegrationMode
extends AOServObjectStringKey
implements
    Comparable<EmailSpamAssassinIntegrationMode>,
    DtoFactory<com.aoindustries.aoserv.client.dto.EmailSpamAssassinIntegrationMode> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    // TODO: private static final long serialVersionUID = 1L;

    public static final String
        NONE="none",
        POP3="pop3",
        IMAP="imap"
    ;

    public static final String DEFAULT_SPAMASSASSIN_INTEGRATION_MODE=IMAP;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public EmailSpamAssassinIntegrationMode(AOServConnector connector, String name, short sortOrder) {
        super(connector, name);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(EmailSpamAssassinIntegrationMode other) {
        return compare(sortOrder, other.sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique name of the mode")
    public String getName() {
        return getKey();
    }

    @SchemaColumn(order=1, description="provides ordering of the modes")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public EmailSpamAssassinIntegrationMode(AOServConnector connector, com.aoindustries.aoserv.client.dto.EmailSpamAssassinIntegrationMode dto) {
        this(connector, dto.getName(), dto.getSortOrder());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.EmailSpamAssassinIntegrationMode getDto() {
        return new com.aoindustries.aoserv.client.dto.EmailSpamAssassinIntegrationMode(getKey(), sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("EmailSpamAssassinIntegrationMode."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<EmailInbox> getEmailInboxes() throws RemoteException {
        return getConnector().getEmailInboxes().filterIndexed(EmailInbox.COLUMN_SA_INTEGRATION_MODE, this);
    }
    // </editor-fold>
}