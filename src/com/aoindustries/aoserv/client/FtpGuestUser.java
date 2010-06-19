/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;

/**
 * If a <code>LinuxAccount</code> has a <code>FtpGuestUser</code> attached to it,
 * FTP connections will be limited with their home directory as the root
 * directory.
 *
 * @see  LinuxAccount
 * @see  LinuxServerAccount
 *
 * @author  AO Industries, Inc.
 */
final public class FtpGuestUser extends AOServObjectIntegerKey<FtpGuestUser> implements BeanFactory<com.aoindustries.aoserv.client.beans.FtpGuestUser> /*, TODO: Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public FtpGuestUser(FtpGuestUserService<?,?> service, int linuxAccount) {
        super(service, linuxAccount);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(FtpGuestUser other) throws RemoteException {
        return key==other.key ? 0 : getLinuxAccount().compareToImpl(other.getLinuxAccount());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_LINUX_ACCOUNT = "linux_account";
    @SchemaColumn(order=0, name=COLUMN_LINUX_ACCOUNT, index=IndexType.PRIMARY_KEY, description="the resource id of the Linux account")
    public LinuxAccount getLinuxAccount() throws RemoteException {
        return getService().getConnector().getLinuxAccounts().get(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.FtpGuestUser getBean() {
        return new com.aoindustries.aoserv.client.beans.FtpGuestUser(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccount());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getLinuxAccount().toStringImpl();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
    	getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.FTP_GUEST_USERS,
            pkey
    	);
    }
     */
    // </editor-fold>
}