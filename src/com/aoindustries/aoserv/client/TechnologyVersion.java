package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * Each <code>TechnologyName</code> may have multiple versions installed.
 * Each of those versions is a <code>TechnologyVersion</code>.
 *
 * @see  TechnologyName
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyVersion extends GlobalObjectIntegerKey<TechnologyVersion> {

    static final int COLUMN_PKEY=0;

    String name, version;
    long updated;
    private String owner;
    int operating_system_version;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case 2: return version;
            case 3: return new java.sql.Date(updated);
            case 4: return owner;
            case 5: return operating_system_version==-1?null:Integer.valueOf(operating_system_version);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdTomcatVersion getHttpdTomcatVersion(AOServConnector connector) {
	return connector.httpdTomcatVersions.get(pkey);
    }

    public MasterUser getOwner(AOServConnector connector) {
	MasterUser obj = connector.masterUsers.get(owner);
	if (obj == null) throw new WrappedException(new SQLException("Unable to find MasterUser: " + owner));
	return obj;
    }

    public OperatingSystemVersion getOperatingSystemVersion(AOServConnector conn) {
        OperatingSystemVersion osv=conn.operatingSystemVersions.get(operating_system_version);
        if(osv==null) throw new WrappedException(new SQLException("Unable to find OperatingSystemVersion: "+operating_system_version));
        return osv;
    }

    protected int getTableIDImpl() {
	return SchemaTable.TECHNOLOGY_VERSIONS;
    }

    public TechnologyName getTechnologyName(AOServConnector connector) {
        TechnologyName technologyName = connector.technologyNames.get(name);
        if (technologyName == null) throw new WrappedException(new SQLException("Unable to find TechnologyName: " + name));
        return technologyName;
    }

    public long getUpdated() {
	return updated;
    }

    public String getVersion() {
	return version;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getInt(1);
	name = result.getString(2);
	version = result.getString(3);
	updated = result.getTimestamp(4).getTime();
	owner = result.getString(5);
        operating_system_version = result.getInt(6);
        if(result.wasNull()) operating_system_version=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	name=in.readUTF();
	version=in.readUTF();
	updated=in.readLong();
	owner=in.readUTF();
        operating_system_version=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeUTF(version);
	out.writeLong(updated);
	out.writeUTF(owner);
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_0_A_108)>=0) out.writeCompressedInt(operating_system_version);
    }
}