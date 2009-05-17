package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  CachedObjectIntegerKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTableIntegerKey<V extends CachedObjectIntegerKey<V>> extends CachedTable<Integer,V> {

    CachedTableIntegerKey(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
    }

    /**
     * Gets the object with the provided key.  The key must be an Integer.
     */
    public V get(Object pkey) {
        try {
            return get(((Integer)pkey).intValue());
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    abstract public V get(int pkey) throws IOException, SQLException;
}