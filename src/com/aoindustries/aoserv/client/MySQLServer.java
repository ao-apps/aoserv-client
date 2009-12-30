package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * A <code>MySQLServer</code> corresponds to a unique MySQL install
 * space on one server.  The server name must be unique per server.
 * <code>MySQLDatabase</code>s and <code>MySQLUser</code>s are
 * unique per <code>MySQLServer</code>.
 *
 * @see  MySQLDatabase
 * @see  MySQLUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLServer extends AOServObjectIntegerKey<MySQLServer> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The supported versions of MySQL.
     */
    public static final String
        VERSION_5_1_PREFIX="5.1.",
        VERSION_5_0_PREFIX="5.0.",
        VERSION_4_1_PREFIX="4.1.",
        VERSION_4_0_PREFIX="4.0."
    ;

    /**
     * The directory that contains the MySQLSQL data files.
     */
    public static final String DATA_BASE_DIR="/var/lib/mysql";

    /**
     * Gets the versions of MySQL in order of
     * preference.  Index <code>0</code> is the most
     * preferred.
     */
    public static final String[] getPreferredVersionPrefixes() {
        return new String[] {
            VERSION_5_1_PREFIX,
            VERSION_5_0_PREFIX,
            VERSION_4_1_PREFIX,
            VERSION_4_0_PREFIX
        };
    }

    /**
     * The maximum length of the name.
     */
    public static final int MAX_SERVER_NAME_LENGTH=31;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private String name;
    final private int version;
    final private int max_connections;
    final private int net_bind;

    public MySQLServer(MySQLServerService<?,?> service, int ao_server_resource, String name, int version, int max_connections, int net_bind) {
        super(service, ao_server_resource);
        this.name = name.intern();
        this.version = version;
        this.max_connections = max_connections;
        this.net_bind = net_bind;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(MySQLServer other) throws RemoteException {
        if(key==other.key) return 0;
        AOServerResource aoResource1 = getAoServerResource();
        AOServerResource aoResource2 = other.getAoServerResource();
        int diff = aoResource1.ao_server==aoResource2.ao_server ? 0 : aoResource1.getAoServer().compareTo(aoResource2.getAoServer());
        if(diff!=0) return diff;
        return compareIgnoreCaseConsistentWithEquals(name, other.name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", unique=true, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    @SchemaColumn(order=1, name="name", description="the name of the database")
    public String getName() {
    	return name;
    }

    @SchemaColumn(order=2, name="version", description="the pkey of the MySQL version")
    public TechnologyVersion getVersion() throws RemoteException {
        TechnologyVersion obj=getService().getConnector().getTechnologyVersions().get(version);
        if(obj==null) throw new RemoteException("Unable to find TechnologyVersion: "+version);
        if(
            obj.getOperatingSystemVersion().getPkey()
            != getAoServerResource().getAoServer().getServer().getOperatingSystemVersion().getPkey()
        ) {
            throw new RemoteException("resource/operating system version mismatch on MySQLServer: #"+key);
        }
    	return obj;
    }

    @SchemaColumn(order=3, name="max_connections", description="the maximum number of connections for the db")
    public int getMaxConnections() {
        return max_connections;
    }

    @SchemaColumn(order=4, name="net_bind", description="the port the servers binds to")
    public NetBind getNetBind() throws RemoteException {
        return getService().getConnector().getNetBinds().get(net_bind);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getAoServerResource(),
            getNetBind()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            // TODO: getFailoverMySQLReplications(),
            // TODO: getMySQLDatabases(),
            // TODO: getMySQLUsers()
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
    public List<FailoverMySQLReplication> getFailoverMySQLReplications() throws IOException, SQLException {
        return getService().getConnector().getFailoverMySQLReplications().getFailoverMySQLReplications(this);
    }

    public List<MySQLDatabase> getMySQLDatabases() throws IOException, SQLException {
        return getService().getConnector().getMysqlDatabases().getMySQLDatabases(this);
    }

    public List<MySQLDBUser> getMySQLDBUsers() throws IOException, SQLException {
        return getService().getConnector().getMysqlDBUsers().getMySQLDBUsers(this);
    }

    public List<MySQLUser> getMySQLUsers() throws IOException, SQLException {
    	return getService().getConnector().getMysqlUsers().getMySQLUsers(this);
    }
     */
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public int addMySQLDatabase(
        String name,
        Business bu
    ) throws IOException, SQLException {
    	return getService().getConnector().getMysqlDatabases().addMySQLDatabase(
            name,
            this,
            bu
    	);
    }

    public static void checkServerName(String name) throws IllegalArgumentException {
        // Must be a-z or 0-9 first, then a-z or 0-9 or . or _
        int len = name.length();
        if (len == 0 || len > MAX_SERVER_NAME_LENGTH) throw new IllegalArgumentException("MySQL server name should not exceed "+MAX_SERVER_NAME_LENGTH+" characters.");

        // The first character must be [a-z] or [0-9]
        char ch = name.charAt(0);
        if ((ch < 'a' || ch > 'z') && (ch<'0' || ch>'9')) throw new IllegalArgumentException("MySQL server names must start with [a-z] or [0-9]");
        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if (
                (ch<'a' || ch>'z')
                && (ch<'0' || ch>'9')
                && ch!='.'
                && ch!='_'
            ) throw new IllegalArgumentException("MySQL server names may only contain [a-z], [0-9], period (.), and underscore (_)");
    	}
    }

    public String getDataDirectory() {
        return DATA_BASE_DIR+'/'+name;
    }
    */
    /**
     * Gets the minor version number in X.X[-max] format.  This corresponds to the installation
     * directory under /usr/mysql/X.X[-max] or /opt/mysql-X.X[-max]
     */
    /* TODO
    public String getMinorVersion() throws SQLException, IOException {
        String techVersion=getVersion().getVersion();
        int pos=techVersion.indexOf('.');
        if(pos==-1) return techVersion;
        int pos2=techVersion.indexOf('.', pos+1);
        if(pos2==-1) return techVersion;
        String S = techVersion.substring(0, pos2);
        if(techVersion.endsWith("-max")) return S+"-max";
        return S;
    }

    public MySQLDatabase getMySQLDatabase(String name) throws IOException, SQLException {
    	return getService().getConnector().getMysqlDatabases().getMySQLDatabase(name, this);
    }

    public MySQLUser getMySQLUser(String username) throws IOException, SQLException {
    	return getService().getConnector().getMysqlUsers().getMySQLUser(username, this);
    }

    public boolean isMySQLDatabaseNameAvailable(String name) throws IOException, SQLException {
    	return getService().getConnector().getMysqlDatabases().isMySQLDatabaseNameAvailable(name, this);
    }

    public void restartMySQL() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.RESTART_MYSQL, pkey);
    }

    public void startMySQL() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.START_MYSQL, pkey);
    }

    public void stopMySQL() throws IOException, SQLException {
        getService().getConnector().requestUpdate(false, AOServProtocol.CommandID.STOP_MYSQL, pkey);
    }
    */
    final public static class MasterStatus implements Serializable {

        private static final long serialVersionUID = 1L;

        final private String file;
        final private String position;
        
        public MasterStatus(
            String file,
            String position
        ) {
            this.file=file;
            this.position=position;
        }

        public String getFile() {
            return file;
        }

        public String getPosition() {
            return position;
        }
    }

    /**
     * Gets the master status or <code>null</code> if no master status provided by MySQL.  If any error occurs, throws either
     * IOException or SQLException.
     */
    /* TODO
    public MasterStatus getMasterStatus() throws IOException, SQLException {
        return getService().getConnector().requestResult(
            true,
            new AOServConnector.ResultRequest<MasterStatus>() {
                MasterStatus result;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.GET_MYSQL_MASTER_STATUS.ordinal());
                    out.writeCompressedInt(pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.NEXT) {
                        result = new MasterStatus(
                            in.readNullUTF(),
                            in.readNullUTF()
                        );
                    } else if(code==AOServProtocol.DONE) {
                        result = null;
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public MasterStatus afterRelease() {
                    return result;
                }
            }
        );
    }
     */
    // </editor-fold>
}
