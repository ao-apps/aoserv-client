package com.aoindustries.aoserv.client;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  CachedObjectStringKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract class CachedTableStringKey<V extends CachedObjectStringKey<V>> extends CachedTable<String,V> {

    CachedTableStringKey(AOServConnector connector, Class<V> clazz) {
	super(connector, clazz);
    }
}