package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;

/**
 * @see  GlobalObjectIntegerKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalTableIntegerKey<V extends GlobalObjectIntegerKey<V>> extends GlobalTable<Integer,V> {

    GlobalTableIntegerKey(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
    }

    /**
     * Gets the object with the provided key.  The key must be an Integer.
     */
    public V get(Object pkey) throws IOException, SQLException {
        return get(((Integer)pkey).intValue());
    }

    abstract public V get(int pkey) throws IOException, SQLException;
}