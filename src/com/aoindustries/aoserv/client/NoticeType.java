/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

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

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="type", index=IndexType.PRIMARY_KEY, description="the unique type name")
    public String getType() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.NoticeType getBean() {
        return new com.aoindustries.aoserv.client.beans.NoticeType(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            // TODO: getNoticeLogs()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "NoticeType."+getKey()+".description");
    }
    // </editor-fold>
}