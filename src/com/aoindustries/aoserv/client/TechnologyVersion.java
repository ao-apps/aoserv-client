/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * Each <code>TechnologyName</code> may have multiple versions installed.
 * Each of those versions is a <code>TechnologyVersion</code>.
 *
 * @see  TechnologyName
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyVersion extends AOServObjectIntegerKey implements Comparable<TechnologyVersion>, DtoFactory<com.aoindustries.aoserv.client.dto.TechnologyVersion> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 2249386919329001586L;

    private String name;
    private String version;
    final private long updated;
    private UserId owner;
    final int operatingSystemVersion;

    public TechnologyVersion(
        AOServConnector connector,
        int pkey,
        String name,
        String version,
        long updated,
        UserId owner,
        int operatingSystemVersion
    ) {
        super(connector, pkey);
        this.name = name;
        this.version = version;
        this.updated = updated;
        this.owner = owner;
        this.operatingSystemVersion = operatingSystemVersion;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
        version = intern(version);
        owner = intern(owner);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TechnologyVersion other) {
        int diff = compareIgnoreCaseConsistentWithEquals(name, other.name);
        if(diff!=0) return diff;
        return compareIgnoreCaseConsistentWithEquals(version, other.version);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated unique key")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_NAME = getMethodColumn(TechnologyVersion.class, "technologyName");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the name of the software package")
    public TechnologyName getTechnologyName() throws RemoteException {
        return getConnector().getTechnologyNames().get(name);
    }

    @SchemaColumn(order=2, description="the version number of the package in #.##.##-## format")
    public String getVersion() {
        return version;
    }

    @SchemaColumn(order=3, description="the time this package was last updated")
    public Timestamp getUpdated() {
    	return new Timestamp(updated);
    }

    public static final MethodColumn COLUMN_OWNER = getMethodColumn(TechnologyVersion.class, "owner");
    @DependencySingleton
    @SchemaColumn(order=4, index=IndexType.INDEXED, description="the business_administrator who is responsible for maintaining this package")
    public MasterUser getOwner() throws RemoteException {
        if(owner==null) return null;
        return getConnector().getMasterUsers().get(owner);
    }

    public static final MethodColumn COLUMN_OPERATING_SYSTEM_VERSION = getMethodColumn(TechnologyVersion.class, "operatingSystemVersion");
    @DependencySingleton
    @SchemaColumn(order=5, index=IndexType.INDEXED, description="the version of the OS that this packages is installed")
    public OperatingSystemVersion getOperatingSystemVersion() throws RemoteException {
        return getConnector().getOperatingSystemVersions().get(operatingSystemVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TechnologyVersion(AOServConnector connector, com.aoindustries.aoserv.client.dto.TechnologyVersion dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getName(),
            dto.getVersion(),
            getTimeMillis(dto.getUpdated()),
            getUserId(dto.getOwner()),
            dto.getOperatingSystemVersion()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TechnologyVersion getDto() {
        return new com.aoindustries.aoserv.client.dto.TechnologyVersion(getKeyInt(), name, version, updated, getDto(owner), operatingSystemVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<HttpdServer> getHttpdServers() throws RemoteException {
        return getConnector().getHttpdServers().filterIndexed(HttpdServer.COLUMN_MOD_PHP_VERSION, this);
    }

    @DependentObjectSet
    public IndexedSet<MySQLServer> getMySQLServers() throws RemoteException {
        return getConnector().getMysqlServers().filterIndexed(MySQLServer.COLUMN_VERSION, this);
    }

    @DependentObjectSingleton
    public HttpdJBossVersion getHttpdJBossVersion() throws RemoteException {
        return getConnector().getHttpdJBossVersions().filterUnique(HttpdJBossVersion.COLUMN_VERSION, this);
    }

    @DependentObjectSingleton
    public HttpdTomcatVersion getHttpdTomcatVersion() throws RemoteException {
        return getConnector().getHttpdTomcatVersions().filterUnique(HttpdTomcatVersion.COLUMN_VERSION, this);
    }

    @DependentObjectSingleton
    public PostgresVersion getPostgresVersion() throws RemoteException {
        return getConnector().getPostgresVersions().filterUnique(PostgresVersion.COLUMN_VERSION, this);
    }

    @DependentObjectSet
    public IndexedSet<PostgresVersion> getPostgresVersionsByPostgisVersion() throws RemoteException {
        return getConnector().getPostgresVersions().filterIndexed(PostgresVersion.COLUMN_POSTGIS_VERSION, this);
    }

    /* TODO
    public HttpdTomcatVersion getHttpdTomcatVersion() throws RemoteException, SQLException {
    	return connector.getHttpdTomcatVersions().get(pkey);
    }
     */
    // </editor-fold>
}
