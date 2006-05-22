package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  ServerReportSection
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class ServerReportSectionTable<T extends ServerReportSection<T>> extends CachedTable<Integer,T> {

    ServerReportSectionTable(AOServConnector connector, Class<T> clazz) {
	super(connector, clazz);
    }
}