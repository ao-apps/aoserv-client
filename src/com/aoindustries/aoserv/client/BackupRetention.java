/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * The possible backup retention values allowed in the system.
 *
 * @author  AO Industries, Inc.
 */
final public class BackupRetention extends AOServObjectShortKey implements Comparable<BackupRetention>, DtoFactory<com.aoindustries.aoserv.client.dto.BackupRetention> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public BackupRetention(AOServConnector connector, short days) {
        super(connector, days);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(BackupRetention other) {
        return AOServObjectUtils.compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="days", index=IndexType.PRIMARY_KEY, description="the number of days to keep the backup data")
    public short getDays() {
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.BackupRetention getDto() {
        return new com.aoindustries.aoserv.client.dto.BackupRetention(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailInboxesByTrashEmailRetention());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailInboxesByJunkEmailRetention());
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getEmailInboxAddresses());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getFailoverFileReplications());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("BackupRetention."+key+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<EmailInbox> getEmailInboxesByTrashEmailRetention() throws RemoteException {
        return getConnector().getEmailInboxes().filterIndexed(EmailInbox.COLUMN_TRASH_EMAIL_RETENTION, this);
    }

    public IndexedSet<EmailInbox> getEmailInboxesByJunkEmailRetention() throws RemoteException {
        return getConnector().getEmailInboxes().filterIndexed(EmailInbox.COLUMN_JUNK_EMAIL_RETENTION, this);
    }

    public IndexedSet<FailoverFileReplication> getFailoverFileReplications() throws RemoteException {
        return getConnector().getFailoverFileReplications().filterIndexed(FailoverFileReplication.COLUMN_RETENTION, this);
    }
    // </editor-fold>
}
