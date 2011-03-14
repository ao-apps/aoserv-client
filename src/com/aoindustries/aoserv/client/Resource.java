/*
 * Copyright 2009-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A <code>Resource</code> represents one accountable item.  For the purposes
 * of controlling disable/enable/remove sequence, it also has a set of
 * dependencies.
 *
 * @author  AO Industries, Inc.
 */
public abstract class Resource extends AOServObjectIntegerKey {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    // TODO: private static final long serialVersionUID = 1L;

    private String resourceType;
    private AccountingCode accounting;
    final protected long created;
    private UserId createdBy;
    final protected Integer disableLog;
    final protected long lastEnabled;

    protected Resource(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled
    ) {
        super(connector, pkey);
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

    // <editor-fold defaultstate="collapsed" desc="Read-only access to fields for subclasses">
    protected String getResourceTypeName() {
        return resourceType;
    }
    protected AccountingCode getAccounting() {
        return accounting;
    }
    protected UserId getCreatedByUsername() {
        return createdBy;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    public static final Comparator<Resource> resourceComparator = new Comparator<Resource>() {
        @Override
        public int compare(Resource o1, Resource o2) {
            try {
                int diff = o1.accounting==o2.accounting ? 0 : o1.getBusiness().compareTo(o2.getBusiness()); // OK - interned
                if(diff!=0) return diff;
                diff = o1.resourceType==o2.resourceType ? 0 : o1.getResourceType().compareTo(o2.getResourceType()); // OK - interned
                if(diff!=0) return diff;
                return AOServObject.compare(o1.key, o2.key);
            } catch(RemoteException err) {
                throw new WrappedException(err);
            }
        }
    };
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_PKEY = getMethodColumn(Resource.class, "pkey");
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated unique pkey")
    public int getPkey() {
        return key;
    }

    public static final MethodColumn COLUMN_RESOURCE_TYPE = getMethodColumn(Resource.class, "resourceType");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the type of resource")
    public ResourceType getResourceType() throws RemoteException {
        return getConnector().getResourceTypes().get(resourceType);
    }

    /**
     * Gets the business that is responsible for any charges caused by this resource.
     * This may be filtered.
     */
    public static final MethodColumn COLUMN_BUSINESS = getMethodColumn(Resource.class, "business");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the business that owns this resource")
    public Business getBusiness() throws RemoteException {
        return getConnector().getBusinesses().filterUnique(Business.COLUMN_ACCOUNTING, accounting);
    }

    /**
     * Gets the time this was initially created.
     */
    @SchemaColumn(order=3, description="the time the resources was created")
    public Timestamp getCreated() {
    	return new Timestamp(created);
    }

    /**
     * May be filtered.
     */
    public static final MethodColumn COLUMN_CREATED_BY = getMethodColumn(Resource.class, "createdBy");
    @DependencySingleton
    @SchemaColumn(order=4, index=IndexType.INDEXED, description="the administrator who created the resource")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        try {
            return getConnector().getBusinessAdministrators().get(createdBy);
        } catch(NoSuchElementException err) {
            // Filtered
            return null;
        }
    }

    public static final MethodColumn COLUMN_DISABLE_LOG = getMethodColumn(Resource.class, "disableLog");
    @DependencySingleton
    @SchemaColumn(order=5, index=IndexType.INDEXED, description="indicates the resource is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disableLog==null) return null;
        return getConnector().getDisableLogs().get(disableLog);
    }
    public boolean isDisabled() {
        return disableLog!=null;
    }

    /**
     * Gets the time this resource was last enabled.  Initially this will be the
     * same as the created time.  This is used to pro-rate billing.
     */
    @SchemaColumn(order=6, description="the time the resources was last enabled or the creation time if never disabled")
    public Timestamp getLastEnabled() {
        return new Timestamp(lastEnabled);
    }
    protected static final int RESOURCE_LAST_COLUMN = 6;
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
