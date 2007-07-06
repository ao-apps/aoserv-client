package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.table.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An <code>AOServTable</code> provides access to one
 * set of <code>AOServObject</code>s.  The subclasses often provide additional
 * methods for manipulating the data outside the scope
 * of a single <code>AOServObject</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServObject
 */
abstract public class AOServTable<K,V extends AOServObject<K,V>> implements Map<K,V>, Iterable<V>, Table<V> {

    /**
     * Indicates ascending sort.
     */
    public static final boolean ASCENDING=true;
    
    /**
     * Indicates descending sort.
     */
    public static final boolean DESCENDING=false;

    final AOServConnector connector;
    //final SimpleAOClient client;
    final Class<V> clazz;

    /**
     * The list of <code>TableListener</code>s.
     */
    List<TableListenerEntry> tableListeners;

    /**
     * The lock used for cache event handling.
     */
    final Object eventLock=new Object();

    /**
     * The thread that is performing the batched updates.
     */
    TableEventThread thread;

    /**
     * The list of <code>ProgressListener</code>s.
     */
    List<ProgressListener> progressListeners=new ArrayList<ProgressListener>();

    /**
     * All of the table load listeners.
     */
    List<TableLoadListenerEntry> loadListeners=new ArrayList<TableLoadListenerEntry>();

    /**
     * The number of items to give room for beyond the size of the
     * previously loaded table.
     */
    private static final int TABLE_SIZE_PADDING=10;

    protected AOServTable(AOServConnector connector, Class<V> clazz) {
        this.connector=connector;
        //this.client=new SimpleAOClient(connector);
        this.clazz=clazz;
    }

    final public void addProgressListener(ProgressListener listener) {
        synchronized(progressListeners) {
            progressListeners.add(listener);
        }
    }

    /**
     * Registers a <code>TableListener</code> to be notified when
     * the cached data for this table expires.  The default of
     * 1000ms of batching is used.
     *
     * @see  #addTableListener(TableListener,long)
     */
    final public void addTableListener(TableListener listener) {
        addTableListener(listener, 1000);
    }

    /**
     * Registers a <code>TableListener</code> to be notified when
     * the cached data for this table expires.  Repetative incoming
     * requests will be batched into fewer events, in increments
     * provided by batchTime.  If batchTime is 0, the event is immediately
     * and always distributed.  Batched events are performed in
     * concurrent Threads, while immediate events are triggered by the
     * central cache invalidation thread.  In other words, don't use
     * a batchTime of zero unless you absolutely need your code to
     * run immediately, because it causes serial processing of the event
     * and may potentially slow down the responsiveness of the server.
     */
    final public void addTableListener(TableListener listener, long batchTime) {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "addTableListener(TableListener,long)", null);
        try {
            if(batchTime<0) throw new IllegalArgumentException("batchTime<0: "+batchTime);

            synchronized(eventLock) {
		if(tableListeners==null) {
		    tableListeners=new ArrayList<TableListenerEntry>();
                }
                if(batchTime>0 && thread==null) thread=new TableEventThread(this);

                tableListeners.add(new TableListenerEntry(listener, batchTime));

                // Tell the thread to recalc its stuff
                eventLock.notifyAll();
            }

            connector.addingTableListener();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public void addTableLoadListener(TableLoadListener listener, Object param) {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "addTableLoadListener(TableLoadListener,Object)", null);
        try {
            synchronized(loadListeners) {
                loadListeners.add(new TableLoadListenerEntry(listener, param));
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Clears the cache, freeing up memory.  The data will be reloaded upon
     * next use.
     */
    public void clearCache() {
    }

    final public AOServConnector getConnector() {
        return connector;
    }

    final public SchemaColumn[] getDefaultSortSchemaColumns() {
        Profiler.startProfile(Profiler.UNKNOWN, AOServTable.class, "getDefaultSortSchemaColumns()", null);
        try {
            String[] sortColumns=getDefaultSortColumns();
            if(sortColumns==null) return null;
            int len=sortColumns.length;
            SchemaTable schemaTable=connector.schemaTables.get(getTableID());
            SchemaColumn[] schemaColumns=new SchemaColumn[len];
            for(int c=0;c<len;c++) {
                String columnName=sortColumns[c];
                SchemaColumn col=schemaTable.getSchemaColumn(connector, columnName);
                if(col==null) throw new WrappedException(new SQLException("Unable to find SchemaColumn: "+columnName+" on "+schemaTable.getName()));
                schemaColumns[c]=col;
            }
            return schemaColumns;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Gets the default sorting for this table.
     *
     * @return  <code>null</code> if the sorting is performed by the server or the array of column names
     */
    final public String[] getDefaultSortColumns() {
        return getDefaultSortColumnsImpl();
    }
    private String[] defaultSortColumns;
    synchronized protected String[] getDefaultSortColumnsImpl() {
        if(defaultSortColumns==null) {
            String defaultOrderBy=getTableSchema().getDefaultOrderBy();
            if(defaultOrderBy==null) return null;
            String[] columns=StringUtility.splitString(defaultOrderBy, ',');
            String[] sortColumns=new String[columns.length];
            for(int c=0;c<columns.length;c++) {
                String column=columns[c].toLowerCase();
                if(column.endsWith(" desc")) column=column.substring(0, column.length()-5);
                else if(column.endsWith(" asc")) column=column.substring(0, column.length()-4);
                sortColumns[c]=column.trim();
            }
            defaultSortColumns=sortColumns;
        }
        return defaultSortColumns;
    }

    /**
     * Gets the default sort orders for this table.
     *
     * @return  <code>null</code> if the sorting is performed by the server or an array of booleans, true meaning ascending
     *
     * @see  #ASCENDING
     * @see  #DESCENDING
     */
    final public boolean[] getDefaultSortOrders() {
        return getDefaultSortOrdersImpl();
    }

    private boolean[] defaultSortOrders;
    synchronized protected boolean[] getDefaultSortOrdersImpl() {
        if(defaultSortOrders==null) {
            String defaultOrderBy=getTableSchema().getDefaultOrderBy();
            if(defaultOrderBy==null) return null;
            String[] columns=StringUtility.splitString(defaultOrderBy, ',');
            boolean[] sortOrders=new boolean[columns.length];
            for(int c=0;c<columns.length;c++) {
                sortOrders[c]=columns[c].trim().toLowerCase().endsWith(" desc") ? DESCENDING : ASCENDING;
            }
            defaultSortOrders=sortOrders;
        }
        return defaultSortOrders;
    }

    final public SQLExpression[] getDefaultSortSQLExpressions() {
        Profiler.startProfile(Profiler.UNKNOWN, AOServTable.class, "getDefaultSortSQLExpressions()", null);
        try {
            String[] columns=getDefaultSortColumns();
            if(columns==null) return null;
            int len=columns.length;
            SQLExpression[] exprs=new SQLExpression[len];
            for(int c=0;c<len;c++) exprs[c]=getSQLExpression(columns[c]);
            return exprs;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    protected V getNewObject() throws IOException {
        try {
            return clazz.newInstance();
        } catch(InstantiationException err) {
            IOException ioErr=new IOException("Error loading class");
            ioErr.initCause(err);
            throw ioErr;
        } catch(IllegalAccessException err) {
            IOException ioErr=new IOException("Error loading class");
            ioErr.initCause(err);
            throw ioErr;
        }
    }

    protected int getMaxConnectionsPerThread() {
        return 1;
    }

    @SuppressWarnings({"unchecked"})
    protected V getObject(AOServProtocol.CommandID commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServTable.class, "getObject(AOServProtocol.CommandID,...)", null);
        try {
            try {
                AOServConnection connection=connector.getConnection(getMaxConnectionsPerThread());
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID.ordinal());
                    AOServConnector.writeParams(params, out);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.NEXT) {
                        V obj=getNewObject();
                        obj.read(in);
                        if(obj instanceof SingleTableObject) ((SingleTableObject<K,V>)obj).setTable(this);
                        return obj;
                    }
                    AOServProtocol.checkResult(code, in);
                    return null;
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    connector.releaseConnection(connection);
                }
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected List<V> getObjects(AOServProtocol.CommandID commID, Object ... params) {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "getObjects(AOServProtocol.CommandID,...)", null);
        try {
            List<V> list=new ArrayList<V>();
            getObjects(list, commID, params);
            return list;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected void getObjects(List<V> list, AOServProtocol.CommandID commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServTable.class, "getObjects(List<V>,AOServProtocol.CommandID,...)", null);
        try {
            try {
                // Get a snapshot of all listeners
                ProgressListener[] listeners=getProgressListeners();
                int listenerCount=listeners==null?0:listeners.length;
                int[] progressScales=null;
                int[] lastProgresses=null;
                if(listeners!=null) {
                    progressScales=new int[listenerCount];
                    for(int c=0;c<listenerCount;c++) progressScales[c]=listeners[c].getScale();
                    lastProgresses=new int[listenerCount];
                }
                // Get a snapshot of all load listeners
                TableLoadListenerEntry[] loadListeners=getTableLoadListeners();
                int loadCount=loadListeners==null?0:loadListeners.length;
                // Start the progresses at zero
                for(int c=0;c<listenerCount;c++) listeners[c].progressChanged(0, progressScales[c], this);
                // Tell each load listener that we are starting
                for(int c=0;c<loadCount;c++) {
                    TableLoadListenerEntry entry=loadListeners[c];
                    entry.param=entry.listener.tableLoadStarted(this, entry.param);
                }

                AOServConnection connection=connector.getConnection(getMaxConnectionsPerThread());
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID.ordinal());
                    out.writeBoolean(listeners!=null);
                    AOServConnector.writeParams(params, out);
                    out.flush();

                    getObjects0(
                        list,
                        connection,
                        listeners,
                        progressScales,
                        lastProgresses,
                        loadListeners
                    );
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    connector.releaseConnection(connection);
                }

                sortIfNeeded(list);
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected List<V> getObjects(AOServProtocol.CommandID commID, Streamable param1) {
        List<V> list=new ArrayList<V>();
        getObjects(list, commID, param1);
        return list;
    }

    @SuppressWarnings({"unchecked"})
    private void getObjects0(
        List<V> list,
        AOServConnection connection,
        ProgressListener[] listeners,
        int[] progressScales,
        int[] lastProgresses,
        TableLoadListenerEntry[] loadListeners
    ) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, AOServTable.class, "getObjects0(List<V>,AOServConnection,ProgressListener[],int[],int[],TableLoadListenerEntry[])", null);
        try {
            int listenerCount=listeners==null?0:listeners.length;
            int loadCount=loadListeners==null?0:loadListeners.length;

            CompressedDataInputStream in=connection.getInputStream();
            int code=listeners==null?AOServProtocol.NEXT:in.readByte();
            if(code==AOServProtocol.NEXT) {
                int size;
                if(listeners==null) {
                    size=0;
                } else {
                    size=in.readCompressedInt();
                }
                int objCount=0;
                while((code=in.readByte())==AOServProtocol.NEXT) {
                    V obj=getNewObject();
                    obj.read(in);
                    if(obj instanceof SingleTableObject) ((SingleTableObject<K,V>)obj).setTable(this);

                    // Sort and add
                    list.add(obj);

                    // Notify of progress changes
                    objCount++;
                    if(listeners!=null) {
                        for(int c=0;c<listenerCount;c++) {
                            int currentProgress=(int)(((long)objCount)*progressScales[c]/size);
                            if(currentProgress!=lastProgresses[c]) listeners[c].progressChanged(lastProgresses[c]=currentProgress, progressScales[c], this);
                        }
                    }

                    // Tell each load listener of the new object
                    for(int c=0;c<loadCount;c++) {
                        TableLoadListenerEntry entry=loadListeners[c];
                        entry.param=entry.listener.tableRowLoaded(this, obj, objCount-1, entry.param);
                    }
                }
                AOServProtocol.checkResult(code, in);
                if(listenerCount>0 && size!=objCount) throw new IOException("Unexpected number of objects returned: expected="+size+", returned="+objCount);
                // Show at final progress scale, just in case previous algorithm did not get the scale there.
                for(int c=0;c<listenerCount;c++) if(lastProgresses[c]!=progressScales[c]) listeners[c].progressChanged(lastProgresses[c]=progressScales[c], progressScales[c], this);

                // Tell each load listener that we are done
                for(int c=0;c<loadCount;c++) {
                    TableLoadListenerEntry entry=loadListeners[c];
                    entry.param=entry.listener.tableLoadCompleted(this, entry.param);
                }
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected List<V> getObjectsNoProgress(AOServProtocol.CommandID commID, Object ... params) {
        List<V> list=new ArrayList<V>();
        getObjectsNoProgress(list, commID, params);
        return list;
    }

    protected void getObjectsNoProgress(List<V> list, AOServProtocol.CommandID commID, Object ... params) {
        Profiler.startProfile(Profiler.IO, AOServTable.class, "getObjectNoProgress(List<V>,AOServProtocol.CommandID,...)", null);
        try {
            try {
                // Get a snapshot of all load listeners
                TableLoadListenerEntry[] loadListeners=getTableLoadListeners();
                int loadCount=loadListeners==null?0:loadListeners.length;
                // Tell each load listener that we are starting
                for(int c=0;c<loadCount;c++) {
                    TableLoadListenerEntry entry=loadListeners[c];
                    entry.param=entry.listener.tableLoadStarted(this, entry.param);
                }

                AOServConnection connection=connector.getConnection(getMaxConnectionsPerThread());
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(commID.ordinal());
                    AOServConnector.writeParams(params, out);
                    out.flush();

                    getObjectsNoProgress0(
                        list,
                        connection,
                        loadListeners
                    );
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    connector.releaseConnection(connection);
                }

                sortIfNeeded(list);
            } catch(IOException err) {
                System.err.println("Error trying to getObjects for table "+getTableID());
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    @SuppressWarnings({"unchecked"})
    private void getObjectsNoProgress0(
        List<V> list,
        AOServConnection connection,
        TableLoadListenerEntry[] loadListeners
    ) {
        Profiler.startProfile(Profiler.IO, AOServTable.class, "getObjectsNoProgress0(List<V>,AOServConnection,TableLoadListenerEntry[])", null);
        try {
            try {
                int loadCount=loadListeners==null?0:loadListeners.length;

                // Load the data
                CompressedDataInputStream in=connection.getInputStream();
                int objCount=0;
                int code;
                while((code=in.readByte())==AOServProtocol.NEXT) {
                    V obj=getNewObject();
                    obj.read(in);
                    if(obj instanceof SingleTableObject) ((SingleTableObject<K,V>)obj).setTable(this);
                    list.add(obj);

                    // Tell each load listener of the new object
                    for(int c=0;c<loadCount;c++) {
                        TableLoadListenerEntry entry=loadListeners[c];
                        entry.param=entry.listener.tableRowLoaded(this, obj, objCount-1, entry.param);
                    }
                }
                AOServProtocol.checkResult(code, in);

                // Tell each load listener that we are done
                for(int c=0;c<loadCount;c++) {
                    TableLoadListenerEntry entry=loadListeners[c];
                    entry.param=entry.listener.tableLoadCompleted(this, entry.param);
                }
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Sorts the table using the default sort columns and orders.  If no defaults have been provided, then
     * the table is not sorted.
     *
     * @see  #getDefaultSortSQLExpressions()
     */
    protected void sortIfNeeded(List<V> list) {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "sortIfNeeded(List<V>)", null);
        try {
            // Get the details for the sorting
            SQLExpression[] sortExpressions=getDefaultSortSQLExpressions();
            if(sortExpressions!=null) {
                boolean[] sortOrders=getDefaultSortOrders();
                connector.schemaTypes.sort(list, sortExpressions, sortOrders);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private ProgressListener[] getProgressListeners() {
        synchronized(progressListeners) {
            int size=progressListeners.size();
            if(size==0) return null;
            return progressListeners.toArray(new ProgressListener[size]);
        }
    }

    /**
     * Gets an approximate number of accessible rows in the database.
     *
     * @see  #size()
     */
    public int getCachedRowCount() {
        return size();
    }

    /**
     * Gets the list of all accessible rows.
     *
     * @return  a <code>List</code> containing all of the rows
     *
     * @exception  IOException  if unable to access the server
     * @exception  SQLException  if unable to access the database
     */
    abstract public List<V> getRows();
    
    final public SQLExpression getSQLExpression(String expr) {
        Profiler.startProfile(Profiler.UNKNOWN, AOServTable.class, "getSQLExpression(String)", null);
        try {
            int joinPos=expr.indexOf('.');
            if(joinPos==-1) joinPos=expr.length();
            int castPos=expr.indexOf("::");
            if(castPos==-1) castPos=expr.length();
            int columnNameEnd=Math.min(joinPos, castPos);
            String columnName=expr.substring(0, columnNameEnd);
            SchemaColumn lastColumn=getTableSchema().getSchemaColumn(connector, columnName);
            if(lastColumn==null) throw new IllegalArgumentException("Unable to find column: "+getTableName()+'.'+columnName);

            SQLExpression sql=new SQLColumnValue(connector, lastColumn);
            expr=expr.substring(columnNameEnd);

            while(expr.length()>0) {
                if(expr.charAt(0)=='.') {
                    List<SchemaForeignKey> keys=lastColumn.getReferences(connector);
                    if(keys.size()!=1) throw new IllegalArgumentException("Column "+lastColumn.getSchemaTable(connector).getName()+'.'+lastColumn.column_name+" should reference precisely one column, references "+keys.size());

                    joinPos=expr.indexOf('.', 1);
                    if(joinPos==-1) joinPos=expr.length();
                    castPos=expr.indexOf("::", 1);
                    if(castPos==-1) castPos=expr.length();
                    int joinNameEnd=Math.min(joinPos, castPos);
                    columnName=expr.substring(1, joinNameEnd);
                    SchemaColumn keyColumn=keys.get(0).getForeignColumn(connector);
                    SchemaTable valueTable=keyColumn.getSchemaTable(connector);
                    SchemaColumn valueColumn=valueTable.getSchemaColumn(connector, columnName);
                    if(valueColumn==null) throw new IllegalArgumentException("Unable to find column: "+valueTable.getName()+'.'+columnName+" referenced from "+getTableName());

                    sql=new SQLColumnJoin(connector, sql, keyColumn, valueColumn);
                    expr=expr.substring(joinNameEnd);

                    lastColumn=valueColumn;
                } else if(expr.charAt(0)==':' && expr.length()>1 && expr.charAt(1)==':') {
                    joinPos=expr.indexOf('.', 2);
                    if(joinPos==-1) joinPos=expr.length();
                    castPos=expr.indexOf("::", 2);
                    if(castPos==-1) castPos=expr.length();
                    int typeNameEnd=Math.min(joinPos, castPos);
                    String typeName=expr.substring(2, typeNameEnd);
                    SchemaType type=connector.schemaTypes.get(typeName);
                    if(type==null) throw new IllegalArgumentException("Unable to find SchemaType: "+typeName);

                    sql=new SQLCast(sql, type);
                    expr=expr.substring(typeNameEnd);
                } else throw new IllegalArgumentException("Unable to parse: "+expr);
            }
            return sql;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Gets the unique identifier for this table.  Each
     * table has a unique identifier, as defined in
     * <code>SchemaTable.TableID</code>.
     *
     * @return  the identifier for this table
     *
     * @see  SchemaTable.TableID
     */
    public abstract SchemaTable.TableID getTableID();

    private TableLoadListenerEntry[] getTableLoadListeners() {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "getTableLoadListeners()", null);
        try {
            synchronized(loadListeners) {
                int size=loadListeners.size();
                if(size==0) return null;
                return loadListeners.toArray(new TableLoadListenerEntry[size]);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public SchemaTable getTableSchema() {
        return connector.schemaTables.get(getTableID());
    }

    final public String getTableName() {
        return getTableSchema().getName();
    }

    /**
     * Gets the rows in a more efficient, indexed manner.
     *
     * @exception UnsupportedOperationException if not supported by the specific table implementation
     */
    final public List<V> getIndexedRows(int col, int value) {
        return getIndexedRows(col, Integer.valueOf(value));
    }

    /**
     * Gets the rows in a more efficient, indexed manner.  This default implementation simply throws UnsupportedOperationException.
     *
     * @exception UnsupportedOperationException if not supported by the specific table implementation
     */
    public List<V> getIndexedRows(int col, Object value) {
        throw new UnsupportedOperationException("getIndexedRows now supported by table implementation");
    }

    final public V getUniqueRow(int col, int value) {
        return getUniqueRowImpl(col, Integer.valueOf(value));
    }

    final public V getUniqueRow(int col, long value) {
        return getUniqueRowImpl(col, Long.valueOf(value));
    }

    final public V getUniqueRow(int col, Object value) {
        if(value==null) return null;
        return getUniqueRowImpl(col, value);
    }

    final public V getUniqueRow(int col, short value) {
        return getUniqueRowImpl(col, Short.valueOf(value));
    }

    protected abstract V getUniqueRowImpl(int col, Object value);

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
        return false;
    }

    /**
     * Checks if the table is loaded.  A table is considered loaded when
     * accessing any part of it will be done entirely locally, avoiding
     * any network traffic.
     */
    public boolean isLoaded() {
        return false;
    }

    /**
     * Prints the contents of this table.
     */
    final public void printTable(AOServConnector conn, PrintWriter out, boolean isInteractive) {
        Profiler.startProfile(Profiler.UNKNOWN, AOServTable.class, "printTable(AOServConnector,PrintWriter,boolean)", null);
        try {
            SchemaTable schemaTable=getTableSchema();
            List<SchemaColumn> cols=schemaTable.getSchemaColumns(conn);
            int numCols=cols.size();
            String[] titles=new String[numCols];
            SchemaType[] types=new SchemaType[numCols];
            boolean[] alignRights=new boolean[numCols];
            for(int c=0;c<numCols;c++) {
                SchemaColumn col=cols.get(c);
                titles[c]=col.getColumnName();
                SchemaType type=types[c]=col.getSchemaType(conn);
                alignRights[c]=type.alignRight();
            }

            Object[] values;
            synchronized(this) {
                List<V> rows=getRows();
                int numRows=rows.size();
                values=new Object[numRows*numCols];
                int pos=0;
                for(int rowIndex=0;rowIndex<numRows;rowIndex++) {
                    V row=rows.get(rowIndex);
                    for(int colIndex=0;colIndex<numCols;colIndex++) {
                        values[pos++]=types[colIndex].getString(row.getColumn(colIndex));
                    }
                }
            }

            SQLUtility.printTable(titles, values, out, isInteractive, alignRights);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Removes a <code>ProgressListener</code> from the list of
     * objects being notified as this table is being loaded.
     */
    final public void removeProgressListener(ProgressListener listener) {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "removeProgressListener(ProgressListener)", null);
        try {
            synchronized(progressListeners) {
                int size=progressListeners.size();
                for(int c=0;c<size;c++) {
                    Object O=progressListeners.get(c);
                    if(O==listener) {
                        progressListeners.remove(c);
                        break;
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>TableListener</code> from the list of
     * objects being notified when the data is updated.
     */
    final public void removeTableListener(TableListener listener) {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "removeTableListener(TableListener)", null);
        try {
            if(tableListeners!=null) {
                synchronized(eventLock) {
                    int size=tableListeners.size();
                    for(int c=0;c<size;c++) {
                        TableListenerEntry entry=tableListeners.get(c);
                        if(entry.listener==listener) {
                            tableListeners.remove(c);
                            size--;
                            if(entry.delay>0 && thread!=null) {
                                // If all remaining listeners are immediate (delay 0), kill the thread
                                boolean foundDelayed=false;
                                for(int d=0;d<size;d++) {
                                    TableListenerEntry tle=tableListeners.get(d);
                                    if(tle.delay>0) {
                                        foundDelayed=true;
                                        break;
                                    }
                                }
                                if(!foundDelayed) {
                                    // The thread will terminate itself once the reference to it is removed
                                    thread=null;
                                }
                            }
                            break;
                        }
                    }
                    // Tell the thread to recalc its stuff
                    eventLock.notifyAll();
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes a <code>TableLoadListener</code> from the list of
     * objects being notified when the table is being loaded.
     */
    final public void removeTableLoadListener(TableLoadListener listener) {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "removeTableLoadListener(TableLoadListener)", null);
        try {
            synchronized(loadListeners) {
                int size=loadListeners.size();
                for(int c=0;c<size;c++) {
                    TableLoadListenerEntry entry=(TableLoadListenerEntry)loadListeners.get(c);
                    if(entry.listener==listener) {
                        loadListeners.remove(c);
                        break;
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    void tableUpdated() {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "tableUpdated()", null);
        try {
            // Notify all immediate listeners
            if(tableListeners!=null) {
                Iterator<TableListenerEntry> I=tableListeners.iterator();
                while(I.hasNext()) {
                    TableListenerEntry entry=I.next();
                    if(entry.delay<=0) {
                        entry.listener.tableUpdated(this);
                    }
                }
            }

            long time=System.currentTimeMillis();

            // Notify the batching thread of the update
            synchronized(eventLock) {
                if(tableListeners!=null) {
                    int size=tableListeners.size();
                    for(int c=0;c<size;c++) {
                        TableListenerEntry entry=tableListeners.get(c);
                        if(entry.delay>0 && entry.delayStart==-1) entry.delayStart=time;
                    }
                    eventLock.notify();
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    final public String toString() {
        Profiler.startProfile(Profiler.FAST, AOServTable.class, "toString()", null);
        try {
            return getTableSchema().display;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    // Iterable methods
    public Iterator<V> iterator() {
        return getRows().iterator();
    }

    // Map methods
    public Set<Entry<K,V>> entrySet() {
        return new EntrySet<K,V>(getRows());
    }

    public Collection<V> values() {
        return getRows();
    }

    public Set<K> keySet() {
        return new KeySet<K,V>(getRows());
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        throw new UnsupportedOperationException();
    }

    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }
    
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings({"unchecked"})
    public boolean containsValue(Object value) {
        V aoObj=(V)value;
        Object key=aoObj.getKey();
        return containsKey(key);
    }

    public boolean containsKey(Object key) {
        return get(key)!=null;
    }

    public boolean isEmpty() {
        return getRows().isEmpty();
    }

    public int size() {
        return getRows().size();
    }
}
