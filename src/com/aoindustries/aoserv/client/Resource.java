/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
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
    private String resourceType;
    private AccountingCode accounting;
    final private long created;
    private UserId createdBy;
    final private Integer disableLog;
    final private long lastEnabled;

    public Resource(
        ResourceService<?,?> service,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled
    ) {
        super(service, pkey);
        this.resourceType = resourceType;
        this.accounting = accounting;
        this.created = created;
        this.createdBy = createdBy;
        this.disableLog = disableLog;
        this.lastEnabled = lastEnabled;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        resourceType = intern(resourceType);
        accounting = intern(accounting);
        createdBy = intern(createdBy);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Resource other) throws RemoteException {
        int diff = accounting==other.accounting ? 0 : getBusiness().compareToImpl(other.getBusiness()); // OK - interned
        if(diff!=0) return diff;
        diff = resourceType==other.resourceType ? 0 : getResourceType().compareToImpl(other.getResourceType()); // OK - interned
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
        return getService().getConnector().getResourceTypes().get(resourceType);
    }

    /**
     * Gets the business that is responsible for any charges caused by this resource.
     * This may be filtered.
     */
    static final String COLUMN_ACCOUNTING = "accounting";
    @SchemaColumn(order=2, name=COLUMN_ACCOUNTING, index=IndexType.INDEXED, description="the business that owns this resource")
    public Business getBusiness() throws RemoteException {
        return getService().getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
    }

    /**
     * Gets the time this was initially created.
     */
    @SchemaColumn(order=3, name="created", description="the time the resources was created")
    public Timestamp getCreated() {
    	return new Timestamp(created);
    }

    /**
     * May be filtered.
     */
    static final String COLUMN_CREATED_BY = "created_by";
    @SchemaColumn(order=4, name=COLUMN_CREATED_BY, index=IndexType.INDEXED, description="the administrator who created the resource")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        try {
            return getService().getConnector().getBusinessAdministrators().get(createdBy);
        } catch(NoSuchElementException err) {
            // Filtered
            return null;
        }
    }

    static final String COLUMN_DISABLE_LOG = "disable_log";
    @SchemaColumn(order=5, name=COLUMN_DISABLE_LOG, index=IndexType.INDEXED, description="indicates the resource is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getService().getConnector().getDisableLogs().get(disableLog);
    }

    /**
     * Gets the time this resource was last enabled.  Initially this will be the
     * same as the created time.  This is used to pro-rate billing.
     */
    @SchemaColumn(order=6, name="last_enabled", description="the time the resources was last enabled or the creation time if never disabled")
    public Timestamp getLastEnabled() {
        return new Timestamp(lastEnabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.Resource getBean() {
        return new com.aoindustries.aoserv.client.beans.Resource(key, resourceType, getBean(accounting), getCreated(), getBean(createdBy), disableLog, getLastEnabled());
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
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getResourceType());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getBusiness());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getCreatedBy());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDisableLog());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getDependentObjectByResourceType());
        return unionSet;
    }

    private static final Set<String> aoServerResourceTypes = new HashSet<String>(
        Arrays.asList(
            ResourceType.MYSQL_DATABASE,
            ResourceType.MYSQL_SERVER,
            ResourceType.MYSQL_USER,
            ResourceType.POSTGRESQL_DATABASE,
            ResourceType.POSTGRESQL_SERVER,
            ResourceType.POSTGRESQL_USER,
            ResourceType.EMAIL_INBOX,
            ResourceType.FTPONLY_ACCOUNT,
            ResourceType.SHELL_ACCOUNT,
            ResourceType.SYSTEM_ACCOUNT,
            ResourceType.SHELL_GROUP,
            ResourceType.SYSTEM_GROUP,
            ResourceType.HTTPD_JBOSS_SITE,
            ResourceType.HTTPD_STATIC_SITE,
            ResourceType.HTTPD_TOMCAT_SHARED_SITE,
            ResourceType.HTTPD_TOMCAT_STD_SITE,
            ResourceType.CVS_REPOSITORY,
            ResourceType.HTTPD_SERVER,
            ResourceType.PRIVATE_FTP_SERVER
        )
    );

    private static final Set<String> serverResourceTypes = new HashSet<String>(
        Arrays.asList(
            ResourceType.IP_ADDRESS
        )
    );

    private AOServObject getDependentObjectByResourceType() throws RemoteException {
        // The following string comparisons safely use == instead of .equals because resourceType is interned.
        if(resourceType==ResourceType.DNS_RECORD) return getDnsRecord();
        if(resourceType==ResourceType.DNS_ZONE) return getDnsZone();
        if(aoServerResourceTypes.contains(resourceType)) return getAoServerResource();
        if(serverResourceTypes.contains(resourceType)) return getServerResource();
        /* TODO: else*/ throw new AssertionError("Unexpected resource type: "+resourceType);
        // TODO: if(obj==null) throw new SQLException("Type-specific resource object not found: "+pkey);
        // TODO: return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    public ServerResource getServerResource() throws RemoteException {
        return getService().getConnector().getServerResources().get(key);
    }

    public DnsRecord getDnsRecord() throws RemoteException {
        return getService().getConnector().getDnsRecords().get(key);
    }

    public DnsZone getDnsZone() throws RemoteException {
        return getService().getConnector().getDnsZones().get(key);
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
    public List<Reason> getCannotEnableReasons() throws IOException, SQLException {
        List<Reason> reasons = new ArrayList<Reason>();

        // Be currently disabled
        DisableLog dl=getDisableLog();
        if(dl==null) reasons.add(new Reason(ApplicationResources.accessor.getMessage("Resource.getCannotEnableReasons.notDisabled"), this));
        else {
            // Be enableable by the current user
            if(!dl.canEnable()) reasons.add(new Reason(ApplicationResources.accessor.getMessage("Resource.getCannotEnableReasons.notAllowed"), this));
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
    public List<Reason> getAllCannotEnableReasons() throws IOException, SQLException {
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
    public List<Reason> getCannotDisableReasons() throws IOException, SQLException {
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
    public List<Reason> getAllCannotDisableReasons() throws IOException, SQLException {
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
    public List<Reason> getCannotRemoveReasons() throws IOException, SQLException {
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
    public List<Reason> getAllCannotRemoveReasons() throws IOException, SQLException {
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
