/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/**
 * <code>Ticket</code>s are prioritized by both the client and
 * support personnel.  Each priority is set as a <code>TicketPriority</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketPriority extends AOServObjectStringKey<TicketPriority> implements BeanFactory<com.aoindustries.aoserv.client.beans.TicketPriority> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The possible ticket priorities.
     */
    public static final String
        LOW="0-Low",
        NORMAL="1-Normal",
        HIGH="2-High",
        URGENT="3-Urgent"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public TicketPriority(TicketPriorityService<?,?> table, String priority) {
        super(table, priority);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="priority", index=IndexType.PRIMARY_KEY, description="the unique priority")
    public String getPriority() {
    	return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TicketPriority getBean() {
        return new com.aoindustries.aoserv.client.beans.TicketPriority(getKey());
    }
    // </editor-fold>
}
