package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Set;

/**
 * Each <code>TechnologyName</code> may have multiple versions installed.
 * Each of those versions is a <code>TechnologyVersion</code>.
 *
 * @see  TechnologyName
 *
 * @author  AO Industries, Inc.
 */
final public class TechnologyVersion extends AOServObjectIntegerKey<TechnologyVersion> implements BeanFactory<com.aoindustries.aoserv.client.beans.TechnologyVersion> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String name;
    final private String version;
    final private Timestamp updated;
    final private String owner;
    final int operatingSystemVersion;

    public TechnologyVersion(
        TechnologyVersionService<?,?> service,
        int pkey,
        String name,
        String version,
        Timestamp updated,
        String owner,
        int operatingSystemVersion
    ) {
        super(service, pkey);
        this.name = name.intern();
        this.version = version;
        this.updated = updated;
        this.owner = owner.intern();
        this.operatingSystemVersion = operatingSystemVersion;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(TechnologyVersion other) {
        int diff = AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(name, other.name);
        if(diff!=0) return diff;
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(version, other.version);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated unique key")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_NAME = "name";
    @SchemaColumn(order=1, name=COLUMN_NAME, index=IndexType.INDEXED, description="the name of the software package")
    public TechnologyName getTechnologyName() throws RemoteException {
        return getService().getConnector().getTechnologyNames().get(name);
    }

    @SchemaColumn(order=2, name="version", description="the version number of the package in #.##.##-## format")
    public String getVersion() {
        return version;
    }

    @SchemaColumn(order=3, name="updated", description="the time this package was last updated")
    public Timestamp getUpdated() {
    	return updated;
    }

    /* TODO
    @SchemaColumn(order=4, name="owner", description="the business_administrator who is responsible for maintaining this package")
    public MasterUser getOwner() throws RemoteException {
        return getService().getConnector().getMasterUsers().get(owner);
    }*/

    static final String COLUMN_OPERATING_SYSTEM_VERSION = "operating_system_version";
    @SchemaColumn(order=4, name=COLUMN_OPERATING_SYSTEM_VERSION, index=IndexType.INDEXED, description="the version of the OS that this packages is installed")
    public OperatingSystemVersion getOperatingSystemVersion() throws RemoteException {
        return getService().getConnector().getOperatingSystemVersions().get(operatingSystemVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TechnologyVersion getBean() {
        return new com.aoindustries.aoserv.client.beans.TechnologyVersion(key, name, version, updated, owner, operatingSystemVersion);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getTechnologyName(),
            // TODO: getOwner(),
            getOperatingSystemVersion()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            AOServObjectUtils.createDependencySet(
                getPostgresVersion()
            ),
            getMySQLServers()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<MySQLServer> getMySQLServers() throws RemoteException {
        return getService().getConnector().getMysqlServers().getIndexed(MySQLServer.COLUMN_VERSION, this);
    }

    public PostgresVersion getPostgresVersion() throws RemoteException {
        return getService().getConnector().getPostgresVersions().get(key);
    }

    /* TODO
    public HttpdTomcatVersion getHttpdTomcatVersion() throws RemoteException, SQLException {
    	return connector.getHttpdTomcatVersions().get(pkey);
    }
     */
    // </editor-fold>
}
