package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Represents one context within a <code>HttpdTomcatSite</code>.
 *
 * @see  HttpdTomcatSite
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatContext extends CachedObjectIntegerKey<HttpdTomcatContext> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_TOMCAT_SITE=1
    ;
    static final String COLUMN_TOMCAT_SITE_name = "tomcat_site";
    static final String COLUMN_PATH_name = "path";

    /**
     * These are the default values for a new site.
     */
    public static final String DEFAULT_CLASS_NAME=null;
    public static final boolean DEFAULT_COOKIES=true;
    public static final boolean DEFAULT_CROSS_CONTEXT=false;
    public static final boolean DEFAULT_OVERRIDE=false;
    public static final boolean DEFAULT_PRIVILEGED=false;
    public static final boolean DEFAULT_RELOADABLE=false;
    public static final boolean DEFAULT_USE_NAMING=true;
    public static final String DEFAULT_WRAPPER_CLASS=null;
    public static final int DEFAULT_DEBUG=0;
    public static final String DEFAULT_WORK_DIR=null;

    /**
     * The ROOT webapp details
     */
    public static final String ROOT_PATH="";
    public static final String ROOT_DOC_BASE="ROOT";

    int tomcat_site;
    private String class_name;
    private boolean cookies;
    private boolean cross_context;
    private String doc_base;
    private boolean override;
    String path;
    private boolean privileged;
    private boolean reloadable;
    private boolean use_naming;
    private String wrapper_class;
    private int debug;
    private String work_dir;

    public int addHttpdTomcatDataSource(
        String name,
        String driverClassName,
        String url,
        String username,
        String password,
        int maxActive,
        int maxIdle,
        int maxWait,
        String validationQuery
    ) {
        return table.connector.httpdTomcatDataSources.addHttpdTomcatDataSource(this, name, driverClassName, url, username, password, maxActive, maxIdle, maxWait, validationQuery);
    }

    public int addHttpdTomcatParameter(String name, String value, boolean override, String description) {
        return table.connector.httpdTomcatParameters.addHttpdTomcatParameter(this, name, value, override, description);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(path.length()==0) reasons.add(new CannotRemoveReason<HttpdTomcatContext>("Not allowed to remove the root context", this));
        return reasons;
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_TOMCAT_SITE: return Integer.valueOf(tomcat_site);
            case 2: return class_name;
            case 3: return cookies?Boolean.TRUE:Boolean.FALSE;
            case 4: return cross_context?Boolean.TRUE:Boolean.FALSE;
            case 5: return doc_base;
            case 6: return override?Boolean.TRUE:Boolean.FALSE;
            case 7: return path;
            case 8: return privileged?Boolean.TRUE:Boolean.FALSE;
            case 9: return reloadable?Boolean.TRUE:Boolean.FALSE;
            case 10: return use_naming?Boolean.TRUE:Boolean.FALSE;
            case 11: return wrapper_class;
            case 12: return Integer.valueOf(debug);
            case 13: return work_dir;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdTomcatSite getHttpdTomcatSite() {
	HttpdTomcatSite obj=table.connector.httpdTomcatSites.get(tomcat_site);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdTomcatSite: "+tomcat_site));
	return obj;
    }

    public String getClassName() {
        return class_name;
    }
    
    public boolean useCookies() {
        return cookies;
    }
    
    public boolean allowCrossContext() {
        return cross_context;
    }
    
    public String getDocBase() {
        return doc_base;
    }
    
    public boolean allowOverride() {
        return override;
    }
    
    public String getPath() {
        return path;
    }
    
    public boolean isPrivileged() {
        return privileged;
    }
    
    public boolean isReloadable() {
        return reloadable;
    }
    
    public boolean useNaming() {
        return use_naming;
    }
    
    public String getWrapperClass() {
        return wrapper_class;
    }
    
    public int getDebugLevel() {
        return debug;
    }
    
    public String getWorkDir() {
        return work_dir;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_CONTEXTS;
    }

    public List<HttpdTomcatDataSource> getHttpdTomcatDataSources() {
        return table.connector.httpdTomcatDataSources.getHttpdTomcatDataSources(this);
    }

    public HttpdTomcatDataSource getHttpdTomcatDataSource(String name) {
        return table.connector.httpdTomcatDataSources.getHttpdTomcatDataSource(this, name);
    }

    public List<HttpdTomcatParameter> getHttpdTomcatParameters() {
        return table.connector.httpdTomcatParameters.getHttpdTomcatParameters(this);
    }

    public HttpdTomcatParameter getHttpdTomcatParameter(String name) {
        return table.connector.httpdTomcatParameters.getHttpdTomcatParameter(this, name);
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        tomcat_site=result.getInt(2);
        class_name=result.getString(3);
        cookies=result.getBoolean(4);
        cross_context=result.getBoolean(5);
        doc_base=result.getString(6);
        override=result.getBoolean(7);
        path=result.getString(8);
        privileged=result.getBoolean(9);
        reloadable=result.getBoolean(10);
        use_naming=result.getBoolean(11);
        wrapper_class=result.getString(12);
        debug=result.getInt(13);
        work_dir=result.getString(14);
    }

    public static boolean isValidDocBase(String docBase) {
        return
            docBase.length()>1
            && docBase.charAt(0)=='/'
            && docBase.indexOf("//")==-1
            && docBase.indexOf("..")==-1
            && docBase.indexOf('"')==-1
            && docBase.indexOf('\\')==-1
            && docBase.indexOf('\n')==-1
            && docBase.indexOf('\r')==-1
        ;
    }

    public static boolean isValidPath(String path) {
        return path.length()==0 || isValidDocBase(path);
    }

    public static boolean isValidWorkDir(String workDir) {
        return workDir==null || isValidDocBase(workDir);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        tomcat_site=in.readCompressedInt();
        class_name=in.readNullUTF();
        cookies=in.readBoolean();
        cross_context=in.readBoolean();
        doc_base=in.readUTF();
        override=in.readBoolean();
        path=in.readUTF();
        privileged=in.readBoolean();
        reloadable=in.readBoolean();
        use_naming=in.readBoolean();
        wrapper_class=in.readNullUTF();
        debug=in.readCompressedInt();
        work_dir=in.readNullUTF();
    }

    public void setAttributes(
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
    ) {
        try {
            // Create the new profile
            IntList invalidateList;
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.CommandID.SET_HTTPD_TOMCAT_CONTEXT_ATTRIBUTES.ordinal());
                out.writeCompressedInt(pkey);
                out.writeNullUTF(className);
                out.writeBoolean(cookies);
                out.writeBoolean(crossContext);
                out.writeUTF(docBase);
                out.writeBoolean(override);
                out.writeUTF(path);
                out.writeBoolean(privileged);
                out.writeBoolean(reloadable);
                out.writeBoolean(useNaming);
                out.writeNullUTF(wrapperClass);
                out.writeCompressedInt(debug);
                out.writeNullUTF(workDir);
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
                table.connector.releaseConnection(connection);
            }
            table.connector.tablesUpdated(invalidateList);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void remove() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_TOMCAT_CONTEXTS, pkey);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(tomcat_site);
        out.writeNullUTF(class_name);
        out.writeBoolean(cookies);
        out.writeBoolean(cross_context);
        out.writeUTF(doc_base);
        out.writeBoolean(override);
        out.writeUTF(path);
        out.writeBoolean(privileged);
        out.writeBoolean(reloadable);
        out.writeBoolean(use_naming);
        out.writeNullUTF(wrapper_class);
        out.writeCompressedInt(debug);
        out.writeNullUTF(work_dir);
    }
}