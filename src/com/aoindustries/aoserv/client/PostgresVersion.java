package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A <code>PostgresVersion</code> flags which <code>TechnologyVersion</code>s
 * are a version of PostgreSQL.
 *
 * @see  PostgresServer
 * @see  TechnologyVersion
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresVersion extends AOServObjectIntegerKey<PostgresVersion> implements BeanFactory<com.aoindustries.aoserv.client.beans.PostgresVersion> {

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
    private static final List<String> preferredMinorVersions = Collections.unmodifiableList(
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
    final private String minorVersion;
    final private Integer postgisVersion;

    public PostgresVersion(
        PostgresVersionService<?,?> service,
        int version,
        String minorVersion,
        Integer postgisVersion
    ) {
        super(service, version);
        this.minorVersion = minorVersion.intern();
        this.postgisVersion = postgisVersion;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(PostgresVersion other) throws RemoteException {
        return key==other.key ? 0 : getTechnologyVersion().compareTo(other.getTechnologyVersion());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="version", index=IndexType.PRIMARY_KEY, description="a reference to the PostgreSQL details in the <code>technology_versions</code> table")
    public TechnologyVersion getTechnologyVersion() throws RemoteException {
        return getService().getConnector().getTechnologyVersions().get(key);
    }

    @SchemaColumn(order=1, name="minor_version", description="the minor version for this version")
    public String getMinorVersion() {
        return minorVersion;
    }

    /**
     * Gets the PostGIS version of <code>null</code> if not supported by this PostgreSQL version....
     */
    @SchemaColumn(order=2, name="postgis_version", description="a reference to the PostGIS defails in the <code>technology_versions</code>")
    public TechnologyVersion getPostgisVersion() throws RemoteException {
        if(postgisVersion==null) return null;
        TechnologyVersion tv = getService().getConnector().getTechnologyVersions().get(postgisVersion);
        if(tv==null) throw new RemoteException("Unable to find TechnologyVersion: "+postgisVersion);
        if(
            tv.operatingSystemVersion
            != getTechnologyVersion().operatingSystemVersion
        ) {
            throw new RemoteException("postgresql/postgis version mismatch on PostgresVersion: #"+key);
        }
        return tv;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.PostgresVersion getBean() {
        return new com.aoindustries.aoserv.client.beans.PostgresVersion(key, minorVersion, postgisVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTechnologyVersion(),
            getPostgisVersion()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getPostgresServers(),
            getPostgresEncodings()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<PostgresServer> getPostgresServers() throws RemoteException {
        return getService().getConnector().getPostgresServers().getIndexed(PostgresServer.COLUMN_VERSION, this);
    }

    public Set<PostgresEncoding> getPostgresEncodings() throws RemoteException {
        return getService().getConnector().getPostgresEncodings().getIndexed(PostgresEncoding.COLUMN_POSTGRES_VERSION, this);
    }
    /* TODO
    public PostgresEncoding getPostgresEncoding(AOServConnector connector, String encoding) throws IOException, SQLException {
        return connector.getPostgresEncodings().getPostgresEncoding(this, encoding);
    }
     */
    // </editor-fold>
}
