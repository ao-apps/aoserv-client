package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.sort.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  SchemaType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaTypeTable extends GlobalTableIntegerKey<SchemaType> {

    SchemaTypeTable(AOServConnector connector) {
	super(connector, SchemaType.class);
    }

    protected String[] getDefaultSortColumnsImpl() {
        return null;
    }

    protected boolean[] getDefaultSortOrdersImpl() {
        return null;
    }

    protected int getMaxConnectionsPerThread() {
        return 2;
    }

    public SchemaType get(Object pkey) {
        if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
        else if(pkey instanceof String) return get((String)pkey);
        else throw new IllegalArgumentException("Must be an Integer or a String");
    }

    public SchemaType get(int num) {
        return getRows().get(num);
    }

    public SchemaType get(String type) {
        return getUniqueRow(SchemaType.COLUMN_TYPE, type);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_TYPES;
    }

    public <T extends AOServObject> void sort(T[] list, SQLExpression[] sortExpressions, boolean[] sortOrders) {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaTypeTable.class, "sort(<T extends AOServObject>[],SQLExpression[],boolean[])", null);
        try {
            AutoSort.sortStatic(
                list,
                new SQLComparator<T>(
                    connector,
                    sortExpressions,
                    sortOrders
                )
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public <T extends AOServObject> void sort(List<T> list, SQLExpression[] sortExpressions, boolean[] sortOrders) {
        Profiler.startProfile(Profiler.FAST, SchemaTypeTable.class, "sort(List<T extends AOServObject>,SQLExpression[],boolean[])", null);
        try {
            AutoSort.sortStatic(
                list,
                new SQLComparator<T>(
                    connector,
                    sortExpressions,
                    sortOrders
                )
            );
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}