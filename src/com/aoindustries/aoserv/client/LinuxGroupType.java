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
 * Each <code>LinuxGroup</code>'s use is limited by which
 * <code>LinuxGroupType</code> is associated with it.  Typically,
 * but not required, a <code>LinuxAccount</code> will have a
 * <code>LinuxAccountType</code> that matchs its primary
 * <code>LinuxGroup</code>'s <code>LinuxGroupType</code>.
 *
 * @see  LinuxGroup
 * @see  LinuxAccountType
 * @see  LinuxAccount
 * @see  LinuxGroupAccount
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupType extends GlobalObjectStringKey<LinuxGroupType> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_DESCRIPTION_name = "description";

    private String description;

    /**
     * The available group types.
     */
    public static final String
        USER="user",
        EMAIL="email",
        FTPONLY="ftponly",
        SYSTEM="system",
        BACKUP="backup",
        APPLICATION="application"
    ;

    Object getColumnImpl(int i) {
	if(i==COLUMN_NAME) return pkey;
	if(i==1) return description;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public String getName() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_GROUP_TYPES;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
    }

    @Override
    String toStringImpl() {
	return description;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
    }
}