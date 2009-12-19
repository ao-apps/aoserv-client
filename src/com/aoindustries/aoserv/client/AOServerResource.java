package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A <code>Resource</code> that exists on an <code>AOServer</code>.  All resources on a server must
 * be removed before the related <code>BusinessServer</code> may be removed.
 *
 * @see  BusinessServer
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerResource extends CachedObjectIntegerKey<AOServerResource> {

    static final int
        COLUMN_RESOURCE=0,
        COLUMN_AO_SERVER=1
    ;
    static final String COLUMN_RESOURCE_name = "resource";
    static final String COLUMN_AO_SERVER_name = "ao_server";

    int ao_server;

    /**
     * Gets the resource that this represents.
     */
    public Resource getResource() throws IOException, SQLException {
        Resource re = table.connector.getResources().get(pkey);
        if(re==null) throw new SQLException("Unable to find Resource: "+pkey);
        return re;
    }

    /**
     * Gets the server that this resource is on.
     */
    public AOServer getAoServer() throws SQLException, IOException {
        AOServer s=table.connector.getAoServers().get(ao_server);
        if(s==null) throw new SQLException("Unable to find AOServer: "+ao_server);
        return s;
    }

    private Server getServer() throws SQLException, IOException {
        Server s=table.connector.getServers().get(ao_server);
        if(s==null) throw new SQLException("Unable to find Server: "+ao_server);
        return s;
    }

    /**
     * Gets the <code>BusinessServer</code> that this depends on.  This resource
     * must be removed before the business' access to the server may be revoked.
     * This may be filtered.
     */
    public BusinessServer getBusinessServer() throws IOException, SQLException {
        return table.connector.getBusinessServers().getBusinessServer(getResource().accounting, ao_server);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_RESOURCE: return pkey;
            case COLUMN_AO_SERVER: return ao_server;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.AO_SERVER_RESOURCES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        ao_server = result.getInt(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        ao_server = in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ao_server);
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getResource(),
            getAoServer(),
            getBusinessServer()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getDependentObjectByResourceType()
        );
    }

    private AOServObject getDependentObjectByResourceType() throws IOException, SQLException {
        String resource_type = getResource().resource_type;
        AOServObject obj;
        if(resource_type.equals(ResourceType.MYSQL_SERVER)) obj = getMySQLServer();
        else throw new AssertionError("Unexpected resource type: "+resource_type);
        if(obj==null) throw new SQLException("Type-specific aoserver resource object not found: "+pkey);
        return obj;
    }

    public MySQLServer getMySQLServer() throws IOException, SQLException {
        return table.connector.getMysqlServers().get(pkey);
    }
}
