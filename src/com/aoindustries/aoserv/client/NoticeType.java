/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.util.Locale;

/**
 * Each reason for notifying clients is represented by a
 * <code>NoticeType</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeType extends AOServObjectStringKey<NoticeType> implements BeanFactory<com.aoindustries.aoserv.client.beans.NoticeType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        NONPAY="nonpay",
        BADCARD="badcard",
        DISABLE_WARNING="disable_warning",
        DISABLED="disabled",
        ENABLED="enabled"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public NoticeType(NoticeTypeService<?,?> service, String type) {
        super(service, type);
    }
    // </editor-fold>

    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "NoticeType."+key+".description");
    }

    public String getType() {
        return key;
    }
}