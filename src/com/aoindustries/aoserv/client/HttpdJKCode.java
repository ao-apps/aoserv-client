/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/**
 * When using Apache's <code>mod_jk</code>, each connection to a servlet
 * container is assigned a unique two-character identifier.  This
 * identifier is represented by an <code>HttpdJKCode</code>.
 *
 * @see  HttpdWorker
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJKCode extends AOServObjectStringKey implements Comparable<HttpdJKCode>, DtoFactory<com.aoindustries.aoserv.client.dto.HttpdJKCode> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public HttpdJKCode(AOServConnector connector, String code) {
        super(connector, code);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(HttpdJKCode other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="code", index=IndexType.PRIMARY_KEY, description="the unique, two-character code")
    public String getCode() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.HttpdJKCode getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdJKCode(getKey());
    }
    // </editor-fold>
}