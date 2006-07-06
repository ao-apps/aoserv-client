package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * For AO Industries use only.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DistroFileType extends GlobalObjectStringKey<DistroFileType> {

    public static final int COLUMN_TYPE=0;

    private String description;

    /**
     * The different file types.
     */
    public static final String
        CONFIG="config",
        NO_RECURSE="no_recurse",
        SYSTEM="system",
        USER="user"
    ;

    public Object getColumn(int i) {
        if(i==COLUMN_TYPE) return pkey;
        if(i==1) return description;
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public String getType() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.DISTRO_FILE_TYPES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	description=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
    }
}