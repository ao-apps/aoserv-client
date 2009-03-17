package com.aoindustries.aoserv.client;

import com.aoindustries.util.WrappedException;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Shell
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ShellTable extends GlobalTableStringKey<Shell> {

    ShellTable(AOServConnector connector) {
	super(connector, Shell.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Shell.COLUMN_PATH_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SHELLS;
    }

    public Shell get(Object pkey) {
        try {
            return getUniqueRow(Shell.COLUMN_PATH, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }
}