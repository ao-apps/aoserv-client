/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoindustries.aoserv.client.backup;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>FailoverFileSchedule</code> controls which time of day (in server
 * time zone) the failover file replications will occur.
 *
 * @author  AO Industries, Inc.
 */
public final class FileReplicationSchedule extends CachedObjectIntegerKey<FileReplicationSchedule> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_REPLICATION=1
	;
	static final String COLUMN_REPLICATION_name = "replication";
	static final String COLUMN_HOUR_name = "hour";
	static final String COLUMN_MINUTE_name = "minute";

	private int replication;
	private short hour;
	private short minute;
	private boolean enabled;

	/**
	 * @deprecated  Only required for implementation, do not use directly.
	 *
	 * @see  #init(java.sql.ResultSet)
	 * @see  #read(com.aoapps.hodgepodge.io.stream.StreamableInput, com.aoindustries.aoserv.client.schema.AoservProtocol.Version)
	 */
	@Deprecated/* Java 9: (forRemoval = true) */
	public FileReplicationSchedule() {
		// Do nothing
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_REPLICATION: return replication;
			case 2: return hour;
			case 3: return minute;
			case 4: return enabled;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public FileReplication getFailoverFileReplication() throws SQLException, IOException {
		FileReplication ffr=table.getConnector().getBackup().getFileReplication().get(replication);
		if(ffr==null) throw new SQLException("Unable to find FailoverFileReplication: "+replication);
		return ffr;
	}

	public short getHour() {
		return hour;
	}

	public short getMinute() {
		return minute;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.FAILOVER_FILE_SCHEDULE;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey=result.getInt(1);
		replication=result.getInt(2);
		hour=result.getShort(3);
		minute=result.getShort(4);
		enabled=result.getBoolean(5);
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readCompressedInt();
		replication=in.readCompressedInt();
		hour=in.readShort();
		minute=in.readShort();
		enabled=in.readBoolean();
	}

	@Override
	public String toStringImpl() throws SQLException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(getFailoverFileReplication().toStringImpl());
		sb.append('@');
		if(hour < 10) sb.append('0');
		sb.append(hour);
		sb.append(':');
		if(minute < 10) sb.append('0');
		sb.append(minute);
		return sb.toString();
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(replication);
		out.writeShort(hour);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_31)>=0) out.writeShort(minute);
		out.writeBoolean(enabled);
	}
}
