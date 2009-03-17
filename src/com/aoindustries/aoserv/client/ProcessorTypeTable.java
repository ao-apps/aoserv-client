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
 * The table containing all of the possible processor types.
 *
 * @author  AO Industries, Inc.
 */
final public class ProcessorTypeTable extends GlobalTableStringKey<ProcessorType> {

    ProcessorTypeTable(AOServConnector connector) {
	super(connector, ProcessorType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(ProcessorType.COLUMN_SORT_ORDER_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public ProcessorType get(Object type) {
        try {
            return getUniqueRow(ProcessorType.COLUMN_TYPE, type);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.PROCESSOR_TYPES;
    }
}