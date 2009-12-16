package com.aoindustries.aoserv.client;

import java.io.IOException;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.sql.SQLException;

/**
 * @see  ResourceType
 *
 * @author  AO Industries, Inc.
 */
final public class ResourceTypeTable extends GlobalTableStringKey<ResourceType> {

    ResourceTypeTable(AOServConnector connector) {
        super(connector, ResourceType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(ResourceType.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public ResourceType get(String name) throws IOException, SQLException {
        return getUniqueRow(ResourceType.COLUMN_NAME, name);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.RESOURCE_TYPES;
    }
}