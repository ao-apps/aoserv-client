/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.MySQLServerName;
import com.aoindustries.table.IndexType;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashSet;
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
final public class MySQLServer extends AOServObjectIntegerKey<MySQLServer> implements BeanFactory<com.aoindustries.aoserv.client.beans.MySQLServer> {

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

    public enum ReservedWord {
        ACTION,
        ADD,
        AFTER,
        AGGREGATE,
        ALL,
        ALTER,
        AND,
        AS,
        ASC,
        AUTO_INCREMENT,
        AVG,
        AVG_ROW_LENGTH,
        BETWEEN,
        BIGINT,
        BINARY,
        BIT,
        BLOB,
        BOOL,
        BOTH,
        BY,
        CASCADE,
        CASE,
        CHANGE,
        CHAR,
        CHARACTER,
        CHECK,
        CHECKSUM,
        COLUMN,
        COLUMNS,
        COMMENT,
        CONSTRAINT,
        CREATE,
        CROSS,
        CURRENT_DATE,
        CURRENT_TIME,
        CURRENT_TIMESTAMP,
        DATA,
        DATABASE,
        DATABASES,
        DATE,
        DATETIME,
        DAY,
        DAY_HOUR,
        DAY_MINUTE,
        DAY_SECOND,
        DAYOFMONTH,
        DAYOFWEEK,
        DAYOFYEAR,
        DEC,
        DECIMAL,
        DEFAULT,
        DELAY_KEY_WRITE,
        DELAYED,
        DELETE,
        DESC,
        DESCRIBE,
        DISTINCT,
        DISTINCTROW,
        DOUBLE,
        DROP,
        ELSE,
        ENCLOSED,
        END,
        ENUM,
        ESCAPE,
        ESCAPED,
        EXISTS,
        EXPLAIN,
        FIELDS,
        FILE,
        FIRST,
        FLOAT,
        FLOAT4,
        FLOAT8,
        FLUSH,
        FOR,
        FOREIGN,
        FROM,
        FULL,
        FUNCTION,
        GLOBAL,
        GRANT,
        GRANTS,
        GROUP,
        HAVING,
        HEAP,
        HIGH_PRIORITY,
        HOSTS,
        HOUR,
        HOUR_MINUTE,
        HOUR_SECOND,
        IDENTIFIED,
        IF,
        IGNORE,
        IN,
        INDEX,
        INFILE,
        INNER,
        INSERT,
        INSERT_ID,
        INT,
        INT1,
        INT2,
        INT3,
        INT4,
        INT8,
        INTEGER,
        INTERVAL,
        INTO,
        IS,
        ISAM,
        JOIN,
        KEY,
        KEYS,
        KILL,
        LAST_INSERT_ID,
        LEADING,
        LEFT,
        LENGTH,
        LIKE,
        LIMIT,
        LINES,
        LOAD,
        LOCAL,
        LOCK,
        LOGS,
        LONG,
        LONGBLOB,
        LONGTEXT,
        LOW_PRIORITY,
        MATCH,
        MAX,
        MAX_ROWS,
        MEDIUMBLOB,
        MEDIUMINT,
        MEDIUMTEXT,
        MIDDLEINT,
        MIN_ROWS,
        MINUTE,
        MINUTE_SECOND,
        MODIFY,
        MONTH,
        MONTHNAME,
        MYISAM,
        NATURAL,
        NO,
        NOT,
        NULL,
        NUMERIC,
        ON,
        OPTIMIZE,
        OPTION,
        OPTIONALLY,
        OR,
        ORDER,
        OUTER,
        OUTFILE,
        PACK_KEYS,
        PARTIAL,
        PASSWORD,
        PRECISION,
        PRIMARY,
        PRIVILEGES,
        PROCEDURE,
        PROCESS,
        PROCESSLIST,
        READ,
        REAL,
        REFERENCES,
        REGEXP,
        RELOAD,
        RENAME,
        REPLACE,
        RESTRICT,
        RETURNS,
        REVOKE,
        RLIKE,
        ROW,
        ROWS,
        SECOND,
        SELECT,
        SET,
        SHOW,
        SHUTDOWN,
        SMALLINT,
        SONAME,
        SQL_BIG_RESULT,
        SQL_BIG_SELECTS,
        SQL_BIG_TABLES,
        SQL_LOG_OFF,
        SQL_LOG_UPDATE,
        SQL_LOW_PRIORITY_UPDATES,
        SQL_SELECT_LIMIT,
        SQL_SMALL_RESULT,
        SQL_WARNINGS,
        STARTING,
        STATUS,
        STRAIGHT_JOIN,
        STRING,
        TABLE,
        TABLES,
        TEMPORARY,
        TERMINATED,
        TEXT,
        THEN,
        TIME,
        TIMESTAMP,
        TINYBLOB,
        TINYINT,
        TINYTEXT,
        TO,
        TRAILING,
        TYPE,
        UNIQUE,
        UNLOCK,
        UNSIGNED,
        UPDATE,
        USAGE,
        USE,
        USING,
        VALUES,
        VARBINARY,
        VARCHAR,
        VARIABLES,
        VARYING,
        WHEN,
        WHERE,
        WITH,
        WRITE,
        YEAR,
        YEAR_MONTH,
        ZEROFILL;

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
    final private MySQLServerName name;
    final private int version;
    final private int maxConnections;
    final private int netBind;

    public MySQLServer(
        MySQLServerService<?,?> service,
        int aoServerResource,
        MySQLServerName name,
        int version,
        int maxConnections,
        int netBind
    ) {
        super(service, aoServerResource);
        this.name = name.intern();
        this.version = version;
        this.maxConnections = maxConnections;
        this.netBind = netBind;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(MySQLServer other) throws RemoteException {
        if(key==other.key) return 0;
        int diff = name.compareTo(other.name);
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
    public MySQLServerName getName() {
    	return name;
    }

    static final String COLUMN_VERSION = "version";
    @SchemaColumn(order=2, name=COLUMN_VERSION, index=IndexType.INDEXED, description="the pkey of the MySQL version")
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
        return maxConnections;
    }

    static final String COLUMN_NET_BIND = "net_bind";
    @SchemaColumn(order=4, name=COLUMN_NET_BIND, index=IndexType.UNIQUE, description="the port the servers binds to")
    public NetBind getNetBind() throws RemoteException {
        return getService().getConnector().getNetBinds().get(netBind);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.MySQLServer getBean() {
        return new com.aoindustries.aoserv.client.beans.MySQLServer(key, name.getBean(), version, maxConnections, netBind);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAoServerResource(),
            getVersion(),
            getNetBind()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getFailoverMySQLReplications(),
            getMySQLDatabases(),
            getMySQLUsers()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        return ApplicationResources.accessor.getMessage(userLocale, "MySQLServer.toString", name, getAoServerResource().getAoServer().getHostname());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<FailoverMySQLReplication> getFailoverMySQLReplications() throws RemoteException {
        return getService().getConnector().getFailoverMySQLReplications().getIndexed(FailoverMySQLReplication.COLUMN_MYSQL_SERVER, this);
    }

    public Set<MySQLDatabase> getMySQLDatabases() throws RemoteException {
        return getService().getConnector().getMysqlDatabases().getIndexed(MySQLDatabase.COLUMN_MYSQL_SERVER, this);
    }

    /* TODO public List<MySQLDBUser> getMySQLDBUsers() throws IOException, SQLException {
        return getService().getConnector().getMysqlDBUsers().getMySQLDBUsers(this);
    }*/

    public Set<MySQLUser> getMySQLUsers() throws RemoteException {
    	return getService().getConnector().getMysqlUsers().getIndexed(MySQLUser.COLUMN_MYSQL_SERVER, this);
    }
    // </editor-fold>

    /**
     * Gets the minor version number in X.X[-max] format.  This corresponds to the installation
     * directory under /usr/mysql/X.X[-max] or /opt/mysql-X.X[-max]
     */
    public String getMinorVersion() throws RemoteException {
        String techVersion=getVersion().getVersion();
        int pos=techVersion.indexOf('.');
        if(pos==-1) return techVersion;
        int pos2=techVersion.indexOf('.', pos+1);
        if(pos2==-1) return techVersion;
        String S = techVersion.substring(0, pos2);
        if(techVersion.endsWith("-max")) return S+"-max";
        return S;
    }

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

    public String getDataDirectory() {
        return DATA_BASE_DIR+'/'+name;
    }
    */

    /* TODO
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
