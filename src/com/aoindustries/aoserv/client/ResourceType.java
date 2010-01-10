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

    public enum Constant {
        aoserv_daemon,
        aoserv_master,
        bandwidth,
        consulting,
        disk,
        distribution_scan,
        drupal,
        failover,
        hardware_disk_7200_120,
        ip,
        joomla,
        mysql_database,
        mysql_replication,
        mysql_server,
        mysql_user,
        postgresql_database,
        postgresql_server,
        postgresql_user,
        rack,
        server_database,
        server_enterprise,
        server_p4,
        server_scsi,
        server_xeon,
        sysadmin,
        httpd_server,
        httpd_shared_tomcat,
        // linux_account types
        shell_account,
        email_inbox,
        ftponly_account,
        system_account,
        // linux_group types
        shell_group,
        system_group,
        // httpd_site types
        httpd_jboss_site,
        httpd_static_site,
        httpd_tomcat_shared_site,
        httpd_tomcat_std_site
    }
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
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.ResourceType getBean() {
        return new com.aoindustries.aoserv.client.beans.ResourceType(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getLinuxAccountType(),
                getLinuxGroupType()
            ),
            getResources()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<Resource> getResources() throws RemoteException {
        return getService().getConnector().getResources().filterIndexed(Resource.COLUMN_RESOURCE_TYPE, this);
    }

    public LinuxAccountType getLinuxAccountType() throws RemoteException {
        return getService().getConnector().getLinuxAccountTypes().get(key);
    }

    public LinuxGroupType getLinuxGroupType() throws RemoteException {
        return getService().getConnector().getLinuxGroupTypes().get(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    public String getDisplayUnit(Locale userLocale, int quantity) {
        if(quantity==1) return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+key+".singularDisplayUnit", quantity);
        else return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+key+".pluralDisplayUnit", quantity);
    }

    public String getPerUnit(Locale userLocale, Object amount) {
        return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+key+".perUnit", amount);
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "ResourceType."+key+".toString");
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
