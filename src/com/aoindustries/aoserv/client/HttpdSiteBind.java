package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  HttpdSite
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdSiteBind extends CachedObjectIntegerKey<HttpdSiteBind> implements Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_HTTPD_SITE=1
    ;
    static final String COLUMN_HTTPD_SITE_name = "httpd_site";
    static final String COLUMN_HTTPD_BIND_name = "httpd_bind";

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

    public int addHttpdSiteURL(String hostname) throws IOException, SQLException {
        return table.connector.getHttpdSiteURLs().addHttpdSiteURL(this, hostname);
    }

    public boolean canDisable() {
        return disable_log==-1;
    }

    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return dl.canEnable() && getHttpdSite().disable_log==-1;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.HTTPD_SITE_BINDS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.HTTPD_SITE_BINDS, pkey);
    }

    public String getAccessLog() {
	return access_log;
    }

    public List<HttpdSiteURL> getAltHttpdSiteURLs() throws IOException, SQLException {
	return table.connector.getHttpdSiteURLs().getAltHttpdSiteURLs(this);
    }

    Object getColumnImpl(int i) {
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

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public String getErrorLog() {
	return error_log;
    }

    public HttpdBind getHttpdBind() throws SQLException, IOException {
	HttpdBind obj=table.connector.getHttpdBinds().get(httpd_bind);
	if(obj==null) throw new SQLException("Unable to find HttpdBind: "+httpd_bind+" for HttpdSite="+httpd_site);
	return obj;
    }

    public HttpdSite getHttpdSite() throws SQLException, IOException {
	HttpdSite obj=table.connector.getHttpdSites().get(httpd_site);
	if(obj==null) throw new SQLException("Unable to find HttpdSite: "+httpd_site);
	return obj;
    }

    public List<HttpdSiteURL> getHttpdSiteURLs() throws IOException, SQLException {
	return table.connector.getHttpdSiteURLs().getHttpdSiteURLs(this);
    }

    public String getPredisableConfig() {
        return predisable_config;
    }

    public HttpdSiteURL getPrimaryHttpdSiteURL() throws SQLException, IOException {
	return table.connector.getHttpdSiteURLs().getPrimaryHttpdSiteURL(this);
    }

    public String getSSLCertFile() {
	return sslCertFile;
    }

    public String getSSLCertKeyFile() {
	return sslCertKeyFile;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_SITE_BINDS;
    }

    public void init(ResultSet result) throws SQLException {
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
	sslCertFile=in.readNullUTF();
	sslCertKeyFile=in.readNullUTF();
        disable_log=in.readCompressedInt();
        predisable_config=in.readNullUTF();
        isManual=in.readBoolean();
        redirect_to_primary_hostname=in.readBoolean();
    }

    public void setIsManual(boolean isManual) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_IS_MANUAL, pkey, isManual);
    }

    public void setRedirectToPrimaryHostname(boolean redirectToPrimaryHostname) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_REDIRECT_TO_PRIMARY_HOSTNAME, pkey, redirectToPrimaryHostname);
    }

    public void setPredisableConfig(final String config) throws IOException, SQLException {
        table.connector.requestUpdate(
            true,
            new AOServConnector.UpdateRequest() {
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.SET_HTTPD_SITE_BIND_PREDISABLE_CONFIG.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeNullUTF(config);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                    table.connector.tablesUpdated(invalidateList);
                }
            }
        );
    }

    @Override
    String toStringImpl() throws SQLException, IOException {
        HttpdSite site=getHttpdSite();
        HttpdBind bind=getHttpdBind();
        return site.toString()+'|'+bind.toString();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(httpd_site);
	out.writeCompressedInt(httpd_bind);
	out.writeUTF(access_log);
	out.writeUTF(error_log);
        out.writeNullUTF(sslCertFile);
        out.writeNullUTF(sslCertKeyFile);
        out.writeCompressedInt(disable_log);
        out.writeNullUTF(predisable_config);
        out.writeBoolean(isManual);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_19)>=0) out.writeBoolean(redirect_to_primary_hostname);
    }
}