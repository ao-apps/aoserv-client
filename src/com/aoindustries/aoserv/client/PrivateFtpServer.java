/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
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
final public class PrivateFtpServer extends AOServerResource implements Comparable<PrivateFtpServer>, DtoFactory<com.aoindustries.aoserv.client.dto.PrivateFtpServer> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 7026264201272779270L;

    final private int netBind;
    private UnixPath logfile;
    private DomainName hostname;
    private Email email;
    final private int linuxAccountGroup;
    final private boolean allowAnonymous;

    public PrivateFtpServer(
        AOServConnector connector,
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        int netBind,
        UnixPath logfile,
        DomainName hostname,
        Email email,
        int linuxAccountGroup,
        boolean allowAnonymous
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
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
    public static final MethodColumn COLUMN_NET_BIND = getMethodColumn(PrivateFtpServer.class, "netBind");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, index=IndexType.UNIQUE, description="the pkey of the net_bind that the FTP server is on")
    public NetBind getNetBind() throws RemoteException {
        return getConnector().getNetBinds().get(netBind);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, description="the file transfers are logged to")
    public UnixPath getLogfile() {
        return logfile;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, description="the hostname the server reports")
    public DomainName getHostname() {
        return hostname;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, description="the email address the server reports")
    public Email getEmail() {
        return email;
    }

    public static final MethodColumn COLUMN_LINUX_ACCOUNT_GROUP = getMethodColumn(PrivateFtpServer.class, "linuxAccountGroup");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, index=IndexType.INDEXED, description="the Linux account and group this FTP server runs as")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getConnector().getLinuxAccountGroups().get(linuxAccountGroup);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, description="enabled or disabled anonymous access to the server")
    public boolean getAllowAnonymous() {
        return allowAnonymous;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PrivateFtpServer(AOServConnector connector, com.aoindustries.aoserv.client.dto.PrivateFtpServer dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getAoServer(),
            dto.getBusinessServer(),
            dto.getNetBind(),
            getUnixPath(dto.getLogfile()),
            getDomainName(dto.getHostname()),
            getEmail(dto.getEmail()),
            dto.getLinuxAccountGroup(),
            dto.isAllowAnonymous()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PrivateFtpServer getDto() {
        return new com.aoindustries.aoserv.client.dto.PrivateFtpServer(
            getKeyInt(),
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            netBind,
            getDto(logfile),
            getDto(hostname),
            getDto(email),
            linuxAccountGroup,
            allowAnonymous
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return hostname.toString();
    }
    // </editor-fold>
}