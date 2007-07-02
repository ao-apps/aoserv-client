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
 * An <code>HttpdTomcatSharedSite</code> is an <code>HttpdTomcatSite</code>
 * running under an <code>HttpdSharedTomcat</code>.
 *
 * @see  HttpdSharedTomcat
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSharedSite extends CachedObjectIntegerKey<HttpdTomcatSharedSite> {

    static final int
        COLUMN_TOMCAT_SITE=0,
        COLUMN_HTTPD_SHARED_TOMCAT=1
    ;

    private int pkey;

    int httpd_shared_tomcat;

    public static final String DEFAULT_TOMCAT_VERSION_PREFIX=HttpdTomcatVersion.VERSION_4_1_PREFIX;

    /**
     * Determines if the API user is allowed to stop the Java virtual machine associated
     * with this site.
     */
    public boolean canStop() {
        HttpdSharedTomcat hst=getHttpdSharedTomcat();
        return getHttpdSharedTomcat()!=null && hst.getDisableLog()==null;
    }

    /**
     * Determines if the API user is allowed to start the Java virtual machine associated
     * with this site.
     */
    public boolean canStart() {
        HttpdSharedTomcat hst=getHttpdSharedTomcat();
        return getHttpdSharedTomcat()==null || hst.getDisableLog()==null;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_TOMCAT_SITE: return Integer.valueOf(pkey);
            case COLUMN_HTTPD_SHARED_TOMCAT: return Integer.valueOf(httpd_shared_tomcat);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdSharedTomcat getHttpdSharedTomcat() {
	// May be null when filtered
	return table.connector.httpdSharedTomcats.get(httpd_shared_tomcat);
    }

    public HttpdTomcatSite getHttpdTomcatSite() {
	HttpdTomcatSite obj=table.connector.httpdTomcatSites.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdTomcatSite: "+pkey));
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_SHARED_SITES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	httpd_shared_tomcat=result.getInt(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	httpd_shared_tomcat=in.readCompressedInt();
    }

    String toStringImpl() {
        return getHttpdTomcatSite().toString();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(httpd_shared_tomcat);
    }
}