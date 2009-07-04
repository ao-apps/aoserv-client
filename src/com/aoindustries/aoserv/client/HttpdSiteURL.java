package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Multiple <code>HttpdSiteURL</code>s may be attached to a unique
 * combination of <code>HttpdSite</code> and <code>HttpdBind</code>,
 * represented by an <code>HttpdSiteBind</code>.  This allows a web
 * site to respond to several different hostnames on the same IP/port
 * combination.
 *
 * @see  HttpdSiteBind
 * @see  HttpdSite
 * @see  HttpdBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteURL extends CachedObjectIntegerKey<HttpdSiteURL> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_HTTPD_SITE_BIND=1
    ;
    static final String COLUMN_HOSTNAME_name = "hostname";
    static final String COLUMN_HTTPD_SITE_BIND_name = "httpd_site_bind";

    int httpd_site_bind;
    private String hostname;
    boolean isPrimary;

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        if(isPrimary) reasons.add(new CannotRemoveReason<HttpdSiteURL>("Not allowed to remove the primary URL", this));
        if(isTestURL()) reasons.add(new CannotRemoveReason<HttpdSiteURL>("Not allowed to remove the test URL", this));

        return reasons;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_HTTPD_SITE_BIND: return Integer.valueOf(httpd_site_bind);
            case 2: return hostname;
            case 3: return isPrimary?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getHostname() {
	return hostname;
    }

    public HttpdSiteBind getHttpdSiteBind() throws SQLException, IOException {
	HttpdSiteBind obj=table.connector.getHttpdSiteBinds().get(httpd_site_bind);
	if(obj==null) throw new SQLException("Unable to find HttpdSiteBind: "+httpd_site_bind);
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SITE_URLS;
    }

    public String getURL() throws SQLException, IOException {
        HttpdSiteBind siteBind=getHttpdSiteBind();
        NetBind netBind=siteBind.getHttpdBind().getNetBind();
        NetPort port=netBind.getPort();
        StringBuilder url=new StringBuilder();
        String protocol;
        if(siteBind.getSSLCertFile()==null) {
            // If HTTP
            url.append("http://");
            protocol=Protocol.HTTP;
    	} else {
            // Otherwise must be HTTPS
            url.append("https://");
            protocol=Protocol.HTTPS;
    	}
        url.append(hostname);
        if(!port.equals(table.connector.getProtocols().get(protocol).getPort(table.connector))) url.append(':').append(port.getPort());
        url.append('/');
        return url.toString();
    }

    public String getURLNoSlash() throws SQLException, IOException {
        HttpdSiteBind siteBind=getHttpdSiteBind();
        NetBind netBind=siteBind.getHttpdBind().getNetBind();
        NetPort port=netBind.getPort();
        StringBuilder url=new StringBuilder();
        String protocol;
        if(siteBind.getSSLCertFile()==null) {
            // If HTTP
            url.append("http://");
            protocol=Protocol.HTTP;
    	} else {
            // Otherwise must be HTTPS
            url.append("https://");
            protocol=Protocol.HTTPS;
    	}
        url.append(hostname);
        if(!port.equals(table.connector.getProtocols().get(protocol).getPort(table.connector))) url.append(':').append(port.getPort());
        return url.toString();
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        httpd_site_bind=result.getInt(2);
        hostname=result.getString(3);
        isPrimary=result.getBoolean(4);
    }

    public boolean isPrimary() {
    	return isPrimary;
    }

    public boolean isTestURL() throws SQLException, IOException {
        HttpdSite hs=getHttpdSiteBind().getHttpdSite();
        return hostname.equals(hs.getSiteName()+'.'+hs.getAOServer().getHostname());
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        httpd_site_bind=in.readCompressedInt();
        hostname=in.readUTF();
        isPrimary=in.readBoolean();
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_SITE_URLS, pkey);
    }

    public void setAsPrimary() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_PRIMARY_HTTPD_SITE_URL, pkey);
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return hostname;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(httpd_site_bind);
        out.writeUTF(hostname);
        out.writeBoolean(isPrimary);
    }
}