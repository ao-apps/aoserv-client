/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class TicketActionType {

    private String type;
    private boolean visibleAdminOnly;

    public TicketActionType() {
    }

    public TicketActionType(String type, boolean visibleAdminOnly) {
        this.type = type;
        this.visibleAdminOnly = visibleAdminOnly;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVisibleAdminOnly() {
        return visibleAdminOnly;
    }

    public void setVisibleAdminOnly(boolean visibleAdminOnly) {
        this.visibleAdminOnly = visibleAdminOnly;
    }
}
