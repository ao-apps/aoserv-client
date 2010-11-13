/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>Resource</code> that exists on an <code>AOServer</code>.  All resources on a server must
 * be removed before the related <code>BusinessServer</code> may be removed.
 *
 * @see  BusinessServer
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerResource extends AOServObjectIntegerKey implements Comparable<AOServerResource>, DtoFactory<com.aoindustries.aoserv.client.dto.AOServerResource> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final int aoServer;
    final int businessServer;

    public AOServerResource(AOServerResourceService<?,?> service, int resource, int aoServer, int businessServer) {
        super(service, resource);
        this.aoServer = aoServer;
        this.businessServer = businessServer;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(AOServerResource other) {
        try {
            return key==other.key ? 0 : getResource().compareTo(other.getResource());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the resource that this represents.
     */
    static final String COLUMN_RESOURCE = "resource";
    @SchemaColumn(order=0, name=COLUMN_RESOURCE, index=IndexType.PRIMARY_KEY, description="the resource id")
    public Resource getResource() throws RemoteException {
        return getService().getConnector().getResources().get(key);
    }

    static final String COLUMN_AO_SERVER = "ao_server";
    @SchemaColumn(order=1, name=COLUMN_AO_SERVER, index=IndexType.INDEXED, description="the server that this resource is on")
    public AOServer getAoServer() throws RemoteException {
        return getService().getConnector().getAoServers().get(aoServer);
    }

    static final String COLUMN_BUSINESS_SERVER = "business_server";
    @SchemaColumn(order=2, name=COLUMN_BUSINESS_SERVER, index=IndexType.INDEXED, description="the business server that this resource depends on")
    public BusinessServer getBusinessServer() throws RemoteException {
        return getService().getConnector().getBusinessServers().get(businessServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.AOServerResource getDto() {
        return new com.aoindustries.aoserv.client.dto.AOServerResource(key, aoServer, businessServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getResource());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusinessServer());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDependentObjectByResourceType());
        return unionSet;
    }

    private AOServObject getDependentObjectByResourceType() throws RemoteException {
        String resourceType = getResource().getResourceType().getName();
        AOServObject obj;
        if(resourceType==ResourceType.MYSQL_DATABASE) obj = getMysqlDatabase(); // OK - interned
        else if(resourceType==ResourceType.MYSQL_SERVER) obj = getMysqlServer(); // OK - interned
        else if(resourceType==ResourceType.MYSQL_USER) obj = getMysqlUser(); // OK - interned
        else if(resourceType==ResourceType.POSTGRESQL_DATABASE) obj = getPostgresDatabase(); // OK - interned
        else if(resourceType==ResourceType.POSTGRESQL_SERVER) obj = getPostgresServer(); // OK - interned
        else if(resourceType==ResourceType.POSTGRESQL_USER) obj = getPostgresUser(); // OK - interned
        else if(
            // linux_accounts
            resourceType==ResourceType.EMAIL_INBOX // OK - interned
            || resourceType==ResourceType.FTPONLY_ACCOUNT // OK - interned
            || resourceType==ResourceType.SHELL_ACCOUNT // OK - interned
            || resourceType==ResourceType.SYSTEM_ACCOUNT // OK - interned
        ) obj = getLinuxAccount();
        else if(
            // linux_groups
            resourceType==ResourceType.SHELL_GROUP // OK - interned
            || resourceType==ResourceType.SYSTEM_GROUP // OK - interned
        ) obj = getLinuxGroup();
        else if(
            // httpd_sites
            resourceType==ResourceType.HTTPD_JBOSS_SITE // OK - interned
            || resourceType==ResourceType.HTTPD_STATIC_SITE // OK - interned
            || resourceType==ResourceType.HTTPD_TOMCAT_SHARED_SITE // OK - interned
            || resourceType==ResourceType.HTTPD_TOMCAT_STD_SITE // OK - interned
        ) obj = getHttpdSite();
        else if(resourceType==ResourceType.CVS_REPOSITORY) obj = getCvsRepository(); // OK - interned
        else if(resourceType==ResourceType.HTTPD_SERVER) obj = getHttpdServer(); // OK - interned
        else if(resourceType==ResourceType.PRIVATE_FTP_SERVER) obj = getPrivateFtpServer(); // OK - interned
        else throw new AssertionError("Unexpected resource type: "+resourceType);
        if(obj==null) throw new RemoteException("Type-specific aoserver resource object not found: "+key);
        return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public CvsRepository getCvsRepository() throws RemoteException {
        return getService().getConnector().getCvsRepositories().get(key);
    }

    public HttpdServer getHttpdServer() throws RemoteException {
        return getService().getConnector().getHttpdServers().get(key);
    }

    public HttpdSite getHttpdSite() throws RemoteException {
        return getService().getConnector().getHttpdSites().get(key);
    }

    public LinuxAccount getLinuxAccount() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().get(key);
    }

    public LinuxGroup getLinuxGroup() throws RemoteException {
        return getService().getConnector().getLinuxGroups().get(key);
    }

    public MySQLDatabase getMysqlDatabase() throws RemoteException {
        return getService().getConnector().getMysqlDatabases().get(key);
    }

    public MySQLServer getMysqlServer() throws RemoteException {
        return getService().getConnector().getMysqlServers().get(key);
    }

    public MySQLUser getMysqlUser() throws RemoteException {
        return getService().getConnector().getMysqlUsers().get(key);
    }

    public PrivateFtpServer getPrivateFtpServer() throws RemoteException {
        return getService().getConnector().getPrivateFtpServers().get(key);
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
