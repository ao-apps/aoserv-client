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
 * @see  MasterHost
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHostTable extends CachedTableIntegerKey<MasterHost> {

    MasterHostTable(AOServConnector connector) {
	super(connector, MasterHost.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(MasterHost.COLUMN_USERNAME_name, ASCENDING),
        new OrderBy(MasterHost.COLUMN_HOST_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public MasterHost get(Object pkey) {
        try {
            return getUniqueRow(MasterHost.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public MasterHost get(int pkey) throws IOException, SQLException {
	return getUniqueRow(MasterHost.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_HOSTS;
    }
}