package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

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
}