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
 * @see  SchemaColumn
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaColumnTable extends GlobalTableIntegerKey<SchemaColumn> {

    /** Avoid repeated copies. */
    private static final int numTables = SchemaTable.TableID.values().length;
    /**
     * The columns for tables are cached for faster lookups.
     */
    private static final List<List<SchemaColumn>> tableColumns=new ArrayList<List<SchemaColumn>>(numTables);
    static {
        for(int c=0;c<numTables;c++) tableColumns.add(null);
    }

    /**
     * The nameToColumns are cached for faster lookups.
     */
    private static final List<Map<String,SchemaColumn>> nameToColumns=new ArrayList<Map<String,SchemaColumn>>(numTables);
    static {
        for(int c=0;c<numTables;c++) nameToColumns.add(null);
    }

    SchemaColumnTable(AOServConnector connector) {
        super(connector, SchemaColumn.class);
    }

    @Override
    OrderBy[] getDefaultOrderBy() {
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

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_COLUMNS;
    }

    public void clearCache() {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaColumnTable.class, "clearCache()", null);
        try {
            super.clearCache();
            synchronized(this) {
                for(int c=0;c<numTables;c++) {
                    synchronized(tableColumns) {
                        tableColumns.set(c, null);
                    }
                    synchronized(nameToColumns) {
                        Map<String,SchemaColumn> map=nameToColumns.get(c);
                        if(map!=null) map.clear();
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}