/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2009, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.GlobalObjectStringKey;
import com.aoindustries.aoserv.client.password.PasswordChecker;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>LinuxAccountType</code> of a <code>LinuxAccount</code>
 * controls which systems the account may access.  If the
 * <code>LinuxAccount</code> is able to access multiple
 * <code>Server</code>s, its type will be the same on all servers.
 *
 * TODO: Make this class an enum?  How would API version compatibility work?  Same for group type.
 *
 * @see  User
 * @see  UserServer
 *
 * @author  AO Industries, Inc.
 */
public final class UserType extends GlobalObjectStringKey<UserType> {

	static final int COLUMN_NAME=0;
	static final String COLUMN_DESCRIPTION_name = "description";

	private String description;
	private boolean is_email;

	/**
	 * The different Linux account types.
	 */
	public static final String
		BACKUP="backup",
		EMAIL="email",
		FTPONLY="ftponly",
		USER="user",
		MERCENARY="mercenary",
		SYSTEM="system",
		APPLICATION="application"
	;

	private static final PosixPath[] backupShells={
		Shell.BASH
	};

	private static final PosixPath[] emailShells={
		Shell.PASSWD
	};

	private static final PosixPath[] ftpShells={
		Shell.FTPONLY,
		Shell.FTPPASSWD
	};

	private static final PosixPath[] mercenaryShells={
		Shell.BASH
	};

	private static final PosixPath[] systemShells={
		Shell.BASH,
		Shell.FALSE,
		Shell.NOLOGIN,
		Shell.SYNC,
		Shell.HALT,
		Shell.SHUTDOWN//,
		//Shell.TRUE
	};

	private static final PosixPath[] applicationShells={
		Shell.BASH,
		Shell.FALSE//,
		//Shell.NULL,
		//Shell.TRUE
	};

	private static final PosixPath[] userShells={
		//Shell.ASH,
		Shell.BASH,
		//Shell.BASH2,
		//Shell.BSH,
		//Shell.CSH,
		Shell.FALSE,
		Shell.KSH,
		Shell.SH,
		Shell.TCSH//,
		//Shell.TRUE
	};

	public PasswordChecker.PasswordStrength getPasswordStrength() {
		return getPasswordStrength(pkey);
	}

	public static PasswordChecker.PasswordStrength getPasswordStrength(String type) {
		return type.equals(EMAIL) ? PasswordChecker.PasswordStrength.SUPER_LAX : PasswordChecker.PasswordStrength.STRICT;
	}

	public List<Shell> getAllowedShells(AOServConnector connector) throws SQLException, IOException {
		PosixPath[] paths=getShellList(pkey);

		ShellTable shellTable=connector.getLinux().getShell();
		int len=paths.length;
		List<Shell> shells=new ArrayList<>(len);
		for(int c=0;c<len;c++) {
			Shell shell=shellTable.get(paths[c]);
			if(shell==null) throw new SQLException("Unable to find Shell: "+paths[c]);
			shells.add(shell);
		}
		return shells;
	}

	@Override
	protected Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return description;
		if(i==2) return is_email;
		throw new IllegalArgumentException("Invalid index: " + i);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return pkey;
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	private static PosixPath[] getShellList(String type) throws SQLException {
		if(type.equals(BACKUP)) return backupShells;
		if(type.equals(EMAIL)) return emailShells;
		if(type.equals(FTPONLY)) return ftpShells;
		if(type.equals(USER)) return userShells;
		if(type.equals(MERCENARY)) return mercenaryShells;
		if(type.equals(SYSTEM)) return systemShells;
		if(type.equals(APPLICATION)) return applicationShells;
		throw new SQLException("Unknown type: "+type);
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.LINUX_ACCOUNT_TYPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		description = result.getString(2);
		is_email = result.getBoolean(3);
	}

	public boolean canPostgresIdent() {
		return canPostgresIdent(pkey);
	}

	public static boolean canPostgresIdent(String type) {
		return
			APPLICATION.equals(type)
			|| USER.equals(type);
	}

	public boolean isAllowedShell(Shell shell) throws SQLException {
		return isAllowedShell(shell.getPath());
	}

	public boolean isAllowedShell(PosixPath path) throws SQLException {
		return isAllowedShell(pkey, path);
	}

	public static boolean isAllowedShell(String type, PosixPath path) throws SQLException {
		PosixPath[] paths=getShellList(type);
		int len=paths.length;
		for(int c=0;c<len;c++) {
			if(paths[c].equals(path)) return true;
		}
		return false;
	}

	public boolean isEmail() {
		return is_email;
	}

	@Override
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
		pkey=in.readUTF().intern();
		description=in.readUTF();
		is_email=in.readBoolean();
	}

	@Override
	public String toStringImpl() {
		return description;
	}

	@Override
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeUTF(pkey);
		out.writeUTF(description);
		out.writeBoolean(is_email);
	}

	public static boolean canSetPassword(String type) {
		return
			APPLICATION.equals(type)
			|| EMAIL.equals(type)
			|| FTPONLY.equals(type)
			|| USER.equals(type)
		;
	}

	public boolean canSetPassword() {
		return canSetPassword(pkey);
	}
}
