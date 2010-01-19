package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * The possible backup retention values allowed in the system.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupRetention extends AOServObjectShortKey<BackupRetention> implements BeanFactory<com.aoindustries.aoserv.client.beans.BackupRetention> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String display;

    public BackupRetention(BackupRetentionService<?,?> service, short days, String display) {
        super(service, days);
        this.display = display;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        display = intern(display);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="days", index=IndexType.PRIMARY_KEY, description="the number of days to keep the backup data")
    public short getDays() {
        return key;
    }

    @SchemaColumn(order=1, name="display", description="the text displayed for this time increment")
    public String getDisplay() {
        return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.BackupRetention getBean() {
        return new com.aoindustries.aoserv.client.beans.BackupRetention(key, display);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getEmailInboxesByTrashEmailRetention(),
            getEmailInboxesByJunkEmailRetention()
            // TODO: getEmailInboxAddresses()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
    	return display;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<EmailInbox> getEmailInboxesByTrashEmailRetention() throws RemoteException {
        return getService().getConnector().getEmailInboxes().filterIndexed(EmailInbox.COLUMN_TRASH_EMAIL_RETENTION, this);
    }

    public IndexedSet<EmailInbox> getEmailInboxesByJunkEmailRetention() throws RemoteException {
        return getService().getConnector().getEmailInboxes().filterIndexed(EmailInbox.COLUMN_JUNK_EMAIL_RETENTION, this);
    }
    // </editor-fold>
}
