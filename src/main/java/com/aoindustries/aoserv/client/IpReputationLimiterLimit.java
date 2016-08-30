/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2012, 2016  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The limit for one class of a <code>IpReputationLimiter</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationLimiterLimit extends CachedObjectIntegerKey<IpReputationLimiterLimit> {

	static final int
		COLUMN_PKEY    = 0,
		COLUMN_LIMITER = 1
	;
	static final String
		COLUMN_LIMITER_name = "limiter",
		COLUMN_CLASS_name = "class"
	;

	/**
	 * The set of possible classes
	 */
	public enum LimiterClass {
		/**
		 * Manual Good
		 */
		gm,

		/**
		 * Manual Bad
		 */
		bm,

		/**
		 * Definite Bad
		 */
		bd,

		/**
		 * Uncertain Bad
		 */
		bu,

		/**
		 * Uncertain Good
		 */
		gu,

		/**
		 * Definite Good
		 */
		gd,

		/**
		 * Network Good
		 */
		gn,

		/**
		 * Unknown
		 */
		uu
	}

	/**
	 * The set of possible units
	 */
	public enum TimeUnit {
		second,
		minute,
		hour,
		day
	}

			int          limiter;
	private LimiterClass clazz;
	private short        synPerIpBurst;
	private short        synPerIpRate;
	private TimeUnit     synPerIpUnit;
	private short        synPerIpSize;
	private short        synBurst;
	private short        synRate;
	private TimeUnit     synUnit;
	private int          packetPerIpBurst;
	private int          packetPerIpRate;
	private TimeUnit     packetPerIpUnit;
	private int          packetPerIpSize;
	private int          packetBurst;
	private int          packetRate;
	private TimeUnit     packetUnit;

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.IP_REPUTATION_LIMITER_LIMITS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey             = result.getInt(pos++);
		limiter          = result.getInt(pos++);
		clazz            = LimiterClass.valueOf(result.getString(pos++));
		synPerIpBurst    = result.getShort(pos++);
		synPerIpRate     = result.getShort(pos++);
		synPerIpUnit     = TimeUnit.valueOf(result.getString(pos++));
		synPerIpSize     = result.getShort(pos++);
		synBurst         = result.getShort(pos++);
		synRate          = result.getShort(pos++);
		synUnit          = TimeUnit.valueOf(result.getString(pos++));
		packetPerIpBurst = result.getInt(pos++);
		packetPerIpRate  = result.getInt(pos++);
		packetPerIpUnit  = TimeUnit.valueOf(result.getString(pos++));
		packetPerIpSize  = result.getInt(pos++);
		packetBurst      = result.getInt(pos++);
		packetRate       = result.getInt(pos++);
		packetUnit       = TimeUnit.valueOf(result.getString(pos++));
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(limiter);
		out.writeUTF          (clazz.name());
		out.writeShort        (synPerIpBurst);
		out.writeShort        (synPerIpRate);
		out.writeUTF          (synPerIpUnit.name());
		out.writeShort        (synPerIpSize);
		out.writeShort        (synBurst);
		out.writeShort        (synRate);
		out.writeUTF          (synUnit.name());
		out.writeInt          (packetPerIpBurst);
		out.writeInt          (packetPerIpRate);
		out.writeUTF          (packetPerIpUnit.name());
		out.writeInt          (packetPerIpSize);
		out.writeInt          (packetBurst);
		out.writeInt          (packetRate);
		out.writeUTF          (packetUnit.name());
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey             = in.readCompressedInt();
		limiter          = in.readCompressedInt();
		clazz            = LimiterClass.valueOf(in.readUTF());
		synPerIpBurst    = in.readShort();
		synPerIpRate     = in.readShort();
		synPerIpUnit     = TimeUnit.valueOf(in.readUTF());
		synPerIpSize     = in.readShort();
		synBurst         = in.readShort();
		synRate          = in.readShort();
		synUnit          = TimeUnit.valueOf(in.readUTF());
		packetPerIpBurst = in.readInt();
		packetPerIpRate  = in.readInt();
		packetPerIpUnit  = TimeUnit.valueOf(in.readUTF());
		packetPerIpSize  = in.readInt();
		packetBurst      = in.readInt();
		packetRate       = in.readInt();
		packetUnit       = TimeUnit.valueOf(in.readUTF());
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY     : return pkey;
			case COLUMN_LIMITER  : return limiter;
			case 2               : return clazz.name();
			case 3               : return synPerIpBurst;
			case 4               : return synPerIpRate;
			case 5               : return synPerIpUnit.name();
			case 6               : return synPerIpSize;
			case 7               : return synBurst;
			case 8               : return synRate;
			case 9               : return synUnit.name();
			case 10              : return packetPerIpBurst;
			case 11              : return packetPerIpRate;
			case 12              : return packetPerIpUnit.name();
			case 13              : return packetPerIpSize;
			case 14              : return packetBurst;
			case 15              : return packetRate;
			case 16              : return packetUnit.name();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public IpReputationLimiter getLimiter() throws SQLException, IOException {
		IpReputationLimiter obj = table.connector.getIpReputationLimiters().get(limiter);
		if(obj==null) throw new SQLException("Unable to find IpReputationLimiter: " + limiter);
		return obj;
	}

	/**
	 * Gets the class of this limit.
	 */
	public LimiterClass getLimiterClass() {
		return clazz;
	}

	public short getSynPerIpBurst() {
		return synPerIpBurst;
	}

	public short getSynPerIpRate() {
		return synPerIpRate;
	}

	public TimeUnit getSynPerIpUnit() {
		return synPerIpUnit;
	}

	public short getSynPerIpSize() {
		return synPerIpSize;
	}

	public short getSynBurst() {
		return synBurst;
	}

	public short getSynRate() {
		return synRate;
	}

	public TimeUnit getSynUnit() {
		return synUnit;
	}

	public int getPacketPerIpBurst() {
		return packetPerIpBurst;
	}

	public int getPacketPerIpRate() {
		return packetPerIpRate;
	}

	public TimeUnit getPacketPerIpUnit() {
		return packetPerIpUnit;
	}

	public int getPacketPerIpSize() {
		return packetPerIpSize;
	}

	public int getPacketBurst() {
		return packetBurst;
	}

	public int getPacketRate() {
		return packetRate;
	}

	public TimeUnit getPacketUnit() {
		return packetUnit;
	}
}
