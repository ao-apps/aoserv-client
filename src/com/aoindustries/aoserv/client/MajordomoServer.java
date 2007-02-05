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
import java.util.Collections;
import java.util.List;

/**
 * A <code>MajordomoServer</code> provides Majordomo functionality for
 * a <code>EmailDomain</code>.  Once the <code>MajordomoServer</code>
 * is established, any number of <code>MajordomoList</code>s may be
 * added to it.
 *
 * @see  EmailDomain
 * @see  MajordomoList
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoServer extends CachedObjectIntegerKey<MajordomoServer> implements Removable {

    static final int COLUMN_DOMAIN=0;

    /**
     * The default number of days to keep backups.
     */
    public static final short DEFAULT_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL;
    public static final short DEFAULT_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION;

    /**
     * The directory that stores the majordomo servers.
     */
    public static final String MAJORDOMO_SERVER_DIRECTORY="/etc/mail/majordomo";

    /**
     * The username part of the email address used to directly email majordomo.
     */
    public static final String MAJORDOMO_ADDRESS="majordomo";

    /**
     * The username part of the email address used to directly email the majordomo owner.
     */
    public static final String
        OWNER_MAJORDOMO_ADDRESS="owner-majordomo",
        MAJORDOMO_OWNER_ADDRESS="majordomo-owner"
    ;

    int linux_server_account;
    int linux_server_group;
    String version;
    int majordomo_pipe_address;
    int owner_majordomo_add;
    int majordomo_owner_add;
    private short backup_level;
    private short backup_retention;

    public int addMajordomoList(
        String listName
    ) {
        return table.connector.majordomoLists.addMajordomoList(this, listName);
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public BackupLevel getBackupLevel() {
        Profiler.startProfile(Profiler.FAST, MajordomoServer.class, "getBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getBackupRetention() {
        Profiler.startProfile(Profiler.FAST, MajordomoServer.class, "getBackupRetention()", null);
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
            case COLUMN_DOMAIN: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(linux_server_account);
            case 2: return Integer.valueOf(linux_server_group);
            case 3: return version;
            case 4: return Integer.valueOf(majordomo_pipe_address);
            case 5: return Integer.valueOf(owner_majordomo_add);
            case 6: return Integer.valueOf(majordomo_owner_add);
            case 7: return Short.valueOf(backup_level);
            case 8: return Short.valueOf(backup_retention);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public EmailDomain getDomain() {
	EmailDomain obj=table.connector.emailDomains.get(pkey);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailDomain: "+pkey));
	return obj;
    }

    public LinuxServerAccount getLinuxServerAccount() {
	LinuxServerAccount obj=table.connector.linuxServerAccounts.get(linux_server_account);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: "+linux_server_account));
	return obj;
    }

    public LinuxServerGroup getLinuxServerGroup() {
	LinuxServerGroup obj=table.connector.linuxServerGroups.get(linux_server_group);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: "+linux_server_group));
	return obj;
    }

    public EmailPipeAddress getMajordomoPipeAddress() {
	EmailPipeAddress obj=table.connector.emailPipeAddresses.get(majordomo_pipe_address);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailPipeAddress: "+majordomo_pipe_address));
	return obj;
    }

    public MajordomoList getMajordomoList(String listName) {
        return table.connector.majordomoLists.getMajordomoList(this, listName);
    }

    public List<MajordomoList> getMajordomoLists() {
	return table.connector.majordomoLists.getMajordomoLists(this);
    }

    public EmailAddress getMajordomoOwnerAddress() {
	EmailAddress obj=table.connector.emailAddresses.get(majordomo_owner_add);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+majordomo_owner_add));
	return obj;
    }

    public EmailAddress getOwnerMajordomoAddress() {
	EmailAddress obj=table.connector.emailAddresses.get(owner_majordomo_add);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find EmailAddress: "+owner_majordomo_add));
	return obj;
    }

    protected int getTableIDImpl() {
	return SchemaTable.MAJORDOMO_SERVERS;
    }

    public MajordomoVersion getVersion() {
	MajordomoVersion obj=table.connector.majordomoVersions.get(version);
	if(obj==null) throw new WrappedException(new SQLException("Unable to find MajordomoVersion: "+version));
	return obj;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	linux_server_account=result.getInt(2);
	linux_server_group=result.getInt(3);
	version=result.getString(4);
	majordomo_pipe_address=result.getInt(5);
	owner_majordomo_add=result.getInt(6);
	majordomo_owner_add=result.getInt(7);
        backup_level=result.getShort(8);
        backup_retention=result.getShort(9);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	linux_server_account=in.readCompressedInt();
	linux_server_group=in.readCompressedInt();
	version=in.readUTF();
	majordomo_pipe_address=in.readCompressedInt();
	owner_majordomo_add=in.readCompressedInt();
	majordomo_owner_add=in.readCompressedInt();
        backup_level=in.readShort();
        backup_retention=in.readShort();
    }

    public void remove() {
	table.connector.requestUpdateIL(
            AOServProtocol.REMOVE,
            SchemaTable.MAJORDOMO_SERVERS,
            pkey
	);
    }

    public void setBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, MajordomoServer.class, "setBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.MAJORDOMO_SERVERS, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void write(CompressedDataOutputStream out, String protocolVersion) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(linux_server_account);
	out.writeCompressedInt(linux_server_group);
	out.writeUTF(version);
	out.writeCompressedInt(majordomo_pipe_address);
	out.writeCompressedInt(owner_majordomo_add);
	out.writeCompressedInt(majordomo_owner_add);
        out.writeShort(backup_level);
        out.writeShort(backup_retention);
    }
}