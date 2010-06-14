/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.PostgresDatabaseName;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

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
final public class PostgresDatabase extends AOServObjectIntegerKey<PostgresDatabase> implements BeanFactory<com.aoindustries.aoserv.client.beans.PostgresDatabase> /* TODO: , Dumpable, Removable*/, JdbcProvider {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

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
    private PostgresDatabaseName name;
    final private int postgresServer;
    final private int datdba;
    final private int encoding;
    final private boolean isTemplate;
    final private boolean allowConn;
    final private boolean enablePostgis;

    public PostgresDatabase(
        PostgresDatabaseService<?,?> service,
        int aoServerResource,
        PostgresDatabaseName name,
        int postgresServer,
        int datdba,
        int encoding,
        boolean isTemplate,
        boolean allowConn,
        boolean enablePostgis
    ) {
        super(service, aoServerResource);
        this.name = name;
        this.postgresServer = postgresServer;
        this.datdba = datdba;
        this.encoding = encoding;
        this.isTemplate = isTemplate;
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
    protected int compareToImpl(PostgresDatabase other) throws RemoteException {
        int diff = name.compareTo(other.name);
        if(diff!=0) return diff;
        return postgresServer==other.postgresServer ? 0 : getPostgresServer().compareTo(other.getPostgresServer());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="ao_server_resource", index=IndexType.PRIMARY_KEY, description="the unique resource id")
    public AOServerResource getAoServerResource() throws RemoteException {
        return getService().getConnector().getAoServerResources().get(key);
    }

    static final String COLUMN_NAME = "name";
    @SchemaColumn(order=1, name=COLUMN_NAME, index=IndexType.INDEXED, description="the name of the database")
    public PostgresDatabaseName getName() {
        return name;
    }

    static final String COLUMN_POSTGRES_SERVER = "postgres_server";
    @SchemaColumn(order=2, name=COLUMN_POSTGRES_SERVER, index=IndexType.INDEXED, description="the pkey of the PostgreSQL server")
    public PostgresServer getPostgresServer() throws RemoteException {
        return getService().getConnector().getPostgresServers().get(postgresServer);
    }

    static final String COLUMN_DATDBA = "datdba";
    @SchemaColumn(order=3, name=COLUMN_DATDBA, index=IndexType.INDEXED, description="the datdba for the database")
    public PostgresUser getDatDBA() throws RemoteException {
        return getService().getConnector().getPostgresUsers().get(datdba);
    }

    static final String COLUMN_ENCODING = "encoding";
    @SchemaColumn(order=4, name=COLUMN_ENCODING, index=IndexType.INDEXED, description="the pkey of the encoding system used for the database")
    public PostgresEncoding getPostgresEncoding() throws RemoteException {
    	PostgresEncoding obj=getService().getConnector().getPostgresEncodings().get(encoding);
        // Make sure the postgres encoding postgresql version matches the server this database is part of
//        if(
//            obj.postgresVersion
//            != getPostgresServer().version
//        ) {
//            throw new RemoteException("encoding/postgres server version mismatch on PostgresDatabase: #"+key);
//        }
    	return obj;
    }

    @SchemaColumn(order=5, name="is_template", description="if true, this database is a template")
    public boolean isTemplate() {
    	return isTemplate;
    }

    @SchemaColumn(order=6, name="allow_conn", description="if true, this database is accepting connections")
    public boolean getAllowsConnections() {
        return allowConn;
    }

    @SchemaColumn(order=7, name="enable_postgis", description="indicates PostGIS is enabled on this database")
    public boolean getEnablePostgis() {
        return enablePostgis;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.PostgresDatabase getBean() {
        return new com.aoindustries.aoserv.client.beans.PostgresDatabase(key, getBean(name), postgresServer, datdba, encoding, isTemplate, allowConn, enablePostgis);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAoServerResource(),
            getPostgresServer(),
            getDatDBA(),
            getPostgresEncoding()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) {
        return name.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JdbcProvider">
    /**
     * The classname of the JDBC driver used for the <code>PostgresDatabase</code>.
     */
    public static final String JDBC_DRIVER="org.postgresql.Driver";

    public String getJdbcDriver() {
        return JDBC_DRIVER;
    }

    public String getJdbcUrl(boolean ipOnly) throws RemoteException {
        AOServer ao=getPostgresServer().getAoServerResource().getAoServer();
        return
            "jdbc:postgresql://"
            + (ipOnly
               ?ao.getServer().getNetDevice(ao.getDaemonDeviceID()).getPrimaryIPAddress().getIpAddress().getAddress()
               :ao.getHostname().getDomain()
            )
            + ':'
            + getPostgresServer().getNetBind().getPort()
            + '/'
            + getName()
        ;
    }

    public String getJdbcDocumentationUrl() throws RemoteException {
        String version=getPostgresServer().getPostgresVersion().getTechnologyVersion().getVersion();
        return "http://www.aoindustries.com/docs/postgresql-"+version+"/jdbc.html";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /* TODO
    public void dump(PrintWriter out) throws IOException, SQLException {
        dump((Writer)out);
    }

    public void dump(final Writer out) throws IOException, SQLException {
        getService().getConnector().requestUpdate(
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

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        
        PostgresServer ps=getPostgresServer();
        if(!allowConn) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a PostgreSQL database that does not allow connections: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
        if(isTemplate) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a template PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));
        if(
            name.equals(AOINDUSTRIES)
            || name.equals(AOSERV)
            || name.equals(AOWEB)
        ) reasons.add(new CannotRemoveReason<PostgresDatabase>("Not allowed to drop a special PostgreSQL database: "+name+" on "+ps.getName()+" on "+ps.getAOServer().getHostname(), this));

        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	getService().getConnector().requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.POSTGRES_DATABASES,
            pkey
    	);
    }
     */
    // </editor-fold>
}
