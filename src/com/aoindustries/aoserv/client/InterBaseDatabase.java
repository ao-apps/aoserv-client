package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * An <code>InterBaseDatabase</code> represents one database.
 *
 * @see  InterBaseDatabase
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class InterBaseDatabase extends CachedObjectIntegerKey<InterBaseDatabase> implements Removable, Dumpable, JdbcProvider {

    static final int
        COLUMN_PKEY=0,
        COLUMN_DB_GROUP=2,
        COLUMN_DATDBA=3
    ;

    /**
     * The extension used for the files that store the databases.
     */
    public static final String DB_FILENAME_EXTENSION=".gdb";

    /**
     * The name of the main database.
     */
    public static final String ISC4="isc4";

    /**
     * The default number of days to keep backups.
     */
    public static final short DEFAULT_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL;
    public static final short DEFAULT_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION;

    /**
     * The classname of the JDBC driver used for the <code>InterBaseDatabase</code>.
     */
    public static final String JDBC_DRIVER="interbase.interclient.Driver";

    /**
     * The URL for JDBC documentation.
     */
    public static final String JDBC_DOCUMENTATION_URL="http://www.aoindustries.com/docs/interclient/";

    String name;
    int db_group;
    int datdba;
    private short backup_level;
    private short backup_retention;

    public int backup() {
	int backupPKey=table.connector.requestIntQueryIL(
            AOServProtocol.BACKUP_INTERBASE_DATABASE,
            pkey
	);
	return backupPKey;
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
                masterOut.writeCompressedInt(AOServProtocol.DUMP_INTERBASE_DATABASE);
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
        BackupLevel bl=table.connector.backupLevels.get(backup_level);
        if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
        return bl;
    }

    public BackupRetention getBackupRetention() {
        BackupRetention br=table.connector.backupRetentions.get(backup_retention);
        if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+backup_retention));
        return br;
    }

    public Object getColumn(int i) {
	switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return name;
            case COLUMN_DB_GROUP: return Integer.valueOf(db_group);
            case COLUMN_DATDBA: return Integer.valueOf(datdba);
            case 4: return Short.valueOf(backup_level);
            case 5: return Short.valueOf(backup_retention);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public InterBaseServerUser getDatabaseAdministrator() {
        InterBaseServerUser isu=table.connector.interBaseServerUsers.get(datdba);
        if(isu==null) throw new WrappedException(new SQLException("Unable to find InterBaseServerUser: "+datdba));
        return isu;
    }

    public String getJdbcDriver() {
        return JDBC_DRIVER;
    }

    public String getJdbcUrl(boolean ipOnly) {
	AOServer ao=getInterBaseDBGroup().getLinuxServerGroup().getAOServer();
        return
            "jdbc:interbase://"
            + (ipOnly
	       ?ao.getNetDevice(ao.getDaemonDeviceID().getName()).getPrimaryIPAddress().getIPAddress()
	       :ao.getServer().getHostname()
	    )
            + getPath()
        ;
    }
    
    public String getJdbcDocumentationUrl() {
        return JDBC_DOCUMENTATION_URL;
    }

    public String getName() {
        return name;
    }
    
    public InterBaseDBGroup getInterBaseDBGroup() {
        InterBaseDBGroup idg=table.connector.interBaseDBGroups.get(db_group);
        if(idg==null) throw new WrappedException(new SQLException("Unable to find InterBaseDBGroup: "+db_group));
        return idg;
    }

    public String getPath() {
        return getInterBaseDBGroup().getPath()+'/'+name+DB_FILENAME_EXTENSION;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.INTERBASE_DATABASES;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        name=result.getString(2);
        db_group=result.getInt(3);
        datdba=result.getInt(4);
        backup_level=result.getShort(5);
        backup_retention=result.getShort(6);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        name=in.readUTF();
        db_group=in.readCompressedInt();
        datdba=in.readCompressedInt();
        backup_level=in.readShort();
        backup_retention=in.readShort();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.TableID.INTERBASE_DATABASES,
            pkey
	);
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(name);
        out.writeCompressedInt(db_group);
        out.writeCompressedInt(datdba);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_114)>=0) {
            out.writeShort(backup_level);
            out.writeShort(backup_retention);
        } else out.writeCompressedInt(backup_retention);
    }
}
