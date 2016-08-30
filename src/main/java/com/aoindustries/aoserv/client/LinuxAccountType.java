/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2000-2009, 2016  AO Industries, Inc.
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
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>LinuxAccountType</code> of a <code>LinuxAccount</code>
 * controls which systems the account may access.  If the
 * <code>LinuxAccount</code> is able to access multiple
 * <code>Server</code>s, its type will be the same on all servers.
 *
 * @see  LinuxAccount
 * @see  LinuxServerAccount
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountType extends GlobalObjectStringKey<LinuxAccountType> {

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

	private static final String[] backupShells={
		Shell.BASH
	};

	private static final String[] emailShells={
		Shell.PASSWD
	};

	private static final String[] ftpShells={
		Shell.FTPONLY,
		Shell.FTPPASSWD
	};

	private static final String[] mercenaryShells={
		Shell.BASH
	};

	private static final String[] systemShells={
		Shell.BASH,
		Shell.FALSE,
		Shell.NOLOGIN,
		Shell.SYNC,
		Shell.HALT,
		Shell.SHUTDOWN//,
		//Shell.TRUE
	};

	private static final String[] applicationShells={
		Shell.BASH,
		Shell.FALSE//,
		//Shell.NULL,
		//Shell.TRUE
	};

	private static final String[] userShells={
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
		String[] paths=getShellList(pkey);

		ShellTable shellTable=connector.getShells();
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
	Object getColumnImpl(int i) {
		if(i==COLUMN_NAME) return pkey;
		if(i==1) return description;
		if(i==2) return is_email;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return pkey;
	}

	private static String[] getShellList(String type) throws SQLException {
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
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.LINUX_ACCOUNT_TYPES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		pkey = result.getString(1);
		description = result.getString(2);
		is_email = result.getBoolean(3);
	}

	public boolean isAllowedShell(Shell shell) throws SQLException {
		return isAllowedShell(shell.pkey);
	}

	public boolean isAllowedShell(String path) throws SQLException {
		return isAllowedShell(pkey, path);
	}

	public static boolean isAllowedShell(String type, String path) throws SQLException {
		String[] paths=getShellList(type);
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
	public void read(CompressedDataInputStream in) throws IOException {
		pkey=in.readUTF().intern();
		description=in.readUTF();
		is_email=in.readBoolean();
	}

	@Override
	String toStringImpl() {
		return description;
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
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
