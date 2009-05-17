package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;

/**
 * @see  GlobalObjectStringKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public abstract class GlobalTableStringKey<V extends GlobalObjectStringKey<V>> extends GlobalTable<String,V> {

    GlobalTableStringKey(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
    }

    /**
     * Gets the object with the provided key.  The key must be a string.
     */
    public V get(Object pkey) {
        try {
            return get((String)pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    abstract public V get(String pkey) throws IOException, SQLException;
}