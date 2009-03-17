package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
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
        try {
            return getUniqueRow(NoticeType.COLUMN_TYPE, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NOTICE_TYPES;
    }
}