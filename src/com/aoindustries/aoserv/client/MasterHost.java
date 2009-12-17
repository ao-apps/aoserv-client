package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * A <code>MasterHost</code> controls which hosts a <code>MasterUser</code>
 * is allowed to connect from.  Because <code>MasterUser</code>s have more
 * control over the system, this extra security measure is taken.
 *
 * @see  MasterUser
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHost extends CachedObjectIntegerKey<MasterHost> {

    static final int
        COLUMN_PKEY = 0,
        COLUMN_USERNAME = 1
    ;
    static final String COLUMN_USERNAME_name = "username";
    static final String COLUMN_HOST_name = "host";

    private String username;
    private String host;

    Object getColumnImpl(int i) {
        if(i==COLUMN_PKEY) return Integer.valueOf(pkey);
        if(i==COLUMN_USERNAME) return username;
        if(i==2) return host;
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getHost() {
	return host;
    }

    public MasterUser getMasterUser() throws SQLException, IOException {
	MasterUser obj=table.connector.getMasterUsers().get(username);
	if(obj==null) throw new SQLException("Unable to find MasterUser: "+username);
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_HOSTS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	username=result.getString(2);
	host=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        username=in.readUTF().intern();
        host=in.readUTF().intern();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getMasterUser()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(username);
        out.writeUTF(host);
    }
}