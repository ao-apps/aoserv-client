/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * A <code>MySQLDatabase</code> corresponds to a unique MySQL table
 * space on one server.  The database name must be unique per server
 * and, to aid in account portability, will typically be unique
 * across the entire system.
 *
 * @see  MySQLDBUser
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDatabase extends AOServObjectIntegerKey<MySQLDatabase> implements DtoFactory<com.aoindustries.aoserv.client.dto.MySQLDatabase> /* TODO: implements Removable, Dumpable, JdbcProvider*/ {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

    /**
     * The classname of the JDBC driver used for the <code>MySQLDatabase</code>.
     */
    public static final String
        REDHAT_JDBC_DRIVER="com.mysql.jdbc.Driver",
        CENTOS_JDBC_DRIVER="com.mysql.jdbc.Driver"
    ;
    
    /**
     * The URL for MySQL JDBC documentation.
     */
    public static final String
        REDHAT_JDBC_DOCUMENTATION_URL="http://www.mysql.com/documentation/connector-j/index.html",
        CENTOS_JDBC_DOCUMENTATION_URL="http://www.mysql.com/documentation/connector-j/index.html"
    ;

    /**
     * The root database for a mysql installation.
     */
    public static final MySQLDatabaseName MYSQL;

    /**
     * A special database that is never removed.
     */
    public static final MySQLDatabaseName INFORMATION_SCHEMA;

    static {
        try {
            MYSQL = MySQLDatabaseName.valueOf("mysql").intern();
            INFORMATION_SCHEMA = MySQLDatabaseName.valueOf("information_schema").intern();
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private MySQLDatabaseName name;
    final private int mysqlServer;

    public MySQLDatabase(
        MySQLDatabaseService<?,?> service,
        int aoServerResource,
        MySQLDatabaseName name,
        int mysqlServer
    ) {
        super(service, aoServerResource);
        this.name = name;
        this.mysqlServer = mysqlServer;
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
    protected int compareToImpl(MySQLDatabase other) throws RemoteException {
        int diff = name.compareTo(other.name);
        if(diff!=0) return diff;
        return mysqlServer==other.mysqlServer ? 0 : getMysqlServer().compareToImpl(other.getMysqlServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    static final String COLUMN_NAME = "name";
    @SchemaColumn(order=1, name=COLUMN_NAME, index=IndexType.INDEXED, description="the name of the database")
    public MySQLDatabaseName getName() {
        return name;
    }

    static final String COLUMN_MYSQL_SERVER = "mysql_server";
    @SchemaColumn(order=2, name=COLUMN_MYSQL_SERVER, index=IndexType.INDEXED, description="the pkey of the server that this database is hosted on")
    public MySQLServer getMysqlServer() throws RemoteException {
        return getService().getConnector().getMysqlServers().get(mysqlServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.MySQLDatabase getDto() {
        return new com.aoindustries.aoserv.client.dto.MySQLDatabase(key, getDto(name), mysqlServer);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServerResource());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMysqlServer());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMySQLDBUsers());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return name.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<MySQLDBUser> getMySQLDBUsers() throws RemoteException {
        return getService().getConnector().getMysqlDBUsers().filterIndexed(MySQLDBUser.COLUMN_MYSQL_DATABASE, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
    public int addMySQLDBUser(
        MySQLUser mu,
        boolean canSelect,
        boolean canInsert,
        boolean canUpdate,
        boolean canDelete,
        boolean canCreate,
        boolean canDrop,
        boolean canIndex,
        boolean canAlter,
        boolean canCreateTempTable,
        boolean canLockTables,
        boolean canCreateView,
        boolean canShowView,
        boolean canCreateRoutine,
        boolean canAlterRoutine,
        boolean canExecute,
        boolean canEvent,
        boolean canTrigger
    ) throws IOException, SQLException {
        return getService().getConnector().getMysqlDBUsers().addMySQLDBUser(
            this,
            mu,
            canSelect,
            canInsert,
            canUpdate,
            canDelete,
            canCreate,
            canDrop,
            canIndex,
            canAlter,
            canCreateTempTable,
            canLockTables,
            canCreateView,
            canShowView,
            canCreateRoutine,
            canAlterRoutine,
            canExecute,
            canEvent,
            canTrigger
        );
    }

    public void dump(PrintWriter out) throws IOException, SQLException {
        dump((Writer)out);
    }

    public void dump(final Writer out) throws IOException, SQLException {
        getService().getConnector().requestUpdate(
            false,
            new AOServConnector.UpdateRequest() {
                public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
                    masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_MYSQL_DATABASE.ordinal());
                    masterOut.writeCompressedInt(pkey);
                }

                public void readResponse(CompressedDataInputStream masterIn) throws IOException, SQLException {
                    Reader nestedIn = new InputStreamReader(new NestedInputStream(masterIn), "UTF-8");
                    try {
                        char[] chars=BufferManager.getChars();
                        try {
                            int len;
                            while((len=nestedIn.read(chars, 0, BufferManager.BUFFER_SIZE))!=-1) {
                                out.write(chars, 0, len);
                            }
                        } finally {
                            BufferManager.release(chars);
                        }
                    } finally {
                        nestedIn.close();
                    }
                }

                public void afterRelease() {
                }
            }
        );
    }

    public String getJdbcDriver() throws SQLException, IOException {
        int osv=getMySQLServer().getAoServerResource().getAoServer().getServer().getOperatingSystemVersion().getPkey();
        switch(osv) {
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DRIVER;
            case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64: return CENTOS_JDBC_DRIVER;
            default : throw new SQLException("Unsupported OperatingSystemVersion: "+osv);
        }
    }

    public String getJdbcUrl(boolean ipOnly) throws SQLException, IOException {
        MySQLServer ms=getMySQLServer();
    	AOServer ao=ms.getAoServerResource().getAoServer();
        return
            "jdbc:mysql://"
            + (ipOnly
               ?ao.getServer().getNetDevice(ao.getDaemonDeviceID().getName()).getPrimaryIPAddress().getIPAddress()
    	       :ao.getHostname()
            )
            + ":"
            + ms.getNetBind().getPort().getPort()
            + "/"
            + getName()
        ;
    }

    public String getJdbcDocumentationUrl() throws SQLException, IOException {
        int osv=getMySQLServer().getAoServerResource().getAoServer().getServer().getOperatingSystemVersion().getPkey();
        switch(osv) {
            case OperatingSystemVersion.MANDRIVA_2006_0_I586 : return MANDRAKE_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64 : return CENTOS_JDBC_DOCUMENTATION_URL;
            default : throw new SQLException("Unsupported OperatingSystemVersion: "+osv);
        }
    }

    public MySQLDBUser getMySQLDBUser(MySQLUser mu) throws IOException, SQLException {
    	return getService().getConnector().getMysqlDBUsers().getMySQLDBUser(this, mu);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(
            name==MYSQL // OK - interned
        ) reasons.add(new CannotRemoveReason<MySQLDatabase>("Not allowed to remove the MySQL database named "+MYSQL, this));
        if(
            name==INFORMATION_SCHEMA // OK - interned
        ) {
            String version = getMySQLServer().getVersion().getVersion();
            if(
                version.startsWith(MySQLServer.VERSION_5_0_PREFIX)
                || version.startsWith(MySQLServer.VERSION_5_1_PREFIX)
            ) reasons.add(new CannotRemoveReason<MySQLDatabase>("Not allowed to remove the MySQL database named "+INFORMATION_SCHEMA, this));
        }
        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_DATABASES,
            pkey
    	);
    }
*/
    public enum Engine {
        CSV,
        MyISAM,
        InnoDB,
        HEAP,
        MEMORY
    }

    public static class TableStatus implements Serializable {

        private static final long serialVersionUID = 1L;

        public enum RowFormat {
            Compact,
            Dynamic,
            Fixed
        }

        public enum Collation {
            latin1_swedish_ci,
            utf8_bin,
            utf8_general_ci,
            utf8_unicode_ci
        }

        private final String name;
        private final Engine engine;
        private final Integer version;
        private final RowFormat rowFormat;
        private final Long rows;
        private final Long avgRowLength;
        private final Long dataLength;
        private final Long maxDataLength;
        private final Long indexLength;
        private final Long dataFree;
        private final Long autoIncrement;
        private final String createTime;
        private final String updateTime;
        private final String checkTime;
        private final Collation collation;
        private final String checksum;
        private final String createOptions;
        private final String comment;

        public TableStatus(
            String name,
            Engine engine,
            Integer version,
            RowFormat rowFormat,
            Long rows,
            Long avgRowLength,
            Long dataLength,
            Long maxDataLength,
            Long indexLength,
            Long dataFree,
            Long autoIncrement,
            String createTime,
            String updateTime,
            String checkTime,
            Collation collation,
            String checksum,
            String createOptions,
            String comment
        ) {
            this.name = name;
            this.engine = engine;
            this.version = version;
            this.rowFormat = rowFormat;
            this.rows = rows;
            this.avgRowLength = avgRowLength;
            this.dataLength = dataLength;
            this.maxDataLength = maxDataLength;
            this.indexLength = indexLength;
            this.dataFree = dataFree;
            this.autoIncrement = autoIncrement;
            this.createTime = createTime;
            this.updateTime = updateTime;
            this.checkTime = checkTime;
            this.collation = collation;
            this.checksum = checksum;
            this.createOptions = createOptions;
            this.comment = comment;
        }

        public String getName() {
            return name;
        }

        public Engine getEngine() {
            return engine;
        }

        public Integer getVersion() {
            return version;
        }

        public RowFormat getRowFormat() {
            return rowFormat;
        }

        public Long getRows() {
            return rows;
        }

        public Long getAvgRowLength() {
            return avgRowLength;
        }

        public Long getDataLength() {
            return dataLength;
        }

        public Long getMaxDataLength() {
            return maxDataLength;
        }

        public Long getIndexLength() {
            return indexLength;
        }

        public Long getDataFree() {
            return dataFree;
        }

        public Long getAutoIncrement() {
            return autoIncrement;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public String getCheckTime() {
            return checkTime;
        }

        public Collation getCollation() {
            return collation;
        }

        public String getChecksum() {
            return checksum;
        }

        public String getCreateOptions() {
            return createOptions;
        }

        public String getComment() {
            return comment;
        }
    }

    /**
     * Gets the table status on the master server.
     */
/* TODO
    public List<TableStatus> getTableStatus() throws IOException, SQLException {
        return getTableStatus(null);
    }
*/
    /**
     * Gets the table status on the master server or provided slave server.
     */
/* TODO
    public List<TableStatus> getTableStatus(final FailoverMySQLReplication mysqlSlave) throws IOException, SQLException {
        return getService().getConnector().requestResult(
            true,
            new AOServConnector.ResultRequest<List<TableStatus>>() {
                private List<TableStatus> result;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.GET_MYSQL_TABLE_STATUS.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(mysqlSlave==null ? -1 : mysqlSlave.pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.NEXT) {
                        int size = in.readCompressedInt();
                        List<TableStatus> tableStatuses = new ArrayList<TableStatus>(size);
                        for(int c=0;c<size;c++) {
                            tableStatuses.add(
                                new TableStatus(
                                    in.readUTF(), // name
                                    in.readNullEnum(Engine.class), // engine
                                    in.readNullInteger(), // version
                                    in.readNullEnum(TableStatus.RowFormat.class), // rowFormat
                                    in.readNullLong(), // rows
                                    in.readNullLong(), // avgRowLength
                                    in.readNullLong(), // dataLength
                                    in.readNullLong(), // maxDataLength
                                    in.readNullLong(), // indexLength
                                    in.readNullLong(), // dataFree
                                    in.readNullLong(), // autoIncrement
                                    in.readNullUTF(), // createTime
                                    in.readNullUTF(), // updateTime
                                    in.readNullUTF(), // checkTime
                                    in.readNullEnum(TableStatus.Collation.class), // collation
                                    in.readNullUTF(), // checksum
                                    in.readNullUTF(), // createOptions
                                    in.readNullUTF() // comment
                                )
                            );
                        }
                        this.result = tableStatuses;
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public List<TableStatus> afterRelease() {
                    return result;
                }
            }
        );
    }
    */
    public static class CheckTableResult implements Serializable {

        private static final long serialVersionUID = 1L;

        public enum MsgType {
            status,
            error,
            info,
            warning,
            // From MySQL 5.1
            note,
            Error
        }

        private final MySQLTableName table;
        private final long duration;
        private final MsgType msgType;
        private final String msgText;

        public CheckTableResult(
            MySQLTableName table,
            long duration,
            MsgType msgType,
            String msgText
        ) {
            this.table = table;
            this.duration = duration;
            this.msgType = msgType;
            this.msgText = msgText;
        }

        public MySQLTableName getTable() {
            return table;
        }

        public long getDuration() {
            return duration;
        }

        public MsgType getMsgType() {
            return msgType;
        }

        public String getMsgText() {
            return msgText;
        }
    }

    /**
     * Gets the table status on the master server.
     */
/* TODO
    public List<CheckTableResult> checkTables(final Collection<String> tableNames) throws IOException, SQLException {
        return checkTables(null, tableNames);
    }
*/
    /**
     * Gets the table status on the master server or provided slave server.
     */
/* TODO
    public List<CheckTableResult> checkTables(final FailoverMySQLReplication mysqlSlave, final Collection<String> tableNames) throws IOException, SQLException {
        if(tableNames.isEmpty()) return Collections.emptyList();
        return getService().getConnector().requestResult(
            true,
            new AOServConnector.ResultRequest<List<CheckTableResult>>() {
                private List<CheckTableResult> result;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.CHECK_MYSQL_TABLES.ordinal());
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(mysqlSlave==null ? -1 : mysqlSlave.pkey);
                    int size = tableNames.size();
                    out.writeCompressedInt(size);
                    int count = 0;
                    Iterator<String> iter = tableNames.iterator();
                    while(count<size && iter.hasNext()) {
                        out.writeUTF(iter.next());
                        count++;
                    }
                    if(count!=size) throw new ConcurrentModificationException("count!=size");
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.NEXT) {
                        int size = in.readCompressedInt();
                        List<CheckTableResult> checkTableResults = new ArrayList<CheckTableResult>(size);
                        for(int c=0;c<size;c++) {
                            checkTableResults.add(
                                new CheckTableResult(
                                    in.readUTF(), // table
                                    in.readLong(), // duration
                                    in.readNullEnum(CheckTableResult.MsgType.class), // msgType
                                    in.readNullUTF() // msgText
                                )
                            );
                        }
                        this.result = checkTableResults;
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public List<CheckTableResult> afterRelease() {
                    return result;
                }
            }
        );
    }
*/
    // </editor-fold>
}