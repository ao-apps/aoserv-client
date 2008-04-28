package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * The table containing all of the possible types of actions that may
 * be performed on a ticket.
 *
 * @see Action
 * @see Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ActionTypeTable extends GlobalTableStringKey<ActionType> {

    ActionTypeTable(AOServConnector connector) {
	super(connector, ActionType.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(ActionType.COLUMN_DESCRIPTION_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public ActionType get(Object type) {
        return getUniqueRow(ActionType.COLUMN_TYPE, type);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.ACTION_TYPES;
    }
}