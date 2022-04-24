/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.collections.AoCollections;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
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
public abstract class GlobalTable<K, V extends GlobalObject<K, V>> extends AOServTable<K, V> {

  private static final int numTables = Table.TableID.values().length;

  /**
   * Each table has its own lock because we were getting deadlocks with one lock on GlobalTable.class.
   */
  private static class Lock {
    // Empty lock class to help heap profile
  }
  private static final Lock[] locks = new Lock[numTables];

  static {
    for (int c = 0; c < locks.length; c++) {
      locks[c] = new Lock();
    }
  }

  /**
   * The last time that the data was loaded, or
   * <code>-1</code> if not yet loaded.
   */
  private static final long[] lastLoadeds = new long[numTables];

  static {
    Arrays.fill(lastLoadeds, -1);
  }

  /**
   * The internal objects are stored in a <code>HashMap</code>
   * based on the server and then the table ID, and then the
   * column number.
   */
  @SuppressWarnings("rawtypes")
  private static final List<List<Map<Object, GlobalObject>>> tableHashes = new ArrayList<>(numTables);

  static {
    for (int c = 0; c < numTables; c++) {
      tableHashes.add(null);
    }
  }

  private static final BitSet[] hashLoadeds = new BitSet[numTables];

  /**
   * The internal indexes are stored in a <code>HashMap</code>
   * based on the server and then the table ID, and then the
   * column number.  Each element of the <code>HashMap</code> is
   * a <code>ArrayList</code> or <code>AOServObject[]</code>.
   * All of the List<GlobalObject> stored here are unmodifiable.
   */
  private static final List<List<Map<Object, List<GlobalObject<?, ?>>>>> indexHashes = new ArrayList<>(numTables);

  static {
    for (int c = 0; c < numTables; c++) {
      indexHashes.add(null);
    }
  }

  private static final BitSet[] indexLoadeds = new BitSet[numTables];

  /**
   * The internal objects are stored in this list.  Each of the contained
   * List<GlobalObject> is unmodifiable.
   */
  private static final List<List<GlobalObject<?, ?>>> tableObjs = new ArrayList<>(numTables);

  static {
    for (int c = 0; c < numTables; c++) {
      tableObjs.add(null);
    }
  }

  protected GlobalTable(AOServConnector connector, Class<V> clazz) {
    super(connector, clazz);
  }

  /**
   * Gets the number of accessible rows in the table or <code>-1</code> if the
   * table is not yet loaded.
   */
  @SuppressWarnings("NestedSynchronizedStatement")
  public final int getGlobalRowCount() {
    int ordinal = getTableID().ordinal();
    List<GlobalObject<?, ?>> objs;
    synchronized (locks[ordinal]) {
      synchronized (tableObjs) {
        objs = tableObjs.get(ordinal);
      }
    }
    if (objs != null) {
      return objs.size();
    }
    return -1;
  }

  @Override
  public final List<V> getIndexedRows(int col, Object value) throws IOException, SQLException {
    Table.TableID tableID = getTableID();
    int ordinal = tableID.ordinal();
    synchronized (locks[ordinal]) {
      validateCache();

      BitSet tableLoadeds = indexLoadeds[ordinal];
      if (tableLoadeds == null) {
        indexLoadeds[ordinal] = tableLoadeds = new BitSet(col + 1);
      }
      boolean isHashed = tableLoadeds.get(col);

      List<Map<Object, List<GlobalObject<?, ?>>>> tableValues;
      synchronized (indexHashes) {
        tableValues = indexHashes.get(ordinal);
        if (tableValues == null) {
          indexHashes.set(ordinal, tableValues = new ArrayList<>(col + 1));
        }
      }
      while (tableValues.size() <= col) {
        tableValues.add(null);
      }
      Map<Object, List<GlobalObject<?, ?>>> colIndexes = tableValues.get(col);
      if (colIndexes == null) {
        tableValues.set(col, colIndexes = new HashMap<>());
      }

      if (!isHashed) {
        // Build the modifiable lists in a temporary Map
        Map<Object, List<GlobalObject<?, ?>>> modifiableIndexes = new HashMap<>();
        for (GlobalObject<K, V> O : getRows()) {
          Object cvalue = O.getColumn(col);
          List<GlobalObject<?, ?>> list = modifiableIndexes.get(cvalue);
          if (list == null) {
            modifiableIndexes.put(cvalue, list = new ArrayList<>());
          }
          list.add(O);
        }
        // Wrap each of the newly-created indexes to be unmodifiable
        colIndexes.clear();
        Iterator<Object> keys = modifiableIndexes.keySet().iterator();
        while (keys.hasNext()) {
          Object key = keys.next();
          List<GlobalObject<?, ?>> list = modifiableIndexes.get(key);
          colIndexes.put(key, Collections.unmodifiableList(list));
        }
        tableLoadeds.set(col);
      }
      // This returns unmodifiable lists.
      @SuppressWarnings("unchecked")
      List<V> list = (List<V>) colIndexes.get(value);
      if (list == null) {
        return Collections.emptyList();
      }
      return list;
    }
  }

  @Override
  protected final V getUniqueRowImpl(int col, Object value) throws SQLException, IOException {
    if (value == null) {
      return null;
    }
    Table.TableID tableID = getTableID();
    int ordinal = tableID.ordinal();
    synchronized (locks[ordinal]) {
      validateCache();

      BitSet tableLoadeds = hashLoadeds[ordinal];
      if (tableLoadeds == null) {
        hashLoadeds[ordinal] = tableLoadeds = new BitSet(col + 1);
      }
      boolean isHashed = tableLoadeds.get(col);

      List<V> table = getRows();
      int size = table.size();

      final List<Map<Object, V>> tableValues;
      synchronized (tableHashes) {
        @SuppressWarnings("unchecked")
        List<Map<Object, V>> existing = (List) tableHashes.get(ordinal);
        if (existing != null) {
          tableValues = existing;
        } else {
          tableValues = new ArrayList<>(col + 1);
          @SuppressWarnings({"unchecked", "rawtypes"})
          List<Map<Object, GlobalObject>> toCache = (List) tableValues;
          tableHashes.set(ordinal, toCache);
        }
      }
      while (tableValues.size() <= col) {
        tableValues.add(null);
      }
      Map<Object, V> colValues = tableValues.get(col);
      // Allow 25% growth before rehash
      if (colValues == null) {
        tableValues.set(col, colValues = AoCollections.newHashMap((size * 5) >> 2));
      }

      if (!isHashed) {
        colValues.clear();
        for (int c = 0; c < size; c++) {
          V row = table.get(c);
          Object cvalue = row.getColumn(col);
          if (cvalue != null) {
            V old = colValues.put(cvalue, row);
            if (old != null) {
              throw new SQLException(
                  "Duplicate pkey entry for table " + getTableID() + " (" + getTableName() + "), column #"
                      + col + ": " + cvalue
              );
            }
          }
        }

        tableLoadeds.set(col);
      }

      return colValues.get(value);
    }
  }

  @Override
  public List<V> getRows() throws IOException, SQLException {
    Table.TableID tableID = getTableID();
    int ordinal = tableID.ordinal();
    // We synchronize here to make sure tableObjs is not cleared between validateCache and get, but only on a per-table ID basis
    synchronized (locks[ordinal]) {
      validateCache();
      synchronized (tableObjs) {
        @SuppressWarnings("unchecked")
        List<V> objs = (List) tableObjs.get(ordinal);
        return objs;
      }
    }
  }

  @Override
  public List<V> getRowsCopy() throws IOException, SQLException {
    return new ArrayList<>(getRows());
  }

  /**
   * Determines if the contents are currently hashed in a hashmap.
   */
  boolean isHashed(int column) {
    int ordinal = getTableID().ordinal();
    synchronized (locks[ordinal]) {
      BitSet table = hashLoadeds[ordinal];
      return table != null && table.get(column);
    }
  }

  /**
   * Determines if the contents are currently indexed.
   */
  boolean isIndexed(int column) {
    int ordinal = getTableID().ordinal();
    synchronized (locks[ordinal]) {
      BitSet table = indexLoadeds[ordinal];
      return table != null && table.get(column);
    }
  }

  @Override
  public final boolean isLoaded() {
    Table.TableID tableID = getTableID();
    int ordinal = tableID.ordinal();
    synchronized (locks[ordinal]) {
      return lastLoadeds[ordinal] != -1;
    }
  }

  @Override
  public void clearCache() {
    super.clearCache();
    Table.TableID tableID = getTableID();
    int ordinal = tableID.ordinal();
    synchronized (locks[ordinal]) {
      lastLoadeds[ordinal] = -1;
    }
  }

  /**
   * Reloads the cache if the cache time has expired.
   */
  @SuppressWarnings({"unchecked"})
  private void validateCache() throws IOException, SQLException {
    Table.TableID tableID = getTableID();
    int ordinal = tableID.ordinal();
    synchronized (locks[ordinal]) {
      long currentTime = System.currentTimeMillis();
      long lastLoaded = lastLoadeds[ordinal];
      if (lastLoaded == -1) {
        List<GlobalObject<?, ?>> list = (List) getObjects(true, AoservProtocol.CommandID.GET_TABLE, ordinal);
        synchronized (tableObjs) {
          tableObjs.set(ordinal, Collections.unmodifiableList(list));
        }
        BitSet loaded = hashLoadeds[ordinal];
        if (loaded != null) {
          loaded.clear();
        }
        BitSet indexed = indexLoadeds[ordinal];
        if (indexed != null) {
          indexed.clear();
        }
        lastLoadeds[ordinal] = currentTime;
      }
    }
  }
}
