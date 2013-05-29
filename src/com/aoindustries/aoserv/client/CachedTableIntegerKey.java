package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectIntegerKey
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableIntegerKey<V extends CachedObjectIntegerKey<V>> extends CachedTable<Integer,V> {

    CachedTableIntegerKey(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
    }

    /**
     * Gets the object with the provided key.  The key must be either an Integer or a String.
     * If a String, will be parsed to an integer.
     *
     * @exception IllegalArgumentException if pkey is neither an Integer nor a String.
     * @exception NumberFormatException if String cannot be parsed to an Integer
     */
    public V get(Object pkey) throws IOException, SQLException, IllegalArgumentException, NumberFormatException {
        if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
        else if(pkey instanceof String) return get(new Integer((String)pkey));
        else throw new IllegalArgumentException("pkey is neither an Integer nor a String: "+pkey);
    }

    abstract public V get(int pkey) throws IOException, SQLException;
}