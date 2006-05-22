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
 * @see  GlobalObjectIntegerKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract class GlobalTableIntegerKey<V extends GlobalObjectIntegerKey<V>> extends GlobalTable<Integer,V> {

    GlobalTableIntegerKey(AOServConnector connector, Class<V> clazz) {
	super(connector, clazz);
    }
}