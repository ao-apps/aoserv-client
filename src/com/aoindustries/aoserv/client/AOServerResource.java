package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A <code>Resource</code> that exists on an <code>AOServer</code>.  All resources on a server must
 * be removed before the related <code>BusinessServer</code> may be removed.
 *
 * @see  BusinessServer
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerResource extends AOServObjectIntegerKey<AOServerResource> implements BeanFactory<com.aoindustries.aoserv.client.beans.AOServerResource> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final int aoServer;

    public AOServerResource(AOServerResourceService<?,?> service, int resource, int aoServer) {
        super(service, resource);
        this.aoServer = aoServer;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(AOServerResource other) throws RemoteException {
        return key==other.key ? 0 : getResource().compareTo(other.getResource());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the resource that this represents.
     */
    @SchemaColumn(order=0, name="resource", index=IndexType.PRIMARY_KEY, description="a resource id")
    public Resource getResource() throws RemoteException {
        return getService().getConnector().getResources().get(key);
    }

    /**
     * Gets the server that this resource is on.
     */
    static final String COLUMN_AO_SERVER = "ao_server";
    @SchemaColumn(order=1, name=COLUMN_AO_SERVER, index=IndexType.INDEXED, description="the ao_server")
    public AOServer getAoServer() throws RemoteException {
        return getService().getConnector().getAoServers().get(aoServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.AOServerResource getBean() {
        return new com.aoindustries.aoserv.client.beans.AOServerResource(key, aoServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getResource(),
            getAoServer()
            // TODO: getBusinessServer()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            getDependentObjectByResourceType()
        );
    }

    private AOServObject getDependentObjectByResourceType() throws RemoteException {
        String resourceType = getResource().resourceType;
        AOServObject obj;
        if(resourceType.equals(ResourceType.Constant.mysql_database.name())) obj = getMysqlDatabase();
        else if(resourceType.equals(ResourceType.Constant.mysql_server.name())) obj = getMysqlServer();
        else if(resourceType.equals(ResourceType.Constant.mysql_user.name())) obj = getMysqlUser();
        else if(resourceType.equals(ResourceType.Constant.postgresql_database.name())) obj = getPostgresDatabase();
        else if(resourceType.equals(ResourceType.Constant.postgresql_server.name())) obj = getPostgresServer();
        else if(resourceType.equals(ResourceType.Constant.postgresql_user.name())) obj = getPostgresUser();
        else if(
            // linux_accounts
            resourceType.equals(ResourceType.Constant.email_inbox.name())
            || resourceType.equals(ResourceType.Constant.ftponly_account.name())
            || resourceType.equals(ResourceType.Constant.shell_account.name())
            || resourceType.equals(ResourceType.Constant.system_account.name())
        ) obj = null; // TODO: getLinuxAccount();
        else if(
            // linux_groups
            resourceType.equals(ResourceType.Constant.shell_group.name())
            || resourceType.equals(ResourceType.Constant.system_group.name())
        ) obj = null; // TODO: getLinuxGroup();
        else throw new AssertionError("Unexpected resource type: "+resourceType);
        if(obj==null) throw new RemoteException("Type-specific aoserver resource object not found: "+key);
        return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /**
     * Gets the <code>BusinessServer</code> that this depends on.  This resource
     * must be removed before the business' access to the server may be revoked.
     * This may be filtered.
     */
    /* TODO
    public BusinessServer getBusinessServer() throws IOException, SQLException {
        return getService().getConnector().getBusinessServers().getBusinessServer(getResource().accounting, ao_server);
    }
    */
    public MySQLDatabase getMysqlDatabase() throws RemoteException {
        return getService().getConnector().getMysqlDatabases().get(key);
    }

    public MySQLServer getMysqlServer() throws RemoteException {
        return getService().getConnector().getMysqlServers().get(key);
    }

    public MySQLUser getMysqlUser() throws RemoteException {
        return getService().getConnector().getMysqlUsers().get(key);
    }

    public PostgresDatabase getPostgresDatabase() throws RemoteException {
        return getService().getConnector().getPostgresDatabases().get(key);
    }

    public PostgresServer getPostgresServer() throws RemoteException {
        return getService().getConnector().getPostgresServers().get(key);
    }

    public PostgresUser getPostgresUser() throws RemoteException {
        return getService().getConnector().getPostgresUsers().get(key);
    }
    // </editor-fold>
}
