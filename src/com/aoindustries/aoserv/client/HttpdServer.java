/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionClassSet;
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
    private static final long serialVersionUID = 1L;

    /**
     * The highest recommended number of sites to bind in one server.
     */
    public static final int DEFAULT_MAXIMUM_BINDS=128;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int number;
    final private int maxBinds;
    final private int linuxAccountGroup;
    final private Integer modPhpVersion;
    final private boolean useSuexec;
    final private boolean isShared;
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
        this.isShared = isShared;
        this.useModPerl = useModPerl;
        this.timeout = timeout;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(HttpdServer other) {
        try {
            if(key==other.key) return 0;
            int diff = aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
            if(diff!=0) return diff;
            return AOServObjectUtils.compare(number, other.number);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, name="number", description="the number of the instance on the server")
    public int getNumber() {
        return number;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, name="max_binds", description="the maximum number of httpd_site_binds on this server")
    public int getMaxBinds() {
        return maxBinds;
    }

    static final String COLUMN_LINUX_ACCOUNT_GROUP = "linux_account_group";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, name=COLUMN_LINUX_ACCOUNT_GROUP, index=IndexType.INDEXED, description="the account and group the servers runs as")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getConnector().getLinuxAccountGroups().filterUnique(LinuxAccountGroup.COLUMN_PKEY, linuxAccountGroup);
    }

    static final String COLUMN_MOD_PHP_VERSION = "mod_php_version";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, name=COLUMN_MOD_PHP_VERSION, index=IndexType.INDEXED, description="the version of mod_php to run")
    public TechnologyVersion getModPhpVersion() throws RemoteException {
        if(modPhpVersion==null) return null;
        TechnologyVersion tv=getConnector().getTechnologyVersions().get(modPhpVersion);
        if(tv.operatingSystemVersion!=getAoServer().getServer().operatingSystemVersion) throw new RemoteException("mod_php/operating system version mismatch on HttpdServer: #"+key);
        return tv;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, name="use_suexec", description="indicates that the suexec wrapper will be used for CGI")
    public boolean getUseSuexec() {
        return useSuexec;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, name="is_shared", description="indicates that any user on the server may use this httpd instance")
    public boolean isShared() {
        return isShared;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+7, name="use_mod_perl", description="enables mod_perl")
    public boolean useModPerl() {
        return useModPerl;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+8, name="timeout", description="the timeout setting in seconds")
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
            dto.isIsShared(),
            dto.isUseModPerl(),
            dto.getTimeout()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.HttpdServer getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdServer(
            key,
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
            isShared,
            useModPerl,
            timeout
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionClassSet<AOServObject<?>> addDependencies(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccountGroup());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getModPhpVersion());
        return unionSet;
    }

    @Override
    protected UnionClassSet<AOServObject<?>> addDependentObjects(UnionClassSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(null);
        // TODO: unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdBinds());
        return unionSet;
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
