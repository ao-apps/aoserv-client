/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * Each reason for notifying clients is represented by a
 * <code>NoticeType</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeType extends AOServObjectStringKey implements Comparable<NoticeType>, DtoFactory<com.aoindustries.aoserv.client.dto.NoticeType> {

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
    public NoticeType(AOServConnector connector, String type) {
        super(connector, type);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(NoticeType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="type", index=IndexType.PRIMARY_KEY, description="the unique type name")
    public String getType() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.NoticeType getDto() {
        return new com.aoindustries.aoserv.client.dto.NoticeType(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getNoticeLogs());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("NoticeType."+getKey()+".description");
    }
    // </editor-fold>
}