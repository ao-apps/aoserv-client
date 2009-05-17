package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>HttpdWorker</code> represents a unique combination of
 * <code>HttpdJKCode</code> and <code>HttpdTomcatSite</code>.  The
 * details about which IP address and port the servlet engine is
 * listening on is available.
 *
 * @see  HttpdJKCode
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdWorker extends CachedObjectIntegerKey<HttpdWorker> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_NET_BIND=2,
        COLUMN_TOMCAT_SITE=3
    ;
    static final String COLUMN_NET_BIND_name = "net_bind";
    static final String COLUMN_CODE_name = "code";

    /**
     * Any attempt to use this port for workers causes Apache to not start.
     */
    public static final int ERROR_CAUSING_PORT=1024;

    private String code;
    int net_bind;
    int tomcat_site;

    public HttpdJKCode getCode() throws SQLException, IOException {
	HttpdJKCode obj=table.connector.getHttpdJKCodes().get(code);
	if(obj==null) throw new SQLException("Unable to find HttpdJKCode: "+code);
	return obj;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return code;
            case COLUMN_NET_BIND: return Integer.valueOf(net_bind);
            case COLUMN_TOMCAT_SITE: return tomcat_site==-1?null:Integer.valueOf(tomcat_site);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdJKProtocol getHttpdJKProtocol(AOServConnector connector) throws IOException, SQLException {
	HttpdJKProtocol obj=getNetBind().getAppProtocol().getHttpdJKProtocol(connector);
	if(obj==null) throw new SQLException("Unable to find HttpdJKProtocol: "+net_bind);
	return obj;
    }

    public HttpdSharedTomcat getHttpdSharedTomcat() throws SQLException, IOException {
        return table.connector.getHttpdSharedTomcats().getHttpdSharedTomcat(this);
    }

    public HttpdTomcatSite getHttpdTomcatSite() throws SQLException, IOException {
        if(tomcat_site==-1) return null;
	HttpdTomcatSite obj=table.connector.getHttpdTomcatSites().get(tomcat_site);
	if(obj==null) throw new SQLException("Unable to find HttpdTomcatSite: "+tomcat_site);
	return obj;
    }

    public NetBind getNetBind() throws IOException, SQLException {
	NetBind obj=table.connector.getNetBinds().get(net_bind);
	if(obj==null) throw new SQLException("Unable to find NetBind: "+net_bind);
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_WORKERS;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	code=result.getString(2);
	net_bind=result.getInt(3);
	tomcat_site=result.getInt(4);
        if(result.wasNull()) tomcat_site=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	code=in.readUTF();
	net_bind=in.readCompressedInt();
	tomcat_site=in.readCompressedInt();
    }

    @Override
    String toStringImpl() {
	return pkey+"|"+code;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(code);
	out.writeCompressedInt(net_bind);
	out.writeCompressedInt(tomcat_site);
    }
}