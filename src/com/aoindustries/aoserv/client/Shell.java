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
 * All of the possible Linux login shells are provided as
 * <code>Shell</code>s.
 *
 * @see  LinuxAccount
 * @see  LinuxAccountType
 *
 * @author  AO Industries, Inc.
 */
final public class Shell extends GlobalObjectStringKey<Shell> {

	static final int COLUMN_PATH=0;
	static final String COLUMN_PATH_name = "path";

	public static final String
		BASH="/bin/bash",
		FALSE="/bin/false",
		KSH="/bin/ksh",
		SH="/bin/sh",
		SYNC="/bin/sync",
		TCSH="/bin/tcsh",
		HALT="/sbin/halt",
		NOLOGIN="/sbin/nologin",
		SHUTDOWN="/sbin/shutdown",
		FTPONLY="/usr/bin/ftponly",
		FTPPASSWD="/usr/bin/ftppasswd",
		PASSWD="/usr/bin/passwd"
	;

	private boolean is_login;
	private boolean is_system;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_PATH) return pkey;
		if(i==1) return is_login;
		if(i==2) return is_system;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getPath() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SHELLS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		is_login = result.getBoolean(2);
		is_system = result.getBoolean(3);
	}

	public boolean isLogin() {
		return is_login;
	}

	public boolean isSystem() {
		return is_system;
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		is_login=in.readBoolean();
		is_system=in.readBoolean();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey);
		out.writeBoolean(is_login);
		out.writeBoolean(is_system);
	}
}
