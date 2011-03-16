/*
 * Copyright 2001-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * An <code>HttpdServer</code> represents one running instance of the
 * Apache web server.  Each physical server may run any number of
 * Apache web servers, and each of those may respond to multiple
 * IP addresses and ports, and serve content for many sites.
 *
 * @see  HttpdBind
 * @see  HttpdSite
 * @see  HttpdSiteBind
 *
 * @author  AO Industries, Inc.
 */
final public class HttpdServer extends AOServerResource implements Comparable<HttpdServer>, DtoFactory<com.aoindustries.aoserv.client.dto.HttpdServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    /**
     * The highest recommended number of sites to bind in one server.
     */
    public static final int DEFAULT_MAXIMUM_BINDS=128;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -8904437243470486353L;

    final private int number;
    final private int maxBinds;
    final private int linuxAccountGroup;
    final private Integer modPhpVersion;
    final private boolean useSuexec;
    final private boolean shared;
    final private boolean useModPerl;
    final private int timeout;

    public HttpdServer(
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
        int number,
        int maxBinds,
        int linuxAccountGroup,
        Integer modPhpVersion,
        boolean useSuexec,
        boolean isShared,
        boolean useModPerl,
        int timeout
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.number = number;
        this.maxBinds = maxBinds;
        this.linuxAccountGroup = linuxAccountGroup;
        this.modPhpVersion = modPhpVersion;
        this.useSuexec = useSuexec;
        this.shared = isShared;
        this.useModPerl = useModPerl;
        this.timeout = timeout;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(HttpdServer other) {
        try {
            if(getKeyInt()==other.getKeyInt()) return 0;
            int diff = aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
            if(diff!=0) return diff;
            return compare(number, other.number);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, description="the number of the instance on the server")
    public int getNumber() {
        return number;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, description="the maximum number of httpd_site_binds on this server")
    public int getMaxBinds() {
        return maxBinds;
    }

    public static final MethodColumn COLUMN_LINUX_ACCOUNT_GROUP = getMethodColumn(HttpdServer.class, "linuxAccountGroup");
    /**
     * May be filtered.
     */
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, index=IndexType.INDEXED, description="the account and group the servers runs as")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getConnector().getLinuxAccountGroups().filterUnique(LinuxAccountGroup.COLUMN_PKEY, linuxAccountGroup);
    }

    public static final MethodColumn COLUMN_MOD_PHP_VERSION = getMethodColumn(HttpdServer.class, "modPhpVersion");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, index=IndexType.INDEXED, description="the version of mod_php to run")
    public TechnologyVersion getModPhpVersion() throws RemoteException {
        if(modPhpVersion==null) return null;
        TechnologyVersion tv=getConnector().getTechnologyVersions().get(modPhpVersion);
        if(tv.operatingSystemVersion!=getAoServer().getServer().operatingSystemVersion) throw new RemoteException("mod_php/operating system version mismatch on HttpdServer: #"+getKey());
        return tv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, description="indicates that the suexec wrapper will be used for CGI")
    public boolean getUseSuexec() {
        return useSuexec;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, description="indicates that any user on the server may use this httpd instance")
    public boolean isShared() {
        return shared;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+7, description="enables mod_perl")
    public boolean getUseModPerl() {
        return useModPerl;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+8, description="the timeout setting in seconds")
    public int getTimeOut() {
        return timeout;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public HttpdServer(AOServConnector connector, com.aoindustries.aoserv.client.dto.HttpdServer dto) throws ValidationException {
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
            dto.getNumber(),
            dto.getMaxBinds(),
            dto.getLinuxAccountGroup(),
            dto.getModPhpVersion(),
            dto.isUseSuexec(),
            dto.isShared(),
            dto.isUseModPerl(),
            dto.getTimeout()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.HttpdServer getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdServer(
            getKeyInt(),
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            number,
            maxBinds,
            linuxAccountGroup,
            modPhpVersion,
            useSuexec,
            shared,
            useModPerl,
            timeout
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
    	return "httpd"+number;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    @DependentObjectSet
    public List<HttpdBind> getHttpdBinds() throws IOException, SQLException {
        return getConnector().getHttpdBinds().getHttpdBinds(this);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return getConnector().getHttpdSites().getHttpdSites(this);
    }

    public List<HttpdWorker> getHttpdWorkers() throws IOException, SQLException {
        return getConnector().getHttpdWorkers().getHttpdWorkers(this);
    }
     */
    // </editor-fold>
}
