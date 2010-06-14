/*
 * Copyright 2005-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

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

    public static final String DEFAULT_SPAMASSASSIN_INTEGRATION_MODE=IMAP;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public EmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationModeService<?,?> service, String name, short sortOrder) {
        super(service, name);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(EmailSpamAssassinIntegrationMode other) {
        return AOServObjectUtils.compare(sortOrder, other.sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of the mode")
    public String getName() {
        return getKey();
    }

    @SchemaColumn(order=1, name="sort_order", index=IndexType.UNIQUE, description="provides ordering of the modes")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.EmailSpamAssassinIntegrationMode getBean() {
        return new com.aoindustries.aoserv.client.beans.EmailSpamAssassinIntegrationMode(getKey(), sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getEmailInboxes()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return ApplicationResources.accessor.getMessage("EmailSpamAssassinIntegrationMode."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<EmailInbox> getEmailInboxes() throws RemoteException {
        return getService().getConnector().getEmailInboxes().filterIndexed(EmailInbox.COLUMN_SA_INTEGRATION_MODE, this);
    }
    // </editor-fold>
}