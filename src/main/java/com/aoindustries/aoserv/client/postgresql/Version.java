/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2002-2009, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.postgresql;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.distribution.SoftwareVersion;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>PostgresVersion</code> flags which <code>TechnologyVersion</code>s
 * are a version of PostgreSQL.
 *
 * @see  Server
 * @see  SoftwareVersion
 *
 * @author  AO Industries, Inc.
 */
final public class Version extends GlobalObjectIntegerKey<Version> {

	static final int COLUMN_VERSION = 0;
	static final String COLUMN_MINOR_VERSION_name = "minor_version";
	static final String COLUMN_VERSION_name = "version";

	private String minorVersion;
	private int postgisVersion;

	public static final String TECHNOLOGY_NAME = "postgresql";

	public static final String
		VERSION_7_1 = "7.1",
		VERSION_7_2 = "7.2",
		VERSION_7_3 = "7.3",
		VERSION_8_0 = "8.0",
		VERSION_8_1 = "8.1",
		VERSION_8_3 = "8.3",
		VERSION_9_2 = "9.2",
		VERSION_9_4 = "9.4",
		VERSION_9_5 = "9.5",
		VERSION_9_6 = "9.6",
		VERSION_10 = "10",
		VERSION_11 = "11"
	;

	/**
	 * Gets the versions of PostgreSQL in order of
	 * preference.  Index <code>0</code> is the most
	 * preferred.
	 * <p>
	 * TODO: Rename "getPreferredMajorVersions
	 * </p>
	 */
	public static String[] getPreferredMinorVersions() {
		return new String[] {
			VERSION_11,
			VERSION_10,
			VERSION_9_6,
			VERSION_9_5,
			VERSION_9_4,
			VERSION_9_2,
			VERSION_8_3,
			VERSION_8_1,
			VERSION_8_0,
			VERSION_7_3,
			VERSION_7_2,
			VERSION_7_1
		};
	}

	/**
	 * Checks if a version of PostgreSQL supports <a href="https://www.postgresql.org/docs/10/auth-methods.html#AUTH-PASSWORD">scram-sha-256 authentication</a>.
	 * This is added as of PostgreSQL 10.
	 */
	public boolean isScramSha256(String version) {
		return
			version.startsWith(VERSION_10 + '.')
			|| version.startsWith(VERSION_10 + 'R')
			|| version.startsWith(VERSION_11 + '.')
			|| version.startsWith(VERSION_11 + 'R');
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_VERSION: return pkey;
			case 1: return minorVersion;
			case 2: return postgisVersion == -1 ? null : postgisVersion;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public String getMinorVersion() {
		return minorVersion;
	}

	/**
	 * Gets the PostGIS version of <code>null</code> if not supported by this PostgreSQL version....
	 */
	public SoftwareVersion getPostgisVersion(AOServConnector connector) throws SQLException, IOException {
		if(postgisVersion == -1) return null;
		SoftwareVersion tv = connector.getDistribution().getSoftwareVersion().get(postgisVersion);
		if(tv == null) throw new SQLException("Unable to find TechnologyVersion: " + postgisVersion);
		if(
			tv.getOperatingSystemVersion(connector).getPkey()
			!= getTechnologyVersion(connector).getOperatingSystemVersion(connector).getPkey()
		) {
			throw new SQLException("postgresql/postgis version mismatch on PostgresVersion: #" + pkey);
		}
		return tv;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.POSTGRES_VERSIONS;
	}

	public List<Encoding> getPostgresEncodings(AOServConnector connector) throws IOException, SQLException {
		return connector.getPostgresql().getEncoding().getPostgresEncodings(this);
	}

	public Encoding getPostgresEncoding(AOServConnector connector, String encoding) throws IOException, SQLException {
		return connector.getPostgresql().getEncoding().getPostgresEncoding(this, encoding);
	}

	public SoftwareVersion getTechnologyVersion(AOServConnector connector) throws SQLException, IOException {
		SoftwareVersion obj = connector.getDistribution().getSoftwareVersion().get(pkey);
		if(obj == null) throw new SQLException("Unable to find TechnologyVersion: "+pkey);
		return obj;
	}

	/**
	 * @see  #isScramSha256(java.lang.String)
	 */
	public boolean isScramSha256(AOServConnector connector) throws SQLException, IOException {
		return isScramSha256(getTechnologyVersion(connector).getVersion());
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		minorVersion = result.getString(2);
		postgisVersion = result.getInt(3);
		if(result.wasNull()) postgisVersion = -1;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		minorVersion = in.readUTF().intern();
		postgisVersion = in.readCompressedInt();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_109) <= 0) out.writeCompressedInt(Server.DEFAULT_PORT.getPort());
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_121) >= 0) out.writeUTF(minorVersion);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_27) >= 0) out.writeCompressedInt(postgisVersion);
	}
}
