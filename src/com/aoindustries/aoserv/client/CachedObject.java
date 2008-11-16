package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * A <code>CachedObject</code> is stored in
 * a <code>CachedTable</code> for greater
 * performance.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class CachedObject<K,T extends CachedObject<K,T>> extends AOServObject<K,T> implements SingleTableObject<K,T> {

    protected AOServTable<K,T> table;

    protected CachedObject() {
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<K,T> getTable() {
        return table;
    }

    final public void setTable(AOServTable<K,T> table) {
        if(this.table!=null) throw new IllegalStateException("table already set");
        this.table=table;
    }
}