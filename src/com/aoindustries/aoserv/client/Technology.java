package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;

/**
 * A <code>Technology</code> associates a <code>TechnologyClass</code>
 * with a <code>TechnologyName</code>.
 *
 * @see  TechnologyClass
 * @see  TechnologyName
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Technology extends GlobalObjectIntegerKey<Technology> {

    static final int COLUMN_PKEY=0;
    static final int COLUMN_NAME=1;

    String name, clazz;

    public Object getColumn(int i) {
        if(i==COLUMN_PKEY) return pkey;
	if(i==COLUMN_NAME) return name;
	if(i==2) return clazz;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    protected int getTableIDImpl() {
	return SchemaTable.TECHNOLOGIES;
    }

    public TechnologyClass getTechnologyClass(AOServConnector connector) {
        TechnologyClass technologyClass = connector.technologyClasses.get(clazz);
        if (technologyClass == null) throw new WrappedException(new SQLException("Unable to find TechnologyClass: " + clazz));
        return technologyClass;
    }

    public TechnologyName getTechnologyName(AOServConnector connector) {
        TechnologyName technologyName = connector.technologyNames.get(name);
        if (technologyName == null) throw new WrappedException(new SQLException("Unable to find TechnologyName: " + name));
        return technologyName;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        name = result.getString(2);
        clazz = result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
	name = in.readUTF();
	clazz = in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_4)>=0) out.writeCompressedInt(pkey);
	out.writeUTF(name);
	out.writeUTF(clazz);
    }
}