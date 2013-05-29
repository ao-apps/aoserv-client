package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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

    static final int COLUMN_TYPE=0;
    static final String COLUMN_TYPE_name = "type";

    private String description;

    /**
     * The different file types.
     */
    public static final String
        CONFIG="config",
        NO_RECURSE="no_recurse",
        PRELINK="prelink",
        SYSTEM="system",
        USER="user"
    ;

    Object getColumnImpl(int i) {
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

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DISTRO_FILE_TYPES;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
    }
}