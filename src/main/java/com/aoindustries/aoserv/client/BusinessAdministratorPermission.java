/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2007-2009, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Associates a permission with a business administrator.
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessAdministratorPermission extends CachedObjectIntegerKey<BusinessAdministratorPermission> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_USERNAME=1
	;
	static final String COLUMN_USERNAME_name = "username";
	static final String COLUMN_PERMISSION_name = "permission";

	UserId username;
	String permission;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_USERNAME: return username;
			case 2: return permission;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public BusinessAdministrator getBusinessAdministrator() throws SQLException, IOException {
		BusinessAdministrator ba = table.connector.getBusinessAdministrators().get(username);
		if(ba==null) throw new SQLException("Unable to find BusinessAdministrator: "+username);
		return ba;
	}

	public AOServPermission getAOServPermission() throws SQLException, IOException {
		AOServPermission ap = table.connector.getAoservPermissions().get(permission);
		if(ap==null) throw new SQLException("Unable to find AOServPermission: "+permission);
		return ap;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BUSINESS_ADMINISTRATOR_PERMISSIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = result.getInt(1);
			username = UserId.valueOf(result.getString(2));
			permission = result.getString(3);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			username = UserId.valueOf(in.readUTF()).intern();
			permission = in.readUTF().intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(username.toString());
		out.writeUTF(permission);
	}
}
