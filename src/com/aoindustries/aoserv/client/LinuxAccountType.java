package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
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
 * @version  1.0a
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
        Shell.SHUTDOWN,
        Shell.TRUE
    };

    private static final String[] applicationShells={
        Shell.BASH,
        Shell.FALSE,
        Shell.NULL,
        Shell.TRUE
    };

    private static final String[] userShells={
        Shell.ASH,
        Shell.BASH,
        Shell.BASH2,
        Shell.BSH,
        Shell.CSH,
        Shell.FALSE,
        Shell.SH,
        Shell.TCSH,
        Shell.TRUE
    };

    public boolean enforceStrongPassword() {
	return enforceStrongPassword(pkey);
    }

    public static boolean enforceStrongPassword(String type) {
	return !type.equals(EMAIL);
    }

    public List<Shell> getAllowedShells(AOServConnector connector) {
	String[] paths=getShellList(pkey);

	ShellTable shellTable=connector.shells;
	int len=paths.length;
	List<Shell> shells=new ArrayList<Shell>(len);
	for(int c=0;c<len;c++) {
            Shell shell=shellTable.get(paths[c]);
            if(shell==null) throw new WrappedException(new SQLException("Unable to find Shell: "+paths[c]));
            shells.add(shell);
	}
	return shells;
    }

    public Object getColumn(int i) {
	if(i==COLUMN_NAME) return pkey;
	if(i==1) return description;
	if(i==2) return is_email?Boolean.TRUE:Boolean.FALSE;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public String getName() {
	return pkey;
    }

    private static String[] getShellList(String type) {
	if(type.equals(BACKUP)) return backupShells;
	if(type.equals(EMAIL)) return emailShells;
	if(type.equals(FTPONLY)) return ftpShells;
	if(type.equals(USER)) return userShells;
	if(type.equals(MERCENARY)) return mercenaryShells;
	if(type.equals(SYSTEM)) return systemShells;
	if(type.equals(APPLICATION)) return applicationShells;
	throw new WrappedException(new SQLException("Unknown type: "+type));
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.LINUX_ACCOUNT_TYPES;
    }

    void initImpl(ResultSet result) throws SQLException {
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

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
	is_email=in.readBoolean();
    }

    String toStringImpl() {
	return description;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
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