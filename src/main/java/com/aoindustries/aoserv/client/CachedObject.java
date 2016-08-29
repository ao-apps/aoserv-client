/*
 * Copyright 2001-2009, 2014 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * A <code>CachedObject</code> is stored in
 * a <code>CachedTable</code> for greater
 * performance.
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedObject<K,T extends CachedObject<K,T>> extends AOServObject<K,T> implements SingleTableObject<K,T> {

    protected AOServTable<K,T> table;

    protected CachedObject() {
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
	@Override
    final public AOServTable<K,T> getTable() {
        return table;
    }

	@Override
    final public void setTable(AOServTable<K,T> table) {
        if(this.table!=null) throw new IllegalStateException("table already set");
        this.table=table;
    }
}