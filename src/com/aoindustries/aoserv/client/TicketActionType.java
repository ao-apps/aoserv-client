/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * All of the types of ticket changes are represented by these
 * <code>TicketActionType</code>s.
 *
 * @see TicketAction
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketActionType extends AOServObjectStringKey implements Comparable<TicketActionType>, DtoFactory<com.aoindustries.aoserv.client.dto.TicketActionType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        SET_BUSINESS="set_business",
        SET_CONTACT_EMAILS="set_contact_emails",
        SET_CONTACT_PHONE_NUMBERS="set_contact_phone_numbers",
        SET_CLIENT_PRIORITY="set_client_priority",
        SET_SUMMARY="set_summary",
        ADD_ANNOTATION="add_annotation",
        SET_STATUS="set_status",
        SET_ADMIN_PRIORITY="set_admin_priority",
        ASSIGN="assign",
        SET_CATEGORY="set_category",
        SET_INTERNAL_NOTES="set_internal_notes",
        SET_TYPE="set_type"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private boolean visibleAdminOnly;

    public TicketActionType(AOServConnector connector, String type, boolean visibleAdminOnly) {
        super(connector, type);
        this.visibleAdminOnly = visibleAdminOnly;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TicketActionType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="type", index=IndexType.PRIMARY_KEY, description="the type name")
    public String getType() {
        return getKey();
    }

    @SchemaColumn(order=1, name="visible_admin_only", description="when true, only visible to ticket administrators")
    public boolean isVisibleAdminOnly() {
        return visibleAdminOnly;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TicketActionType(AOServConnector connector, com.aoindustries.aoserv.client.dto.TicketActionType dto) {
        this(connector, dto.getType(), dto.isVisibleAdminOnly());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TicketActionType getDto() {
        return new com.aoindustries.aoserv.client.dto.TicketActionType(getKey(), visibleAdminOnly);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject<?>> getDependentObjects() throws RemoteException {
        return getTicketActions();
    }

    /*
    @Override
    protected UnionClassSet<AOServObject<?>> addDependentObjects(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(null);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, );
        return unionSet;
    }*/
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("TicketActionType."+getKey()+".toString");
    }

    /**
     * Generates a locale-specific summary.
     */
    String generateSummary(AOServConnector connector, String oldValue, String newValue) {
        if(oldValue==null) {
            if(newValue==null) return ApplicationResources.accessor.getMessage("TicketActionType."+getKey()+".generatedSummary.null.null");
            return ApplicationResources.accessor.getMessage("TicketActionType."+getKey()+".generatedSummary.null.notNull", newValue);
        } else {
            if(newValue==null) return ApplicationResources.accessor.getMessage("TicketActionType."+getKey()+".generatedSummary.notNull.null", oldValue);
            return ApplicationResources.accessor.getMessage("TicketActionType."+getKey()+".generatedSummary.notNull.notNull", oldValue, newValue);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<TicketAction> getTicketActions() throws RemoteException {
        return getConnector().getTicketActions().filterIndexed(TicketAction.COLUMN_ACTION_TYPE, this);
    }
    // </editor-fold>
}
