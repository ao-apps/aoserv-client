/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * The entire contents of servers are periodically replicated to another server.  In the
 * event of hardware failure, this other server may be booted to take place of the
 * failed machine.  All transfers to the failover server are logged.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileLog extends AOServObject<Integer,FailoverFileLog> implements SingleTableObject<Integer,FailoverFileLog> {

	static final String COLUMN_REPLICATION_name = "replication";
	static final String COLUMN_END_TIME_name = "end_time";

	private AOServTable<Integer,FailoverFileLog> table;

	private int pkey;
	private int replication;
	private long startTime;
	private long endTime;
	private int scanned;
	private int updated;
	private long bytes;
	private boolean is_successful;

	@Override
	public boolean equals(Object O) {
		return
			O instanceof FailoverFileLog
			&& ((FailoverFileLog)O).pkey==pkey
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
			case 2: return getStartTime();
			case 3: return getEndTime();
			case 4: return scanned;
			case 5: return updated;
			case 6: return bytes;
			case 7: return is_successful;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public Timestamp getStartTime() {
		return new Timestamp(startTime);
	}

	public Timestamp getEndTime() {
		return new Timestamp(endTime);
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

	public FailoverFileReplication getFailoverFileReplication() throws SQLException, IOException {
		FailoverFileReplication ffr=table.getConnector().getFailoverFileReplications().get(replication);
		if(ffr==null) throw new SQLException("Unable to find FailoverFileReplication: "+replication);
		return ffr;
	}

	/**
	 * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
	 *
	 * @return  the <code>AOServTable</code>.
	 */
	@Override
	public AOServTable<Integer,FailoverFileLog> getTable() {
		return table;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FAILOVER_FILE_LOG;
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
		startTime=result.getTimestamp(3).getTime();
		endTime=result.getTimestamp(4).getTime();
		scanned=result.getInt(5);
		updated=result.getInt(6);
		bytes=result.getLong(7);
		is_successful=result.getBoolean(8);
	}

	public boolean isSuccessful() {
		return is_successful;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readCompressedInt();
		replication=in.readCompressedInt();
		startTime=in.readLong();
		endTime=in.readLong();
		scanned=in.readCompressedInt();
		updated=in.readCompressedInt();
		bytes=in.readLong();
		is_successful=in.readBoolean();
	}

	@Override
	public void setTable(AOServTable<Integer,FailoverFileLog> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(replication);
		out.writeLong(startTime);
		out.writeLong(endTime);
		out.writeCompressedInt(scanned);
		out.writeCompressedInt(updated);
		out.writeLong(bytes);
		out.writeBoolean(is_successful);
	}
}
