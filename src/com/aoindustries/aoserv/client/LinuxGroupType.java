package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

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
final public class LinuxGroupType extends AOServObjectStringKey<LinuxGroupType> implements BeanFactory<com.aoindustries.aoserv.client.beans.LinuxGroupType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The available group types.
     */
    public enum Constant {
        shell_group(ResourceType.Constant.shell_group),
        system_group(ResourceType.Constant.system_group);

        private final ResourceType.Constant resourceType;

        private Constant(ResourceType.Constant resourceType) {
            this.resourceType = resourceType;
        }

        public ResourceType.Constant getResourceType() {
            return resourceType;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public LinuxGroupType(LinuxGroupTypeService<?,?> service, String resourceType) {
        super(service, resourceType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="resource_type", index=IndexType.PRIMARY_KEY, description="the resource type this represents")
    public ResourceType getResourceType() throws RemoteException {
        return getService().getConnector().getResourceTypes().get(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.LinuxGroupType getBean() {
        return new com.aoindustries.aoserv.client.beans.LinuxGroupType (key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getLinuxGroups()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "LinuxGroupType."+key+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public Set<LinuxGroup> getLinuxGroups() throws RemoteException {
        // TODO: return getService().getConnector().getTicketCategories().getIndexed(COLUMN_PARENT, this);
    }
     */
    // </editor-fold>
}
