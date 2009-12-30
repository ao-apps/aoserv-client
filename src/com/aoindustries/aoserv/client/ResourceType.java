package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * A <code>ResourceType</code> is a measurable hardware resource.  A <code>PackageDefinition</code>
 * comes with a set of resources, and when those <code>PackageDefinitionLimit</code>s are exceeded,
 * an additional amount is charged to the <code>Business</code>.
 *
 * @see  PackageDefinition
 *
 * @author  AO Industries, Inc.
 */
// TODO: Make all AOServObject be a BeanFactory
final public class ResourceType extends AOServObjectStringKey<ResourceType> implements BeanFactory<com.aoindustries.aoserv.client.beans.ResourceType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        AOSERV_DAEMON="aoserv_daemon",
        AOSERV_MASTER="aoserv_master",
        BANDWIDTH="bandwidth",
        CONSULTING="consulting",
        DISK="disk",
        DISTRIBUTION_SCAN="distribution_scan",
        DRUPAL="drupal",
        EMAIL="email",
        FAILOVER="failover",
        HARDWARE_DISK_7200_120="hardware_disk_7200_120",
        HTTPD="httpd",
        IP="ip",
        JAVAVM="javavm",
        JOOMLA="joomla",
        MYSQL_REPLICATION="mysql_replication",
        MYSQL_SERVER = "mysql_server",
        RACK="rack",
        SERVER_DATABASE="server_database",
        SERVER_ENTERPRISE="server_enterprise",
        SERVER_P4="server_p4",
        SERVER_SCSI="server_scsi",
        SERVER_XEON="server_xeon",
        SITE="site",
        SYSADMIN="sysadmin",
        USER="user"
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
    @SchemaColumn(order=0, name="name", unique=true, description="the name of the resource type")
    public String getName() {
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.ResourceType getBean() {
        return new com.aoindustries.aoserv.client.beans.ResourceType(key);
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
