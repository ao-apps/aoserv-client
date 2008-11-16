package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import com.aoindustries.util.sort.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>FilesystemCachedTable</code> stores all of the
 * available <code>FilesystemCachedObject</code>s in a
 * temporary file and performs all subsequent data access
 * locally.  The server notifies the client when a table
 * is updated, and the caches are then invalidated.  Once
 * invalidated, the data is reloaded upon next use.
 * <p>
 * The file format is a simple fixed record length format.
 *
 * TODO: It is possible to use the same column sorting technique
 * to implement the getIndexedRows method from AOServTable.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public abstract class FilesystemCachedTable<K,V extends FilesystemCachedObject<K,V>> extends AOServTable<K,V> implements FileListObjectFactory<V> {

    /**
     * The last time that the data was loaded, or
     * <code>-1</code> if not yet loaded.
     */
    private long lastLoaded=-1;

    /**
     * One file list may exist per column.  Only the unique columns will have a non-null value.
     * Once a file is sorted on the specific column, its FileList is wrapped in an
     * unmodifiable list.  This allows the list to be returned to any number of callers without
     * any additional copying.  If the data is reloaded, a new FileList will be created, leaving
     * the old copy intact for those still using the previous copy.
     */
    private List<List<V>> columnLists;

    /**
     * The raw list of objects as downloaded from the master.
     */
    private FileList<V> tableList;

    /**
     * This is an unmodifiable list and may be returned to any number of callers without copying.
     */
    private List<V> unmodifiableTableList;

    protected FilesystemCachedTable(AOServConnector connector, Class<V> clazz) {
        super(connector, clazz);
    }

    abstract int getRecordLength();

    /**
     * Clears the cache, freeing up memory.  The data will be reloaded upon
     * next use.
     */
    public void clearCache() {
        super.clearCache();
        synchronized(this) {
            lastLoaded=-1;
            tableList=null;
            unmodifiableTableList=null;
            if(columnLists!=null) columnLists.clear();
        }
    }

    /**
     * Reloads the cache if the cache has expired.  All accesses are already synchronized.
     */
    private void validateCache() {
        try {
            long currentTime=System.currentTimeMillis();
            if(
               // If cache never loaded
               lastLoaded==-1
               // If the system time was reset to previous time
               || currentTime<lastLoaded
            ) {
                SchemaTable schemaTable=getTableSchema();
                FileList<V> newTableList=new FileList<V>(
                    schemaTable.getName(),
                    "table",
                    getRecordLength(),
                    this
                );
                getObjects(newTableList, AOServProtocol.CommandID.GET_TABLE, getTableID());
                tableList=newTableList;
                unmodifiableTableList=Collections.unmodifiableList(tableList);
                lastLoaded=currentTime;

                if(columnLists!=null) columnLists.clear();
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets the complete list of objects in the table.  This list is unmodifiable and will not ever be changed.
     * Newer data will be contained in new lists so that any calling code sees a snapshot of the code and may
     * safely assume the data is constant as long as the code uses the same reference to List returned
     * here.
     */
    public final List<V> getRows() {
        synchronized(this) {
            validateCache();
            return unmodifiableTableList;
        }
    }

    final protected V getUniqueRowImpl(int col, Object value) {
        try {
            SchemaTable schemaTable=getTableSchema();
            SchemaColumn schemaColumn=schemaTable.getSchemaColumn(connector, col);
            SQLComparator<V> Vcomparator=new SQLComparator<V>(
                connector,
                new SQLExpression[] {
                    new SQLColumnValue(connector, schemaColumn)
                },
                new boolean[] {ASCENDING}
            );

            SQLComparator<Object> Ocomparator=new SQLComparator<Object>(
                connector,
                new SQLExpression[] {
                    new SQLColumnValue(connector, schemaColumn)
                },
                new boolean[] {ASCENDING}
            );

            synchronized(this) {
                validateCache();
                // Create any needed objects
                int minLength=col+1;
                if(columnLists==null) columnLists=new ArrayList<List<V>>(minLength);
                else while(columnLists.size()<minLength) columnLists.add(null);
                List<V> unmodifiableSortedList = columnLists.get(col);
                if(unmodifiableSortedList==null) {
                    FileList<V> sortedFileList=new FileList<V>(
                        schemaTable.getName()+'.'+schemaColumn.getColumnName(),
                        "unique",
                        getRecordLength(),
                        tableList.getObjectFactory()
                    );
                    sortedFileList.addAll(tableList);
                    AutoSort.sortStatic(sortedFileList, Vcomparator);
                    unmodifiableSortedList=Collections.unmodifiableList(sortedFileList);
                    columnLists.set(col, unmodifiableSortedList);
                }
                int index=Collections.binarySearch(unmodifiableSortedList, value, Ocomparator);
                return index<0?null:unmodifiableSortedList.get(index);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Determines if the contents are currently sorted for quick unique lookups.
     */
    boolean isSorted(int uniqueColumn) {
        return
            columnLists!=null
            && columnLists.size()>uniqueColumn
            && columnLists.get(uniqueColumn)!=null
        ;
    }

    final public boolean isLoaded() {
        return lastLoaded!=-1;
    }

    public V createInstance() throws IOException {
        V obj = (V)getNewObject();
        if(obj instanceof SingleTableObject) ((SingleTableObject<K,V>)obj).setTable(this);
        return obj;
    }
}