/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.FileList;
import com.aoapps.hodgepodge.io.FileListObjectFactory;
import com.aoapps.hodgepodge.sort.ComparisonSortAlgorithm;
import com.aoapps.hodgepodge.sort.FastQSort;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.sql.SQLColumnValue;
import com.aoindustries.aoserv.client.sql.SQLComparator;
import com.aoindustries.aoserv.client.sql.SQLExpression;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * @author  AO Industries, Inc.
 */
// TODO: Is this worth maintaining?
// TODO: Build on persistent collections instead?
public abstract class FilesystemCachedTable<K, V extends FilesystemCachedObject<K, V>> extends AOServTable<K, V> implements FileListObjectFactory<V> {

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

	protected abstract int getRecordLength();

	/**
	 * Clears the cache, freeing up memory.  The data will be reloaded upon
	 * next use.
	 */
	// TODO: Should we close the tableList right away to free disk space?  What if API users are using the list?
	// TODO: Currently this relies on the garbage collector, which could not run for a very long time and disk
	// TODO: space could grow significantly.
	@Override
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
	private void validateCache() throws IOException, SQLException {
		assert Thread.holdsLock(this);
		long currentTime=System.currentTimeMillis();
		if(
		   // If cache never loaded
		   lastLoaded==-1
		   // If the system time was reset to previous time
		   || currentTime<lastLoaded
		) {
			Table schemaTable = getTableSchema();
			FileList<V> newTableList=new FileList<>(
				schemaTable.getName(),
				"rows",
				getRecordLength(),
				this
			);
			getObjects(true, newTableList, AoservProtocol.CommandID.GET_TABLE, getTableID());
			tableList=newTableList;
			unmodifiableTableList=Collections.unmodifiableList(tableList);
			lastLoaded=currentTime;

			if(columnLists!=null) columnLists.clear();
		}
	}

	/**
	 * Gets the complete list of objects in the table.  This list is unmodifiable and will not ever be changed.
	 * Newer data will be contained in new lists so that any calling code sees a snapshot of the code and may
	 * safely assume the data is constant as long as the code uses the same reference to List returned
	 * here.
	 */
	// TODO: Create a way to copy the set of rows from this table in List form, which would then allow them to be sorted
	//       This is necessary since lists from getRows() are unmodifiable.
	//       Also, need a way to "close" this list as soon as no longer using it to free up disk resources.
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public final List<V> getRows() throws IOException, SQLException {
		synchronized(this) {
			validateCache();
			return unmodifiableTableList;
		}
	}

	@Override
	public List<V> getRowsCopy() throws IOException, SQLException {
		synchronized(this) {
			validateCache();
			Table schemaTable = getTableSchema();
			FileList<V> newCopyList = new FileList<>(
				schemaTable.getName(),
				"rowsCopy",
				tableList.getRecordLength(), // Use the same record length to support disk-to-disk copy
				this
			);
			// addAll will do a disk-to-disk copy of the objects
			newCopyList.addAll(tableList);
			return newCopyList;
		}
	}

	/**
	 * FastQSort accesses the disk file less than other algorithms, and does
	 * not load all the objects into memory at once like the default Java
	 * merge sort.
	 */
	@Override
	protected ComparisonSortAlgorithm<Object> getSortAlgorithm() {
		return FastQSort.getInstance();
	}

	@Override
	protected final V getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(value == null) return null;
		Table schemaTable = getTableSchema();
		Column schemaColumn = schemaTable.getSchemaColumn(connector, col);
		SQLComparator<V> vComparator = new SQLComparator<>(
			connector,
			new SQLExpression[] {
				new SQLColumnValue(connector, schemaColumn)
			},
			new boolean[] {ASCENDING}
		);

		SQLComparator<Object> oComparator = new SQLComparator<>(
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
			if(columnLists==null) {
				columnLists=new ArrayList<>(minLength);
			} else {
				while(columnLists.size()<minLength) {
					columnLists.add(null);
				}
			}
			List<V> unmodifiableSortedList = columnLists.get(col);
			if(unmodifiableSortedList==null) {
				FileList<V> sortedFileList=new FileList<>(
					schemaTable.getName() + '.' + schemaColumn.getName(),
					"unique",
					getRecordLength(),
					tableList.getObjectFactory()
				);
				sortedFileList.addAll(tableList);
				getSortAlgorithm().sort(sortedFileList, vComparator);
				unmodifiableSortedList=Collections.unmodifiableList(sortedFileList);
				columnLists.set(col, unmodifiableSortedList);
			}
			int index = Collections.binarySearch(unmodifiableSortedList, value, oComparator);
			// TODO: Assertion to ensure unique, reading the record before and after to make sure has a different value?
			return index<0?null:unmodifiableSortedList.get(index);
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

	@Override
	public final boolean isLoaded() {
		return lastLoaded!=-1;
	}

	@Override
	public V createInstance() throws IOException {
		V obj = getNewObject();
		if(obj instanceof SingleTableObject<?, ?>) ((SingleTableObject<K, V>)obj).setTable(this);
		return obj;
	}
}
