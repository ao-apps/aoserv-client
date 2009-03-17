package com.aoindustries.aoserv.client;

import com.aoindustries.util.WrappedException;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Rack
 *
 * @author  AO Industries, Inc.
 */
final public class RackTable extends CachedTableIntegerKey<Rack> {

    RackTable(AOServConnector connector) {
	super(connector, Rack.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Rack.COLUMN_FARM_name, ASCENDING),
        new OrderBy(Rack.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Rack get(Object pkey) {
        try {
            return getUniqueRow(Rack.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.RACKS;
    }
}
