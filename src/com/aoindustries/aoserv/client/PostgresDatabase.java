/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>PostgresDatabase</code> corresponds to a unique PostgreSQL table
 * space on one server.  The database name must be unique per server
 * and, to aid in account portability, will typically be unique
 * across the entire system.
 *
 * @see  PostgresEncoding
 * @see  PostgresServerUser
 *
 * @author  AO Industries, Inc.
 */
final public class PostgresDatabase extends AOServerResource implements Comparable<PostgresDatabase>, DtoFactory<com.aoindustries.aoserv.client.dto.PostgresDatabase> /* TODO: , Dumpable, Removable*/, JdbcProvider {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    /**
     * Special databases.
     */
    public static final PostgresDatabaseName
        AOINDUSTRIES,
        AOSERV,
        AOWEB,
        TEMPLATE0,
        TEMPLATE1
    ;
    static {
        try {
            AOINDUSTRIES = PostgresDatabaseName.valueOf("aoindustries").intern();
            AOSERV = PostgresDatabaseName.valueOf("aoserv").intern();
            AOWEB = PostgresDatabaseName.valueOf("aoindustries").intern();
            TEMPLATE0 = PostgresDatabaseName.valueOf("template0").intern();
            TEMPLATE1 = PostgresDatabaseName.valueOf("template1").intern();
        } catch(ValidationException err) {
            throw new AssertionError(err.getMessage());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = -5325299154750602717L;

    private PostgresDatabaseName name;
    final private int postgresServer;
    final private int datdba;
    final private int encoding;
    final private boolean template;
    final private boolean allowConn;
    final private boolean enablePostgis;

    public PostgresDatabase(
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
        PostgresDatabaseName name,
        int postgresServer,
        int datdba,
        int encoding,
        boolean template,
        boolean allowConn,
        boolean enablePostgis
    ) {
        super(connector, pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.name = name;
        this.postgresServer = postgresServer;
        this.datdba = datdba;
        this.encoding = encoding;
        this.template = template;
        this.allowConn = allowConn;
        this.enablePostgis = enablePostgis;
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
    public int compareTo(PostgresDatabase other) {
        try {
            int diff = name.compareTo(other.name);
            if(diff!=0) return diff;
            return postgresServer==other.postgresServer ? 0 : getPostgresServer().compareTo(other.getPostgresServer());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    public static final MethodColumn COLUMN_NAME = getMethodColumn(PostgresDatabase.class, "name");
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+1, index=IndexType.INDEXED, description="the name of the database")
    public PostgresDatabaseName getName() {
        return name;
    }

    public static final MethodColumn COLUMN_POSTGRES_SERVER = getMethodColumn(PostgresDatabase.class, "postgresServer");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+2, index=IndexType.INDEXED, description="the pkey of the PostgreSQL server")
    public PostgresServer getPostgresServer() throws RemoteException {
        return getConnector().getPostgresServers().get(postgresServer);
    }

    public static final MethodColumn COLUMN_DATDBA = getMethodColumn(PostgresDatabase.class, "datDba");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+3, index=IndexType.INDEXED, description="the datdba for the database")
    public PostgresUser getDatDba() throws RemoteException {
        return getConnector().getPostgresUsers().get(datdba);
    }

    public static final MethodColumn COLUMN_ENCODING = getMethodColumn(PostgresDatabase.class, "encoding");
    @DependencySingleton
    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+4, index=IndexType.INDEXED, description="the pkey of the encoding system used for the database")
    public PostgresEncoding getEncoding() throws RemoteException {
    	PostgresEncoding obj=getConnector().getPostgresEncodings().get(encoding);
        // Make sure the postgres encoding postgresql version matches the server this database is part of
//        if(
//            obj.postgresVersion
//            != getPostgresServer().version
//        ) {
//            throw new RemoteException("encoding/postgres server version mismatch on PostgresDatabase: #"+key);
//        }
    	return obj;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+5, description="if true, this database is a template")
    public boolean isTemplate() {
    	return template;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+6, description="if true, this database is accepting connections")
    public boolean getAllowConn() {
        return allowConn;
    }

    @SchemaColumn(order=AOSERVER_RESOURCE_LAST_COLUMN+7, description="indicates PostGIS is enabled on this database")
    public boolean getEnablePostgis() {
        return enablePostgis;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public PostgresDatabase(AOServConnector connector, com.aoindustries.aoserv.client.dto.PostgresDatabase dto) throws ValidationException {
        this(
            connector,
            dto.getPkey(),
            dto.getResourceType(),
            getAccountingCode(dto.getAccounting()),
            getTimeMillis(dto.getCreated()),
            getUserId(dto.getCreatedBy()),
            dto.getDisableLog(),
            getTimeMillis(dto.getLastEnabled()),
            dto.getAoServer(),
            dto.getBusinessServer(),
            getPostgresDatabaseName(dto.getName()),
            dto.getPostgresServer(),
            dto.getDatdba(),
            dto.getEncoding(),
            dto.isTemplate(),
            dto.isAllowConn(),
            dto.isEnablePostgis()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.PostgresDatabase getDto() {
        return new com.aoindustries.aoserv.client.dto.PostgresDatabase(
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
            postgresServer,
            datdba,
            encoding,
            template,
            allowConn,
            enablePostgis
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() {
        return name.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JdbcProvider">
    /**
     * The classname of the JDBC driver used for the <code>PostgresDatabase</code>.
     */
    public static final String JDBC_DRIVER="org.postgresql.Driver";

    @Override
    public String getJdbcDriver() {
        return JDBC_DRIVER;
    }

    @Override
    public String getJdbcUrl(boolean ipOnly) throws RemoteException {
        AOServer ao=getPostgresServer().getAoServer();
        return
            "jdbc:postgresql://"
            + (ipOnly
               ?ao.getServer().getNetDevice(ao.getDaemonDeviceId()).getPrimaryIPAddress().getIpAddress().toString()
               :ao.getHostname().toString()
            )
            + ':'
            + getPostgresServer().getNetBind().getPort()
            + '/'
            + getName()
        ;
    }

    @Override
    public String getJdbcDocumentationUrl() throws RemoteException {
        String version=getPostgresServer().getVersion().getVersion().getVersion();
        return "http://www.aoindustries.com/docs/postgresql-"+version+"/jdbc.html";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void dump(PrintWriter out) throws IOException, SQLException {
        dump((Writer)out);
    }

    public void dump(final Writer out) throws IOException, SQLException {
        getConnector().requestUpdate(
            false,
            new AOServConnector.UpdateRequest() {

                public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
                    masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_POSTGRES_DATABASE.ordinal());
                    masterOut.writeCompressedInt(pkey);
                }

                public void readResponse(CompressedDataInputStream masterIn) throws IOException, SQLException {
                    int code;
                    byte[] buff=BufferManager.getBytes();
                    try {
                        char[] chars=BufferManager.getChars();
                        try {
                            while((code=masterIn.readByte())==AOServProtocol.NEXT) {
                                int len=masterIn.readShort();
                                masterIn.readFully(buff, 0, len);
                                for(int c=0;c<len;c++) chars[c]=(char)buff[c];
                                out.write(chars, 0, len);
                            }
                        } finally {
                            BufferManager.release(chars);
                        }
                    } finally {
                        BufferManager.release(buff);
                    }
                    if(code!=AOServProtocol.DONE) {
                        AOServProtocol.checkResult(code, masterIn);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public void afterRelease() {
                }
            }
        );
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        
        PostgresServer ps=getPostgresServer();
        if(!allowConn) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a PostgreSQL database that does not allow connections: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
        if(template) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a template PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
        if(
            name==AOINDUSTRIES // OK - interned
            || name==AOSERV // OK - interned
            || name==AOWEB // OK - interned
        ) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a special PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));

        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_DATABASES,
            pkey
    	);
    }
     */
    // </editor-fold>
}
