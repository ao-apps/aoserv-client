package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
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

    /**
     * The default number of days to keep backups.
     */
    public static final short DEFAULT_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL;
    public static final short DEFAULT_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION;

    /**
     * The classname of the JDBC driver used for the <code>MySQLDatabase</code>.
     */
    public static final String
        REDHAT_JDBC_DRIVER="com.mysql.jdbc.Driver",
        MANDRAKE_JDBC_DRIVER="com.mysql.jdbc.Driver"
    ;
    
    /**
     * The URL for MySQL JDBC documentation.
     * TODO: put the Mandrake documentation on http://www.aoindustries.com/docs/
     */
    public static final String
        REDHAT_JDBC_DOCUMENTATION_URL="http://www.mysql.com/documentation/connector-j/index.html",
        MANDRAKE_JDBC_DOCUMENTATION_URL="http://www.mysql.com/documentation/connector-j/index.html"
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
    private short backup_level;
    private short backup_retention;

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

    public int backup() {
	return table.connector.requestIntQueryIL(
            AOServProtocol.CommandID.BACKUP_MYSQL_DATABASE,
            pkey
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

    public BackupLevel getBackupLevel() {
        Profiler.startProfile(Profiler.FAST, MySQLDatabase.class, "getBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getBackupRetention() {
        Profiler.startProfile(Profiler.FAST, MySQLDatabase.class, "getBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_MYSQL_SERVER: return Integer.valueOf(mysql_server);
            case COLUMN_PACKAGE: return packageName;
            case 4: return Short.valueOf(backup_level);
            case 5: return Short.valueOf(backup_retention);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getJdbcDriver() {
        int osv=getMySQLServer().getAOServer().getServer().getOperatingSystemVersion().getPkey();
        switch(osv) {
            case OperatingSystemVersion.MANDRAKE_10_1_I586 : return MANDRAKE_JDBC_DRIVER;
            case OperatingSystemVersion.MANDRIVA_2006_0_I586 : return MANDRAKE_JDBC_DRIVER;
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DRIVER;
            default : throw new WrappedException(new SQLException("Unsupported OperatingSystemVersion: "+osv));
        }
    }

    public String getJdbcUrl(boolean ipOnly) {
        MySQLServer ms=getMySQLServer();
	AOServer ao=ms.getAOServer();
        return
            "jdbc:mysql://"
            + (ipOnly
               ?ao.getNetDevice(ao.getDaemonDeviceID().getName()).getPrimaryIPAddress().getIPAddress()
	       :ao.getServer().getHostname()
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
            case OperatingSystemVersion.MANDRAKE_10_1_I586 : return MANDRAKE_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.MANDRIVA_2006_0_I586 : return MANDRAKE_JDBC_DOCUMENTATION_URL;
            case OperatingSystemVersion.REDHAT_ES_4_X86_64 : return REDHAT_JDBC_DOCUMENTATION_URL;
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

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	name=result.getString(2);
	mysql_server=result.getInt(3);
	packageName=result.getString(4);
        backup_level=result.getShort(5);
        backup_retention=result.getShort(6);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	name=in.readUTF();
	mysql_server=in.readCompressedInt();
	packageName=in.readUTF().intern();
        backup_level=in.readShort();
        backup_retention=in.readShort();
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

    public void setBackupRetention(short days) {
        table.connector.requestUpdateIL(AOServProtocol.CommandID.SET_BACKUP_RETENTION, days, SchemaTable.TableID.MYSQL_DATABASES, pkey);
    }

    String toStringImpl() {
	return name;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeUTF(name);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_4)<0) out.writeCompressedInt(-1);
        else out.writeCompressedInt(mysql_server);
	out.writeUTF(packageName);
        out.writeShort(backup_level);
        out.writeShort(backup_retention);
    }
}