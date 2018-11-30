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
package com.aoindustries.aoserv.client.linux;

import com.aoindustries.aoserv.client.GlobalObjectUnixPathKey;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All of the possible Linux login shells are provided as
 * <code>Shell</code>s.
 *
 * @see  LinuxAccount
 * @see  LinuxAccountType
 *
 * @author  AO Industries, Inc.
 */
final public class Shell extends GlobalObjectUnixPathKey<Shell> {

	static final int COLUMN_PATH=0;
	static final String COLUMN_PATH_name = "path";

	public static final UnixPath
		BASH,
		FALSE,
		KSH,
		SH,
		SYNC,
		TCSH,
		HALT,
		NOLOGIN,
		SHUTDOWN,
		FTPONLY,
		FTPPASSWD,
		PASSWD
	;
	static {
		try {
			BASH = UnixPath.valueOf("/bin/bash").intern();
			FALSE = UnixPath.valueOf("/bin/false").intern();
			KSH = UnixPath.valueOf("/bin/ksh").intern();
			SH = UnixPath.valueOf("/bin/sh").intern();
			SYNC = UnixPath.valueOf("/bin/sync").intern();
			TCSH = UnixPath.valueOf("/bin/tcsh").intern();
			HALT = UnixPath.valueOf("/sbin/halt").intern();
			NOLOGIN = UnixPath.valueOf("/sbin/nologin").intern();
			SHUTDOWN = UnixPath.valueOf("/sbin/shutdown").intern();
			FTPONLY = UnixPath.valueOf("/usr/bin/ftponly").intern();
			FTPPASSWD = UnixPath.valueOf("/usr/bin/ftppasswd").intern();
			PASSWD = UnixPath.valueOf("/usr/bin/passwd").intern();
		} catch(ValidationException e) {
			throw new AssertionError("These hard-coded values are valid", e);
		}
	}

	private boolean is_login;
	private boolean is_system;

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_PATH) return pkey;
		if(i==1) return is_login;
		if(i==2) return is_system;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public UnixPath getPath() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SHELLS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey = UnixPath.valueOf(result.getString(1));
			is_login = result.getBoolean(2);
			is_system = result.getBoolean(3);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	public boolean isLogin() {
		return is_login;
	}

	public boolean isSystem() {
		return is_system;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = UnixPath.valueOf(in.readUTF()).intern();
			is_login=in.readBoolean();
			is_system=in.readBoolean();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeBoolean(is_login);
		out.writeBoolean(is_system);
	}
}
