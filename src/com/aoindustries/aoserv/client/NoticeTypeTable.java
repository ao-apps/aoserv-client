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
 * @see  NoticeType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeTypeTable extends GlobalTableStringKey<NoticeType> {

    NoticeTypeTable(AOServConnector connector) {
	super(connector, NoticeType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(NoticeType.COLUMN_TYPE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public NoticeType get(Object pkey) {
	return getUniqueRow(NoticeType.COLUMN_TYPE, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NOTICE_TYPES;
    }
}