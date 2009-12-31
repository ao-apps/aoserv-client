package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.util.Locale;

/**
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatus extends AOServObjectStringKey<TicketStatus> implements BeanFactory<com.aoindustries.aoserv.client.beans.TicketStatus> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The different ticket statuses.
     */
    public static final String
        JUNK="junk",
        DELETED="deleted",
        CLOSED="closed",
        BOUNCED="bounced",
        HOLD="hold",
        OPEN="open"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private short sortOrder;

    public TicketStatus(TicketStatusService<?,?> table, String status, short sortOrder) {
        super(table, status);
        this.sortOrder = sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(TicketStatus other) {
        return compare(sortOrder, other.sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="status", index=IndexType.PRIMARY_KEY, description="the name of this status")
    public String getStatus() {
        return key;
    }

    @SchemaColumn(order=1, name="sort_order", index=IndexType.UNIQUE, description="the default sort ordering")
    public short getSortOrder() {
        return sortOrder;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TicketStatus getBean() {
        return new com.aoindustries.aoserv.client.beans.TicketStatus(key, sortOrder);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TicketStatus."+key+".toString");
    }

    /**
     * Localized description.
     */
    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TicketStatus."+key+".description");
    }
    // </editor-fold>
}