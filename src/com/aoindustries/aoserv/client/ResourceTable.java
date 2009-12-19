package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  Resource
 *
 * @author  AO Industries, Inc.
 */
final public class ResourceTable extends CachedTableIntegerKey<Resource> {

    ResourceTable(AOServConnector connector) {
    	super(connector, Resource.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Resource.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(Resource.COLUMN_RESOURCE_TYPE_name, ASCENDING),
        new OrderBy(Resource.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Resource get(int pkey) throws IOException, SQLException {
        return getUniqueRow(Resource.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.RESOURCES;
    }
}
