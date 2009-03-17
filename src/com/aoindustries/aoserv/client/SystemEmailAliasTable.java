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
 * @see  SystemEmailAlias
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SystemEmailAliasTable extends CachedTableIntegerKey<SystemEmailAlias> {

    SystemEmailAliasTable(AOServConnector connector) {
	super(connector, SystemEmailAlias.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(SystemEmailAlias.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(SystemEmailAlias.COLUMN_ADDRESS_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    List<SystemEmailAlias> getSystemEmailAliases(AOServer ao) throws IOException, SQLException {
        return getIndexedRows(SystemEmailAlias.COLUMN_AO_SERVER, ao.pkey);
    }

    public SystemEmailAlias get(Object pkey) {
        try {
            return getUniqueRow(SystemEmailAlias.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SystemEmailAlias get(int pkey) throws IOException, SQLException {
	return getUniqueRow(SystemEmailAlias.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SYSTEM_EMAIL_ALIASES;
    }
}