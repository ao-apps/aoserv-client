/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Each <code>TechnologyName</code> may have multiple versions installed.
 * Each of those versions is a <code>TechnologyVersion</code>.
 *
 * @see  TechnologyName
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyVersion extends GlobalObjectIntegerKey<TechnologyVersion> {

	static final int COLUMN_PKEY = 0;
	static final String COLUMN_VERSION_name = "version";
	static final String COLUMN_NAME_name = "name";

	String name, version;
	long updated;
	private UserId owner;
	int operating_system_version;
	private long disable_time;
	private String disable_reason;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return name;
			case 2: return version;
			case 3: return getUpdated();
			case 4: return owner;
			case 5: return operating_system_version == -1 ? null : operating_system_version;
			case 6: return getDisableTime();
			case 7: return disable_reason;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public HttpdTomcatVersion getHttpdTomcatVersion(AOServConnector connector) throws IOException, SQLException {
		return connector.getHttpdTomcatVersions().get(pkey);
	}

	public MasterUser getOwner(AOServConnector connector) throws SQLException, IOException {
		// May be filtered
		if(owner == null) return null;
		MasterUser obj = connector.getMasterUsers().get(owner);
		if (obj == null) throw new SQLException("Unable to find MasterUser: " + owner);
		return obj;
	}

	public OperatingSystemVersion getOperatingSystemVersion(AOServConnector conn) throws SQLException, IOException {
		OperatingSystemVersion osv=conn.getOperatingSystemVersions().get(operating_system_version);
		if(osv==null) throw new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version);
		return osv;
	}

	/**
	 * Checks if enabled at the given time.
	 */
	public boolean isEnabled(long time) {
		return disable_time == -1 || disable_time > time;
	}

	public Timestamp getDisableTime() {
		return new Timestamp(disable_time);
	}

	public String getDisableReason() {
		return disable_reason;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TECHNOLOGY_VERSIONS;
	}

	public TechnologyName getTechnologyName(AOServConnector connector) throws SQLException, IOException {
		TechnologyName technologyName = connector.getTechnologyNames().get(name);
		if (technologyName == null) throw new SQLException("Unable to find TechnologyName: " + name);
		return technologyName;
	}

	public Timestamp getUpdated() {
		return new Timestamp(updated);
	}

	public String getVersion() {
		return version;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			name = result.getString(2);
			version = result.getString(3);
			updated = result.getTimestamp(4).getTime();
			{
				String s = result.getString(5);
				owner = AOServProtocol.FILTERED.equals(s) ? null : UserId.valueOf(s);
			}
			operating_system_version = result.getInt(6);
			if(result.wasNull()) operating_system_version = -1;
			Timestamp T = result.getTimestamp(7);
			disable_time = T == null ? -1 : T.getTime();
			disable_reason = result.getString(8);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			name = in.readUTF().intern();
			version = in.readUTF();
			updated = in.readLong();
			{
				String s = in.readUTF();
				if(AOServProtocol.FILTERED.equals(s)) {
					owner = null;
				} else {
					owner = UserId.valueOf(s).intern();
				}
			}
			operating_system_version = in.readCompressedInt();
			disable_time = in.readLong();
			disable_reason = in.readNullUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeUTF(version);
		out.writeLong(updated);
		out.writeUTF(owner==null ? AOServProtocol.FILTERED : owner.toString());
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_108) >= 0) {
			out.writeCompressedInt(operating_system_version);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_78) >= 0) {
			out.writeLong(disable_time);
			out.writeNullUTF(disable_reason);
		}
	}
}
