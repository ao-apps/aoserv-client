package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>HttpdStdTomcatSite</code> inicates that a
 * <code>HttpdTomcatSite</code> is configured in the standard layout
 * of one Tomcat instance per Java virtual machine.
 *
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatStdSite extends CachedObjectIntegerKey<HttpdTomcatStdSite> {

    static final int COLUMN_TOMCAT_SITE=0;

    public static final String DEFAULT_TOMCAT_VERSION_PREFIX=HttpdTomcatVersion.VERSION_4_1_PREFIX;

    int tomcat4_shutdown_port;
    private String tomcat4_shutdown_key;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_TOMCAT_SITE: return Integer.valueOf(pkey);
            case 1: return tomcat4_shutdown_port==-1?null:Integer.valueOf(tomcat4_shutdown_port);
            case 2: return tomcat4_shutdown_key;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdTomcatSite getHttpdTomcatSite() {
	HttpdTomcatSite obj=table.connector.httpdTomcatSites.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdTomcatSite: "+pkey));
	return obj;
    }

    public String getTomcat4ShutdownKey() {
        return tomcat4_shutdown_key;
    }

    public NetBind getTomcat4ShutdownPort() {
        if(tomcat4_shutdown_port==-1) return null;
        NetBind nb=table.connector.netBinds.get(tomcat4_shutdown_port);
        if(nb==null) throw new WrappedException(new SQLException("Unable to find NetBind: "+tomcat4_shutdown_port));
        return nb;
    }

    protected int getTableIDImpl() {
	return SchemaTable.HTTPD_TOMCAT_STD_SITES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
        tomcat4_shutdown_port=result.getInt(2);
        if(result.wasNull()) tomcat4_shutdown_port=-1;
        tomcat4_shutdown_key=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
        tomcat4_shutdown_port=in.readCompressedInt();
        tomcat4_shutdown_key=in.readNullUTF();
    }

    String toStringImpl() {
        return getHttpdTomcatSite().toString();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeCompressedInt(tomcat4_shutdown_port);
        out.writeNullUTF(tomcat4_shutdown_key);
    }
}