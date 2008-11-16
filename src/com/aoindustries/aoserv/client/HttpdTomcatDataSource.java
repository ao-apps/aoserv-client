package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Represents one data source within a <code>HttpdTomcatContext</code>.
 *
 * @see  HttpdTomcatContext
 *
 * @version  1.5
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatDataSource extends CachedObjectIntegerKey<HttpdTomcatDataSource> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_TOMCAT_CONTEXT=1
    ;
    static final String COLUMN_TOMCAT_CONTEXT_name = "tomcat_context";
    static final String COLUMN_NAME_name = "name";

    int tomcat_context;
    String name;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int maxActive;
    private int maxIdle;
    private int maxWait;
    private String validationQuery;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_TOMCAT_CONTEXT: return tomcat_context;
            case 2: return name;
            case 3: return driverClassName;
            case 4: return url;
            case 5: return username;
            case 6: return password;
            case 7: return maxActive;
            case 8: return maxIdle;
            case 9: return maxWait;
            case 10: return validationQuery;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdTomcatContext getHttpdTomcatContext() {
	HttpdTomcatContext obj=table.connector.httpdTomcatContexts.get(tomcat_context);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find HttpdTomcatContext: "+tomcat_context));
	return obj;
    }

    public String getName() {
        return name;
    }
    
    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }
    
    public String getValidationQuery() {
        return validationQuery;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        tomcat_context=result.getInt(2);
        name=result.getString(3);
        driverClassName=result.getString(4);
        url=result.getString(5);
        username=result.getString(6);
        password=result.getString(7);
        maxActive=result.getInt(8);
        maxIdle=result.getInt(9);
        maxWait=result.getInt(10);
        validationQuery=result.getString(11);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        tomcat_context=in.readCompressedInt();
        name=in.readUTF();
        driverClassName=in.readUTF();
        url=in.readUTF();
        username=in.readUTF().intern();
        password=in.readUTF();
        maxActive=in.readCompressedInt();
        maxIdle=in.readCompressedInt();
        maxWait=in.readCompressedInt();
        validationQuery=in.readNullUTF();
    }

    public void remove() {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.REMOVE, SchemaTable.TableID.HTTPD_TOMCAT_DATA_SOURCES, pkey);
    }

    public void update(
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
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.UPDATE_HTTPD_TOMCAT_DATA_SOURCE,
            pkey,
            name,
            driverClassName,
            url,
            username,
            password,
            maxActive,
            maxIdle,
            maxWait,
            validationQuery==null ? "" : validationQuery
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(tomcat_context);
        out.writeUTF(name);
        out.writeUTF(driverClassName);
        out.writeUTF(url);
        out.writeUTF(username);
        out.writeUTF(password);
        out.writeCompressedInt(maxActive);
        out.writeCompressedInt(maxIdle);
        out.writeCompressedInt(maxWait);
        out.writeNullUTF(validationQuery);
    }
}
