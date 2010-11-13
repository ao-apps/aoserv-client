/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * Each <code>LinuxGroup</code>'s use is limited by which
 * <code>LinuxGroupType</code> is associated with it.  Typically,
 * but not required, a <code>LinuxAccount</code> will have a
 * <code>LinuxAccountType</code> that matchs its primary
 * <code>LinuxGroup</code>'s <code>LinuxGroupType</code>.
 *
 * @see  LinuxGroup
 * @see  LinuxAccountType
 * @see  LinuxAccount
 * @see  LinuxGroupAccount
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroupType extends AOServObjectStringKey implements Comparable<LinuxGroupType>, DtoFactory<com.aoindustries.aoserv.client.dto.LinuxGroupType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

//    /**
//     * The available group types.
//     */
//    public static final String
//        SHELL_GROUP = ResourceType.SHELL_GROUP,
//        system_group(ResourceType.Constant.system_group);
//
//        private final ResourceType.Constant resourceType;
//
//        private Constant(ResourceType.Constant resourceType) {
//            this.resourceType = resourceType;
//        }
//
//        public ResourceType.Constant getResourceType() {
//            return resourceType;
//        }
//    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public LinuxGroupType(LinuxGroupTypeService<?,?> service, String resourceType) {
        super(service, resourceType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(LinuxGroupType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="resource_type", index=IndexType.PRIMARY_KEY, description="the resource type this represents")
    public ResourceType getResourceType() throws RemoteException {
        return getService().getConnector().getResourceTypes().get(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.LinuxGroupType getDto() {
        return new com.aoindustries.aoserv.client.dto.LinuxGroupType (getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getResourceType());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxGroups());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("LinuxGroupType."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<LinuxGroup> getLinuxGroups() throws RemoteException {
        return getService().getConnector().getLinuxGroups().filterIndexed(LinuxGroup.COLUMN_LINUX_GROUP_TYPE, this);
    }
    // </editor-fold>
}
