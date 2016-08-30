/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2013, 2016  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
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
