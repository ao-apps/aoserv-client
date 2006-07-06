package com.aoindustries.aoserv.client;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  CachedObjectIntegerKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract class CachedTableIntegerKey<V extends CachedObjectIntegerKey<V>> extends CachedTable<Integer,V> {

    CachedTableIntegerKey(AOServConnector connector, Class<V> clazz) {
	super(connector, clazz);
    }
}