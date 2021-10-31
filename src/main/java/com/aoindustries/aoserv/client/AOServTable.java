/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.hodgepodge.sort.ComparisonSortAlgorithm;
import com.aoapps.hodgepodge.sort.JavaSort;
import com.aoapps.hodgepodge.table.TableListener;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.sql.SQLUtility;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.schema.Type;
import com.aoindustries.aoserv.client.sql.Parser;
import com.aoindustries.aoserv.client.sql.SQLExpression;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

/**
 * An <code>AOServTable</code> provides access to one
 * set of <code>AOServObject</code>s.  The subclasses often provide additional
 * methods for manipulating the data outside the scope
 * of a single <code>AOServObject</code>.
 *
 * @author  AO Industries, Inc.
 *
 * @see  AOServObject
 */
public abstract class AOServTable<K, V extends AOServObject<K, V>> implements Iterable<V>, com.aoapps.hodgepodge.table.Table<V> {

	protected final AOServConnector connector;
	//final SimpleAOClient client;
	final Class<V> clazz;

	private class TableListenersLock {
		@Override
		public String toString() {
			return "tableListenersLock - "+getTableID();
		}
	};

	private static final class TableListenerEntry {

		private final TableListener listener;
		private final long delay;
		// All accesses should be protected by the table.eventLock
		long delayStart = -1;

		private TableListenerEntry(TableListener listener, long delay) {
			this.listener = listener;
			this.delay = delay;
		}
	}

	private final TableListenersLock tableListenersLock = new TableListenersLock();

	/**
	 * The list of <code>TableListener</code>s.
	 */
	private List<TableListenerEntry> tableListeners;

	/**
	 * The lock used for cache event handling.
	 */
	class EventLock {
		@Override
		public String toString() {
			return "EventLock - "+getTableID();
		}
	}
	final EventLock eventLock=new EventLock();

	private final class TableEventThread extends Thread {

		private TableEventThread() {
			setName("TableEventThread #" + getId() + " ("+AOServTable.this.getTableID()+") - "+AOServTable.this.getClass().getName());
			setDaemon(true);
		}

		@Override
		@SuppressWarnings({"NestedSynchronizedStatement", "UseSpecificCatch", "TooBroadCatch"})
		public void run() {
			OUTER_LOOP :
			while(!Thread.currentThread().isInterrupted()) {
				try {
					synchronized(eventLock) {
						while(!Thread.currentThread().isInterrupted()) {
							if(thread != this) break OUTER_LOOP;
							long time = System.currentTimeMillis();
							// Run anything that should be ran, calculating the minimum sleep time
							// for the next wait period.
							long minTime = Long.MAX_VALUE;
							// Get a copy to not hold lock too long
							List<TableListenerEntry> tableListenersSnapshot;
							synchronized(tableListenersLock) {
								tableListenersSnapshot = (tableListeners == null) ? null : new ArrayList<>(tableListeners);
							}
							if(tableListenersSnapshot != null) {
								int size = tableListenersSnapshot.size();
								for (TableListenerEntry entry : tableListenersSnapshot) {
									// skip immediate listeners
									long delay = entry.delay;
									if(delay>0) {
										long delayStart = entry.delayStart;
										// Is the table idle?
										if (delayStart != -1) {
											// Has the system time been modified to an earlier time?
											if (delayStart > time) delayStart = entry.delayStart = time;
											long endTime = delayStart + delay;
											if (time >= endTime) {
												// Ready to run
												entry.delayStart = -1;
												// System.out.println("DEBUG: Started TableEventThread: run: "+getName()+" calling tableUpdated on "+entry.listener);
												// Run in a different thread to avoid deadlock and increase concurrency responding to table update events.
												AOServConnector.executorService.submit(() -> entry.listener.tableUpdated(AOServTable.this));
											} else {
												// Remaining delay
												long remaining = endTime - time;
												if (remaining < minTime) minTime = remaining;
											}
										}
									}
								}
							}
							if(minTime==Long.MAX_VALUE) {
								// System.out.println("DEBUG: TableEventThread: run: "+getName()+" size="+size+", waiting indefinitely");
								eventLock.wait();
							} else {
								// System.out.println("DEBUG: TableEventThread: run: "+getName()+" size="+size+", waiting for "+minTime+" ms");
								eventLock.wait(minTime);
							}
						}
					}
				} catch (ThreadDeath td) {
					throw td;
				} catch (InterruptedException e) {
					connector.getLogger().log(Level.WARNING, null, e);
					// Restore the interrupted status
					Thread.currentThread().interrupt();
				} catch (Throwable t) {
					connector.getLogger().log(Level.SEVERE, null, t);
				}
			}
		}
	}

	/**
	 * The thread that is performing the batched updates.
	 * All access should be protected by the eventLock.
	 */
	private TableEventThread thread;

	/**
	 * The list of <code>ProgressListener</code>s.
	 */
	final List<ProgressListener> progressListeners = new ArrayList<>();

	private static final class TableLoadListenerEntry {

		private final TableLoadListener listener;
		private Object param;

		private TableLoadListenerEntry(TableLoadListener listener, Object param) {
			this.listener=listener;
			this.param=param;
		}
	}

	/**
	 * All of the table load listeners.
	 */
	final List<TableLoadListenerEntry> _loadListeners = new ArrayList<>();

	protected AOServTable(AOServConnector connector, Class<V> clazz) {
		this.connector=connector;
		//this.client=new SimpleAOClient(connector);
		this.clazz=clazz;
	}

	public final void addProgressListener(ProgressListener listener) {
		synchronized(progressListeners) {
			progressListeners.add(listener);
		}
	}

	/**
	 * Checks if this table has at least one listener.
	 */
	public final boolean hasAnyTableListener() {
		synchronized(tableListenersLock) {
			return tableListeners!=null && !tableListeners.isEmpty();
		}
	}

	public final boolean hasTableListener(TableListener listener) {
		synchronized(tableListenersLock) {
			if(tableListeners==null) return false;
			for(TableListenerEntry tableListenerEntry : tableListeners) {
				if(tableListenerEntry.listener==listener) return true;
			}
			return false;
		}
	}

	/**
	 * Registers a <code>TableListener</code> to be notified when
	 * the cached data for this table expires.  The default of
	 * 1000ms of batching is used.
	 *
	 * @see  #addTableListener(TableListener,long)
	 */
	@Override
	public final void addTableListener(TableListener listener) {
		addTableListener(listener, 1000);
	}

	/**
	 * Registers a <code>TableListener</code> to be notified when
	 * the cached data for this table expires.  Repitative incoming
	 * requests will be batched into fewer events, in increments
	 * provided by batchTime.  If batchTime is 0, the event is immediately
	 * and always distributed.  Batched events are performed in
	 * concurrent Threads, while immediate events are triggered by the
	 * central cache invalidation thread.  In other words, don't use
	 * a batchTime of zero unless you absolutely need your code to
	 * run immediately, because it causes serial processing of the event
	 * and may potentially slow down the responsiveness of the server.
	 */
	@Override
	public final void addTableListener(TableListener listener, long batchTime) {
		if(batchTime<0) throw new IllegalArgumentException("batchTime<0: "+batchTime);

		synchronized(tableListenersLock) {
			if(tableListeners==null) tableListeners=new ArrayList<>();
			tableListeners.add(new TableListenerEntry(listener, batchTime));
		}
		synchronized(eventLock) {
			if(batchTime > 0 && thread == null) {
				(thread = new TableEventThread()).start();
				// System.out.println("DEBUG: Started TableEventThread: "+thread.getName());
			}
			// Tell the thread to recalc its stuff
			eventLock.notifyAll();
		}

		connector.addingTableListener();
	}

	public final void addTableLoadListener(TableLoadListener listener, Object param) {
		synchronized(_loadListeners) {
			_loadListeners.add(new TableLoadListenerEntry(listener, param));
		}
	}

	/**
	 * Clears the cache, freeing up memory.  The data will be reloaded upon
	 * next use.
	 */
	@SuppressWarnings("NoopMethodInAbstractClass")
	public void clearCache() {
	}

	public final AOServConnector getConnector() {
		return connector;
	}

	/*
	 * Commented-out because I'm not sure if this handles the references like ao_server.server.farm.name
	 *  - Dan 2008-04-18
	public final SchemaColumn[] getDefaultSortSchemaColumns() {
		OrderBy[] orderBys=getDefaultOrderBy();
		if(orderBys==null) return null;
		int len=orderBys.length;
		SchemaTable schemaTable=connector.schemaTables.get(getTableID());
		SchemaColumn[] schemaColumns=new SchemaColumn[len];
		for(int c=0;c<len;c++) {
			String columnName=orderBys[c].getExpression();
			SchemaColumn col=schemaTable.getSchemaColumn(connector, columnName);
			if(col==null) throw new SQLException("Unable to find SchemaColumn: "+columnName+" on "+schemaTable.getName());
			schemaColumns[c]=col;
		}
		return schemaColumns;
	}*/

	/**
	 * Indicates ascending sort.
	 */
	public static final boolean ASCENDING=true;

	/**
	 * Indicates descending sort.
	 */
	public static final boolean DESCENDING=false;

	public static class OrderBy {
		private final String expression;
		private final boolean order;

		public OrderBy(String expression, boolean order) {
			this.expression = expression;
			this.order = order;
		}

		/**
		 * Gets the column name(s) that is used for sorting, may be a complex expression (currently supports things like ao_server.server.farm.name)
		 */
		String getExpression() {
			return expression;
		}

		/**
		 * Gets the ASCENDING or DESCENDING order.
		 */
		boolean getOrder() {
			return order;
		}
	}

	/**
	 * Gets the default sorting for this table.
	 *
	 * @return  {@code null} if the sorting is performed by the server or the array of column names
	 */
	protected abstract OrderBy[] getDefaultOrderBy();

	// TODO: Make AOServObject Comparable like in AOServ 2.0, and let them sort themselves out
	public final SQLExpression[] getDefaultOrderBySQLExpressions() throws SQLException, IOException {
		OrderBy[] orderBys = getDefaultOrderBy();
		if(orderBys == null) return null;
		int len = orderBys.length;
		SQLExpression[] exprs = new SQLExpression[len];
		for(int c = 0; c < len; c++) {
			exprs[c] = Parser.parseSQLExpression(this, orderBys[c].getExpression());
		}
		return exprs;
	}

	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	protected V getNewObject() throws IOException {
		try {
			try {
				return clazz.getConstructor().newInstance();
			} catch(InvocationTargetException e) {
				// Unwrap cause for more direct stack traces
				Throwable cause = e.getCause();
				throw (cause == null) ? e : cause;
			}
		} catch(Throwable t) {
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	/*
	protected int getMaxConnectionsPerThread() {
		return 1;
	}*/

	/**
	 * Gets a single object or {@code null} when not found.
	 */
	protected V getObject(boolean allowRetry, final AoservProtocol.CommandID commID, final Object ... params) throws IOException, SQLException {
		return connector.requestResult(allowRetry,
			commID,
			new AOServConnector.ResultRequest<V>() {
				private V result;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					AOServConnector.writeParams(params, out);
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.NEXT) {
						V obj=getNewObject();
						obj.read(in, AoservProtocol.Version.CURRENT_VERSION);
						if(obj instanceof SingleTableObject) {
							@SuppressWarnings("unchecked")
							SingleTableObject<K, V> sto = (SingleTableObject)obj;
							sto.setTable(AOServTable.this);
						}
						result = obj;
					} else {
						AoservProtocol.checkResult(code, in);
						result = null;
					}
				}

				@Override
				public V afterRelease() {
					return result;
				}
			}
		);
	}

	protected List<V> getObjects(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		List<V> list=new ArrayList<>();
		getObjects(allowRetry, list, commID, params);
		return list;
	}

	private void getObjects(boolean allowRetry, final boolean withProgress, final List<V> list, final AoservProtocol.CommandID commID, final Object ... params) throws IOException, SQLException {
		final int initialSize = list.size();
		// Get a snapshot of all listeners
		final ProgressListener[] progListeners = withProgress ? getProgressListeners() : null;
		final int progCount = progListeners == null ? 0 : progListeners.length;
		final int[] progressScales;
		final int[] lastProgresses;
		if(progListeners != null) {
			progressScales = new int[progCount];
			for(int c = 0; c < progCount; c++) {
				progressScales[c] = progListeners[c].getScale();
			}
			lastProgresses = new int[progCount];
		} else {
			progressScales = null;
			lastProgresses = null;
		}

		// Get a snapshot of all load listeners
		final TableLoadListenerEntry[] loadListeners = getTableLoadListeners();
		final int loadCount = loadListeners == null ? 0 : loadListeners.length;

		// Start the progresses at zero.  Progress notified before table listeners, so GUI elements can set a progress bar back to zero before showing it on table load
		for(int c = 0; c < progCount; c++) {
			progListeners[c].onProgressChanged(this, 0, progressScales[c]);
		}

		// Tell each load listener that we are starting
		for(int c = 0; c < loadCount; c++) {
			TableLoadListenerEntry entry = loadListeners[c];
			entry.param = entry.listener.onTableLoadStarted(this, entry.param);
		}

		try {
			try {
				connector.requestUpdate(
					allowRetry,
					commID,
					new AOServConnector.UpdateRequest() {

						@Override
						public void writeRequest(StreamableOutput out) throws IOException {
							if(withProgress) out.writeBoolean(progListeners != null);
							AOServConnector.writeParams(params, out);
						}

						@Override
						public void readResponse(StreamableInput in) throws IOException, SQLException {
							// Remove anything that was added during a previous attempt
							if(initialSize == 0) {
								list.clear();
							} else {
								while(list.size() > initialSize) {
									list.remove(list.size() - 1);
								}
							}
							// Set the progresses back to zero
							for(int c = 0; c < progCount; c++) {
								if(lastProgresses[c] != 0) {
									progListeners[c].onProgressChanged(
										AOServTable.this,
										lastProgresses[c] = 0,
										progressScales[c]
									);
								}
							}
							// Load the data
							int code = (progListeners == null) ? AoservProtocol.NEXT : in.readByte();
							if(code == AoservProtocol.NEXT) {
								final long size;
								if(progListeners == null) {
									size = -1; // Unknown
								} else {
									size = in.readLong();
								}
								// Tell each load listener about the number of rows
								for(int c = 0; c < loadCount; c++) {
									TableLoadListenerEntry entry = loadListeners[c];
									entry.param = entry.listener.onTableLoadRowCount(
										AOServTable.this,
										entry.param,
										size == -1 ? null : size
									);
								}

								long objCount = 0;
								while((code = in.readByte()) == AoservProtocol.NEXT) {
									V obj = getNewObject();
									obj.read(in, AoservProtocol.Version.CURRENT_VERSION);
									if(obj instanceof SingleTableObject) {
										@SuppressWarnings("unchecked")
										SingleTableObject<K, V> sto = (SingleTableObject)obj;
										sto.setTable(AOServTable.this);
									}

									// Sort and add
									list.add(obj);

									// Notify of progress changes
									objCount++;
									for(int c = 0; c < progCount; c++) {
										int currentProgress = (int)(objCount * progressScales[c] / size);
										if(currentProgress != lastProgresses[c]) {
											progListeners[c].onProgressChanged(
												AOServTable.this,
												lastProgresses[c] = currentProgress,
												progressScales[c]
											);
										}
									}

									// Tell each load listener of the new object
									for(int c = 0; c < loadCount; c++) {
										TableLoadListenerEntry entry = loadListeners[c];
										entry.param = entry.listener.onTableRowLoaded(
											AOServTable.this,
											entry.param,
											objCount - 1,
											obj
										);
									}
								}
								AoservProtocol.checkResult(code, in);
								if(size != -1 && size != objCount) throw new IOException("Unexpected number of objects returned: expected = " + size + ", returned = " + objCount);
							} else {
								AoservProtocol.checkResult(code, in);
								throw new IOException("Unexpected response code: " + code);
							}
						}

						@Override
						public void afterRelease() {
							try {
								sortIfNeeded(list);
							} catch(IOException | SQLException err) {
								throw new WrappedException(err);
							}
						}
					}
				);
				// Show at final progress scale, just in case previous algorithm did not get the scale there.
				for(int c = 0; c < progCount; c++) {
					if(lastProgresses[c] != progressScales[c]) {
						progListeners[c].onProgressChanged(
							AOServTable.this,
							lastProgresses[c] = progressScales[c],
							progressScales[c]
						);
					}
				}
			} catch(WrappedException err) {
				// Unwrap exceptions to specific types
				Throwable cause = err.getCause();
				if(cause instanceof IOException) throw (IOException)cause;
				if(cause instanceof SQLException) throw (SQLException)cause;
				throw err;
			}
		} catch(Error | RuntimeException | IOException | SQLException e) {
			// Tell each load listener that we failed
			for(int c = 0; c < loadCount; c++) {
				TableLoadListenerEntry entry = loadListeners[c];
				entry.param = entry.listener.onTableLoadFailed(AOServTable.this, entry.param, e);
			}
			throw e;
		}
		// Tell each load listener that we are done
		for(int c = 0; c < loadCount; c++) {
			TableLoadListenerEntry entry = loadListeners[c];
			entry.param = entry.listener.onTableLoadCompleted(AOServTable.this, entry.param);
		}
	}

	protected void getObjects(boolean allowRetry, final List<V> list, final AoservProtocol.CommandID commID, final Object ... params) throws IOException, SQLException {
		getObjects(allowRetry, true, list, commID, params);
	}

	/**
	 * Limited to {@link Integer#MAX_VALUE} rows.
	 *
	 * @see  #getObjects(boolean, java.util.List, com.aoindustries.aoserv.client.schema.AoservProtocol.CommandID, java.lang.Object...)
	 */
	protected List<V> getObjects(boolean allowRetry, AoservProtocol.CommandID commID, AOServWritable param1) throws IOException, SQLException {
		List<V> list = new ArrayList<>();
		getObjects(allowRetry, list, commID, param1);
		return list;
	}

	protected void getObjectsNoProgress(boolean allowRetry, final List<V> list, final AoservProtocol.CommandID commID, final Object ... params) throws IOException, SQLException {
		getObjects(allowRetry, false, list, commID, params);
	}

	/**
	 * Limited to {@link Integer#MAX_VALUE} rows.
	 *
	 * @see  #getObjectsNoProgress(boolean, java.util.List, com.aoindustries.aoserv.client.schema.AoservProtocol.CommandID, java.lang.Object...)
	 */
	protected List<V> getObjectsNoProgress(boolean allowRetry, AoservProtocol.CommandID commID, Object ... params) throws IOException, SQLException {
		List<V> list = new ArrayList<>();
		getObjectsNoProgress(allowRetry, list, commID, params);
		return list;
	}

	/**
	 * Gets the ComparisonSortAlgorithm used to sort the table.
	 * <p>
	 * <b>Implementation Note:</b><br>
	 * Defaults to {@link JavaSort}.
	 * </p>
	 */
	protected ComparisonSortAlgorithm<Object> getSortAlgorithm() {
		return JavaSort.getInstance();
	}

	/**
	 * Sorts the table using the default sort columns and orders.  If no defaults have been provided, then
	 * the table is not sorted.
	 *
	 * @see  #getDefaultOrderBySQLExpressions()
	 */
	protected void sortIfNeeded(List<V> list) throws SQLException, IOException {
		// Get the details for the sorting
		SQLExpression[] sortExpressions = getDefaultOrderBySQLExpressions();
		if(sortExpressions != null) {
			OrderBy[] orderBys = getDefaultOrderBy();
			boolean[] sortOrders = new boolean[orderBys.length];
			for(int c = 0; c < orderBys.length; c++) {
				sortOrders[c] = orderBys[c].getOrder();
			}
			connector.sort(getSortAlgorithm(), list, sortExpressions, sortOrders);
		}
	}

	/**
	 * Gets a copy of the progress listeners, or {@code null} when none registered.
	 */
	private ProgressListener[] getProgressListeners() {
		synchronized(progressListeners) {
			int size = progressListeners.size();
			if(size == 0) return null;
			return progressListeners.toArray(new ProgressListener[size]);
		}
	}

	/**
	 * Gets an approximate number of accessible rows in the database.
	 *
	 * @see  #size()
	 */
	public int getCachedRowCount() throws IOException, SQLException {
		return size();
	}

	/**
	 * Gets the list of all accessible rows.
	 *
	 * @return  a <code>List</code> containing all of the rows
	 *
	 * @exception  IOException  if unable to access the server
	 * @exception  SQLException  if unable to access the database
	 *
	 * @see  #getRowsCopy()
	 */
	// TODO: Make rows an autoclosable, and use this to free storage promptly where needed?
	// TODO: This means that tables themselves would not be Iterable.
	// TODO: This would also create a try-with-resources requirement in places where not expected, like <c:forEach> iterations
	// TODO: This idea most relevant since we have FilesystemCachedTable.  Less important otherwise since garbage collector handles it all.
	// TODO: Implementations could return a wrapper around the list, that counds the number of users,
	//           and each user gets a smaller wrapper that handles close() once and decrements the use counter.
	//           Once the use counter gets to zero the list is available for release.
	//           Tables that are all on heap could just return a simpler wrapper that ensures no use-after-close.
	// TODO: This would extend to get getIndexed, too, and getRowsCopy().
	@Override
	public List<V> getRows() throws IOException, SQLException {
		return Collections.unmodifiableList(getRowsCopy());
	}

	/**
	 * Gets a modifiable copy of the rows, which may then be manipulated, such as for sorting.
	 * <p>
	 * This gives the table implementation a way to create a defensive copy most
	 * efficient to its underlying storage mechanism.
	 * </p>
	 * <p>
	 * Note: It is best to use {@link #getSortAlgorithm()} when sorting rows, as
	 * the choice of sorting can be very important when objects are pulled from
	 * non-heap source like filesystem-based objects.  It is very easy for the
	 * sort itself to end up pulling all objects into heap.
	 * </p>
	 *
	 * @see  #getRows()
	 * @see  #getSortAlgorithm()
	 */
	public abstract List<V> getRowsCopy() throws IOException, SQLException;

	/**
	 * Gets the unique identifier for this table.  Each
	 * table has a unique identifier, as defined in
	 * <code>SchemaTable.TableID</code>.
	 *
	 * @return  the identifier for this table
	 *
	 * @see  com.aoindustries.aoserv.client.schema.Table.TableID
	 */
	public abstract Table.TableID getTableID();

	private TableLoadListenerEntry[] getTableLoadListeners() {
		synchronized(_loadListeners) {
			int size=_loadListeners.size();
			if(size==0) return null;
			return _loadListeners.toArray(new TableLoadListenerEntry[size]);
		}
	}

	public final Table getTableSchema() throws IOException, SQLException {
		return connector.getSchema().getTable().get(getTableID());
	}

	@Override
	public final String getTableName() throws IOException, SQLException {
		return getTableSchema().getName();
	}

	/**
	 * Gets the rows in a more efficient, indexed manner.
	 *
	 * @exception UnsupportedOperationException if not supported by the specific table implementation
	 */
	public final List<V> getIndexedRows(int col, int value) throws IOException, SQLException {
		return getIndexedRows(col, Integer.valueOf(value));
	}

	/**
	 * Gets the rows in a more efficient, indexed manner.
	 * <p>
	 * <b>Implementation Note:</b><br>
	 * This default implementation simply throws UnsupportedOperationException.
	 * </p>
	 *
	 * @exception UnsupportedOperationException if not supported by the specific table implementation
	 */
	public List<V> getIndexedRows(int col, Object value) throws IOException, SQLException {
		throw new UnsupportedOperationException("getIndexedRows now supported by table implementation");
	}

	// TODO: Why do these exist as final?  Seems they should not exist at all since there's no way to implement primitive optimizations in subclasses
	public final V getUniqueRow(int col, int value) throws IOException, SQLException {
		return getUniqueRowImpl(col, value);
	}

	// TODO: Why do these exist as final?  Seems they should not exist at all since there's no way to implement primitive optimizations in subclasses
	public final V getUniqueRow(int col, long value) throws IOException, SQLException {
		return getUniqueRowImpl(col, value);
	}

	public final V getUniqueRow(int col, Object value) throws IOException, SQLException {
		if(value == null) return null;
		return getUniqueRowImpl(col, value);
	}

	// TODO: Why do these exist as final?  Seems they should not exist at all since there's no way to implement primitive optimizations in subclasses
	public final V getUniqueRow(int col, short value) throws IOException, SQLException {
		return getUniqueRowImpl(col, value);
	}

	/**
	 * Gets a row given a unique column value.
	 *
	 * @param col  the column index to search
	 * @param value  when {@code null}, no row is matched, even if there is a single row with a uniquely {@code null} value
	 */
	protected abstract V getUniqueRowImpl(int col, Object value) throws IOException, SQLException;

	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IOException, SQLException {
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
	public final void printTable(AOServConnector conn, PrintWriter out, boolean isInteractive) throws IOException, SQLException {
		Table schemaTable = getTableSchema();
		List<Column> columns = schemaTable.getSchemaColumns(conn);
		final int numCols = columns.size();
		String[] titles = new String[numCols];
		final Type[] types = new Type[numCols];
		int supportsAnyPrecisionCount = 0;
		boolean[] alignRights = new boolean[numCols];
		for(int c = 0 ; c < numCols; c++) {
			Column column = columns.get(c);
			titles[c] = column.getName();
			Type type = types[c]=column.getType(conn);
			if(type.supportsPrecision()) supportsAnyPrecisionCount++;
			alignRights[c] = type.alignRight();
		}

		// Get the data
		final List<V> rows = getRows();
		final int numRows = rows.size();

		// Evaluate the expressions while finding the maximum precisions per column.
		// The precisions allow uniform formatting within a column to depend on the overall contents of the column.
		final int[] precisions = new int[numCols];
		Arrays.fill(precisions, -1);
		// Only iterate through all rows here when needing to process precisions
		if(supportsAnyPrecisionCount > 0) {
			// Stop searching if all max precisions have been found
			int precisionsNotMaxedCount = supportsAnyPrecisionCount;
			ROWS :
			for(V row : rows) {
				for(int col = 0; col < numCols; col++) {
					Type type = types[col];
					// Skip evaluation when precision not supported
					if(type.supportsPrecision()) {
						int maxPrecision = type.getMaxPrecision();
						int current = precisions[col];
						if(
							maxPrecision == -1
							|| current == -1
							|| current < maxPrecision
						) {
							int precision = type.getPrecision(row.getColumn(col));
							if(
								precision != -1
								&& (current == -1 || precision > current)
							) {
								precisions[col] = precision;
								if(maxPrecision != -1 && precision >= maxPrecision) {
									precisionsNotMaxedCount--;
									// Stop searching when all precision-based columns are maxed
									if(precisionsNotMaxedCount <= 0) break ROWS;
								}
							}
						}
					}
				}
			}
		}

		// Print the results
		SQLUtility.printTable(
			titles,
			(Iterable<String[]>)() -> new Iterator<String[]>() {
				private int index = 0;

				@Override
				public boolean hasNext() {
					return index < numRows;
				}

				@Override
				public String[] next() {
					// Convert the results to strings
					AOServObject<?, ?> row = rows.get(index++);
					String[] strings = new String[numCols];
					for(int col = 0; col < numCols; col++) {
						strings[col] = types[col].getString(row.getColumn(col), precisions[col]);
					}
					return strings;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			},
			out,
			isInteractive,
			alignRights
		);
	}

	/**
	 * Removes a {@link ProgressListener} from the list of
	 * objects being notified as this table is being loaded.
	 */
	public final void removeProgressListener(ProgressListener listener) {
		synchronized(progressListeners) {
			for(int i = progressListeners.size() - 1; i >= 0; i--) {
				ProgressListener pl = progressListeners.get(i);
				if(pl == listener) {
					progressListeners.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * Removes a {@link TableListener} from the list of
	 * objects being notified when the data is updated.
	 */
	@Override
	public final void removeTableListener(TableListener listener) {
		// Get thread reference and release eventLock to avoid deadlock
		Thread myThread;
		synchronized(eventLock) {
			myThread = thread;
		}
		boolean stopThread = false;
		synchronized(tableListenersLock) {
			if(tableListeners != null) {
				int size = tableListeners.size();
				for(int i = size - 1; i >= 0; i--) {
					TableListenerEntry entry = tableListeners.get(i);
					if(entry.listener == listener) {
						tableListeners.remove(i);
						size--;
						if(entry.delay > 0 && myThread != null) {
							// If all remaining listeners are immediate (delay 0), kill the thread
							boolean foundDelayed = false;
							for(int j = 0; j < size; j++) {
								TableListenerEntry tle = tableListeners.get(j);
								if(tle.delay > 0) {
									foundDelayed = true;
									break;
								}
							}
							if(!foundDelayed) stopThread = true;
						}
						break;
					}
				}
			}
		}
		synchronized(eventLock) {
			if(stopThread) {
				// The thread will terminate itself once the reference to it is removed
				thread = null;
			}
			// Tell the thread to recalc its stuff
			eventLock.notifyAll();
		}
	}

	/**
	 * Removes a <code>TableLoadListener</code> from the list of
	 * objects being notified when the table is being loaded.
	 */
	public final void removeTableLoadListener(TableLoadListener listener) {
		synchronized(_loadListeners) {
			int size=_loadListeners.size();
			for(int c=0;c<size;c++) {
				TableLoadListenerEntry entry=_loadListeners.get(c);
				if(entry.listener==listener) {
					_loadListeners.remove(c);
					break;
				}
			}
		}
	}

	void tableUpdated() {
		List<TableListenerEntry> tableListenersSnapshot;
		synchronized(tableListenersLock) {
			tableListenersSnapshot = this.tableListeners==null ? null : new ArrayList<>(this.tableListeners);
		}
		if(tableListenersSnapshot!=null) {
			// Notify all immediate listeners
			Iterator<TableListenerEntry> iter = tableListenersSnapshot.iterator();
			while(iter.hasNext()) {
				final TableListenerEntry entry = iter.next();
				if(entry.delay <= 0) {
					// Run in a different thread to avoid deadlock and increase concurrency responding to table update events.
					AOServConnector.executorService.submit(() -> entry.listener.tableUpdated(AOServTable.this));
				}
			}

			synchronized(eventLock) {
				// Notify the batching thread of the update
				int size=tableListenersSnapshot.size();
				boolean modified = false;
				for(int c=0;c<size;c++) {
					TableListenerEntry entry=tableListenersSnapshot.get(c);
					if(entry.delay>0 && entry.delayStart==-1) {
						entry.delayStart=System.currentTimeMillis();
						modified = true;
					}
				}
				if(modified) eventLock.notifyAll();
			}
		}
	}

	@Override
	public final String toString() {
		try {
			return getTableSchema().getDisplay();
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}

	// Iterable methods
	@Override
	public Iterator<V> iterator() {
		try {
			return getRows().iterator();
		} catch(IOException | SQLException err) {
			throw new WrappedException(err);
		}
	}

	/**
	 * Gets a Map-compatible view of this table.
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public Map<K, V> getMap() {
		return map;
	}

	/**
	 * Gets the value for the associated key or {@code null} if the data
	 * doesn't exist or is filtered.
	 *
	 * @param key  when {@code null}, will always return {@code null}
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	public abstract V get(Object key) throws IOException, SQLException;

	private final Map<K, V> map = new Map<K, V>() {
		// Map methods
		@Override
		public V get(Object key) {
			try {
				return AOServTable.this.get(key);
			} catch(IOException | SQLException err) {
				throw new WrappedException(err);
			}
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			try {
				return new EntrySet<>(getRows());
			} catch(IOException | SQLException err) {
				throw new WrappedException(err);
			}
		}

		@Override
		public Collection<V> values() {
			try {
				return getRows();
			} catch(IOException | SQLException err) {
				throw new WrappedException(err);
			}
		}

		@Override
		public Set<K> keySet() {
			try {
				return new KeySet<>(getRows());
			} catch(IOException | SQLException err) {
				throw new WrappedException(err);
			}
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsValue(Object value) {
			if(!clazz.isInstance(value)) {
				return false;
			} else {
				return containsKey(clazz.cast(value).getKey());
			}
		}

		@Override
		public boolean containsKey(Object key) {
			try {
				return AOServTable.this.get(key)!=null;
			} catch(IOException | SQLException err) {
				throw new WrappedException(err);
			}
		}

		@Override
		public boolean isEmpty() {
			try {
				return AOServTable.this.isEmpty();
			} catch(IOException | SQLException err) {
				throw new WrappedException(err);
			}
		}

		@Override
		public int size() {
			try {
				return AOServTable.this.size();
			} catch(IOException | SQLException err) {
				throw new WrappedException(err);
			}
		}
	};

	public boolean isEmpty() throws IOException, SQLException {
		return getRows().isEmpty();
	}

	public int size() throws IOException, SQLException {
		// TODO: A variant of getRows() that does not sort, sort when first needed (See ao-rtd)
		return getRows().size();
	}

	/**
	 * This is size for JavaBeans compatibility.
	 */
	public final int getSize() throws IOException, SQLException {
		return size();
	}
}
