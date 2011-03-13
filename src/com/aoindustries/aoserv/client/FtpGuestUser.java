/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
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
final public class FtpGuestUser extends AOServObjectIntegerKey implements Comparable<FtpGuestUser>, DtoFactory<com.aoindustries.aoserv.client.dto.FtpGuestUser> /*, TODO: Removable */ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public FtpGuestUser(AOServConnector connector, int linuxAccount) {
        super(connector, linuxAccount);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(FtpGuestUser other) {
        try {
            return key==other.key ? 0 : getLinuxAccount().compareTo(other.getLinuxAccount());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_LINUX_ACCOUNT = "linux_account";
    @DependencySingleton
    @SchemaColumn(order=0, name=COLUMN_LINUX_ACCOUNT, index=IndexType.PRIMARY_KEY, description="the resource id of the Linux account")
    public LinuxAccount getLinuxAccount() throws RemoteException {
        return getConnector().getLinuxAccounts().get(key);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public FtpGuestUser(AOServConnector connector, com.aoindustries.aoserv.client.dto.FtpGuestUser dto) {
        this(connector, dto.getLinuxAccount());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.FtpGuestUser getDto() {
        return new com.aoindustries.aoserv.client.dto.FtpGuestUser(key);
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
    	getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.FTP_GUEST_USERS,
            pkey
    	);
    }
     */
    // </editor-fold>
}