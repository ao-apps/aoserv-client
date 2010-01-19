package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>ResourceType</code> is a measurable hardware resource.  A <code>PackageDefinition</code>
 * comes with a set of resources, and when those <code>PackageDefinitionLimit</code>s are exceeded,
 * an additional amount is charged to the <code>Business</code>.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
final public class ResourceType extends AOServObjectStringKey<ResourceType> implements BeanFactory<com.aoindustries.aoserv.client.beans.ResourceType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        // linux_account types
        SHELL_ACCOUNT="shell_account",
        EMAIL_INBOX="email_inbox",
        FTPONLY_ACCOUNT="ftponly_account",
        SYSTEM_ACCOUNT="system_account",
        // linux_group types
        SHELL_GROUP="shell_group",
        SYSTEM_GROUP="system_group",
        // httpd_site types
        HTTPD_JBOSS_SITE="httpd_jboss_site",
        HTTPD_STATIC_SITE="httpd_static_site",
        HTTPD_TOMCAT_SHARED_SITE="httpd_tomcat_shared_site",
        HTTPD_TOMCAT_STD_SITE="httpd_tomcat_std_site",
        // others
        CVS_REPOSITORY = "cvs_repository",
        DISTRIBUTION_SCAN="distribution_scan",
        DNS_RECORD = "dns_record",
        DNS_ZONE = "dns_zone",
        HTTPD_SERVER = "httpd_server",
        IP_ADDRESS="ip_address",
        MYSQL_DATABASE="mysql_database",
        MYSQL_SERVER="mysql_server",
        MYSQL_USER="mysql_user",
        POSTGRESQL_DATABASE="postgresql_database",
        POSTGRESQL_SERVER="postgresql_server",
        POSTGRESQL_USER="postgresql_user",
        PRIVATE_FTP_SERVER="private_ftp_server"
    ;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public ResourceType(ResourceTypeService<?,?> service, String name) {
        super(service, name);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Columns">
    /**
     * Gets the unique name of this resource type.
     */
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the name of the resource type")
    public String getName() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.ResourceType getBean() {
        return new com.aoindustries.aoserv.client.beans.ResourceType(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getDependentObjectByResourceType()
            ),
            getResources()
        );
    }

    private AOServObject getDependentObjectByResourceType() throws RemoteException {
        String key = getKey();
        AOServObject obj;
        if(
            key.equals(ResourceType.EMAIL_INBOX)
            || key.equals(ResourceType.FTPONLY_ACCOUNT)
            || key.equals(ResourceType.SHELL_ACCOUNT)
            || key.equals(ResourceType.SYSTEM_ACCOUNT)
        ) return getLinuxAccountType();
        if(
            key.equals(ResourceType.SHELL_GROUP)
            || key.equals(ResourceType.SYSTEM_GROUP)
        ) return getLinuxGroupType();
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Resource> getResources() throws RemoteException {
        return getService().getConnector().getResources().filterIndexed(Resource.COLUMN_RESOURCE_TYPE, this);
    }

    public LinuxAccountType getLinuxAccountType() throws RemoteException {
        return getService().getConnector().getLinuxAccountTypes().get(getKey());
    }

    public LinuxGroupType getLinuxGroupType() throws RemoteException {
        return getService().getConnector().getLinuxGroupTypes().get(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDisplayUnit(Locale userLocale, int quantity) {
        if(quantity==1) return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+getKey()+".singularDisplayUnit", quantity);
        else return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+getKey()+".pluralDisplayUnit", quantity);
    }

    public String getPerUnit(Locale userLocale, Object amount) {
        return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+getKey()+".perUnit", amount);
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+getKey()+".toString");
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<Resource> getResources(AOServConnector connector) throws IOException, SQLException {
        return connector.getResources().getIndexedRows(Resource.COLUMN_RESOURCE_TYPE, pkey);
    }
     */
    // </editor-fold>
}
