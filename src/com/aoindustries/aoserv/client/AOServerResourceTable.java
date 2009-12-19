package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  AOServerResource
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerResourceTable extends CachedTableIntegerKey<AOServerResource> {

    AOServerResourceTable(AOServConnector connector) {
    	super(connector, AOServerResource.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(AOServerResource.COLUMN_RESOURCE_name+'.'+Resource.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(AOServerResource.COLUMN_RESOURCE_name+'.'+Resource.COLUMN_RESOURCE_TYPE_name, ASCENDING),
        new OrderBy(AOServerResource.COLUMN_RESOURCE_name+'.'+Resource.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public AOServerResource get(int resource) throws IOException, SQLException {
        return getUniqueRow(AOServerResource.COLUMN_RESOURCE, resource);
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.AO_SERVER_RESOURCES;
    }
}
