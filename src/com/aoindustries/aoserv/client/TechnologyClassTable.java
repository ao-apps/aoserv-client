package com.aoindustries.aoserv.client;

import java.io.IOException;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.sql.SQLException;

/**
 * @see  TechnologyClass
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyClassTable extends GlobalTableStringKey<TechnologyClass> {

    TechnologyClassTable(AOServConnector connector) {
	super(connector, TechnologyClass.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(TechnologyClass.COLUMN_NAME_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.TECHNOLOGY_CLASSES;
    }

    public TechnologyClass get(String name) throws IOException, SQLException {
        return getUniqueRow(TechnologyClass.COLUMN_NAME, name);
    }
}