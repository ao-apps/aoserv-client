package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * A <code>PostgresVersion</code> flags which <code>TechnologyVersion</code>s
 * are a version of PostgreSQL.
 *
 * @see  PostgresServer
 * @see  TechnologyVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresVersion extends GlobalObjectIntegerKey<PostgresVersion> {

    static final int COLUMN_VERSION=0;

    private String minorVersion;

    public static final String TECHNOLOGY_NAME="postgresql";

    public static final String
        VERSION_7_1_3="7.1.3",
        VERSION_7_2_7="7.2.7",
        VERSION_7_3_13="7.3.13",
        VERSION_8_0_6="8.0.6",
        VERSION_8_1_2="8.1.2"
    ;

    /**
     * Gets the versions of PostgreSQL in order of
     * preference.  Index <code>0</code> is the most
     * preferred.
     */
    public static final String[] getPreferredVersions() {
        return new String[] {
            VERSION_8_1_2,
            VERSION_8_0_6,
            VERSION_7_3_13,
            VERSION_7_2_7,
            VERSION_7_1_3
        };
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_VERSION: return Integer.valueOf(pkey);
            case 1: return minorVersion;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getMinorVersion() {
        return minorVersion;
    }

    protected int getTableIDImpl() {
	return SchemaTable.POSTGRES_VERSIONS;
    }

    public List<PostgresEncoding> getPostgresEncodings(AOServConnector connector) {
        return connector.postgresEncodings.getPostgresEncodings(this);
    }

    public PostgresEncoding getPostgresEncoding(AOServConnector connector, String encoding) {
        return connector.postgresEncodings.getPostgresEncoding(this, encoding);
    }

    public TechnologyVersion getTechnologyVersion(AOServConnector connector) {
	TechnologyVersion obj=connector.technologyVersions.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find TechnologyVersion: "+pkey));
	return obj;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
        minorVersion=result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
        minorVersion=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_0_A_109)<=0) out.writeCompressedInt(5432);
        if(AOServProtocol.compareVersions(protocolVersion, AOServProtocol.VERSION_1_0_A_121)>=0) out.writeUTF(minorVersion);
    }
}
