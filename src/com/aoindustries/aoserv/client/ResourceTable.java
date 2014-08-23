package com.aoindustries.aoserv.client;

import java.io.IOException;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.sql.SQLException;

/**
 * @see  Resource
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

    public Resource get(String name) throws IOException, SQLException {
        return getUniqueRow(Resource.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.RESOURCES;
    }
}