package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * All of the possible Linux login shells are provided as
 * <code>Shell</code>s.
 *
 * @see  LinuxAccount
 * @see  LinuxAccountType
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Shell extends GlobalObjectStringKey<Shell> {

    static final int COLUMN_PATH=0;

    public static final String
        ASH="/bin/ash",
        BASH="/bin/bash",
        BASH2="/bin/bash2",
        BSH="/bin/bsh",
        CSH="/bin/csh",
        FALSE="/bin/false",
        FTPONLY="/usr/bin/ftponly",
        FTPPASSWD="/usr/bin/ftppasswd",
        HALT="/sbin/halt",
        NOLOGIN="/sbin/nologin",
        NULL="/dev/null",
        PASSWD="/usr/bin/passwd",
        SH="/bin/sh",
        SHUTDOWN="/sbin/shutdown",
        SYNC="/bin/sync",
        TCSH="/bin/tcsh",
        TRUE="/bin/true"
    ;

    private boolean is_login;
    private boolean is_system;

    public Object getColumn(int i) {
	if(i==COLUMN_PATH) return pkey;
	if(i==1) return is_login?Boolean.TRUE:Boolean.FALSE;
	if(i==2) return is_system?Boolean.TRUE:Boolean.FALSE;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getPath() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SHELLS;
    }

    void initImpl(ResultSet result) throws SQLException {
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

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	is_login=in.readBoolean();
	is_system=in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeBoolean(is_login);
	out.writeBoolean(is_system);
    }
}