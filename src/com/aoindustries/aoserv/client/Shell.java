/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.aoserv.client.validator.UnixPath;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * All of the possible Linux login shells are provided as
 * <code>Shell</code>s.
 *
 * @see  LinuxAccount
 * @see  LinuxAccountType
 *
 * @author  AO Industries, Inc.
 */
final public class Shell extends AOServObjectUnixPathKey<Shell> implements BeanFactory<com.aoindustries.aoserv.client.beans.Shell> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final UnixPath
        BASH,
        KSH,
        SH,
        SYNC,
        TCSH,
        HALT,
        NOLOGIN,
        SHUTDOWN,
        FTPPASSWD,
        PASSWD
    ;
    static {
        try {
            BASH = UnixPath.valueOf("/bin/bash").intern();
            KSH = UnixPath.valueOf("/bin/ksh").intern();
            SH = UnixPath.valueOf("/bin/sh").intern();
            SYNC = UnixPath.valueOf("/bin/sync").intern();
            TCSH = UnixPath.valueOf("/bin/tcsh").intern();
            HALT = UnixPath.valueOf("/sbin/halt").intern();
            NOLOGIN = UnixPath.valueOf("/sbin/nologin").intern();
            SHUTDOWN = UnixPath.valueOf("/sbin/shutdown").intern();
            FTPPASSWD = UnixPath.valueOf("/usr/bin/ftppasswd").intern();
            PASSWD = UnixPath.valueOf("/usr/bin/passwd").intern();
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public Shell(ShellService<?,?> service, UnixPath path) {
        super(service, path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="path", index=IndexType.PRIMARY_KEY, description="the complete path to the executable")
    public UnixPath getPath() {
    	return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Shell getBean() {
        return new com.aoindustries.aoserv.client.beans.Shell(getBean(getKey()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccounts());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<LinuxAccount> getLinuxAccounts() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().filterIndexed(LinuxAccount.COLUMN_SHELL, this);
    }
    // </editor-fold>
}
