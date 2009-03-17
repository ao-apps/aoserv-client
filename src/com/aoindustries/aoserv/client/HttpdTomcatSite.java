package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * An <code>HttpdTomcatSite</code> indicates that an <code>HttpdSite</code>
 * uses the Jakarta Tomcat project as its servlet engine.  The servlet
 * engine may be configured in several ways, only what is common to
 * every type of Tomcat installation is stored in <code>HttpdTomcatSite</code>.
 *
 * @see  HttpdSite
 * @see  HttpdTomcatStdSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatSite extends CachedObjectIntegerKey<HttpdTomcatSite> {

    static final int COLUMN_HTTPD_SITE=0;
    static final String COLUMN_HTTPD_SITE_name = "httpd_site";

    private int version;
    private boolean use_apache;

    /**
     * The minimum amount of time in milliseconds between Java VM starts.
     */
    public static final int MINIMUM_START_JVM_DELAY=30000;

    /**
     * The minimum amount of time in milliseconds between Java VM start and stop.
     */
    public static final int MINIMUM_STOP_JVM_DELAY=15000;

    public int addHttpdTomcatContext(
        String className,
        boolean cookies,
        boolean crossContext,
        String docBase,
        boolean override,
        String path,
        boolean privileged,
        boolean reloadable,
        boolean useNaming,
        String wrapperClass,
        int debug,
        String workDir
    ) throws IOException, SQLException {
        return table.connector.httpdTomcatContexts.addHttpdTomcatContext(
            this,
            className,
            cookies,
            crossContext,
            docBase,
            override,
            path,
            privileged,
            reloadable,
            useNaming,
            wrapperClass,
            debug,
            workDir
        );
    }

    /**
     * Determines if the API user is allowed to stop the Java virtual machine associated
     * with this site.
     */
    public boolean canStop() throws SQLException, IOException {
        if(getHttpdSite().getDisableLog()!=null) return false;
        HttpdTomcatSharedSite shr=getHttpdTomcatSharedSite();
        if(shr!=null) return shr.canStop();
        return true;
    }

    /**
     * Determines if the API user is allowed to start the Java virtual machine associated
     * with this site.
     */
    public boolean canStart() throws SQLException, IOException {
        if(getHttpdSite().getDisableLog()!=null) return false;
        HttpdTomcatSharedSite shr=getHttpdTomcatSharedSite();
        if(shr!=null) return shr.canStart();
        return true;
    }

    Object getColumnImpl(int i) {
        if(i==COLUMN_HTTPD_SITE) return Integer.valueOf(pkey);
        if(i==1) return Integer.valueOf(version);
        if(i==2) return use_apache?Boolean.TRUE:Boolean.FALSE;
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public HttpdJBossSite getHttpdJBossSite() throws SQLException, IOException {
        return table.connector.httpdJBossSites.get(pkey);
    }

    public HttpdSite getHttpdSite() throws SQLException, IOException {
        HttpdSite obj=table.connector.httpdSites.get(pkey);
        if(obj==null) throw new SQLException("Unable to find HttpdSite: "+pkey);
        return obj;
    }

    public HttpdTomcatContext getHttpdTomcatContext(String path) throws IOException, SQLException {
        return table.connector.httpdTomcatContexts.getHttpdTomcatContext(this, path);
    }

    public List<HttpdTomcatContext> getHttpdTomcatContexts() throws IOException, SQLException {
        return table.connector.httpdTomcatContexts.getHttpdTomcatContexts(this);
    }

    public HttpdTomcatSharedSite getHttpdTomcatSharedSite() throws IOException, SQLException {
        return table.connector.httpdTomcatSharedSites.get(pkey);
    }

    public HttpdTomcatStdSite getHttpdTomcatStdSite() throws IOException, SQLException {
        return table.connector.httpdTomcatStdSites.get(pkey);
    }

    public HttpdTomcatVersion getHttpdTomcatVersion() throws SQLException, IOException {
        HttpdTomcatVersion obj=table.connector.httpdTomcatVersions.get(version);
        if(obj==null) throw new SQLException("Unable to find HttpdTomcatVersion: "+version);
        if(
            obj.getTechnologyVersion(table.connector).getOperatingSystemVersion(table.connector).getPkey()
            != getHttpdSite().getAOServer().getServer().getOperatingSystemVersion().getPkey()
        ) {
            throw new SQLException("resource/operating system version mismatch on HttpdTomcatSite: #"+pkey);
        }
        // Make sure version shared JVM if is a shared site
        HttpdTomcatSharedSite sharedSite = getHttpdTomcatSharedSite();
        if(sharedSite!=null) {
            if(
                obj.pkey
                != sharedSite.getHttpdSharedTomcat().getHttpdTomcatVersion().pkey
            ) {
                throw new SQLException("HttpdTomcatSite/HttpdSharedTomcat version mismatch on HttpdTomcatSite: #"+pkey);
            }
        }
        return obj;
    }

    public List<HttpdWorker> getHttpdWorkers() throws IOException, SQLException {
        return table.connector.httpdWorkers.getHttpdWorkers(this);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.HTTPD_TOMCAT_SITES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        version=result.getInt(2);
        use_apache=result.getBoolean(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        version=in.readCompressedInt();
        use_apache=in.readBoolean();
    }

    public String startJVM() throws IOException, SQLException {
        AOServConnection connection=table.connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.START_JVM.ordinal());
            out.writeCompressedInt(pkey);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) return in.readBoolean()?in.readUTF():null;
            AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            table.connector.releaseConnection(connection);
        }
    }

    public String stopJVM() throws IOException, SQLException {
        AOServConnection connection=table.connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.STOP_JVM.ordinal());
            out.writeCompressedInt(pkey);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) return in.readBoolean()?in.readUTF():null;
            AOServProtocol.checkResult(code, in);
            throw new IOException("Unexpected response code: "+code);
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            table.connector.releaseConnection(connection);
        }
    }

    @Override
    String toStringImpl() throws SQLException, IOException {
        return getHttpdSite().toStringImpl();
    }

    public boolean useApache() {
        return use_apache;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(version);
        out.writeBoolean(use_apache);
    }
}