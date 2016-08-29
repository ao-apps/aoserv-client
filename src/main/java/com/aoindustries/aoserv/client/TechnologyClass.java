/*
 * Copyright 2000-2013, 2016 by AO Industries, Inc.,
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
 * A <code>TechnologyClass</code> is one type of software package
 * installed on the servers.
 *
 * @see  Technology
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyClass extends GlobalObjectStringKey<TechnologyClass> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_NAME_name = "name";

	/**
	 * The possible <code>TechnologyClass</code>es.
	 */
	public static final String
		APACHE="Apache",
		EMAIL="E-Mail",
		ENCRYPTION="Encryption",
		INTERBASE="InterBase",
		JAVA="Java",
		LINUX="Linux",
		MYSQL="MySQL",
		PERL="PERL",
		PHP="PHP",
		POSTGRESQL="PostgreSQL",
		X11="X11",
		XML="XML"
	;

	private String description;

	@Override
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

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.TECHNOLOGY_CLASSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		description = result.getString(2);
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		description=in.readUTF();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
	}
}
