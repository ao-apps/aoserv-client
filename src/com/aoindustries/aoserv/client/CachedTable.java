package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>CachedTable</code> stores all of the
 * available <code>CachedObject</code>s and performs
 * all subsequent data access locally.  The server
 * notifies the client when a table is updated, and
 * the caches are then invalidated.  Once invalidated,
 * the data is reloaded upon next use.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
abstract public class CachedTable<K,V extends CachedObject<K,V>> extends AOServTable<K,V> {

    /**
     * The last time that the data was loaded, or
     * <code>-1</code> if not yet loaded.
     */
    private long lastLoaded=-1;

    /**
     * The internal objects are stored in <code>HashMaps</code>
     * based on unique columns.
     */
    private List<Map<Object,V>> columnHashes;
    private BitSet columnsHashed;

    /**
     * The internal objects are stored in <code>HashMaps</code> of <code>CachedObject[]</code>
     * based on indexes columns.  Each of the contained List<T> are unmodifiable.
     */
    private List<Map<Object,List<V>>> indexHashes;
    private BitSet indexesHashed;

    /**
     * The internal objects are stored in an unmodifiable list
     * for access to the entire table.
     */
    private List<V> tableData;

    protected CachedTable(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "<init>(AOServConnector,Clazz)", null);
        Profiler.endProfile(Profiler.FAST);
    }

    public List<V> getIndexedRows(int col, Object value) {
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "getIndexedRows(int,Object)", null);
        try {
            synchronized(this) {
                validateCache();
                int minLength=col+1;
                if(indexHashes==null) {
                    indexHashes=new ArrayList<Map<Object,List<V>>>(minLength);
                    indexesHashed=new BitSet(minLength);
                }
                while(indexHashes.size()<minLength) indexHashes.add(null);
                Map<Object,List<V>> map=indexHashes.get(col);
                if(map==null) indexHashes.set(col, map=new HashMap<Object,List<V>>());
                if(!indexesHashed.get(col)) {
                    // Build the modifiable lists in a temporary Map
                    Map<Object,List<V>> modifiableIndexes=new HashMap<Object,List<V>>();
                    for(V obj : tableData) {
                        Object cvalue=obj.getColumn(col);
                        List<V> list=modifiableIndexes.get(cvalue);
                        if(list==null) modifiableIndexes.put(cvalue, list=new ArrayList<V>());
                        list.add(obj);
                    }
                    // Wrap each of the newly-created indexes to be unmodifiable
                    map.clear();
                    Iterator<Object> keys=modifiableIndexes.keySet().iterator();
                    while(keys.hasNext()) {
                        Object key=keys.next();
                        List<V> list=modifiableIndexes.get(key);
                        map.put(key, Collections.unmodifiableList(list));
                    }
                    indexesHashed.set(col);
                }
                // Conversion to array is delayed so that indexed but unused parts save the step.
                List<V> list = map.get(value);
                if(list==null) return Collections.emptyList();
                return list;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final protected V getUniqueRowImpl(int col, Object value) {
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "getUniqueRowImpl(int,Object)", null);
        try {
            synchronized(this) {
                validateCache();
                int minLength=col+1;
                if(columnHashes==null) {
                    columnHashes=new ArrayList<Map<Object,V>>(minLength);
                    columnsHashed=new BitSet(minLength);
                }
                while(columnHashes.size()<minLength) columnHashes.add(null);
                Map<Object,V> map=columnHashes.get(col);
                if(!columnsHashed.get(col)) {
                    List<V> table=tableData;
                    int size=table.size();
                    // HashMap default load factor is .75, so we go slightly more than 1/.75, or slightly more than 4/3.  13/9 is a little more than 4/3.
                    // This ensures the HashMap will not be restructured while loading the data.
                    if(map==null) columnHashes.set(col, map=new HashMap<Object,V>(size*13/9));
                    else map.clear();
                    for(int c=0;c<size;c++) {
                        V O=table.get(c);
                        Object cvalue=O.getColumn(col);
                        if(cvalue!=null) {
                            Object old=map.put(cvalue, O);
                            if(old!=null) throw new WrappedException(new SQLException("Duplicate unique entry for table #"+getTableID()+" ("+getTableName()+"), column "+col+": "+cvalue));
                        }
                    }
                    columnsHashed.set(col);
                }
                return map.get(value);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the complete list of objects in the table.
     */
    public List<V> getRows() {
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "getRows()", null);
        try {
            synchronized(this) {
                validateCache();
                return tableData;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if the contents are currently hashed in a hashmap.
     */
    boolean isHashed(int uniqueColumn) {
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "isHashed(int)", null);
        try {
            return
                columnsHashed!=null
                && columnsHashed.get(uniqueColumn)
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if the contents of this column are indexed.
     */
    boolean isIndexed(int uniqueColumn) {
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "isIndexed(int)", null);
        try {
            return
                indexesHashed!=null
                && indexesHashed.get(uniqueColumn)
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public boolean isLoaded() {
        return lastLoaded!=-1;
    }

    /**
     * Clears the cache, freeing up memory.  The data will be reloaded upon
     * next use.
     */
    public void clearCache() {
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "clearCache()", null);
        try {
            synchronized(this) {
                lastLoaded=-1;
                if(columnHashes!=null) {
                    int len=columnHashes.size();
                    for(int c=0;c<len;c++) {
                        Map map=columnHashes.get(c);
                        if(map!=null) map.clear();
                    }
                }
                if(columnsHashed!=null) columnsHashed.clear();
                if(indexHashes!=null) {
                    int len=indexHashes.size();
                    for(int c=0;c<len;c++) {
                        Map<Object,List<V>> map=indexHashes.get(c);
                        if(map!=null) map.clear();
                    }
                }
                if(indexesHashed!=null) indexesHashed.clear();
            }
            super.clearCache();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Reloads the cache if the cache time has expired.  All accesses are already synchronized.
     */
    private void validateCache() {
        Profiler.startProfile(Profiler.FAST, CachedTable.class, "validateCache()", null);
        try {
            long currentTime=System.currentTimeMillis();
            if(
               // If cache never loaded
               lastLoaded==-1
               // If the system time was reset to previous time
               || currentTime<lastLoaded
            ) {
                tableData=Collections.unmodifiableList(getObjects(AOServProtocol.GET_TABLE, getTableID()));
                lastLoaded=currentTime;
                if(columnHashes!=null) {
                    int len=columnHashes.size();
                    for(int c=0;c<len;c++) {
                        Map map=columnHashes.get(c);
                        if(map!=null) map.clear();
                    }
                }
                if(columnsHashed!=null) columnsHashed.clear();
                if(indexHashes!=null) {
                    int len=indexHashes.size();
                    for(int c=0;c<len;c++) {
                        Map<Object,List<V>> map=indexHashes.get(c);
                        if(map!=null) map.clear();
                    }
                }
                if(indexesHashed!=null) indexesHashed.clear();
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}