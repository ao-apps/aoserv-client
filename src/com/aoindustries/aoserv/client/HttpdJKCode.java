/*
 * Copyright 2000-2009 by AO Industries, Inc.,
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
final public class HttpdJKCode extends AOServObjectStringKey<HttpdJKCode> implements BeanFactory<com.aoindustries.aoserv.client.beans.HttpdJKCode> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public HttpdJKCode(HttpdJKCodeService<?,?> service, String code) {
        super(service, code);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="code", index=IndexType.PRIMARY_KEY, description="the unique, two-character code")
    public String getCode() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.HttpdJKCode getBean() {
        return new com.aoindustries.aoserv.client.beans.HttpdJKCode(getKey());
    }
    // </editor-fold>
}