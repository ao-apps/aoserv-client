package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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