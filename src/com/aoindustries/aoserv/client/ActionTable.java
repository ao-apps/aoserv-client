package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.WrappedException;
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

    public Action get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public Action get(int pkey) {
        return getObject(AOServProtocol.GET_OBJECT, SchemaTable.ACTIONS, pkey);
    }

    List<Action> getActions(Ticket ticket) {
        Profiler.startProfile(Profiler.IO, ActionTable.class, "getActions(Ticket)", null);
        try {
            return getObjects(AOServProtocol.GET_ACTIONS_TICKET, ticket.pkey);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    List<Action> getActions(BusinessAdministrator ba) {
        Profiler.startProfile(Profiler.IO, ActionTable.class, "getActions(BusinessAdministrator)", null);
        try {
            return getObjects(AOServProtocol.GET_ACTIONS_BUSINESS_ADMINISTRATOR, ba.pkey);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int getCachedRowCount() {
        return connector.requestIntQuery(AOServProtocol.GET_CACHED_ROW_COUNT, SchemaTable.ACTIONS);
    }

    public int size() {
        return connector.requestIntQuery(AOServProtocol.GET_ROW_COUNT, SchemaTable.ACTIONS);
    }

    public List<Action> getRows() {
        List<Action> list=new ArrayList<Action>();
        getObjects(list, AOServProtocol.GET_TABLE, SchemaTable.ACTIONS);
        return list;
    }

    int getTableID() {
        return SchemaTable.ACTIONS;
    }

    protected Action getUniqueRowImpl(int col, Object value) {
        if(col!=0) throw new IllegalArgumentException("Not a unique column: "+col);
        return get(value);
    }
}
