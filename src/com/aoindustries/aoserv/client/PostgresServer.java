/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>PostgresServer</code> corresponds to a unique PostgreSQL install
 * space on one server.  The server name must be unique per server.
 * <code>PostgresDatabase</code>s and <code>PostgresServerUser</code>s are
 * unique per <code>PostgresServer</code>.
 *
 * @see  PostgresVersion
 * @see  PostgresDatabase
 * @see  PostgresServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresServer extends AOServObjectIntegerKey<PostgresServer> implements BeanFactory<com.aoindustries.aoserv.client.beans.PostgresServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The directory that contains the PostgreSQL data files.
     */
    public static final String DATA_BASE_DIR="/var/lib/pgsql";

    /**
     * The maximum length of the name.
     */
    public static final int MAX_SERVER_NAME_LENGTH=31;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String name;
    final int version;
    final private int maxConnections;
    final private int netBind;
    final private int sortMem;
    final private int sharedBuffers;
    final private boolean fsync;

    public PostgresServer(
        PostgresServerService<?,?> service,
        int aoServerResource,
        String name,
        int version,
        int maxConnections,
        int netBind,
        int sortMem,
        int sharedBuffers,
        boolean fsync
    ) {
        super(service, aoServerResource);
        this.name = name.intern();
        this.version = version;
        this.maxConnections = maxConnections;
        this.netBind = netBind;
        this.sortMem = sortMem;
        this.sharedBuffers = sharedBuffers;
        this.fsync = fsync;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(PostgresServer other) throws RemoteException {
        if(key==other.key) return 0;
        int diff = compareIgnoreCaseConsistentWithEquals(name, other.name);
        if(diff!=0) return diff;
        AOServerResource aoResource1 = getAoServerResource();
        AOServerResource aoResource2 = other.getAoServerResource();
        return aoResource1.aoServer==aoResource2.aoServer ? 0 : aoResource1.getAoServer().compareTo(aoResource2.getAoServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    @SchemaColumn(order=1, name="name", description="the name of the database")
    public String getName() {
        return name;
    }

    static final String COLUMN_VERSION = "version";
    @SchemaColumn(order=2, name=COLUMN_VERSION, index=IndexType.INDEXED, description="the pkey of the PostgreSQL version")
    public PostgresVersion getPostgresVersion() throws RemoteException {
        PostgresVersion obj=getService().getConnector().getPostgresVersions().get(version);
        if(obj==null) throw new RemoteException("Unable to find PostgresVersion: "+version);
        AOServerResource aoServerResource = getAoServerResource();
        if(aoServerResource!=null) {
            if(!StringUtility.equals(obj.getTechnologyVersion().operatingSystemVersion, aoServerResource.getAoServer().getServer().operatingSystemVersion)) {
                throw new RemoteException("resource/operating system version mismatch on PostgresServer: #"+key);
            }
        }
    	return obj;
    }

    @SchemaColumn(order=3, name="max_connections", description="the maximum number of connections for the db")
    public int getMaxConnections() {
        return maxConnections;
    }

    static final String COLUMN_NET_BIND = "net_bind";
    @SchemaColumn(order=4, name=COLUMN_NET_BIND, index=IndexType.UNIQUE, description="the port the servers binds to")
    public NetBind getNetBind() throws RemoteException {
        return getService().getConnector().getNetBinds().get(netBind);
    }

    @SchemaColumn(order=5, name="sort_mem", description="the amount of shared memory used for sorting")
    public int getSortMem() {
        return sortMem;
    }

    @SchemaColumn(order=6, name="shared_buffers", description="the number of shared buffers")
    public int getSharedBuffers() {
        return sharedBuffers;
    }

    @SchemaColumn(order=7, name="fsync", description="indicates that writes are synchronous")
    public boolean getFsync() {
        return fsync;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.PostgresServer getBean() {
        return new com.aoindustries.aoserv.client.beans.PostgresServer(key, name, version, maxConnections, netBind, sortMem, sharedBuffers, fsync);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getAoServerResource(),
            getPostgresVersion(),
            getNetBind()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getPostgresDatabases(),
            getPostgresUsers()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return name+" on "+getAoServerResource().getAoServer().getHostname();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public List<PostgresDatabase> getPostgresDatabases() throws IOException, SQLException {
    	return getService().getConnector().getPostgresDatabases().getPostgresDatabases(this);
    }
    */
    public Set<PostgresUser> getPostgresUsers() throws RemoteException {
        return getService().getConnector().getPostgresUsers().getIndexed(PostgresUser.COLUMN_POSTGRES_SERVER, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addPostgresDatabase(
        String name,
        PostgresServerUser datdba,
        PostgresEncoding encoding,
        boolean enablePostgis
    ) throws IOException, SQLException {
	return getService().getConnector().getPostgresDatabases().addPostgresDatabase(
            name,
            this,
            datdba,
            encoding,
            enablePostgis
	);
    }

    public static void checkServerName(String name) throws IllegalArgumentException {
	// Must be a-z or 0-9 first, then a-z or 0-9 or . or _
	int len = name.length();
	if (len == 0 || len > MAX_SERVER_NAME_LENGTH) throw new IllegalArgumentException("PostgreSQL server name should not exceed "+MAX_SERVER_NAME_LENGTH+" characters.");

        // The first character must be [a-z] or [0-9]
	char ch = name.charAt(0);
	if ((ch < 'a' || ch > 'z') && (ch<'0' || ch>'9')) throw new IllegalArgumentException("PostgreSQL server names must start with [a-z] or [0-9]");
        // The rest may have additional characters
	for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if (
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='.'
                && ch!='_'
            ) throw new IllegalArgumentException("PostgreSQL server names may only contain [a-z], [0-9], period (.), and underscore (_)");
	}
    }

    public String getDataDirectory() {
        return DATA_BASE_DIR+'/'+name;
    }

    public PostgresDatabase getPostgresDatabase(String name) throws IOException, SQLException {
    	return getService().getConnector().getPostgresDatabases().getPostgresDatabase(name, this);
    }

    public PostgresServerUser getPostgresServerUser(String username) throws IOException, SQLException {
	return getService().getConnector().getPostgresServerUsers().getPostgresServerUser(username, this);
    }

    public boolean isPostgresDatabaseNameAvailable(String name) throws IOException, SQLException {
    	return getService().getConnector().getPostgresDatabases().isPostgresDatabaseNameAvailable(name, this);
    }

    public void restartPostgreSQL() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.RESTART_POSTGRESQL, pkey);
    }

    public void startPostgreSQL() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.START_POSTGRESQL, pkey);
    }

    public void stopPostgreSQL() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.STOP_POSTGRESQL, pkey);
    }
     */
    // </editor-fold>
}
