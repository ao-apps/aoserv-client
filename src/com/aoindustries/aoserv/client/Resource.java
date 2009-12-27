package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
final public class Resource extends AOServObjectIntegerKey<Resource> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String resource_type;
    final private String accounting;
    final private Timestamp created;
    final private String created_by;
    final private int disable_log;
    final private Timestamp last_enabled;

    public Resource(ResourceService<?,?> service, int pkey, String resource_type, String accounting, Timestamp created, String created_by, int disable_log, Timestamp last_enabled) {
        super(service, pkey);
        this.resource_type = resource_type.intern();
        this.accounting = accounting.intern();
        this.created = created;
        this.created_by = accounting.intern();
        this.disable_log = disable_log;
        this.last_enabled = last_enabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Resource other) throws RemoteException {
        int diff = compareIgnoreCaseConsistentWithEquals(accounting, other.accounting);
        if(diff!=0) return diff;
        diff = getResourceType().compareTo(other.getResourceType());
        if(diff!=0) return diff;
        return compare(key, other.key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", unique=true, description="a generated unique pkey")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="resource_type", description="the type of resource")
    public ResourceType getResourceType() throws RemoteException {
        ResourceType r=getService().getConnector().getResourceTypes().get(resource_type);
        if(r==null) throw new RemoteException("Unable to find ResourceType: "+resource_type);
        return r;
    }

    /**
     * Gets the business that is responsible for any charges caused by this resource.
     * This may be filtered.
     */
    @SchemaColumn(order=2, name="accounting", description="the business that owns this resource")
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
    @SchemaColumn(order=4, name="created_by", description="the administrator who created the resource")
    public BusinessAdministrator getCreatedBy() throws RemoteException {
        return getService().getConnector().getBusinessAdministrators().get(created_by);
    }

    @SchemaColumn(order=5, name="disable_log", description="indicates the resource is disabled")
    public DisableLog getDisableLog() throws RemoteException {
        if(disable_log==-1) return null;
        DisableLog obj=getService().getConnector().getDisableLogs().get(disable_log);
        if(obj==null) throw new RemoteException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    /**
     * Gets the time this resource was last enabled.  Initially this will be the
     * same as the created time.  This is used to pro-rate billing.
     */
    @SchemaColumn(order=6, name="last_enabled", description="the time the resources was last enabled or the creation time if never disabled")
    public Timestamp getLastEnabled() {
        return last_enabled;
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
        return createDependencySet(
            getBusiness(),
            getCreatedBy()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            getDependentObjectByResourceType()
            // TODO: getAoServerResource()
        );
    }

    private AOServObject getDependentObjectByResourceType() throws RemoteException {
        AOServObject obj;
        if(resource_type.equals(ResourceType.MYSQL_SERVER)) return null;
        else throw new AssertionError("Unexpected resource type: "+resource_type);
        // TODO: if(obj==null) throw new SQLException("Type-specific resource object not found: "+pkey);
        // TODO: return obj;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public boolean isDisabled() {
        return disable_log!=-1;
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
    /* TODO
    public AOServerResource getAoServerResource() throws IOException, SQLException {
        return getService().getConnector().getAoServerResources().get(pkey);
    }
     */
}
