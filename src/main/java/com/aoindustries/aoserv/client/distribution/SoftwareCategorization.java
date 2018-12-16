/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.distribution;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>Technology</code> associates a <code>TechnologyClass</code>
 * with a <code>TechnologyName</code>.
 *
 * @see  SoftwareCategory
 * @see  Software
 *
 * @author  AO Industries, Inc.
 */
final public class SoftwareCategorization extends GlobalObjectIntegerKey<SoftwareCategorization> {

	static final int COLUMN_PKEY=0;
	static final int COLUMN_NAME=1;
	static final String COLUMN_NAME_name = "name";
	static final String COLUMN_CLASS_name = "class";

	String name, clazz;

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_PKEY) return pkey;
		if(i==COLUMN_NAME) return name;
		if(i==2) return clazz;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.TECHNOLOGIES;
	}

	public SoftwareCategory getTechnologyClass(AOServConnector connector) throws SQLException, IOException {
		SoftwareCategory technologyClass = connector.getDistribution().getTechnologyClasses().get(clazz);
		if (technologyClass == null) throw new SQLException("Unable to find TechnologyClass: " + clazz);
		return technologyClass;
	}

	public Software getTechnologyName(AOServConnector connector) throws SQLException, IOException {
		Software technologyName = connector.getDistribution().getTechnologyNames().get(name);
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
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_4)>=0) out.writeCompressedInt(pkey);
		out.writeUTF(name);
		out.writeUTF(clazz);
	}
}
