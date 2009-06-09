package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectStringKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableStringKey<V extends CachedObjectStringKey<V>> extends CachedTable<String,V> {

    CachedTableStringKey(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
    }

    /**
     * Gets the object with the provided key.  The key must be a string.
     */
    public V get(Object pkey) throws IOException, SQLException {
        return get((String)pkey);
    }

    abstract public V get(String pkey) throws IOException, SQLException;
}