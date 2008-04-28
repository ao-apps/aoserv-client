package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Resource
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ResourceTable extends GlobalTableStringKey<Resource> {

    ResourceTable(AOServConnector connector) {
	super(connector, Resource.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Resource.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Resource get(Object pkey) {
	return getUniqueRow(Resource.COLUMN_NAME, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.RESOURCES;
    }
}