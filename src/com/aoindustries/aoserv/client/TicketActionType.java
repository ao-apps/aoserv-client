/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.Locale;

/**
 * All of the types of ticket changes are represented by these
 * <code>TicketActionType</code>s.
 *
 * @see TicketAction
 * @see Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketActionType extends AOServObjectStringKey<TicketActionType> implements BeanFactory<com.aoindustries.aoserv.client.beans.TicketActionType> {

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

    public TicketActionType(TicketActionTypeService<?,?> service, String type, boolean visibleAdminOnly) {
        super(service, type);
        this.visibleAdminOnly = visibleAdminOnly;
    }
    // </editor-fold>

    public String getType() {
        return key;
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "TicketActionType."+pkey+".toString");
    }

    /**
     * Generates a locale-specific summary.
     */
    String generateSummary(AOServConnector connector, Locale userLocale, String oldValue, String newValue) {
        if(oldValue==null) {
            if(newValue==null) return ApplicationResources.accessor.getMessage(userLocale, "TicketActionType."+key+".generatedSummary.null.null");
            return ApplicationResources.accessor.getMessage(userLocale, "TicketActionType."+key+".generatedSummary.null.notNull", newValue);
        } else {
            if(newValue==null) return ApplicationResources.accessor.getMessage(userLocale, "TicketActionType."+key+".generatedSummary.notNull.null", oldValue);
            return ApplicationResources.accessor.getMessage(userLocale, "TicketActionType."+key+".generatedSummary.notNull.notNull", oldValue, newValue);
        }
    }
}
