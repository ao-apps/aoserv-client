/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client;

import com.aoapps.collections.AoCollections;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A <code>CachedTable</code> stores all of the
 * available <code>CachedObject</code>s and performs
 * all subsequent data access locally.  The server
 * notifies the client when a table is updated, and
 * the caches are then invalidated.  Once invalidated,
 * the data is reloaded upon next use.
 *
 * @author  AO Industries, Inc.
 */
public abstract class CachedTable<K, V extends CachedObject<K, V>> extends AOServTable<K, V> {

	/**
	 * The last time that the data was loaded, or
	 * <code>-1</code> if not yet loaded.
	 */
	private long lastLoaded=-1;

	/**
	 * The internal objects are stored in <code>HashMaps</code>
	 * based on unique columns.
	 */
	private List<Map<Object, V>> columnHashes;
	private BitSet columnsHashed;

	/**
	 * The internal objects are stored in <code>HashMaps</code> of <code>CachedObject[]</code>
	 * based on indexed columns.  Each of the contained List<T> are unmodifiable.
	 */
	private List<Map<Object, List<V>>> indexHashes;
	private BitSet indexesHashed;

	/**
	 * The internal objects are stored in an unmodifiable list
	 * for access to the entire table.
	 */
	private List<V> tableData;

	protected CachedTable(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	@Override
	public List<V> getIndexedRows(int col, Object value) throws IOException, SQLException {
		synchronized(this) {
			validateCache();
			int minLength=col+1;
			if(indexHashes==null) {
				indexHashes=new ArrayList<>(minLength);
				indexesHashed=new BitSet(minLength);
			}
			while(indexHashes.size()<minLength) {
				indexHashes.add(null);
			}
			Map<Object, List<V>> map=indexHashes.get(col);
			if(map==null) indexHashes.set(col, map=new HashMap<>());
			if(!indexesHashed.get(col)) {
				// Build the modifiable lists in a temporary Map
				Map<Object, List<V>> modifiableIndexes=new HashMap<>();
				for(V obj : tableData) {
					Object cvalue=obj.getColumn(col);
					List<V> list=modifiableIndexes.get(cvalue);
					if(list==null) modifiableIndexes.put(cvalue, list=new ArrayList<>());
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
	}

	@Override
	protected final V getUniqueRowImpl(int col, Object value) throws IOException, SQLException {
		if(value == null) return null;
		synchronized(this) {
			validateCache();
			int minLength=col+1;
			if(columnHashes==null) {
				columnHashes=new ArrayList<>(minLength);
				columnsHashed=new BitSet(minLength);
			}
			while(columnHashes.size()<minLength) {
				columnHashes.add(null);
			}
			Map<Object, V> map=columnHashes.get(col);
			if(!columnsHashed.get(col)) {
				List<V> table=tableData;
				int size=table.size();
				// Allow 25% growth before rehash
				if(map == null) columnHashes.set(col, map = AoCollections.newHashMap((size * 5) >> 2));
				else map.clear();
				for(int c=0;c<size;c++) {
					V O=table.get(c);
					Object cvalue=O.getColumn(col);
					if(cvalue!=null) {
						Object old=map.put(cvalue, O);
						if(old!=null) throw new SQLException("Duplicate unique entry for table #"+getTableID()+" ("+getTableName()+"), column "+col+": "+cvalue);
					}
				}
				columnsHashed.set(col);
			}
			return map.get(value);
		}
	}

	/**
	 * Gets the complete list of objects in the table.
	 */
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField") // Returning unmodifiable
	public List<V> getRows() throws IOException, SQLException {
		synchronized(this) {
			validateCache();
			return tableData;
		}
	}

	@Override
	public List<V> getRowsCopy() throws IOException, SQLException {
		return new ArrayList<>(getRows());
	}

	/**
	 * Determines if the contents are currently hashed in a hashmap.
	 */
	boolean isHashed(int uniqueColumn) {
		return
			columnsHashed!=null
			&& columnsHashed.get(uniqueColumn)
		;
	}

	/**
	 * Determines if the contents of this column are indexed.
	 */
	boolean isIndexed(int uniqueColumn) {
		return
			indexesHashed!=null
			&& indexesHashed.get(uniqueColumn)
		;
	}

	@Override
	public final boolean isLoaded() {
		return lastLoaded!=-1;
	}

	/**
	 * Clears the cache, freeing up memory.  The data will be reloaded upon
	 * next use.
	 */
	@Override
	public void clearCache() {
		super.clearCache();
		synchronized(this) {
			lastLoaded=-1;
			if(columnHashes!=null) {
				int len=columnHashes.size();
				for(int c=0;c<len;c++) {
					Map<Object, V> map = columnHashes.get(c);
					if(map!=null) map.clear();
				}
			}
			if(columnsHashed!=null) columnsHashed.clear();
			if(indexHashes!=null) {
				int len=indexHashes.size();
				for(int c=0;c<len;c++) {
					Map<Object, List<V>> map=indexHashes.get(c);
					if(map!=null) map.clear();
				}
			}
			if(indexesHashed!=null) indexesHashed.clear();
		}
	}

	/**
	 * Reloads the cache if the cache time has expired.  All accesses are already synchronized.
	 */
	private void validateCache() throws IOException, SQLException {
		long currentTime=System.currentTimeMillis();
		if(
		   // If cache never loaded
		   lastLoaded==-1
		   // If the system time was reset to previous time
		   || currentTime<lastLoaded
		) {
			tableData=Collections.unmodifiableList(getObjects(true, AoservProtocol.CommandID.GET_TABLE, getTableID()));
			lastLoaded=currentTime;
			if(columnHashes!=null) {
				int len=columnHashes.size();
				for(int c=0;c<len;c++) {
					Map<Object, V> map = columnHashes.get(c);
					if(map!=null) map.clear();
				}
			}
			if(columnsHashed!=null) columnsHashed.clear();
			if(indexHashes!=null) {
				int len=indexHashes.size();
				for(int c=0;c<len;c++) {
					Map<Object, List<V>> map=indexHashes.get(c);
					if(map!=null) map.clear();
				}
			}
			if(indexesHashed!=null) indexesHashed.clear();
		}
	}
}
