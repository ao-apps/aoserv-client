/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.master;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AoservObject;
import com.aoindustries.aoserv.client.AoservTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * To aid in system reliability, scalability, and debugging, many server
 * runtime statistics are maintained.  <code>MasterServerStat</code>
 * provides table-like access to this data.
 *
 * @author  AO Industries, Inc.
 */
public final class ServerStat extends AoservObject<String, ServerStat> implements SingleTableObject<String, ServerStat> {

  public static final String BYTE_ARRAY_CACHE_CREATES = "byte_array_cache_creates";
  public static final String BYTE_ARRAY_CACHE_USES = "byte_array_cache_uses";
  public static final String BYTE_ARRAY_CACHE_ZERO_FILLS = "byte_array_cache_zero_fills";
  public static final String BYTE_ARRAY_CACHE_COLLECTED = "byte_array_cache_collected";
  public static final String CHAR_ARRAY_CACHE_CREATES = "char_array_cache_creates";
  public static final String CHAR_ARRAY_CACHE_USES = "char_array_cache_uses";
  public static final String CHAR_ARRAY_CACHE_ZERO_FILLS = "char_array_cache_zero_fills";
  public static final String CHAR_ARRAY_CACHE_COLLECTED = "char_array_cache_collected";
  public static final String DAEMON_CONCURRENCY = "daemon_concurrency";
  public static final String DAEMON_CONNECTIONS = "daemon_connections";
  public static final String DAEMON_CONNECTS = "daemon_connects";
  public static final String DAEMON_COUNT = "daemon_count";
  public static final String DAEMON_DOWN_COUNT = "daemon_down_count";
  public static final String DAEMON_MAX_CONCURRENCY = "daemon_max_concurrency";
  public static final String DAEMON_POOL_SIZE = "daemon_pool_size";
  public static final String DAEMON_TOTAL_TIME = "daemon_total_time";
  public static final String DAEMON_TRANSACTIONS = "daemon_transactions";
  public static final String DB_CONCURRENCY = "db_concurrency";
  public static final String DB_CONNECTIONS = "db_connections";
  public static final String DB_CONNECTS = "db_connects";
  public static final String DB_MAX_CONCURRENCY = "db_max_concurrency";
  public static final String DB_POOL_SIZE = "db_pool_size";
  public static final String DB_TOTAL_TIME = "db_total_time";
  public static final String DB_TRANSACTIONS = "db_transactions";
  public static final String ENTROPY_AVAIL = "entropy_avail";
  public static final String ENTROPY_POOLSIZE = "entropy_poolsize";
  public static final String ENTROPY_READ_BYTES = "entropy_read_bytes";
  public static final String ENTROPY_READ_COUNT = "entropy_read_count";
  public static final String ENTROPY_WRITE_BYTES = "entropy_write_bytes";
  public static final String ENTROPY_WRITE_COUNT = "entropy_write_count";
  public static final String MEMORY_FREE = "memory_free";
  public static final String MEMORY_TOTAL = "memory_total";
  // TODO: Coalesce version ranges
  public static final String PROTOCOL_VERSION = "protocol_version";
  public static final String REQUEST_CONCURRENCY = "request_concurrency";
  public static final String REQUEST_CONNECTIONS = "request_connections";
  public static final String REQUEST_MAX_CONCURRENCY = "request_max_concurrency";
  public static final String REQUEST_TOTAL_TIME = "request_total_time";
  public static final String REQUEST_TRANSACTIONS = "request_transactions";
  public static final String THREAD_COUNT = "thread_count";
  public static final String UPTIME = "uptime";

  static final int COLUMN_NAME = 0;

  private String name;
  private String value;
  private String description;
  private AoservTable<String, ServerStat> table;

  /**
   * @deprecated  Only required for implementation, do not use directly.
   *
   * @see  #init(java.sql.ResultSet)
   * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
   */
  @Deprecated // Java 9: (forRemoval = true)
  public ServerStat() {
    // Do nothing
  }

  /**
   * @deprecated  Only required for implementation, do not use directly.
   */
  @Deprecated // Java 9: (forRemoval = true)
  // Only used by aoserv-master
  public ServerStat(String name, String value, String description) {
    this.name = name;
    this.value = value;
    this.description = description;
  }

  @Override
  protected Object getColumnImpl(int i) {
    switch (i) {
      case COLUMN_NAME:
        return name;
      case 1:
        return value;
      case 2:
        return description;
      default:
        throw new IllegalArgumentException("Invalid index: " + i);
    }
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getKey() {
    return name;
  }

  @Override
  public AoservTable<String, ServerStat> getTable() {
    return table;
  }

  @Override
  public Table.TableId getTableId() {
    return Table.TableId.MASTER_SERVER_STATS;
  }

  public String getValue() {
    return value;
  }

  @Override
  public void init(ResultSet result) throws SQLException {
    throw new SQLException("Should not be read from the database, should be generated.");
  }

  @Override
  public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
    name = in.readUTF().intern();
    value = in.readNullUTF();
    description = in.readUTF();
  }

  @Override
  public void setTable(AoservTable<String, ServerStat> table) {
    if (this.table != null) {
      throw new IllegalStateException("table already set");
    }
    this.table = table;
  }

  @Override
  public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
    out.writeUTF(name);
    out.writeNullUTF(value);
    out.writeUTF(description);
  }
}
