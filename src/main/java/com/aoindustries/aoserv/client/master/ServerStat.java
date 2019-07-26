/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2015, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
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
final public class ServerStat extends AOServObject<String,ServerStat> implements SingleTableObject<String,ServerStat> {

	public static final String
		BYTE_ARRAY_CACHE_CREATES = "byte_array_cache_creates",
		BYTE_ARRAY_CACHE_USES = "byte_array_cache_uses",
		BYTE_ARRAY_CACHE_ZERO_FILLS = "byte_array_cache_zero_fills",
		BYTE_ARRAY_CACHE_COLLECTED = "byte_array_cache_collected",
		CHAR_ARRAY_CACHE_CREATES = "char_array_cache_creates",
		CHAR_ARRAY_CACHE_USES="char_array_cache_uses",
		CHAR_ARRAY_CACHE_ZERO_FILLS = "char_array_cache_zero_fills",
		CHAR_ARRAY_CACHE_COLLECTED = "char_array_cache_collected",
		DAEMON_CONCURRENCY = "daemon_concurrency",
		DAEMON_CONNECTIONS = "daemon_connections",
		DAEMON_CONNECTS = "daemon_connects",
		DAEMON_COUNT = "daemon_count",
		DAEMON_DOWN_COUNT = "daemon_down_count",
		DAEMON_MAX_CONCURRENCY = "daemon_max_concurrency",
		DAEMON_POOL_SIZE = "daemon_pool_size",
		DAEMON_TOTAL_TIME = "daemon_total_time",
		DAEMON_TRANSACTIONS = "daemon_transactions",
		DB_CONCURRENCY = "db_concurrency",
		DB_CONNECTIONS = "db_connections",
		DB_CONNECTS = "db_connects",
		DB_MAX_CONCURRENCY = "db_max_concurrency",
		DB_POOL_SIZE = "db_pool_size",
		DB_TOTAL_TIME = "db_total_time",
		DB_TRANSACTIONS = "db_transactions",
		ENTROPY_AVAIL = "entropy_avail",
		ENTROPY_POOLSIZE = "entropy_poolsize",
		ENTROPY_READ_BYTES = "entropy_read_bytes",
		ENTROPY_READ_COUNT = "entropy_read_count",
		ENTROPY_WRITE_BYTES = "entropy_write_bytes",
		ENTROPY_WRITE_COUNT = "entropy_write_count",
		MEMORY_FREE = "memory_free",
		MEMORY_TOTAL = "memory_total",
		// TODO: Coalesce version ranges
		PROTOCOL_VERSION = "protocol_version",
		REQUEST_CONCURRENCY = "request_concurrency",
		REQUEST_CONNECTIONS = "request_connections",
		REQUEST_MAX_CONCURRENCY = "request_max_concurrency",
		REQUEST_TOTAL_TIME = "request_total_time",
		REQUEST_TRANSACTIONS = "request_transactions",
		THREAD_COUNT = "thread_count",
		UPTIME = "uptime"
	;

	String name;
	private String value;
	private String description;
	private AOServTable<String,ServerStat> table;

	public ServerStat() {
	}

	public ServerStat(String name, String value, String description) {
		this.name=name;
		this.value=value;
		this.description=description;
	}

	@Override
	protected Object getColumnImpl(int i) {
		if(i==0) return name;
		if(i==1) return value;
		if(i==2) return description;
		throw new IllegalArgumentException("Invalid index: " + i);
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

	/**
	 * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
	 *
	 * @return  the <code>AOServTable</code>.
	 */
	@Override
	public AOServTable<String,ServerStat> getTable() {
		return table;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MASTER_SERVER_STATS;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		throw new SQLException("Should not be read from the database, should be generated.");
	}

	@Override
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		name=in.readUTF().intern();
		value=in.readNullUTF();
		description=in.readUTF();
	}

	@Override
	public void setTable(AOServTable<String,ServerStat> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(name);
		out.writeNullUTF(value);
		out.writeUTF(description);
	}
}
