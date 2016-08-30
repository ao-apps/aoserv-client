/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
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
	private String owner;
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
		pkey = result.getInt(1);
		name = result.getString(2);
		version = result.getString(3);
		updated = result.getTimestamp(4).getTime();
		owner = result.getString(5);
		operating_system_version = result.getInt(6);
		if(result.wasNull()) operating_system_version = -1;
		Timestamp T = result.getTimestamp(7);
		disable_time = T == null ? -1 : T.getTime();
		disable_reason = result.getString(8);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		name = in.readUTF().intern();
		version = in.readUTF();
		updated = in.readLong();
		owner = in.readUTF().intern();
		operating_system_version = in.readCompressedInt();
		disable_time = in.readLong();
		disable_reason = in.readNullUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeUTF(version);
		out.writeLong(updated);
		out.writeUTF(owner);
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_0_A_108) >= 0) {
			out.writeCompressedInt(operating_system_version);
		}
		if(protocolVersion.compareTo(AOServProtocol.Version.VERSION_1_78) >= 0) {
			out.writeLong(disable_time);
			out.writeNullUTF(disable_reason);
		}
	}
}