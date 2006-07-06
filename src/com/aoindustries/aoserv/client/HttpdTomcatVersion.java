package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>HttpdTomcatVersion</code> flags which
 * <code>TechnologyVersion</code>s are a version of the Jakarta
 * Tomcat servlet engine.  Multiple versions of the Tomcat servlet
 * engine are supported, but only one version may be configured within
 * each Java virtual machine.
 *
 * @see  HttpdTomcatSite
 * @see  TechnologyVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatVersion extends GlobalObjectIntegerKey<HttpdTomcatVersion> {

    static final int COLUMN_VERSION=0;

    private String install_dir;
    private boolean requires_mod_jk;

    public static final String TECHNOLOGY_NAME="jakarta-tomcat";

    public static final String
        VERSION_3_1_PREFIX="3.1.",
        VERSION_3_2_PREFIX="3.2.",
        VERSION_4_1_PREFIX="4.1.",
        VERSION_5_5_PREFIX="5.5."
    ;

    public static final String
        VERSION_4_PREFIX="4.",
        VERSION_5_PREFIX="5."
    ;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_VERSION: return Integer.valueOf(pkey);
            case 1: return install_dir;
            case 2: return requires_mod_jk?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getInstallDirectory() {
	return install_dir;
    }

    protected int getTableIDImpl() {
	return SchemaTable.HTTPD_TOMCAT_VERSIONS;
    }

    public TechnologyVersion getTechnologyVersion(AOServConnector connector) {
	TechnologyVersion obj=connector.technologyVersions.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find TechnologyVersion: "+pkey));
	return obj;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	install_dir=result.getString(2);
        requires_mod_jk=result.getBoolean(3);
    }

    public boolean isTomcat4(AOServConnector connector) {
        String version = getTechnologyVersion(connector).getVersion();
        return version.startsWith(VERSION_4_PREFIX) || version.startsWith(VERSION_5_PREFIX);
    }

    public boolean isTomcat55(AOServConnector connector) {
        String version = getTechnologyVersion(connector).getVersion();
        return version.startsWith(VERSION_5_5_PREFIX);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	install_dir=in.readUTF();
        requires_mod_jk=in.readBoolean();
    }

    public boolean requiresModJK() {
        return requires_mod_jk;
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(install_dir);
        out.writeBoolean(requires_mod_jk);
    }
}