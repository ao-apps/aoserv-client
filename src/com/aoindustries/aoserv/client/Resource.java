package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A <code>Resource</code> represents one accountable item.  For the purposes
 * of controlling disable/enable/remove sequence, it also has a set of
 * dependencies.
 *
 * @author  AO Industries, Inc.
 */
final public class Resource extends AOServObjectIntegerKey<Resource> implements BeanFactory<com.aoindustries.aoserv.client.beans.Resource> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final String resourceType;
    final private String accounting;
    final private Timestamp created;
    final private UserId createdBy;
    final private Integer disableLog;
    final private Timestamp lastEnabled;

    public Resource(
        ResourceService<?,?> service,
        int pkey,
        String resourceType,
        String accounting,
        Timestamp created,
        UserId createdBy,
        Integer disableLog,
        Timestamp lastEnabled
    ) {
        super(service, pkey);
        this.resourceType = resourceType.intern();
        this.accounting = accounting.intern();
        this.created = created;
        this.createdBy = createdBy.intern();
        this.disableLog = disableLog;
        this.lastEnabled = lastEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Resource other) throws RemoteException {
        int diff = AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(accounting, other.accounting);
        if(diff!=0) return diff;
        diff = resourceType.equals(other.resourceType) ? 0 : getResourceType().compareTo(other.getResourceType());
        if(diff!=0) return diff;
        return AOServObjectUtils.compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique pkey")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_RESOURCE_TYPE = "resource_type";
    @SchemaColumn(order=1, name=COLUMN_RESOURCE_TYPE, index=IndexType.INDEXED, description="the type of resource")
    public ResourceType getResourceType() throws RemoteException {
        ResourceType r=getService().getConnector().getResourceTypes().get(resourceType);
        if(r==null) throw new RemoteException("Unable to find ResourceType: "+resourceType);
        return r;
    }

    /**
     * Gets the business that is responsible for any charges caused by this resource.
     * This may be filtered.
     */
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=2, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business that owns this resource")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().get(accounting);
    }

    /**
     * Gets the time this was initially created.
     */
    @SchemaColumn(order=3, name="created", description="the time the resources was created")
    public Timestamp getCreated() {
    	return created;
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_CREATED_BY = "created_by";
    @SchemaColumn(order=4, name=COLUMN_CREATED_BY, index=IndexType.INDEXED, description="the administrator who created the resource")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().get(createdBy);
    }

    @SchemaColumn(order=5, name="disable_log", description="indicates the resource is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        DisableLog obj=getService().getConnector().getDisableLogs().get(disableLog);
        if(obj==null) throw new RemoteException("Unable to find DisableLog: "+disableLog);
        return obj;
    }

    /**
     * Gets the time this resource was last enabled.  Initially this will be the
     * same as the created time.  This is used to pro-rate billing.
     */
    @SchemaColumn(order=6, name="last_enabled", description="the time the resources was last enabled or the creation time if never disabled")
    public Timestamp getLastEnabled() {
        return lastEnabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Resource getBean() {
        return new com.aoindustries.aoserv.client.beans.Resource(key, resourceType, accounting, created, createdBy.getBean(), disableLog, lastEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    /**
     * Gets the set of all resources this depends on, topologically sorted
     * from most distant to closest.
     */
    public List<Resource> getAllDependencies() throws RemoteException {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Gets the set of all resources that are dependent on this, topologically
     * sorted from most distant to closest.
     */
    public List<Resource> getAllDependentResources() throws RemoteException {
        // TODO
        return Collections.emptyList();
    }

    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getResourceType(),
            getBusiness(),
            getCreatedBy(),
            getDisableLog()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getDependentObjectByResourceType(),
            getAoServerResource()
        );
    }

    private AOServObject getDependentObjectByResourceType() throws RemoteException {
        AOServObject obj;
        if(resourceType.equals(ResourceType.Constant.mysql_database.name())) return null; // Is an ao_server_resource
        else if(resourceType.equals(ResourceType.Constant.mysql_server.name())) return null; // Is an ao_server_resource
        else if(resourceType.equals(ResourceType.Constant.mysql_user.name())) return null; // Is an ao_server_resource
        else if(resourceType.equals(ResourceType.Constant.postgresql_database.name())) return null; // Is an ao_server_resource
        else if(resourceType.equals(ResourceType.Constant.postgresql_server.name())) return null; // Is an ao_server_resource
        else if(resourceType.equals(ResourceType.Constant.postgresql_user.name())) return null; // Is an ao_server_resource
        else if(
            // linux_accounts
            resourceType.equals(ResourceType.Constant.email_inbox.name())
            || resourceType.equals(ResourceType.Constant.ftponly_account.name())
            || resourceType.equals(ResourceType.Constant.shell_account.name())
            || resourceType.equals(ResourceType.Constant.system_account.name())
        ) return null; // Is an ao_server_resource
        else if(
            // linux_groups
            resourceType.equals(ResourceType.Constant.shell_group.name())
            || resourceType.equals(ResourceType.Constant.system_group.name())
        ) return null; // Is an ao_server_resource
        else if(
            // httpd_sites
            resourceType.equals(ResourceType.Constant.httpd_jboss_site.name())
            || resourceType.equals(ResourceType.Constant.httpd_static_site.name())
            || resourceType.equals(ResourceType.Constant.httpd_tomcat_shared_site.name())
            || resourceType.equals(ResourceType.Constant.httpd_tomcat_std_site.name())
        ) return null; // Is an ao_server_resource
        else throw new AssertionError("Unexpected resource type: "+resourceType);
        // TODO: if(obj==null) throw new SQLException("Type-specific resource object not found: "+pkey);
        // TODO: return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public boolean isDisabled() {
        return disableLog!=null;
    }*/

    /**
     * Gets the reasons this resource would not be enableable, even if all dependencies were enabled.
     * In order to be enabled, this resource must:
     * <ol>
     *   <li>Be currently disabled</li>
     *   <li>Be enableable by the current user</li>
     *   <li>Not be restricted by any type-specific rules</li>
     * <ol>
     *
     * @return  an empty <code>List</code> if this resource would be enableable given all dependencies were enabled, or a list of reasons
     */
    /* TODO
    public List<Reason> getCannotEnableReasons(Locale userLocale) throws IOException, SQLException {
        List<Reason> reasons = new ArrayList<Reason>();

        // Be currently disabled
        DisableLog dl=getDisableLog();
        if(dl==null) reasons.add(new Reason(ApplicationResources.accessor.getMessage(userLocale, "Resource.getCannotEnableReasons.notDisabled"), this));
        else {
            // Be enableable by the current user
            if(!dl.canEnable()) reasons.add(new Reason(ApplicationResources.accessor.getMessage(userLocale, "Resource.getCannotEnableReasons.notAllowed"), this));
        }

        // TODO: Not be restricted by any type-specific rules

        return reasons;
    }
    */
    /**
     * Gets all the reasons this resource may not be enabled, including reasons why any of the dependencies
     * may not be enabled.
     *
     * @return  an empty <code>List</code> if this resource may be enabled, or a list of reasons
     *
     * @see  #getAllDependencies() for the order these are returned
     */
    /* TODO
    public List<Reason> getAllCannotEnableReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }
     */

    /**
     * Enables all disabled dependencies and then this resource.
     *
     * @see  #getAllDependencies() for the order these are enabled
     */
    /* TODO
    public void enable() throws IOException, SQLException {
        // TODO: Add here per type
        throw new AssertionError("Unexpected resource type: "+resource_type);
    }
    */
    /**
     * Gets the reasons this resource would not be disableable, even if all dependent resources were disabled.
     *
     * @return  an empty <code>List</code> if this resource would be disableable given all dependent resources were disabled, or a list of reasons
     */
    /* TODO
    public List<Reason> getCannotDisableReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }
    */
    /**
     * Gets all the reasons this resource may not be disabled, including reasons why any of the dependent resources
     * may not be disabled.
     *
     * @return  an empty <code>List</code> if this resource may be disabled, or a list of reasons
     *
     * @see  #getAllDependentResources() for the order these are returned
     */
    /* TODO
    public List<Reason> getAllCannotDisableReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }
    */
    /**
     * Disables all enabled dependent resources and then this resource.
     *
     * @see  #getAllDependentResources() for the order these are disabled
     */
    /* TODO
    public void disable(DisableLog dl) throws IOException, SQLException {
        // TODO: Add here per type
        throw new AssertionError("Unexpected resource type: "+resource_type);
    }
     */
    /**
     * Gets the reasons this resource would not be removable, even if all dependent resources were removed.
     *
     * @return  an empty <code>List</code> if this resource would be removable given all dependent resources were removed, or a list of reasons
     */
    /* TODO
    public List<Reason> getCannotRemoveReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }
    */
    /**
     * Gets all the reasons this resource may not be removed, including reasons why any of the dependent resources
     * may not be removed.
     *
     * @return  an empty <code>List</code> if this resource may be removed, or a list of reasons
     *
     * @see  #getAllDependentResources() for the order these are returned
     */
    /* TODO
    public List<Reason> getAllCannotRemoveReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }
    */
    /**
     * Removes all dependent resources and then this resource.
     *
     * @see  #getAllDependentResources() for the order these are removed
     */
    /* TODO
    public void remove() throws IOException, SQLException {
        // TODO: Add here per type
        throw new AssertionError("Unexpected resource type: "+resource_type);
    }
    */
    // </editor-fold>
}
