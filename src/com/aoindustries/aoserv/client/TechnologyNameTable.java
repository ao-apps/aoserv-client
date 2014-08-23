package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  TechnologyName
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyNameTable extends GlobalTableStringKey<TechnologyName> {

    TechnologyNameTable(AOServConnector connector) {
	super(connector, TechnologyName.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TechnologyName.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TECHNOLOGY_NAMES;
    }

    public TechnologyName get(String name) throws IOException, SQLException {
        return getUniqueRow(TechnologyName.COLUMN_NAME, name);
    }
}