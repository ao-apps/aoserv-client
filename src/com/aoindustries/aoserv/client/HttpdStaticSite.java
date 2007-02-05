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
 * An <code>HttpdStaticSite</code> inicates that an <code>HttpdSite</code>
 * serves static content only.
 *
 * @see  HttpdSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdStaticSite extends CachedObjectIntegerKey<HttpdStaticSite> {

    static final int COLUMN_HTTPD_SITE=0;

    public Object getColumn(int i) {
	if(i==COLUMN_HTTPD_SITE) return Integer.valueOf(pkey);
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public HttpdSite getHttpdSite() {
	HttpdSite obj=table.connector.httpdSites.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdSite: "+pkey));
	return obj;
    }

    protected int getTableIDImpl() {
	return SchemaTable.HTTPD_STATIC_SITES;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
    }

    String toStringImpl() {
        return getHttpdSite().toString();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
    }
}