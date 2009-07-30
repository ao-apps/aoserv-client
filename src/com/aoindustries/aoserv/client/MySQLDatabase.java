package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>MySQLDatabase</code> corresponds to a unique MySQL table
 * space on one server.  The database name must be unique per server
 * and, to aid in account portability, will typically be unique
 * across the entire system.
 *
 * @see  MySQLDBUser
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MySQLDatabase extends CachedObjectIntegerKey<MySQLDatabase> implements Removable, Dumpable, JdbcProvider {

    static final int
        COLUMN_PKEY=0,
        COLUMN_MYSQL_SERVER=2,
        COLUMN_PACKAGE=3
    ;
    static final String COLUMN_NAME_name = "name";
    static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

    /**
     * The classname of the JDBC driver used for the <code>MySQLDatabase</code>.
     */
    public static final String
        REDHAT_JDBC_DRIVER="com.mysql.jdbc.Driver",
        MANDRAKE_JDBC_DRIVER="com.mysql.jdbc.Driver",
        CENTOS_JDBC_DRIVER="com.mysql.jdbc.Driver"
    ;
    
    /**
     * The URL for MySQL JDBC documentation.
     * TODO: put the Mandrake documentation on http://www.aoindustries.com/docs/
     */
    public static final String
        REDHAT_JDBC_DOCUMENTATION_URL="http://www.mysql.com/documentation/connector-j/index.html",
        MANDRAKE_JDBC_DOCUMENTATION_URL="http://www.mysql.com/documentation/connector-j/index.html",
        CENTOS_JDBC_DOCUMENTATION_URL="http://www.mysql.com/documentation/connector-j/index.html"
    ;

    /**
     * The longest name allowed for a MySQL database.
     */
    public static final int MAX_DATABASE_NAME_LENGTH=64;

    /**
     * The root database for a mysql installation.
     */
    public static final String MYSQL="mysql";

    /**
     * A special database that is never removed.
     */
    public static final String INFORMATION_SCHEMA="information_schema";

    String name;
    int mysql_server;
    String packageName;

    public int addMySQLServerUser(
        MySQLServerUser msu,
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
        return table.connector.getMysqlDBUsers().addMySQLDBUser(
            this,
            msu,
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
        table.connector.requestUpdate(
            false,
            new AOServConnector.UpdateRequest() {
                public void writeRequest(CompressedDataOutputStream masterOut) throws IOException {
                    masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_MYSQL_DATABASE.ordinal());
                    masterOut.writeCompressedInt(pkey);
                }

                public void readResponse(CompressedDataInputStream masterIn) throws IOException, SQLException {
                    /*int code;
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
                    if(code!=AOServProtocol.DONE) AOServProtocol.checkResult(code, masterIn);*/
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

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_MYSQL_SERVER: return Integer.valueOf(mysql_server);
            case COLUMN_PACKAGE: return packageName;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getJdbcDriver() throws SQLException, IOException {
        int osv=getMySQLServer().getAOServer().getServer().getOperatingSystemVersion().getPkey();
        switch(osv) {
            case OperatingSystemVersion.MANDRIVA_2006_0_I586 : return MANDRAKE_JDBC_DRIVER;
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DRIVER;
            case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64: return CENTOS_JDBC_DRIVER;
            default : throw new SQLException("Unsupported OperatingSystemVersion: "+osv);
        }
    }

    public String getJdbcUrl(boolean ipOnly) throws SQLException, IOException {
        MySQLServer ms=getMySQLServer();
	AOServer ao=ms.getAOServer();
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
        int osv=getMySQLServer().getAOServer().getServer().getOperatingSystemVersion().getPkey();
        switch(osv) {
            case OperatingSystemVersion.MANDRIVA_2006_0_I586 : return MANDRAKE_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64 : return CENTOS_JDBC_DOCUMENTATION_URL;
            default : throw new SQLException("Unsupported OperatingSystemVersion: "+osv);
        }
    }

    public MySQLDBUser getMySQLDBUser(MySQLServerUser msu) throws IOException, SQLException {
	return table.connector.getMysqlDBUsers().getMySQLDBUser(this, msu);
    }

    public List<MySQLDBUser> getMySQLDBUsers() throws IOException, SQLException {
        return table.connector.getMysqlDBUsers().getMySQLDBUsers(this);
    }

    public List<MySQLServerUser> getMySQLServerUsers() throws IOException, SQLException {
        return table.connector.getMysqlDBUsers().getMySQLServerUsers(this);
    }

    public String getName() {
	return name;
    }

    public Package getPackage() throws SQLException, IOException {
	Package obj=table.connector.getPackages().get(packageName);
	if(obj==null) throw new SQLException("Unable to find Package: "+packageName);
	return obj;
    }

    public MySQLServer getMySQLServer() throws SQLException, IOException {
	MySQLServer obj=table.connector.getMysqlServers().get(mysql_server);
	if(obj==null) throw new SQLException("Unable to find MySQLServer: "+mysql_server);
	return obj;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MYSQL_DATABASES;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	name=result.getString(2);
	mysql_server=result.getInt(3);
	packageName=result.getString(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	name=in.readUTF();
	mysql_server=in.readCompressedInt();
	packageName=in.readUTF().intern();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons(Locale userLocale) throws SQLException, IOException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(name.equals(MYSQL)) reasons.add(new CannotRemoveReason<MySQLDatabase>("Not allowed to remove the MySQL database named "+MYSQL, this));
        if(name.equals(INFORMATION_SCHEMA)) {
            String version = getMySQLServer().getVersion().getVersion();
            if(
                version.startsWith(MySQLServer.VERSION_5_0_PREFIX)
                || version.startsWith(MySQLServer.VERSION_5_1_PREFIX)
            ) reasons.add(new CannotRemoveReason<MySQLDatabase>("Not allowed to remove the MySQL database named "+INFORMATION_SCHEMA, this));
        }
        return reasons;
    }

    public void remove() throws IOException, SQLException {
    	table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_DATABASES,
            pkey
    	);
    }

    @Override
    String toStringImpl(Locale userLocale) {
	return name;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(name);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_4)<0) out.writeCompressedInt(-1);
        else out.writeCompressedInt(mysql_server);
	out.writeUTF(packageName);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeShort(0);
            out.writeShort(7);
        }
    }
}