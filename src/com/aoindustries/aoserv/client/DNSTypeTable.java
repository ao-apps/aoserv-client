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
 * @see  DNSType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DNSTypeTable extends GlobalTableStringKey<DNSType> {

    DNSTypeTable(AOServConnector connector) {
	super(connector, DNSType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(DNSType.COLUMN_DESCRIPTION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public DNSType get(Object pkey) {
        try {
            return getUniqueRow(DNSType.COLUMN_TYPE, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DNS_TYPES;
    }
}