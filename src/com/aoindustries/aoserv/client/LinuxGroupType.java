/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
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
    public LinuxGroupType(AOServConnector connector, String resourceType) {
        super(connector, resourceType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(LinuxGroupType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_RESOURCE_TYPE = "resource_type";
    @DependencySingleton
    @SchemaColumn(order=0, name=COLUMN_RESOURCE_TYPE, index=IndexType.PRIMARY_KEY, description="the resource type this represents")
    public ResourceType getResourceType() throws RemoteException {
        return getConnector().getResourceTypes().get(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public LinuxGroupType(AOServConnector connector, com.aoindustries.aoserv.client.dto.LinuxGroupType dto) {
        this(connector, dto.getResourceType());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.LinuxGroupType getDto() {
        return new com.aoindustries.aoserv.client.dto.LinuxGroupType (getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("LinuxGroupType."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<LinuxGroup> getLinuxGroups() throws RemoteException {
        return getConnector().getLinuxGroups().filterIndexed(LinuxGroup.COLUMN_LINUX_GROUP_TYPE, this);
    }
    // </editor-fold>
}
