package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see Action
 * @see Ticket
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ActionTable extends AOServTable<Integer,Action> {

    ActionTable(AOServConnector connector) {
	super(connector, Action.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(Action.COLUMN_TICKET_ID_name, ASCENDING),
        new OrderBy(Action.COLUMN_TIME_name, ASCENDING),
        new OrderBy(Action.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public Action get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public Action get(int pkey) {
        return getObject(AOServProtocol.CommandID.GET_OBJECT, SchemaTable.TableID.ACTIONS, pkey);
    }

    List<Action> getActions(Ticket ticket) {
        Profiler.startProfile(Profiler.IO, ActionTable.class, "getActions(Ticket)", null);
        try {
            return getObjects(AOServProtocol.CommandID.GET_ACTIONS_TICKET, ticket.pkey);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    List<Action> getActions(BusinessAdministrator ba) {
        Profiler.startProfile(Profiler.IO, ActionTable.class, "getActions(BusinessAdministrator)", null);
        try {
            return getObjects(AOServProtocol.CommandID.GET_ACTIONS_BUSINESS_ADMINISTRATOR, ba.pkey);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int getCachedRowCount() {
        return connector.requestIntQuery(AOServProtocol.CommandID.GET_CACHED_ROW_COUNT, SchemaTable.TableID.ACTIONS);
    }

    public int size() {
        return connector.requestIntQuery(AOServProtocol.CommandID.GET_ROW_COUNT, SchemaTable.TableID.ACTIONS);
    }

    public List<Action> getRows() {
        List<Action> list=new ArrayList<Action>();
        getObjects(list, AOServProtocol.CommandID.GET_TABLE, SchemaTable.TableID.ACTIONS);
        return list;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.ACTIONS;
    }

    protected Action getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
