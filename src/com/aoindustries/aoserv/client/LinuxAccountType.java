/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
final public class LinuxAccountType extends AOServObjectStringKey implements Comparable<LinuxAccountType>, DtoFactory<com.aoindustries.aoserv.client.dto.LinuxAccountType> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The different Linux account types.
     */
    private enum Constant {
        shell_account(
            ResourceType.SHELL_ACCOUNT,
            true,
            true,
            true,
            PasswordChecker.PasswordStrength.STRICT,
            Shell.NOLOGIN,
            Shell.BASH,
            Shell.KSH,
            Shell.SH,
            Shell.TCSH
        ),
        email_inbox(
            ResourceType.EMAIL_INBOX,
            true,
            false,
            true,
            PasswordChecker.PasswordStrength.SUPER_LAX,
            Shell.NOLOGIN,
            Shell.PASSWD
        ),
        ftponly_account(
            ResourceType.FTPONLY_ACCOUNT,
            false,
            true,
            true,
            PasswordChecker.PasswordStrength.MODERATE,
            Shell.NOLOGIN,
            Shell.FTPPASSWD
        ),
        system_account(
            ResourceType.SYSTEM_ACCOUNT,
            false,
            false,
            false,
            PasswordChecker.PasswordStrength.STRICT,
            Shell.NOLOGIN,
            Shell.BASH,
            Shell.SYNC,
            Shell.HALT,
            Shell.SHUTDOWN
        );

        private final String resourceType;
        private final boolean emailAllowed;
        private final boolean ftpAllowed;
        private final boolean setPasswordAllowed;
        private final PasswordChecker.PasswordStrength passwordStrength;
        private final Set<UnixPath> allowedShells;

        private Constant(
            String resourceType,
            boolean emailAllowed,
            boolean ftpAllowed,
            boolean setPasswordAllowed,
            PasswordChecker.PasswordStrength passwordStrength,
            UnixPath... allowedShells
        ) {
            this.resourceType = resourceType;
            this.emailAllowed = emailAllowed;
            this.ftpAllowed = ftpAllowed;
            this.setPasswordAllowed = setPasswordAllowed;
            this.passwordStrength = passwordStrength;
            if(allowedShells.length==1) this.allowedShells = Collections.singleton(allowedShells[0]);
            else this.allowedShells = Collections.unmodifiableSet(new HashSet<UnixPath>(Arrays.asList(allowedShells)));
        }
        /*
        public String getResourceType() {
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

        public PasswordChecker.PasswordStrength getPasswordStrength() {
            return passwordStrength;
        }

        public Set<UnixPath> getAllowedShells() {
            return allowedShells;
        }
        */
    }

    public boolean isEmailAllowed() {
        return Constant.valueOf(getKey()).emailAllowed;
    }

    public boolean isFtpAllowed() {
        return Constant.valueOf(getKey()).ftpAllowed;
    }

    public boolean isSetPasswordAllowed() {
        return Constant.valueOf(getKey()).setPasswordAllowed;
    }

    public PasswordChecker.PasswordStrength getPasswordStrength() {
        return Constant.valueOf(getKey()).passwordStrength;
    }

    /**
     * This matches that enforced by the linux_accounts table.
     */
    public Set<UnixPath> getAllowedShells() {
        return Constant.valueOf(getKey()).allowedShells;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public LinuxAccountType(AOServConnector connector, String resourceType) {
        super(connector, resourceType);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(LinuxAccountType other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="resource_type", index=IndexType.PRIMARY_KEY, description="the resource type this represents")
    public ResourceType getResourceType() throws RemoteException {
        return getConnector().getResourceTypes().get(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public LinuxAccountType(AOServConnector connector, com.aoindustries.aoserv.client.dto.LinuxAccountType dto) {
        this(connector, dto.getResourceType());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.LinuxAccountType getDto() {
        return new com.aoindustries.aoserv.client.dto.LinuxAccountType(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getResourceType());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccounts());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return ApplicationResources.accessor.getMessage("LinuxAccountType."+getKey()+".toString");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<LinuxAccount> getLinuxAccounts() throws RemoteException {
        return getConnector().getLinuxAccounts().filterIndexed(LinuxAccount.COLUMN_LINUX_ACCOUNT_TYPE, this);
    }
    // </editor-fold>

    /**
     * Checks the strength of a password as required for this type of
     * <code>LinuxAccount</code>.
     *
     * @see PasswordChecker
     */
    public List<PasswordChecker.Result> checkPassword(UserId username, String password) throws IOException {
        return PasswordChecker.checkPassword(username, password, getPasswordStrength());
    }

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<Shell> getAllowedShells(AOServConnector connector) throws SQLException, IOException {
        String[] paths=getShellList(pkey);

        ShellService shellTable=connector.getShells();
        int len=paths.length;
        List<Shell> shells=new ArrayList<Shell>(len);
        for(int c=0;c<len;c++) {
            shells.add(shellTable.get(paths[c]));
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
     */
    // </editor-fold>
}
