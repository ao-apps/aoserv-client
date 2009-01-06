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
        boolean canExecute
    ) {
	return table.connector.mysqlDBUsers.addMySQLDBUser(
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
            canExecute
	);
    }

    public void dump(PrintWriter out) {
	dump((Writer)out);
    }

    public void dump(Writer out) {
        try {
            // Create the new profile
            AOServConnection connection=table.connector.getConnection();
            try {
                CompressedDataOutputStream masterOut=connection.getOutputStream();
                masterOut.writeCompressedInt(AOServProtocol.CommandID.DUMP_MYSQL_DATABASE.ordinal());
                masterOut.writeCompressedInt(pkey);
                masterOut.flush();

                CompressedDataInputStream masterIn=connection.getInputStream();
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
                if(code!=AOServProtocol.DONE) AOServProtocol.checkResult(code, masterIn);
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                table.connector.releaseConnection(connection);
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_MYSQL_SERVER: return Integer.valueOf(mysql_server);
            case COLUMN_PACKAGE: return packageName;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getJdbcDriver() {
        int osv=getMySQLServer().getAOServer().getServer().getOperatingSystemVersion().getPkey();
        switch(osv) {
            case OperatingSystemVersion.MANDRIVA_2006_0_I586 : return MANDRAKE_JDBC_DRIVER;
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DRIVER;
            case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64: return CENTOS_JDBC_DRIVER;
            default : throw new WrappedException(new SQLException("Unsupported OperatingSystemVersion: "+osv));
        }
    }

    public String getJdbcUrl(boolean ipOnly) {
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

    public String getJdbcDocumentationUrl() {
        int osv=getMySQLServer().getAOServer().getServer().getOperatingSystemVersion().getPkey();
        switch(osv) {
            case OperatingSystemVersion.MANDRIVA_2006_0_I586 : return MANDRAKE_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.CENTOS_5_I686_AND_X86_64 : return CENTOS_JDBC_DOCUMENTATION_URL;
            default : throw new WrappedException(new SQLException("Unsupported OperatingSystemVersion: "+osv));
        }
    }

    public MySQLDBUser getMySQLDBUser(MySQLServerUser msu) {
	return table.connector.mysqlDBUsers.getMySQLDBUser(this, msu);
    }

    public List<MySQLDBUser> getMySQLDBUsers() {
        return table.connector.mysqlDBUsers.getMySQLDBUsers(this);
    }

    public List<MySQLServerUser> getMySQLServerUsers() {
        return table.connector.mysqlDBUsers.getMySQLServerUsers(this);
    }

    public String getName() {
	return name;
    }

    public Package getPackage() {
	Package obj=table.connector.packages.get(packageName);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find Package: "+packageName));
	return obj;
    }

    public MySQLServer getMySQLServer() {
	MySQLServer obj=table.connector.mysqlServers.get(mysql_server);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find MySQLServer: "+mysql_server));
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

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();
        if(name.equals(MYSQL)) reasons.add(new CannotRemoveReason<MySQLDatabase>("Not allowed to remove the MySQL database named "+MYSQL, this));
        if(name.equals(INFORMATION_SCHEMA) && getMySQLServer().getVersion().getVersion().startsWith("5.0.")) reasons.add(new CannotRemoveReason<MySQLDatabase>("Not allowed to remove the MySQL database named "+INFORMATION_SCHEMA, this));
        return reasons;
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.MYSQL_DATABASES,
            pkey
	);
    }

    String toStringImpl() {
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