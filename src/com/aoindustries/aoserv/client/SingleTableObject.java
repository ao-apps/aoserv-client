package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Indicates that an object is contained in only one table.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public interface SingleTableObject<K,V extends AOServObject<K,V>> {

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    AOServTable<K,V> getTable();

    void setTable(AOServTable<K,V> table);
}
