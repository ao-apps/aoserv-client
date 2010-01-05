/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * The <code>LinuxAccountType</code> of a <code>LinuxAccount</code>
 * controls which systems the account may access.  If the
 * <code>LinuxAccount</code> is able to access multiple
 * <code>Server</code>s, its type will be the same on all servers.
 *
 * @see  LinuxAccount
 * @see  LinuxServerAccount
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccountType extends AOServObjectStringKey<LinuxAccountType> implements BeanFactory<com.aoindustries.aoserv.client.beans.LinuxAccountType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The different Linux account types.
     */
    public enum Constant {
        shell_account(ResourceType.Constant.shell_account, true, true, true, true, Shell.Constant.NOLOGIN, Shell.Constant.BASH, Shell.Constant.KSH, Shell.Constant.SH, Shell.Constant.TCSH),
        email_inbox(ResourceType.Constant.email_inbox, true, false, true, false),
        ftponly_account(ResourceType.Constant.ftponly_account, false, true, true, false),
        system_account(ResourceType.Constant.system_account, false, false, false, true);

        private final ResourceType.Constant resourceType;
        private final boolean emailAllowed;
        private final boolean ftpAllowed;
        private final boolean setPasswordAllowed;
        private final boolean strongPassword;
        private final Set<Shell.Constant> allowedShells;

        private Constant(ResourceType.Constant resourceType, boolean emailAllowed, boolean ftpAllowed, boolean setPasswordAllowed, boolean strongPassword, Shell.Constant... allowedShells) {
            this.resourceType = resourceType;
            this.emailAllowed = emailAllowed;
            this.ftpAllowed = ftpAllowed;
            this.setPasswordAllowed = setPasswordAllowed;
            this.strongPassword = strongPassword;
            this.allowedShells = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(allowedShells)));
        }

        public ResourceType.Constant getResourceType() {
            return resourceType;
        }

        public boolean isEmailAllowed() {
            return emailAllowed;
        }

        public boolean isFtpAllowed() {
            return ftpAllowed;
        }

        public boolean isSetPasswordAllowed() {
            return setPasswordAllowed;
        }

        /**
         * Indicates that strong passwords should be enforced for this account type.
         */
        public boolean isStrongPassword() {
            return strongPassword;
        }

        public Set<Shell.Constant> getAllowedShells() {
            return allowedShells;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public LinuxAccountType(LinuxAccountTypeService<?,?> service, String resourceType) {
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
    public com.aoindustries.aoserv.client.beans.LinuxAccountType getBean() {
        return new com.aoindustries.aoserv.client.beans.LinuxAccountType(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getResourceType()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getLinuxAccounts()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return ApplicationResources.accessor.getMessage(userLocale, "LinuxAccountType."+key+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<LinuxAccount> getLinuxAccounts() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().getIndexed(LinuxAccount.COLUMN_LINUX_ACCOUNT_TYPE, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<Shell> getAllowedShells(AOServConnector connector) throws SQLException, IOException {
        String[] paths=getShellList(pkey);

        ShellService shellTable=connector.getShells();
        int len=paths.length;
        List<Shell> shells=new ArrayList<Shell>(len);
        for(int c=0;c<len;c++) {
            Shell shell=shellTable.get(paths[c]);
            if(shell==null) throw new SQLException("Unable to find Shell: "+paths[c]);
            shells.add(shell);
        }
        return shells;
    }

    public boolean isAllowedShell(Shell shell) throws SQLException {
        return isAllowedShell(shell.pkey);
    }

    public boolean isAllowedShell(String path) throws SQLException {
        return isAllowedShell(pkey, path);
    }

    public static boolean isAllowedShell(String type, String path) throws SQLException {
        String[] paths=getShellList(type);
        int len=paths.length;
        for(int c=0;c<len;c++) {
            if(paths[c].equals(path)) return true;
        }
        return false;
    }

    public boolean isEmail() {
        return is_email;
    }

    public boolean canSetPassword() {
        return canSetPassword(pkey);
    }
     */
    // </editor-fold>
}
