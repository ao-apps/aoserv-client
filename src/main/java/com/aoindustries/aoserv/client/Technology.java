/*
 * Copyright 2000-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>Technology</code> associates a <code>TechnologyClass</code>
 * with a <code>TechnologyName</code>.
 *
 * @see  TechnologyClass
 * @see  TechnologyName
 *
 * @author  AO Industries, Inc.
 */
final public class Technology extends GlobalObjectIntegerKey<Technology> {

	static final int COLUMN_PKEY=0;
	static final int COLUMN_NAME=1;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_CLASS_name = "class";

	String name, clazz;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_PKEY) return pkey;
		if(i==COLUMN_NAME) return name;
		if(i==2) return clazz;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TECHNOLOGIES;
	}

	public TechnologyClass getTechnologyClass(AOServConnector connector) throws SQLException, IOException {
		TechnologyClass technologyClass = connector.getTechnologyClasses().get(clazz);
		if (technologyClass == null) throw new SQLException("Unable to find TechnologyClass: " + clazz);
		return technologyClass;
	}

	public TechnologyName getTechnologyName(AOServConnector connector) throws SQLException, IOException {
		TechnologyName technologyName = connector.getTechnologyNames().get(name);
		if (technologyName == null) throw new SQLException("Unable to find TechnologyName: " + name);
		return technologyName;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getInt(1);
		name = result.getString(2);
		clazz = result.getString(3);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		name = in.readUTF().intern();
		clazz = in.readUTF().intern();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		if(version.compareTo(AOServProtocol.Version.VERSION_1_4)>=0) out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeUTF(clazz);
	}
}
