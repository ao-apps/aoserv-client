/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/**
 * Each reason for notifying clients is represented by a
 * <code>NoticeType</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeType extends AOServObjectStringKey implements Comparable<NoticeType>, DtoFactory<com.aoindustries.aoserv.client.dto.NoticeType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    public static final String
        NONPAY="nonpay",
        BADCARD="badcard",
        DISABLE_WARNING="disable_warning",
        DISABLED="disabled",
        ENABLED="enabled"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 1445397931590541727L;

    public NoticeType(AOServConnector connector, String type) {
        super(connector, type);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(NoticeType other) {
        return compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique type name")
    public String getType() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public NoticeType(AOServConnector connector, com.aoindustries.aoserv.client.dto.NoticeType dto) {
        this(connector, dto.getType());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.NoticeType getDto() {
        return new com.aoindustries.aoserv.client.dto.NoticeType(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDescription() {
        return ApplicationResources.accessor.getMessage("NoticeType."+getKey()+".description");
    }
    // </editor-fold>
}