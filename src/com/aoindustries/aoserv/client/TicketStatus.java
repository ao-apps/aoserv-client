package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatus extends AOServObjectStringKey<TicketStatus> {

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
    final private short sort_order;

    public TicketStatus(TicketStatusService<?,?> table, String status, short sort_order) {
        super(table, status);
        this.sort_order = sort_order;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(TicketStatus other) {
        return compare(sort_order, other.sort_order);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="status", unique=true, description="the name of this status")
    public String getStatus() {
        return key;
    }

    @SchemaColumn(order=1, name="sort_order", unique=true, description="the default sort ordering")
    public short getSortOrder() {
        return sort_order;
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