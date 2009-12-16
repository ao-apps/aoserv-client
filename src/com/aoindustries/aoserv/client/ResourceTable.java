package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
        new OrderBy(Resource.COLUMN_OWNER_name, ASCENDING),
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

    List<Resource> getResources(Business owner) throws IOException, SQLException {
        return getIndexedRows(Resource.COLUMN_OWNER, owner.pkey);
    }

    List<Resource> getResources(ResourceType resourceType) throws IOException, SQLException {
        return getIndexedRows(Resource.COLUMN_RESOURCE_TYPE, resourceType.pkey);
    }

    List<Resource> getResources(BusinessAdministrator createdBy) throws IOException, SQLException {
        return getIndexedRows(Resource.COLUMN_CREATED_BY, createdBy.pkey);
    }

    List<Resource> getResources(DisableLog dl) throws IOException, SQLException {
        return getIndexedRows(Resource.COLUMN_DISABLE_LOG, dl.pkey);
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.RESOURCES;
    }
}
