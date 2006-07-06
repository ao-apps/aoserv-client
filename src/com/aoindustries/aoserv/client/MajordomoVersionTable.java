package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  MajordomoVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoVersionTable extends GlobalTableStringKey<MajordomoVersion> {

    MajordomoVersionTable(AOServConnector connector) {
	super(connector, MajordomoVersion.class);
    }

    public MajordomoVersion get(Object pkey) {
	return getUniqueRow(MajordomoVersion.COLUMN_VERSION, pkey);
    }

    int getTableID() {
	return SchemaTable.MAJORDOMO_VERSIONS;
    }
}