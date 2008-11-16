package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int tableID=table.pkey;
        synchronized(nameToColumns) {
            Map<String,SchemaColumn> map=nameToColumns.get(tableID);
            if(map==null || map.isEmpty()) {
                List<SchemaColumn> cols=getSchemaColumns(table);
                int len=cols.size();
                if(map==null) nameToColumns.set(tableID, map=new HashMap<String,SchemaColumn>(len*4/3+1));
                for(int c=0;c<len;c++) {
                    SchemaColumn col=cols.get(c);
                    map.put(col.column_name, col);
                }
            }
            return map.get(columnName);
        }
    }

    SchemaColumn getSchemaColumn(SchemaTable table, int columnIndex) {
        return getSchemaColumns(table).get(columnIndex);
    }

    List<SchemaColumn> getSchemaColumns(SchemaTable table) {
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
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_COLUMNS;
    }

    @Override
    public void clearCache() {
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
    }
}