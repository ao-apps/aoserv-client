package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteBind extends CachedObjectIntegerKey<HttpdSiteBind> implements Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_HTTPD_SITE=1
    ;

    int httpd_site;
    private int httpd_bind;
    private String access_log;
    private String error_log;
    private String sslCertFile;
    private String sslCertKeyFile;
    int disable_log;
    private String predisable_config;
    private boolean isManual;
    private boolean redirect_to_primary_hostname;

    public int addHttpdSiteURL(String hostname) {
        return table.connector.httpdSiteURLs.addHttpdSiteURL(this, hostname);
    }

    public boolean canDisable() {
        return disable_log==-1;
    }

    public boolean canEnable() {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getHttpdSite().disable_log==-1;
    }

    public void disable(DisableLog dl) {
        table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.HTTPD_SITE_BINDS, dl.pkey, pkey);
    }
    
    public void enable() {
        table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.HTTPD_SITE_BINDS, pkey);
    }

    public String getAccessLog() {
	return access_log;
    }

    public List<HttpdSiteURL> getAltHttpdSiteURLs() {
	return table.connector.httpdSiteURLs.getAltHttpdSiteURLs(this);
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_HTTPD_SITE: return Integer.valueOf(httpd_site);
            case 2: return Integer.valueOf(httpd_bind);
            case 3: return access_log;
            case 4: return error_log;
            case 5: return sslCertFile;
            case 6: return sslCertKeyFile;
            case 7: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 8: return predisable_config;
            case 9: return isManual?Boolean.TRUE:Boolean.FALSE;
            case 10: return redirect_to_primary_hostname?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public DisableLog getDisableLog() {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.disableLogs.get(disable_log);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
        return obj;
    }

    public String getErrorLog() {
	return error_log;
    }

    public HttpdBind getHttpdBind() {
	HttpdBind obj=table.connector.httpdBinds.get(httpd_bind);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdBind: "+httpd_bind+" for HttpdSite="+httpd_site));
	return obj;
    }

    public HttpdSite getHttpdSite() {
	HttpdSite obj=table.connector.httpdSites.get(httpd_site);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdSite: "+httpd_site));
	return obj;
    }

    public List<HttpdSiteURL> getHttpdSiteURLs() {
	return table.connector.httpdSiteURLs.getHttpdSiteURLs(this);
    }

    public String getPredisableConfig() {
        return predisable_config;
    }

    public HttpdSiteURL getPrimaryHttpdSiteURL() {
	return table.connector.httpdSiteURLs.getPrimaryHttpdSiteURL(this);
    }

    public String getSSLCertFile() {
	return sslCertFile;
    }

    public String getSSLCertKeyFile() {
	return sslCertKeyFile;
    }

    protected int getTableIDImpl() {
	return SchemaTable.HTTPD_SITE_BINDS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	httpd_site=result.getInt(2);
	httpd_bind=result.getInt(3);
	access_log=result.getString(4);
	error_log=result.getString(5);
	sslCertFile=result.getString(6);
	sslCertKeyFile=result.getString(7);
        disable_log=result.getInt(8);
        if(result.wasNull()) disable_log=-1;
        predisable_config=result.getString(9);
        isManual=result.getBoolean(10);
        redirect_to_primary_hostname=result.getBoolean(11);
    }

    public boolean isManual() {
        return isManual;
    }
    
    public boolean getRedirectToPrimaryHostname() {
        return redirect_to_primary_hostname;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	httpd_site=in.readCompressedInt();
	httpd_bind=in.readCompressedInt();
	access_log=in.readUTF();
	error_log=in.readUTF();
	sslCertFile=readNullUTF(in);
	sslCertKeyFile=readNullUTF(in);
        disable_log=in.readCompressedInt();
        predisable_config=readNullUTF(in);
        isManual=in.readBoolean();
        redirect_to_primary_hostname=in.readBoolean();
    }

    public void setIsManual(boolean isManual) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSiteBind.class, "setIsManual(boolean)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_HTTPD_SITE_BIND_IS_MANUAL, pkey, isManual);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setRedirectToPrimaryHostname(boolean redirectToPrimaryHostname) {
        Profiler.startProfile(Profiler.UNKNOWN, HttpdSiteBind.class, "setRedirectToPrimaryHostname(boolean)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, pkey, redirectToPrimaryHostname);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setPredisableConfig(String config) {
        try {
            IntList invalidateList;
            AOServConnector connector=table.connector;
            AOServConnection connection=connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.SET_HTTPD_SITE_BIND_PREDISABLE_CONFIG);
                out.writeCompressedInt(pkey);
                writeNullUTF(out, config);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    String toStringImpl() {
        HttpdSite site=getHttpdSite();
        HttpdBind bind=getHttpdBind();
        return site.toString()+'|'+bind.toString();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(httpd_site);
	out.writeCompressedInt(httpd_bind);
	out.writeUTF(access_log);
	out.writeUTF(error_log);
        writeNullUTF(out, sslCertFile);
        writeNullUTF(out, sslCertKeyFile);
        out.writeCompressedInt(disable_log);
        writeNullUTF(out, predisable_config);
        out.writeBoolean(isManual);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_19)>=0) out.writeBoolean(redirect_to_primary_hostname);
    }
}