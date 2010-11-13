/*
 * Copyright 2001-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
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
final public class HttpdServer extends AOServObjectIntegerKey<HttpdServer> implements Comparable<HttpdServer>, DtoFactory<com.aoindustries.aoserv.client.dto.HttpdServer> {

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
        HttpdServerService<?,?> service,
        int aoServerResource,
        int number,
        int maxBinds,
        int linuxAccountGroup,
        Integer modPhpVersion,
        boolean useSuexec,
        boolean isShared,
        boolean useModPerl,
        int timeout
    ) {
        super(service, aoServerResource);
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
            AOServerResource aoResource1 = getAoServerResource();
            AOServerResource aoResource2 = other.getAoServerResource();
            int diff = aoResource1.aoServer==aoResource2.aoServer ? 0 : aoResource1.getAoServer().compareTo(aoResource2.getAoServer());
            if(diff!=0) return diff;
            return AOServObjectUtils.compare(number, other.number);
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_AO_SERVER_RESOURCE = "ao_server_resource";
    @SchemaColumn(order=0, name=COLUMN_AO_SERVER_RESOURCE, index=IndexType.PRIMARY_KEY, description="the resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    @SchemaColumn(order=1, name="number", description="the number of the instance on the server")
    public int getNumber() {
        return number;
    }

    @SchemaColumn(order=2, name="max_binds", description="the maximum number of httpd_site_binds on this server")
    public int getMaxBinds() {
        return maxBinds;
    }

    static final String COLUMN_LINUX_ACCOUNT_GROUP = "linux_account_group";
    /**
     * May be filtered.
     */
    @SchemaColumn(order=3, name=COLUMN_LINUX_ACCOUNT_GROUP, index=IndexType.INDEXED, description="the account and group the servers runs as")
    public LinuxAccountGroup getLinuxAccountGroup() throws RemoteException {
        return getService().getConnector().getLinuxAccountGroups().filterUnique(LinuxAccountGroup.COLUMN_PKEY, linuxAccountGroup);
    }

    static final String COLUMN_MOD_PHP_VERSION = "mod_php_version";
    @SchemaColumn(order=4, name=COLUMN_MOD_PHP_VERSION, index=IndexType.INDEXED, description="the version of mod_php to run")
    public TechnologyVersion getModPhpVersion() throws RemoteException {
        if(modPhpVersion==null) return null;
        TechnologyVersion tv=getService().getConnector().getTechnologyVersions().get(modPhpVersion);
        if(tv.operatingSystemVersion!=getAoServerResource().getAoServer().getServer().operatingSystemVersion) throw new RemoteException("mod_php/operating system version mismatch on HttpdServer: #"+key);
        return tv;
    }

    @SchemaColumn(order=5, name="use_suexec", description="indicates that the suexec wrapper will be used for CGI")
    public boolean getUseSuexec() {
        return useSuexec;
    }

    @SchemaColumn(order=6, name="is_shared", description="indicates that any user on the server may use this httpd instance")
    public boolean isShared() {
        return isShared;
    }

    @SchemaColumn(order=7, name="use_mod_perl", description="enables mod_perl")
    public boolean useModPerl() {
        return useModPerl;
    }

    @SchemaColumn(order=8, name="timeout", description="the timeout setting in seconds")
    public int getTimeOut() {
        return timeout;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.HttpdServer getDto() {
        return new com.aoindustries.aoserv.client.dto.HttpdServer(key, number, maxBinds, linuxAccountGroup, modPhpVersion, useSuexec, isShared, useModPerl, timeout);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServerResource());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getLinuxAccountGroup());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getModPhpVersion());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
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
        return getService().getConnector().getHttpdBinds().getHttpdBinds(this);
    }

    public List<HttpdSite> getHttpdSites() throws IOException, SQLException {
        return getService().getConnector().getHttpdSites().getHttpdSites(this);
    }

    public List<HttpdWorker> getHttpdWorkers() throws IOException, SQLException {
        return getService().getConnector().getHttpdWorkers().getHttpdWorkers(this);
    }
     */
    // </editor-fold>
}
