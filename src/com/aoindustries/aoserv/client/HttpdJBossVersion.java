package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>HttpdJBossVersion</code> flags which
 * <code>TechnologyVersion</code>s are a version of the JBoss
 * EJB Container.  Sites configured to use JBoss are called
 * HttpdJBossSites.
 * 
 * @see  HttpdJBossSite
 * @see  TechnologyVersion
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdJBossVersion extends GlobalObjectIntegerKey<HttpdJBossVersion> {

    static final int COLUMN_VERSION=0;
    static final String COLUMN_VERSION_name = "version";

    private int tomcatVersion;
    private String templateDir;
    public static final String TECHNOLOGY_NAME="JBoss";

    public static final String
        VERSION_2_2_2="2.2.2"
    ;

    public static final String DEFAULT_VERSION=VERSION_2_2_2;

    public Object getColumn(int i) {
	if(i==COLUMN_VERSION) return Integer.valueOf(pkey);
	if(i==1) return Integer.valueOf(tomcatVersion);
	if(i==2) return templateDir;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public HttpdTomcatVersion getHttpdTomcatVersion(AOServConnector connector) {
	HttpdTomcatVersion obj=connector.httpdTomcatVersions.get(tomcatVersion);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdTomcatVersion: "+tomcatVersion));
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_JBOSS_VERSIONS;
    }

    public TechnologyVersion getTechnologyVersion(AOServConnector connector) {
	TechnologyVersion obj=connector.technologyVersions.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find TechnologyVersion: "+pkey));
	return obj;
    }

    public String getTemplateDirectory() {
	return templateDir;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	tomcatVersion=result.getInt(2);
	templateDir=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	tomcatVersion=in.readCompressedInt();
	templateDir=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(tomcatVersion);
	out.writeUTF(templateDir);
    }
}