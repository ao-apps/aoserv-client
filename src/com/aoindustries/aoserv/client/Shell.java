/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

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

    public enum Constant {
        BASH("/bin/bash"),
        KSH("/bin/ksh"),
        SH("/bin/sh"),
        SYNC("/bin/sync"),
        TCSH("/bin/tcsh"),
        HALT("/sbin/halt"),
        NOLOGIN("/sbin/nologin"),
        SHUTDOWN("/sbin/shutdown"),
        FTPPASSWD("/usr/bin/ftppasswd"),
        PASSWD("/usr/bin/passwd");

        private final UnixPath unixPath;

        private Constant(String path) {
            this.unixPath = new UnixPath(path).intern();
        }

        public UnixPath getUnixPath() {
            return unixPath;
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
    	return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Shell getBean() {
        return new com.aoindustries.aoserv.client.beans.Shell(key.getBean());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getLinuxAccounts()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public Set<LinuxAccount> getLinuxAccounts() throws RemoteException {
        return getService().getConnector().getTicketCategories().getIndexed(COLUMN_PARENT, this);
    } */
    // </editor-fold>
}
