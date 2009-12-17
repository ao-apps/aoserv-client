package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * Each <code>HttpdServer</code> may listen for network connections on
 * multiple <code>NetBind</code>s.  An <code>HttpdBind</code> ties
 * <code>HttpdServer</code>s to <code>NetBinds</code>.
 *
 * @see  HttpdServer
 * @see  NetBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdBind extends CachedObjectIntegerKey<HttpdBind> {

    static final int
        COLUMN_NET_BIND=0,
        COLUMN_HTTPD_SERVER=1
    ;
    static final String COLUMN_NET_BIND_name = "net_bind";
    
    int httpd_server;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NET_BIND: return Integer.valueOf(pkey);
            case COLUMN_HTTPD_SERVER: return Integer.valueOf(httpd_server);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdServer getHttpdServer() throws SQLException, IOException {
        HttpdServer obj=table.connector.getHttpdServers().get(httpd_server);
        if(obj==null) throw new SQLException("Unable to find HttpdServer: "+httpd_server);
        return obj;
    }

    public NetBind getNetBind() throws SQLException, IOException {
        NetBind obj=table.connector.getNetBinds().get(pkey);
        if(obj==null) throw new SQLException("Unable to find NetBind: "+pkey);
        return obj;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.HTTPD_BINDS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        httpd_server=result.getInt(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        httpd_server=in.readCompressedInt();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getNetBind(),
            getHttpdServer()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        HttpdServer server=getHttpdServer();
        NetBind bind=getNetBind();
        return server.toStringImpl(userLocale)+'|'+bind.toStringImpl(userLocale);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(httpd_server);
    }
}