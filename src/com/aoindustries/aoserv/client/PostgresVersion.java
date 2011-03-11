/*
 * Copyright 2002-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * A <code>PostgresVersion</code> flags which <code>TechnologyVersion</code>s
 * are a version of PostgreSQL.
 *
 * @see  PostgresServer
 * @see  TechnologyVersion
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresVersion extends AOServObjectIntegerKey implements Comparable<PostgresVersion>, DtoFactory<com.aoindustries.aoserv.client.dto.PostgresVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    public static final String
        VERSION_7_1="7.1",
        VERSION_7_2="7.2",
        VERSION_7_3="7.3",
        VERSION_8_0="8.0",
        VERSION_8_1="8.1",
        VERSION_8_3="8.3"
    ;

    /**
     * Gets the versions of PostgreSQL in order of
     * preference.  Index <code>0</code> is the most
     * preferred.
     */
    private static final List<String> preferredMinorVersions = AoCollections.optimalUnmodifiableList(
        Arrays.asList(
            VERSION_8_3,
            VERSION_8_1,
            VERSION_8_0,
            VERSION_7_3,
            VERSION_7_2,
            VERSION_7_1
        )
    );
    public static final List<String> getPreferredMinorVersions() {
        return preferredMinorVersions;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private String minorVersion;
    final private Integer postgisVersion;

    public PostgresVersion(
        AOServConnector connector,
        int version,
        String minorVersion,
        Integer postgisVersion
    ) {
        super(connector, version);
        this.minorVersion = minorVersion;
        this.postgisVersion = postgisVersion;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        minorVersion = intern(minorVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(PostgresVersion other) {
        try {
            return key==other.key ? 0 : getTechnologyVersion().compareTo(other.getTechnologyVersion());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    static final String COLUMN_VERSION = "version";
    @SchemaColumn(order=0, name=COLUMN_VERSION, index=IndexType.PRIMARY_KEY, description="a reference to the PostgreSQL details in the <code>technology_versions</code> table")
    public TechnologyVersion getTechnologyVersion() throws RemoteException {
        return getConnector().getTechnologyVersions().get(key);
    }

    @SchemaColumn(order=1, name="minor_version", description="the minor version for this version")
    public String getMinorVersion() {
        return minorVersion;
    }

    /**
     * Gets the PostGIS version of <code>null</code> if not supported by this PostgreSQL version....
     */
    static final String COLUMN_POSTGIS_VERSION = "postgis_version";
    @SchemaColumn(order=2, name=COLUMN_POSTGIS_VERSION, index=IndexType.INDEXED, description="a reference to the PostGIS defails in the <code>technology_versions</code>")
    public TechnologyVersion getPostgisVersion() throws RemoteException {
        if(postgisVersion==null) return null;
        TechnologyVersion tv = getConnector().getTechnologyVersions().get(postgisVersion);
        if(
            tv.operatingSystemVersion
            != getTechnologyVersion().operatingSystemVersion
        ) {
            throw new RemoteException("postgresql/postgis version mismatch on PostgresVersion: #"+key);
        }
        return tv;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PostgresVersion(AOServConnector connector, com.aoindustries.aoserv.client.dto.PostgresVersion dto) {
        this(
            connector,
            dto.getVersion(),
            dto.getMinorVersion(),
            dto.getPostgisVersion()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PostgresVersion getDto() {
        return new com.aoindustries.aoserv.client.dto.PostgresVersion(key, minorVersion, postgisVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject<?>> addDependencies(UnionSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getTechnologyVersion());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgisVersion());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject<?>> addDependentObjects(UnionSet<AOServObject<?>> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresServers());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getPostgresEncodings());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<PostgresServer> getPostgresServers() throws RemoteException {
        return getConnector().getPostgresServers().filterIndexed(PostgresServer.COLUMN_VERSION, this);
    }

    public IndexedSet<PostgresEncoding> getPostgresEncodings() throws RemoteException {
        return getConnector().getPostgresEncodings().filterIndexed(PostgresEncoding.COLUMN_POSTGRES_VERSION, this);
    }
    /* TODO
    public PostgresEncoding getPostgresEncoding(AOServConnector connector, String encoding) throws IOException, SQLException {
        return connector.getPostgresEncodings().getPostgresEncoding(this, encoding);
    }
     */
    // </editor-fold>
}
