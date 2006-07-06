package com.aoindustries.aoserv.client;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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
 * Represents one parameter within a <code>HttpdTomcatContext</code>.
 *
 * @see  HttpdTomcatContext
 *
 * @version  1.5
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdTomcatParameter extends CachedObjectIntegerKey<HttpdTomcatParameter> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_TOMCAT_CONTEXT=1
    ;

    int tomcat_context;
    String name;
    private String value;
    private boolean override;
    private String description;

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_TOMCAT_CONTEXT: return tomcat_context;
            case 2: return name;
            case 3: return value;
            case 4: return override;
            case 5: return description;
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
    
    public String getValue() {
        return value;
    }

    public boolean getOverride() {
        return override;
    }
    
    public String getDescription() {
        return description;
    }

    protected int getTableIDImpl() {
	return SchemaTable.HTTPD_TOMCAT_PARAMETERS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        tomcat_context=result.getInt(2);
        name=result.getString(3);
        value=result.getString(4);
        override=result.getBoolean(5);
        description=result.getString(6);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        tomcat_context=in.readCompressedInt();
        name=in.readUTF();
        value=in.readUTF();
        override=in.readBoolean();
        description=readNullUTF(in);
    }

    public void remove() {
        table.connector.requestUpdateIL(AOServProtocol.REMOVE, SchemaTable.HTTPD_TOMCAT_PARAMETERS, pkey);
    }

    public void update(
        String name,
        String value,
        boolean override,
        String description
    ) {
        table.connector.requestUpdateIL(
            AOServProtocol.UPDATE_HTTPD_TOMCAT_PARAMETER,
            pkey,
            name,
            value,
            override,
            description==null ? "" : description
        );
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(tomcat_context);
        out.writeUTF(name);
        out.writeUTF(value);
        out.writeBoolean(override);
        writeNullUTF(out, description);
    }
}
