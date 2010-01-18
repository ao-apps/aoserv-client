/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.Email;
import com.aoindustries.aoserv.client.validator.UnixPath;
import java.rmi.RemoteException;

/**
 * When a <code>PrivateFTPServer</code> is attached to a
 * <code>NetBind</code>, the FTP server reponds as configured
 * in the <code>PrivateFTPServer</code>.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateFtpServer extends AOServObjectIntegerKey<PrivateFtpServer> implements BeanFactory<com.aoindustries.aoserv.client.beans.PrivateFtpServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int netBind;
    final private UnixPath logfile;
    final private DomainName hostname;
    final private Email email;
    final private int linuxAccountGroup;
    final private boolean allowAnonymous;

    public PrivateFtpServer(
        PrivateFtpServerService<?,?> service,
        int aoServerResource,
        int netBind,
        UnixPath logfile,
        DomainName hostname,
        Email email,
        int linuxAccountGroup,
        boolean allowAnonymous
    ) {
        super(service, aoServerResource);
        this.netBind = netBind;
        this.logfile = logfile;
        this.hostname = hostname;
        this.email = email;
        this.linuxAccountGroup = linuxAccountGroup;
        this.allowAnonymous = allowAnonymous;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(PrivateFtpServer other) throws RemoteException {
        return netBind==other.netBind ? 0 : getNetBind().compareTo(other.getNetBind());
    }
    // </editor-fold>

    public String getEmail() {
        return email;
    }

    public String getHostname() {
        return hostname;
    }

    public NetBind getNetBind() throws SQLException, IOException {
        return getService().getConnector().getNetBinds().get(pkey);
    }

    public String getLogfile() {
        return logfile;
    }

    public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
        return getService().getConnector().getLinuxServerAccounts().get(pub_linux_server_account);
    }

    /**
     * @deprecated  use getLinuxServerAccount().getPrimaryLinuxServerGroup()
     */
    public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
        return getLinuxServerAccount().getPrimaryLinuxServerGroup();
    }

    public boolean allowAnonymous() {
        return allowAnonymous;
    }

    /**
     * @deprecated  use getLinuxServerAccount().getHome()
     */
    public String getRoot() throws SQLException, IOException {
        return getLinuxServerAccount().getHome();
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.PRIVATE_FTP_SERVERS;
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getNetBind(),
            getLinuxServerAccount()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return hostname;
    }
}