package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  AOServProtocol
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class AOServProtocolTable extends GlobalTableStringKey<AOServProtocol> {

    AOServProtocolTable(AOServConnector connector) {
	super(connector, AOServProtocol.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(AOServProtocol.COLUMN_CREATED_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public AOServProtocol get(Object version) {
        try {
            return getUniqueRow(AOServProtocol.COLUMN_VERSION, version);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.AOSERV_PROTOCOLS;
    }
}