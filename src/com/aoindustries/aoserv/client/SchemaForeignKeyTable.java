package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  SchemaForeignKey
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SchemaForeignKeyTable extends GlobalTableIntegerKey<SchemaForeignKey> {

    private static final Map<String,List<SchemaForeignKey>> tableKeys=new HashMap<String,List<SchemaForeignKey>>();
    private static final Map<Integer,List<SchemaForeignKey>> referencesHash=new HashMap<Integer,List<SchemaForeignKey>>();
    private static final Map<Integer,List<SchemaForeignKey>> referencedByHash=new HashMap<Integer,List<SchemaForeignKey>>();

    SchemaForeignKeyTable(AOServConnector connector) {
        super(connector, SchemaForeignKey.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(SchemaForeignKey.COLUMN_PKEY_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        synchronized(SchemaForeignKeyTable.class) {
            tableKeys.clear();
            referencesHash.clear();
            referencedByHash.clear();
        }
    }

    public SchemaForeignKey get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public SchemaForeignKey get(int pkey) {
        return getUniqueRow(SchemaForeignKey.COLUMN_PKEY, pkey);
    }

    List<SchemaForeignKey> getSchemaForeignKeys(SchemaTable table) {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaForeignKeyTable.class, "getSchemaForeignKeys(SchemaTable)", null);
        try {
            synchronized(SchemaForeignKeyTable.class) {
                if(tableKeys.isEmpty()) {
                    List<SchemaForeignKey> cached=getRows();
                    int size=cached.size();
                    for(int c=0;c<size;c++) {
                        SchemaForeignKey key=cached.get(c);
                        String tableName=key.getKeyColumn(connector).table_name;
                        List<SchemaForeignKey> keys=tableKeys.get(tableName);
                        if(keys==null) tableKeys.put(tableName, keys=new ArrayList<SchemaForeignKey>());
                        keys.add(key);
                    }
                }
                List<SchemaForeignKey> matches=tableKeys.get(table.getName());
                if(matches!=null) return matches;
                return Collections.emptyList();
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private void rebuildReferenceHashes() {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaForeignKeyTable.class, "rebuildReferenceHashes()", null);
        try {
            if(
                referencedByHash.isEmpty()
                || referencesHash.isEmpty()
            ) {
                // All methods that call this are already synched
                List<SchemaForeignKey> cached=getRows();
                int size=cached.size();
                for(int c=0;c<size;c++) {
                    SchemaForeignKey key=cached.get(c);
                    Integer keyColumnPKey=Integer.valueOf(key.key_column);
                    Integer foreignColumnPKey=Integer.valueOf(key.foreign_column);

                    // Referenced By
                    List<SchemaForeignKey> referencedBy=referencedByHash.get(keyColumnPKey);
                    if(referencedBy==null) referencedByHash.put(keyColumnPKey, referencedBy=new ArrayList<SchemaForeignKey>());
                    referencedBy.add(key);

                    // References
                    List<SchemaForeignKey> references=referencesHash.get(foreignColumnPKey);
                    if(references==null) referencesHash.put(foreignColumnPKey, references=new ArrayList<SchemaForeignKey>());
                    references.add(key);
                }
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    List<SchemaForeignKey> getSchemaForeignKeysReferencedBy(SchemaColumn column) {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaForeignKeyTable.class, "getSchemaForeignKeysReferencedBy(SchemaColumn)", null);
        try {
            synchronized(SchemaForeignKeyTable.class) {
                rebuildReferenceHashes();
                List<SchemaForeignKey> matches=referencedByHash.get(Integer.valueOf(column.getPkey()));
                if(matches!=null) return matches;
                else return Collections.emptyList();
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    List<SchemaForeignKey> getSchemaForeignKeysReferencing(SchemaColumn column) {
        Profiler.startProfile(Profiler.UNKNOWN, SchemaForeignKeyTable.class, "getSchemaForeignKeysReferencing(SchemaColumn)", null);
        try {
            synchronized(SchemaForeignKeyTable.class) {
                rebuildReferenceHashes();
                List<SchemaForeignKey> matches=referencesHash.get(Integer.valueOf(column.getPkey()));
                if(matches!=null) return matches;
                else return Collections.emptyList();
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.SCHEMA_FOREIGN_KEYS;
    }
}