/*
 * Copyright 2002-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Locale;
import java.util.NoSuchElementException;
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
final public class PostgresServer extends AOServerResource implements Comparable<PostgresServer>, DtoFactory<com.aoindustries.aoserv.client.dto.PostgresServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The directory that contains the PostgreSQL data files.
     */
    public static final String DATA_BASE_DIR="/var/lib/pgsql";

    public enum ReservedWord {
        ABORT,
        ALL,
        ANALYSE,
        ANALYZE,
        AND,
        ANY,
        AS,
        ASC,
        BETWEEN,
        BINARY,
        BIT,
        BOTH,
        CASE,
        CAST,
        CHAR,
        CHARACTER,
        CHECK,
        CLUSTER,
        COALESCE,
        COLLATE,
        COLUMN,
        CONSTRAINT,
        COPY,
        CROSS,
        CURRENT_DATE,
        CURRENT_TIME,
        CURRENT_TIMESTAMP,
        CURRENT_USER,
        DEC,
        DECIMAL,
        DEFAULT,
        DEFERRABLE,
        DESC,
        DISTINCT,
        DO,
        ELSE,
        END,
        EXCEPT,
        EXISTS,
        EXPLAIN,
        EXTEND,
        EXTRACT,
        FALSE,
        FLOAT,
        FOR,
        FOREIGN,
        FROM,
        FULL,
        GLOBAL,
        GROUP,
        HAVING,
        ILIKE,
        IN,
        INITIALLY,
        INNER,
        INOUT,
        INTERSECT,
        INTO,
        IS,
        ISNULL,
        JOIN,
        LEADING,
        LEFT,
        LIKE,
        LIMIT,
        LISTEN,
        LOAD,
        LOCAL,
        LOCK,
        MOVE,
        NATURAL,
        NCHAR,
        NEW,
        NOT,
        NOTNULL,
        NULL,
        NULLIF,
        NUMERIC,
        OFF,
        OFFSET,
        OLD,
        ON,
        ONLY,
        OR,
        ORDER,
        OUT,
        OUTER,
        OVERLAPS,
        POSITION,
        PRECISION,
        PRIMARY,
        PUBLIC,
        REFERENCES,
        RESET,
        RIGHT,
        SELECT,
        SESSION_USER,
        SETOF,
        SHOW,
        SOME,
        SUBSTRING,
        TABLE,
        THEN,
        TO,
        TRAILING,
        TRANSACTION,
        TRIM,
        TRUE,
        UNION,
        UNIQUE,
        USER,
        USING,
        VACUUM,
        VARCHAR,
        VERBOSE,
        WHEN,
        WHERE;

        private static volatile Set<String> reservedWords = null;

        /**
         * Case-insensitive check for if the provided string is a reserved word.
         */
        public static boolean isReservedWord(String value) {
            Set<String> words = reservedWords;
            if(words==null) {
                ReservedWord[] values = values();
                words = new HashSet<String>(values.length*4/3+1);
                for(ReservedWord word : values) words.add(word.name().toLowerCase(Locale.ENGLISH));
                reservedWords = words;
            }
            return words.contains(value.toLowerCase(Locale.ENGLISH));
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Fields">
    private PostgresServerName name;
    final int version;
    final private int maxConnections;
    final private int netBind;
    final private int sortMem;
    final private int sharedBuffers;
    final private boolean fsync;

    public PostgresServer(
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
        PostgresServerName name,
        int version,
        int maxConnections,
        int netBind,
        int sortMem,
        int sharedBuffers,
        boolean fsync
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.name = name;
        this.version = version;
        this.maxConnections = maxConnections;
        this.netBind = netBind;
        this.sortMem = sortMem;
        this.sharedBuffers = sharedBuffers;
        this.fsync = fsync;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PostgresServer other) {
        try {
            if(key==other.key) return 0;
            int diff = name.compareTo(other.name);
            if(diff!=0) return diff;
            return aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_NAME = "name";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, name=COLUMN_NAME, index=IndexType.INDEXED, description="the name of the database")
    public PostgresServerName getName() {
        return name;
    }

    static final String COLUMN_VERSION = "version";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, name=COLUMN_VERSION, index=IndexType.INDEXED, description="the pkey of the PostgreSQL version")
    public PostgresVersion getPostgresVersion() throws RemoteException {
        PostgresVersion obj=getConnector().getPostgresVersions().get(version);
        if(!StringUtility.equals(obj.getTechnologyVersion().operatingSystemVersion, getAoServer().getServer().operatingSystemVersion)) {
            throw new RemoteException("resource/operating system version mismatch on PostgresServer: #"+key);
        }
    	return obj;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, name="max_connections", description="the maximum number of connections for the db")
    public int getMaxConnections() {
        return maxConnections;
    }

    static final String COLUMN_NET_BIND = "net_bind";
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, name=COLUMN_NET_BIND, index=IndexType.UNIQUE, description="the port the servers binds to")
    public NetBind getNetBind() throws RemoteException {
        return getConnector().getNetBinds().get(netBind);
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, name="sort_mem", description="the amount of shared memory used for sorting")
    public int getSortMem() {
        return sortMem;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, name="shared_buffers", description="the number of shared buffers")
    public int getSharedBuffers() {
        return sharedBuffers;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+7, name="fsync", description="indicates that writes are synchronous")
    public boolean getFsync() {
        return fsync;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.PostgresServer getDto() {
        return new com.aoindustries.aoserv.client.dto.PostgresServer(
            key,
            getResourceTypeName(),
            getDto(getAccounting()),
            created,
            getDto(getCreatedByUsername()),
            disableLog,
            lastEnabled,
            aoServer,
            businessServer,
            getDto(name),
            version,
            maxConnections,
            netBind,
            sortMem,
            sharedBuffers,
            fsync
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresVersion());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetBind());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresDatabases());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresUsers());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        return getAoServer().toStringImpl()+"/"+name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<PostgresDatabase> getPostgresDatabases() throws RemoteException {
    	return getConnector().getPostgresDatabases().filterIndexed(PostgresDatabase.COLUMN_POSTGRES_SERVER, this);
    }

    /**
     * Gets the database with the provided name.
     *
     * @throws java.util.NoSuchElementException if not found
     */
    public PostgresDatabase getPostgresDatabase(PostgresDatabaseName name) throws RemoteException, NoSuchElementException {
        PostgresDatabase pd = getPostgresDatabases().filterUnique(PostgresDatabase.COLUMN_NAME, name);
        if(pd==null) throw new NoSuchElementException("Unable to find PostgresDatabase: "+name+" on "+this);
        return pd;
    }

    public IndexedSet<PostgresUser> getPostgresUsers() throws RemoteException {
        return getConnector().getPostgresUsers().filterIndexed(PostgresUser.COLUMN_POSTGRES_SERVER, this);
    }

    /**
     * Gets the user with the provided username.
     *
     * @throws java.util.NoSuchElementException if not found
     */
    public PostgresUser getPostgresUser(PostgresUserId username) throws RemoteException, NoSuchElementException {
        PostgresUser pu = getPostgresUsers().filterUnique(PostgresUser.COLUMN_USERNAME, getConnector().getUsernames().get(username.getUserId()));
        if(pu==null) throw new NoSuchElementException("Unable to find PostgresUser: "+username+"@"+this);
        return pu;
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
	return getConnector().getPostgresDatabases().addPostgresDatabase(
            name,
            this,
            datdba,
            encoding,
            enablePostgis
	);
    }

    public String getDataDirectory() {
        return DATA_BASE_DIR+'/'+name;
    }

    public PostgresServerUser getPostgresServerUser(String username) throws IOException, SQLException {
	return getConnector().getPostgresServerUsers().getPostgresServerUser(username, this);
    }

    public boolean isPostgresDatabaseNameAvailable(String name) throws IOException, SQLException {
    	return getConnector().getPostgresDatabases().isPostgresDatabaseNameAvailable(name, this);
    }

    public void restartPostgreSQL() throws IOException, SQLException {
        getConnector().requestUpdate(false, AOServProtocol.CommandID.RESTART_POSTGRESQL, pkey);
    }

    public void startPostgreSQL() throws IOException, SQLException {
        getConnector().requestUpdate(false, AOServProtocol.CommandID.START_POSTGRESQL, pkey);
    }

    public void stopPostgreSQL() throws IOException, SQLException {
        getConnector().requestUpdate(false, AOServProtocol.CommandID.STOP_POSTGRESQL, pkey);
    }
     */
    // </editor-fold>
}
