/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017  AO Industries, Inc.
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A <code>GlobalTable</code> is shared between all users.
 * The data is cached so that subsequent lookups need not
 * access the server.
 *
 * @author  AO Industries, Inc.
 */
abstract public class GlobalTable<K,V extends GlobalObject<K,V>> extends AOServTable<K,V> {

	private static final int numTables = SchemaTable.TableID.values().length;
	/**
	 * The last time that the data was loaded, or
	 * <code>-1</code> if not yet loaded.
	 */
	private static final long[] lastLoadeds=new long[numTables];
	static {
		Arrays.fill(lastLoadeds, -1);
	}

	/**
	 * Each table has its own lock because we were getting deadlocks with one lock on GlobalTable.class.
	 */
	private static class Lock {}
	private static final Lock[] locks = new Lock[numTables];
	static {
		for(int c=0;c<locks.length;c++) locks[c] = new Lock();
	}

	/**
	 * The internal objects are stored in a <code>HashMap</code>
	 * based on the server and then the table ID, and then the
	 * column number.
	 */
	private static final List<List<Map<Object,GlobalObject>>> tableHashes=new ArrayList<>(numTables);
	static {
		for(int c=0;c<numTables;c++) tableHashes.add(null);
	}
	private static final BitSet[] hashLoadeds=new BitSet[numTables];

	/**
	 * The internal indexes are stored in a <code>HashMap</code>
	 * based on the server and then the table ID, and then the
	 * column number.  Each element of the <code>HashMap</code> is
	 * a <code>ArrayList</code> or <code>AOServObject[]</code>.
	 * All of the List<GlobalObject> stored here are unmodifiable.
	 */
	private static final List<List<Map<Object,List<GlobalObject<?,?>>>>> indexHashes=new ArrayList<>(numTables);
	static {
		for(int c=0;c<numTables;c++) indexHashes.add(null);
	}
	private static final BitSet[] indexLoadeds=new BitSet[numTables];

	/**
	 * The internal objects are stored in this list.  Each of the contained
	 * List<GlobalObject> is unmodifiable.
	 */
	private static final List<List<GlobalObject<?,?>>> tableObjs=new ArrayList<>(numTables);
	static {
		for(int c=0;c<numTables;c++) tableObjs.add(null);
	}

	protected GlobalTable(AOServConnector connector, Class<V> clazz) {
		super(connector, clazz);
	}

	/**
	 * Gets the number of accessible rows in the table or <code>-1</code> if the
	 * table is not yet loaded.
	 */
	public final int getGlobalRowCount() {
		List<GlobalObject<?,?>> objs;
		synchronized(tableObjs) {
			objs=tableObjs.get(getTableID().ordinal());
		}
		if(objs!=null) return objs.size();
		return -1;
	}

	@Override
	@SuppressWarnings({"unchecked"})
	final public List<V> getIndexedRows(int col, Object value) throws IOException, SQLException {
		SchemaTable.TableID tableID=getTableID();
		synchronized(locks[tableID.ordinal()]) {
			validateCache();

			BitSet tableLoadeds=indexLoadeds[tableID.ordinal()];
			if(tableLoadeds==null) indexLoadeds[tableID.ordinal()]=tableLoadeds=new BitSet(col+1);
			boolean isHashed=tableLoadeds.get(col);

			List<Map<Object,List<GlobalObject<?,?>>>> tableValues;
			synchronized(indexHashes) {
				tableValues = indexHashes.get(tableID.ordinal());
				if(tableValues==null) indexHashes.set(tableID.ordinal(), tableValues=new ArrayList<>(col+1));
			}
			while(tableValues.size()<=col) tableValues.add(null);
			Map<Object,List<GlobalObject<?,?>>> colIndexes=tableValues.get(col);
			if(colIndexes==null) tableValues.set(col, colIndexes=new HashMap<>());

			if(!isHashed) {
				// Build the modifiable lists in a temporary Map
				Map<Object,List<GlobalObject<?,?>>> modifiableIndexes=new HashMap<>();
				for(GlobalObject O : getRows()) {
					Object cvalue=O.getColumn(col);
					List<GlobalObject<?,?>> list=modifiableIndexes.get(cvalue);
					if(list==null) modifiableIndexes.put(cvalue, list=new ArrayList<>());
					list.add(O);
				}
				// Wrap each of the newly-created indexes to be unmodifiable
				colIndexes.clear();
				Iterator<Object> keys=modifiableIndexes.keySet().iterator();
				while(keys.hasNext()) {
					Object key=keys.next();
					List<GlobalObject<?,?>> list=modifiableIndexes.get(key);
					colIndexes.put(key, Collections.unmodifiableList(list));
				}
				tableLoadeds.set(col);
			}
			// This returns unmodifable lists.
			List<GlobalObject<?,?>> list=colIndexes.get(value);
			if(list==null) return Collections.emptyList();
			return (List)list;
		}
	}

	@Override
	@SuppressWarnings({"unchecked"})
	final protected V getUniqueRowImpl(int col, Object value) throws SQLException, IOException {
		SchemaTable.TableID tableID=getTableID();
		synchronized(locks[tableID.ordinal()]) {
			validateCache();

			BitSet tableLoadeds=hashLoadeds[tableID.ordinal()];
			if(tableLoadeds==null) hashLoadeds[tableID.ordinal()]=tableLoadeds=new BitSet(col+1);
			boolean isHashed=tableLoadeds.get(col);

			List<V> table=getRows();
			int size=table.size();

			List<Map<Object,GlobalObject>> tableValues;
			synchronized(tableHashes) {
				tableValues = tableHashes.get(tableID.ordinal());
				if(tableValues==null) tableHashes.set(tableID.ordinal(), tableValues=new ArrayList<>(col+1));
			}
			while(tableValues.size()<=col) tableValues.add(null);
			Map<Object,GlobalObject> colValues=tableValues.get(col);
			if(colValues==null) tableValues.set(col, colValues=new HashMap<>(size*13/9));

			if(!isHashed) {
				colValues.clear();
				for(int c=0;c<size;c++) {
					GlobalObject O=table.get(c);
					Object cvalue=O.getColumn(col);
					if(cvalue!=null) {
						GlobalObject old=colValues.put(cvalue, O);
						if(old!=null) throw new SQLException("Duplicate pkey entry for table "+getTableID()+" ("+getTableName()+"), column #"+col+": "+cvalue);
					}
				}

				tableLoadeds.set(col);
			}

			return (V)colValues.get(value);
		}
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public final List<V> getRows() throws IOException, SQLException {
		SchemaTable.TableID tableID = getTableID();
		// We synchronize here to make sure tableObjs is not cleared between validateCache and get, but only on a per-table ID basis
		synchronized(locks[tableID.ordinal()]) {
			validateCache();
			synchronized(tableObjs) {
				List<GlobalObject<?,?>> objs=tableObjs.get(tableID.ordinal());
				return (List)objs;
			}
		}
	}

	/**
	 * Determines if the contents are currently hashed in a hashmap.
	 */
	boolean isHashed(int column) {
		BitSet table=hashLoadeds[getTableID().ordinal()];
		return table!=null && table.get(column);
	}

	/**
	 * Determines if the contents are currently indexed.
	 */
	boolean isIndexed(int column) {
		BitSet table=indexLoadeds[getTableID().ordinal()];
		return table!=null && table.get(column);
	}

	@Override
	final public boolean isLoaded() {
		return lastLoadeds[getTableID().ordinal()]!=-1;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		SchemaTable.TableID tableID=getTableID();
		synchronized(locks[tableID.ordinal()]) {
			lastLoadeds[tableID.ordinal()]=-1;
		}
	}

	/**
	 * Reloads the cache if the cache time has expired.
	 */
	@SuppressWarnings({"unchecked"})
	private void validateCache() throws IOException, SQLException {
		SchemaTable.TableID tableID=getTableID();
		synchronized(locks[tableID.ordinal()]) {
			long currentTime=System.currentTimeMillis();
			long lastLoaded=lastLoadeds[tableID.ordinal()];
			if(lastLoaded==-1) {
				List<GlobalObject<?,?>> list=(List)getObjects(true, AOServProtocol.CommandID.GET_TABLE, tableID.ordinal());
				synchronized(tableObjs) {
					tableObjs.set(tableID.ordinal(), Collections.unmodifiableList(list));
				}
				BitSet loaded=hashLoadeds[tableID.ordinal()];
				if(loaded!=null) loaded.clear();
				BitSet indexed=indexLoadeds[tableID.ordinal()];
				if(indexed!=null) indexed.clear();
				lastLoadeds[tableID.ordinal()]=currentTime;
			}
		}
	}
}
