package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * Multiple versions of Majordomo are supported by the system.
 * Each <code>MajordomoServer</code> is of a specific version,
 * and all its <code>MajordomoList</code>s inherit that
 * <code>MajordomoVersion</code>.
 *
 * @see  MajordomoServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoVersion extends GlobalObjectStringKey<MajordomoVersion> {

    /**
     * The default Majordomo version.
     */
    public static final String DEFAULT_VERSION="1.94.5";

    static final int COLUMN_VERSION=0;

    private long created;

    public Object getColumn(int i) {
	if(i==COLUMN_VERSION) return pkey;
	if(i==1) return new java.sql.Date(created);
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public long getCreated() {
	return created;
    }

    protected int getTableIDImpl() {
	return SchemaTable.MAJORDOMO_VERSIONS;
    }

    public String getVersion() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getString(1);
	created=result.getTimestamp(2).getTime();
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	created=in.readLong();
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeUTF(pkey);
	out.writeLong(created);
    }
}