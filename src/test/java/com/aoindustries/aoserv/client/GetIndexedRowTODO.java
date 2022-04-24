/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2006-2009, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.Column;
import com.aoindustries.aoserv.client.schema.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the aoserv-client object indexing algorithms for accuracy.
 *
 * TODO: This test does not run without a master setup.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class GetIndexedRowTODO extends TestCase {

  private List<AOServConnector> conns;

  public GetIndexedRowTODO(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    conns = AOServConnectorTODO.getTestConnectors();
  }

  @Override
  protected void tearDown() throws Exception {
    conns = null;
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(GetIndexedRowTODO.class);

    return suite;
  }

  /**
   * Test the size() method of each AOServTable.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testGetIndexedRows() throws Exception {
    System.out.println("Testing all indexed rows:");
    System.out.println("+ means supported");
    System.out.println("- means unsupported");
    for (AOServConnector conn : conns) {
      User.Name username = conn.getCurrentAdministrator().getKey();
      System.out.println("    " + username);
      int numTables = Table.TableID.values().length;
      for (int c = 0; c < numTables; c++) {
        // Excluded for testing speed
        if (
            c == Table.TableID.DISTRO_FILES.ordinal()
                || c == Table.TableID.TRANSACTIONS.ordinal()
                || c == Table.TableID.WhoisHistory.ordinal() // TODO: Just exclude output/error columns?
        ) {
          continue;
        }
        AOServTable table = conn.getTable(c);
        String tableName = table.getTableName();
        System.out.print("        " + tableName + ": ");
        List<AOServObject> rows = table.getRows();
        if (rows.isEmpty()) {
          System.out.println("Empty table, cannot test");
        } else {
          List<Column> columns = table.getTableSchema().getSchemaColumns(conn);
          Map<Object, List<AOServObject>> expectedLists = new HashMap<>();
          for (Column column : columns) {
            boolean supported = true;
            String columnName = column.getName();
            try {
              int colIndex = column.getIndex();
              // Build our list of the expected objects by iterating through the entire list
              expectedLists.clear();
              for (AOServObject row : rows) {
                Object value = row.getColumn(colIndex);
                // null values are not indexed
                if (value != null) {
                  List<AOServObject> list = expectedLists.get(value);
                  if (list == null) {
                    expectedLists.put(value, list = new ArrayList<>());
                  }
                  list.add(row);
                }
              }
              // Compare to the lists using the index routines
              for (Object value : expectedLists.keySet()) {
                List<AOServObject> expectedList = expectedLists.get(value);
                List<AOServObject> indexedRows = table.getIndexedRows(colIndex, value);
                assertEquals(tableName + "." + columnName + "=" + value + ": Mismatch in list size: ", expectedList.size(), indexedRows.size());
                if (!expectedList.containsAll(indexedRows)) {
                  fail(tableName + "." + columnName + "=" + value + ": expectedList does not contain all the rows of indexedRows");
                }
                if (!indexedRows.containsAll(expectedList)) {
                  fail(tableName + "." + columnName + "=" + value + ": indexedRows does not contain all the rows of expectedList");
                }
              }
            } catch (UnsupportedOperationException err) {
              supported = false;
            } catch (RuntimeException err) {
              System.out.println("RuntimeException tableName=" + tableName + ", columnName=" + columnName);
              throw err;
            }
            System.out.print(supported ? '+' : '-');
          }
          System.out.println();
        }
      }
    }
  }
}
