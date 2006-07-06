package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
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

    public Resource get(Object pkey) {
	return getUniqueRow(Resource.COLUMN_NAME, pkey);
    }

    int getTableID() {
	return SchemaTable.RESOURCES;
    }
}