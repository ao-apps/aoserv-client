/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.schema;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalTableIntegerKey;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see  ForeignKey
 *
 * @author  AO Industries, Inc.
 */
public final class ForeignKeyTable extends GlobalTableIntegerKey<ForeignKey> {

  private static final Map<String, List<ForeignKey>> tableKeys=new HashMap<>();
  private static final Map<Integer, List<ForeignKey>> referencesHash=new HashMap<>();
  private static final Map<Integer, List<ForeignKey>> referencedByHash=new HashMap<>();

  ForeignKeyTable(AOServConnector connector) {
    super(connector, ForeignKey.class);
  }

  private static final OrderBy[] defaultOrderBy = {
    new OrderBy(ForeignKey.COLUMN_ID_name, ASCENDING)
  };
  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  protected OrderBy[] getDefaultOrderBy() {
    return defaultOrderBy;
  }

  @Override
  public void clearCache() {
    super.clearCache();
    synchronized (ForeignKeyTable.class) {
      tableKeys.clear();
      referencesHash.clear();
      referencedByHash.clear();
    }
  }

  @Override
  public ForeignKey get(int pkey) throws IOException, SQLException {
    return getUniqueRow(ForeignKey.COLUMN_ID, pkey);
  }

  List<ForeignKey> getSchemaForeignKeys(Table table) throws IOException, SQLException {
    synchronized (ForeignKeyTable.class) {
      if (tableKeys.isEmpty()) {
        List<ForeignKey> cached=getRows();
        int size=cached.size();
        for (int c=0;c<size;c++) {
          ForeignKey key=cached.get(c);
          String tableName=key.getColumn(connector).getTable_name();
          List<ForeignKey> keys=tableKeys.get(tableName);
          if (keys == null) {
            tableKeys.put(tableName, keys=new ArrayList<>());
          }
          keys.add(key);
        }
      }
      List<ForeignKey> matches=tableKeys.get(table.getName());
      if (matches != null) {
        return matches;
      }
      return Collections.emptyList();
    }
  }

  private void rebuildReferenceHashes() throws IOException, SQLException {
    if (
      referencedByHash.isEmpty()
      || referencesHash.isEmpty()
    ) {
      // All methods that call this are already synched
      List<ForeignKey> cached=getRows();
      int size=cached.size();
      for (int c=0;c<size;c++) {
        ForeignKey key=cached.get(c);
        Integer keyColumnPKey = key.getColumn_id();
        Integer foreignColumnPKey=key.getForeignColumn_id();

        // Referenced By
        List<ForeignKey> referencedBy=referencedByHash.get(keyColumnPKey);
        if (referencedBy == null) {
          referencedByHash.put(keyColumnPKey, referencedBy=new ArrayList<>());
        }
        referencedBy.add(key);

        // References
        List<ForeignKey> references=referencesHash.get(foreignColumnPKey);
        if (references == null) {
          referencesHash.put(foreignColumnPKey, references=new ArrayList<>());
        }
        references.add(key);
      }
    }
  }

  List<ForeignKey> getSchemaForeignKeysReferencedBy(Column column) throws IOException, SQLException {
    synchronized (ForeignKeyTable.class) {
      rebuildReferenceHashes();
      List<ForeignKey> matches=referencedByHash.get(column.getPkey());
      if (matches != null) {
        return matches;
      } else {
        return Collections.emptyList();
      }
    }
  }

  List<ForeignKey> getSchemaForeignKeysReferencing(Column column) throws IOException, SQLException {
    synchronized (ForeignKeyTable.class) {
      rebuildReferenceHashes();
      List<ForeignKey> matches=referencesHash.get(column.getPkey());
      if (matches != null) {
        return matches;
      } else {
        return Collections.emptyList();
      }
    }
  }

  @Override
  public Table.TableID getTableID() {
    return Table.TableID.SCHEMA_FOREIGN_KEYS;
  }
}
