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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A <code>Resource</code> represents one accountable item.  For the purposes
 * of controlling disable/enable/remove sequence, it also has a set of
 * dependencies.
 *
 * @author  AO Industries, Inc.
 */
final public class Resource extends CachedObjectIntegerKey<Resource> implements Comparable<Resource> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_RESOURCE_TYPE=1,
        COLUMN_ACCOUNTING=2,
        COLUMN_CREATED_BY=4,
        COLUMN_DISABLE_LOG=5
    ;
    static final String COLUMN_PKEY_name = "pkey";
    static final String COLUMN_RESOURCE_TYPE_name = "resource_type";
    static final String COLUMN_ACCOUNTING_name = "accounting";

    String resource_type;
    String accounting;
    private long created;
    private String created_by;
    private int disable_log;
    private long last_enabled;

    public ResourceType getResourceType() throws SQLException, IOException {
        ResourceType r=table.connector.getResourceTypes().get(resource_type);
        if(r==null) throw new SQLException("Unable to find ResourceType: "+resource_type);
        return r;
    }

    /**
     * Gets the business that is responsible for any charges caused by this resource.
     * This may be filtered.
     */
    public Business getBusiness() throws IOException, SQLException {
        return table.connector.getBusinesses().get(accounting);
    }

    /**
     * Gets the time this was initially created.
     */
    public long getCreated() {
    	return created;
    }

    /**
     * May be filtered.
     */
    public BusinessAdministrator getCreatedBy() throws SQLException, IOException {
        return table.connector.getBusinessAdministrators().get(created_by);
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    /**
     * Gets the time this resource was last enabled.  Initially this will be the
     * same as the created time.  This is used to pro-rate billing.
     */
    public long getLastEnabled() {
        return last_enabled;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_ACCOUNTING: return accounting;
            case COLUMN_PKEY: return pkey;
            case COLUMN_RESOURCE_TYPE: return resource_type;
            case 3: return new java.sql.Date(created);
            case COLUMN_CREATED_BY: return created_by;
            case COLUMN_DISABLE_LOG: return disable_log==-1?null:Integer.valueOf(disable_log);
            case 6: return new java.sql.Date(last_enabled);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.RESOURCES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        resource_type = result.getString(2);
        accounting = result.getString(3);
        created = result.getTimestamp(4).getTime();
        created_by = result.getString(5);
        disable_log=result.getInt(6);
        if(result.wasNull()) disable_log=-1;
        last_enabled = result.getTimestamp(7).getTime();
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        resource_type = in.readUTF().intern();
        accounting = in.readUTF().intern();
        created = in.readLong();
        created_by = in.readUTF().intern();
        disable_log = in.readCompressedInt();
        last_enabled = in.readLong();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        if(version.compareTo(AOServProtocol.Version.VERSION_1_61)<=0) throw new IOException("Due to table rename, ResourceType object should be used instead for protocol<=1.61");
        out.writeCompressedInt(pkey);
        out.writeUTF(resource_type);
        out.writeUTF(accounting);
        out.writeLong(created);
        out.writeUTF(created_by);
        out.writeCompressedInt(disable_log);
        out.writeLong(last_enabled);
    }

    /**
     * Gets the set of all resources this depends on, topologically sorted
     * from most distant to closest.
     */
    public List<Resource> getAllDependencies() throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Gets the set of all resources that are dependent on this, topologically
     * sorted from most distant to closest.
     */
    public List<Resource> getAllDependentResources() throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }

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

    /**
     * Gets all the reasons this resource may not be enabled, including reasons why any of the dependencies
     * may not be enabled.
     *
     * @return  an empty <code>List</code> if this resource may be enabled, or a list of reasons
     *
     * @see  #getAllDependencies() for the order these are returned
     */
    public List<Reason> getAllCannotEnableReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Enables all disabled dependencies and then this resource.
     *
     * @see  #getAllDependencies() for the order these are enabled
     */
    public void enable() throws IOException, SQLException {
        // TODO: Add here per type
        throw new AssertionError("Unexpected resource type: "+resource_type);
    }

    /**
     * Gets the reasons this resource would not be disableable, even if all dependent resources were disabled.
     *
     * @return  an empty <code>List</code> if this resource would be disableable given all dependent resources were disabled, or a list of reasons
     */
    public List<Reason> getCannotDisableReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Gets all the reasons this resource may not be disabled, including reasons why any of the dependent resources
     * may not be disabled.
     *
     * @return  an empty <code>List</code> if this resource may be disabled, or a list of reasons
     *
     * @see  #getAllDependentResources() for the order these are returned
     */
    public List<Reason> getAllCannotDisableReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Disables all enabled dependent resources and then this resource.
     *
     * @see  #getAllDependentResources() for the order these are disabled
     */
    public void disable(DisableLog dl) throws IOException, SQLException {
        // TODO: Add here per type
        throw new AssertionError("Unexpected resource type: "+resource_type);
    }

    /**
     * Gets the reasons this resource would not be removable, even if all dependent resources were removed.
     *
     * @return  an empty <code>List</code> if this resource would be removable given all dependent resources were removed, or a list of reasons
     */
    public List<Reason> getCannotRemoveReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Gets all the reasons this resource may not be removed, including reasons why any of the dependent resources
     * may not be removed.
     *
     * @return  an empty <code>List</code> if this resource may be removed, or a list of reasons
     *
     * @see  #getAllDependentResources() for the order these are returned
     */
    public List<Reason> getAllCannotRemoveReasons(Locale userLocale) throws IOException, SQLException {
        // TODO
        return Collections.emptyList();
    }

    /**
     * Removes all dependent resources and then this resource.
     *
     * @see  #getAllDependentResources() for the order these are removed
     */
    public void remove() throws IOException, SQLException {
        // TODO: Add here per type
        throw new AssertionError("Unexpected resource type: "+resource_type);
    }

    public int compareTo(Resource o) {
        int diff = accounting.compareTo(o.accounting);
        if(diff!=0) return diff;
        diff = resource_type.compareTo(o.resource_type);
        if(diff!=0) return diff;
        if(pkey<o.pkey) return -1;
        if(pkey==o.pkey) return 0;
        return 1;
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getBusiness(),
            getCreatedBy()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
            getDependentObjectByResourceType(),
            getAoServerResource()
        );
    }

    public AOServerResource getAoServerResource() throws IOException, SQLException {
        return table.connector.getAoServerResources().get(pkey);
    }

    private AOServObject getDependentObjectByResourceType() throws IOException, SQLException {
        AOServObject obj;
        if(resource_type.equals(ResourceType.MYSQL_SERVER)) return null;
        else throw new AssertionError("Unexpected resource type: "+resource_type);
        // TODO: if(obj==null) throw new SQLException("Type-specific resource object not found: "+pkey);
        // TODO: return obj;
    }
}
