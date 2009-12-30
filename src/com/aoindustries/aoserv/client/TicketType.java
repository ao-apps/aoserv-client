package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * Each <code>Ticket</code> is of a specific <code>TicketType</code>.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketType extends AOServObjectStringKey<TicketType> implements BeanFactory<com.aoindustries.aoserv.client.beans.TicketType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The types of <code>Ticket</code>s.
     */
    public static final String
        CONTACT="contact",
        LOGS="logs",
        SUPPORT="support",
        PROJECTS="projects",
        INTERNAL="internal"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public TicketType(TicketTypeService<?,?> table, String type) {
        super(table, type);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="type", unique=true, description="the unique type name")
    public String getType() {
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TicketType getBean() {
        return new com.aoindustries.aoserv.client.beans.TicketType(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TicketType."+key+".toString");
    }
    // </editor-fold>
}
