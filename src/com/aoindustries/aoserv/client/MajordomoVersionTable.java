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

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MajordomoVersion.COLUMN_VERSION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    @Override
    public MajordomoVersion get(Object pkey) {
	return getUniqueRow(MajordomoVersion.COLUMN_VERSION, pkey);
    }

    @Override
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MAJORDOMO_VERSIONS;
    }
}