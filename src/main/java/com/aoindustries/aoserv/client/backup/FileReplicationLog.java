/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.backup;

import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.sql.UnmodifiableTimestamp;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The entire contents of servers are periodically replicated to another server.  In the
 * event of hardware failure, this other server may be booted to take place of the
 * failed machine.  All transfers to the failover server are logged.
 *
 * @author  AO Industries, Inc.
 */
final public class FileReplicationLog extends AOServObject<Integer,FileReplicationLog> implements SingleTableObject<Integer,FileReplicationLog> {

	static final String COLUMN_REPLICATION_name = "replication";
	static final String COLUMN_END_TIME_name = "end_time";

	private AOServTable<Integer,FileReplicationLog> table;

	private int pkey;
	private int replication;
	private UnmodifiableTimestamp startTime;
	private UnmodifiableTimestamp endTime;
	private int scanned;
	private int updated;
	private long bytes;
	private boolean is_successful;

	@Override
	public boolean equals(Object O) {
		return
			O instanceof FileReplicationLog
			&& ((FileReplicationLog)O).pkey==pkey
		;
	}

	public long getBytes() {
		return bytes;
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case 0: return pkey;
			case 1: return replication;
			case 2: return startTime;
			case 3: return endTime;
			case 4: return scanned;
			case 5: return updated;
			case 6: return bytes;
			case 7: return is_successful;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public UnmodifiableTimestamp getStartTime() {
		return startTime;
	}

	public UnmodifiableTimestamp getEndTime() {
		return endTime;
	}

	public int getPkey() {
		return pkey;
	}

	@Override
	public Integer getKey() {
		return pkey;
	}

	public int getScanned() {
		return scanned;
	}

	public FileReplication getFailoverFileReplication() throws SQLException, IOException {
		FileReplication ffr=table.getConnector().getBackup().getFileReplication().get(replication);
		if(ffr==null) throw new SQLException("Unable to find FailoverFileReplication: "+replication);
		return ffr;
	}

	/**
	 * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
	 *
	 * @return  the <code>AOServTable</code>.
	 */
	@Override
	public AOServTable<Integer,FileReplicationLog> getTable() {
		return table;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.FAILOVER_FILE_LOG;
	}

	public int getUpdated() {
		return updated;
	}

	@Override
	public int hashCode() {
		return pkey;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		replication=result.getInt(2);
		startTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(3));
		endTime = UnmodifiableTimestamp.valueOf(result.getTimestamp(4));
		scanned=result.getInt(5);
		updated=result.getInt(6);
		bytes=result.getLong(7);
		is_successful=result.getBoolean(8);
	}

	public boolean isSuccessful() {
		return is_successful;
	}

	@Override
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readCompressedInt();
		replication=in.readCompressedInt();
		startTime = in.readUnmodifiableTimestamp();
		endTime = in.readUnmodifiableTimestamp();
		scanned=in.readCompressedInt();
		updated=in.readCompressedInt();
		bytes=in.readLong();
		is_successful=in.readBoolean();
	}

	@Override
	public void setTable(AOServTable<Integer,FileReplicationLog> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(replication);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(startTime.getTime());
			out.writeLong(endTime.getTime());
		} else {
			out.writeTimestamp(startTime);
			out.writeTimestamp(endTime);
		}
		out.writeCompressedInt(scanned);
		out.writeCompressedInt(updated);
		out.writeLong(bytes);
		out.writeBoolean(is_successful);
	}
}
