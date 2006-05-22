package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  SchemaColumn
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaColumnTable extends GlobalTableIntegerKey<SchemaColumn> {

    /**
     * The columns for tables are cached for faster lookups.
     */
    private static final List<List<SchemaColumn>> tableColumns=new ArrayList<List<SchemaColumn>>(SchemaTable.NUM_TABLES);
    static {
        for(int c=0;c<SchemaTable.NUM_TABLES;c++) tableColumns.add(null);
    }

    /**
     * The nameToColumns are cached for faster lookups.
     */
    private static final List<Map<String,SchemaColumn>> nameToColumns=new ArrayList<Map<String,SchemaColumn>>(SchemaTable.NUM_TABLES);
    static {
        for(int c=0;c<SchemaTable.NUM_TABLES;c++) nameToColumns.add(null);
    }

    SchemaColumnTable(AOServConnector connector) {
        super(connector, SchemaColumn.class);
    }

    protected String[] getDefaultSortColumnsImpl() {
        return null;
    }

    protected boolean[] getDefaultSortOrdersImpl() {
        return null;
    }

    public SchemaColumn get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SchemaColumn get(int pkey) {
        return getUniqueRow(SchemaColumn.COLUMN_PKEY, pkey);
    }

    SchemaColumn getSchemaColumn(SchemaTable table, String columnName) {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaColumnTable.class, "getSchemaColumn(SchemaTable,String)", null);
        try {
            int tableID=table.pkey;
            synchronized(nameToColumns) {
                Map<String,SchemaColumn> map=nameToColumns.get(tableID);
                if(map==null || map.isEmpty()) {
                    List<SchemaColumn> cols=getSchemaColumns(table);
                    int len=cols.size();
                    if(map==null) nameToColumns.set(tableID, map=new HashMap<String,SchemaColumn>(len*13/9));
                    for(int c=0;c<len;c++) {
                        SchemaColumn col=cols.get(c);
                        map.put(col.column_name, col);
                    }
                }
                return map.get(columnName);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    SchemaColumn getSchemaColumn(SchemaTable table, int columnIndex) {
        Profiler.startProfile(Profiler.FAST, SchemaColumnTable.class, "getSchemaColumn(SchemaTable,int)", null);
        try {
            return getSchemaColumns(table).get(columnIndex);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    List<SchemaColumn> getSchemaColumns(SchemaTable table) {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaColumnTable.class, "getSchemaColumns(SchemaTable)", null);
        try {
            int tableID=table.pkey;
            synchronized(tableColumns) {
                List<SchemaColumn> cols=tableColumns.get(tableID);
                if(cols!=null) return cols;

                String name=table.name;
                List<SchemaColumn> cached=getRows();
                List<SchemaColumn> matches=new ArrayList<SchemaColumn>();
                int size=cached.size();
                for(int c=0;c<size;c++) {
                    SchemaColumn col=cached.get(c);
                    if(col.table_name.equals(name)) matches.add(col);
                }
                matches=Collections.unmodifiableList(matches);
                tableColumns.set(tableID, matches);
                return matches;
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    int getTableID() {
        return SchemaTable.SCHEMA_COLUMNS;
    }

    public void clearCache() {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaColumnTable.class, "clearCache()", null);
        try {
            synchronized(this) {
                for(int c=0;c<SchemaTable.NUM_TABLES;c++) {
                    synchronized(tableColumns) {
                        tableColumns.set(c, null);
                    }
                    synchronized(nameToColumns) {
                        Map<String,SchemaColumn> map=nameToColumns.get(c);
                        if(map!=null) map.clear();
                    }
                }
            }
            super.clearCache();
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}