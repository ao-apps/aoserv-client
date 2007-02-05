package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>MasterHost</code> controls which hosts a <code>MasterUser</code>
 * is allowed to connect from.  Because <code>MasterUser</code>s have more
 * control over the system, this extra security measure is taken.
 *
 * @see  MasterUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHost extends CachedObjectIntegerKey<MasterHost> {

    static final int COLUMN_PKEY=0;

    private String username;
    private String host;

    public Object getColumn(int i) {
	if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
	if(i==1) return username;
	if(i==2) return host;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getHost() {
	return host;
    }

    public MasterUser getMasterUser() {
	MasterUser obj=table.connector.masterUsers.get(username);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find MasterUser: "+username));
	return obj;
    }

    protected int getTableIDImpl() {
	return SchemaTable.MASTER_HOSTS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	username=result.getString(2);
	host=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	username=in.readUTF();
	host=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(username);
	out.writeUTF(host);
    }
}