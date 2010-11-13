/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * When a <code>PrivateFtpServer</code> is attached to a
 * <code>NetBind</code>, the FTP server reponds as configured
 * in the <code>PrivateFtpServer</code>.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateFtpServer extends AOServObjectIntegerKey implements Comparable<PrivateFtpServer>, DtoFactory<com.aoindustries.aoserv.client.dto.PrivateFtpServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int netBind;
    private UnixPath logfile;
    private DomainName hostname;
    private Email email;
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
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        logfile = intern(logfile);
        hostname = intern(hostname);
        email = intern(email);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PrivateFtpServer other) {
        try {
            return netBind==other.netBind ? 0 : getNetBind().compareTo(other.getNetBind());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", index=IndexType.PRIMARY_KEY, description="the resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    static final String COLUMN_NET_BIND = "net_bind";
    @SchemaColumn(order=1, name=COLUMN_NET_BIND, index=IndexType.UNIQUE, description="the pkey of the net_bind that the FTP server is on")
    public NetBind getNetBind() throws RemoteException {
        return getService().getConnector().getNetBinds().get(netBind);
    }

    @SchemaColumn(order=2, name="logfile", description="the file transfers are logged to")
    public UnixPath getLogfile() {
        return logfile;
    }

    @SchemaColumn(order=3, name="hostname", description="the hostname the server reports")
    public DomainName getHostname() {
        return hostname;
    }

    @SchemaColumn(order=4, name="email", description="the email address the server reports")
    public Email getEmail() {
        return email;
    }

    static final String COLUMN_LINUX_ACCOUNT_GROUP = "linux_account_group";
    @SchemaColumn(order=5, name=COLUMN_LINUX_ACCOUNT_GROUP, index=IndexType.INDEXED, description="the Linux account and group this FTP server runs as")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getService().getConnector().getLinuxAccountGroups().get(linuxAccountGroup);
    }

    @SchemaColumn(order=6, name="allow_anonymous", description="enabled or disabled anonymous access to the server")
    public boolean allowAnonymous() {
        return allowAnonymous;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.PrivateFtpServer getDto() {
        return new com.aoindustries.aoserv.client.dto.PrivateFtpServer(key, netBind, getDto(logfile), getDto(hostname), getDto(email), linuxAccountGroup, allowAnonymous);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetBind());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccountGroup());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServerResource());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return hostname.toString();
    }
    // </editor-fold>
}