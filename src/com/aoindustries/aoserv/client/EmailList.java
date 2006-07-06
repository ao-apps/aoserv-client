package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
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
 * Any incoming email addressed to a <code>EmailList</code> is immediately
 * forwarded on to all addresses contained in the list.
 *
 * @see  EmailAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class EmailList extends CachedObjectIntegerKey<EmailList> implements Removable, Disablable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_LINUX_ACCOUNT=2
    ;

    public static final short DEFAULT_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL;
    public static final short DEFAULT_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION;

    /**
     * The directory that email lists are normally contained in.
     */
    public static final String LIST_DIRECTORY="/etc/mail/lists";

    /**
     * The maximum length of an email list name.
     */
    public static final int MAX_NAME_LENGTH=64;

    String path;
    int linux_account;
    int linux_group;
    private short backup_level;
    private short backup_retention;
    int disable_log;

    public int addEmailAddress(EmailAddress address) {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "addEmailAddress(EmailAddress)", null);
        try {
            return table.connector.emailListAddresses.addEmailListAddress(address, this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "canEnable()", null);
        try {
            DisableLog dl=getDisableLog();
            if(dl==null) return false;
            else return
                dl.canEnable()
                && getLinuxServerGroup().getLinuxGroup().getPackage().disable_log==-1
                && getLinuxServerAccount().disable_log==-1
            ;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void disable(DisableLog dl) {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "disable(DisableLog)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.EMAIL_LISTS, dl.pkey, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
    
    public void enable() {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "enable()", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.EMAIL_LISTS, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Gets the list of addresses that email will be sent to, one address per line.
     * The list is obtained from a file on the server that hosts the list.
     */
    public String getAddressList() {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "getAddressList()", null);
        try {
            return table.connector.requestStringQuery(AOServProtocol.GET_EMAIL_LIST_ADDRESS_LIST, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * Gets the number of addresses in an address list.  The number of addresses is equal to the number
     * of non-blank lines.
     */
    public int getAddressListCount() {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "getAddressListCount()", null);
        try {
            String list=getAddressList();
            String[] lines=StringUtility.splitString(list, '\n');
            int count=0;
            for(int c=0;c<lines.length;c++) {
                if(lines[c].trim().length()>0) count++;
            }
            return count;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public BackupLevel getBackupLevel() {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getBackupRetention() {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Integer.valueOf(pkey);
                case 1: return path;
                case COLUMN_LINUX_ACCOUNT: return Integer.valueOf(linux_account);
                case 3: return Integer.valueOf(linux_group);
                case 4: return Short.valueOf(backup_level);
                case 5: return Short.valueOf(backup_retention);
                case 6: return disable_log==-1?null:Integer.valueOf(disable_log);
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public DisableLog getDisableLog() {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getDisableLog()", null);
        try {
            if(disable_log==-1) return null;
            DisableLog obj=table.connector.disableLogs.get(disable_log);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find DisableLog: "+disable_log));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<EmailAddress> getEmailAddresses() {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getEmailAddresses()", null);
        try {
            return table.connector.emailListAddresses.getEmailAddresses(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<EmailListAddress> getEmailListAddresses() {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getEmailListAddresses()", null);
        try {
            return table.connector.emailListAddresses.getEmailListAddresses(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxServerAccount getLinuxServerAccount() {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getLinuxServerAccount()", null);
        try {
            LinuxServerAccount linuxAccountObject = table.connector.linuxServerAccounts.get(linux_account);
            if (linuxAccountObject == null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: " + linux_account));
            return linuxAccountObject;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxServerGroup getLinuxServerGroup() {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getLinuxServerGroup()", null);
        try {
            LinuxServerGroup linuxGroupObject = table.connector.linuxServerGroups.get(linux_group);
            if (linuxGroupObject == null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: " + linux_group));
            return linuxGroupObject;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the full path that should be used for normal email lists.
     */
    public static String getListPath(String name) {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "getListPath(String)", null);
        try {
            if(name.length()>1) {
                char ch=name.charAt(0);
                if(ch>='A' && ch<='Z') ch+=32;
                return LIST_DIRECTORY+'/'+ch+'/'+name;
            } else return LIST_DIRECTORY+"//";
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public MajordomoList getMajordomoList() {
        return table.connector.majordomoLists.get(pkey);
    }

    public String getPath() {
        return path;
    }

    protected int getTableIDImpl() {
        return SchemaTable.EMAIL_LISTS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, EmailList.class, "initImpl(ResultSet)", null);
        try {
            pkey = result.getInt(1);
            path = result.getString(2);
            linux_account = result.getInt(3);
            linux_group = result.getInt(4);
            backup_level = result.getShort(5);
            backup_retention = result.getShort(6);
            disable_log=result.getInt(7);
            if(result.wasNull()) disable_log=-1;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Checks the validity of a list name.
     */
    public static boolean isValidRegularPath(String path) {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "isValidRegularPath(String)", null);
        try {
            // Must start with LIST_DIRECTORY
            if(path==null) return false;
            if(!path.startsWith(LIST_DIRECTORY+'/')) return false;
            path=path.substring(LIST_DIRECTORY.length()+1);
            if(path.length()<2) return false;
            char firstChar=path.charAt(0);
            if(path.charAt(1)!='/') return false;
            path=path.substring(2);
            int len = path.length();
            if (len < 1 || len > MAX_NAME_LENGTH) return false;
            for (int c = 0; c < len; c++) {
                char ch = path.charAt(c);
                if (c == 0) {
                    if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z')) return false;
                    // First character must match with the name
                    if(ch>='A' && ch<='Z') ch+=32;
                    if(ch!=firstChar) return false;
                } else {
                    if ((ch < '0' || ch > '9') && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && ch != '.' && ch != '-' && ch != '_') return false;
                }
            }
            return true;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, EmailList.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            path=in.readUTF();
            linux_account=in.readCompressedInt();
            linux_group=in.readCompressedInt();
            backup_level=in.readShort();
            backup_retention=in.readShort();
            disable_log=in.readCompressedInt();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "remove()", null);
        try {
            table.connector.requestUpdateIL(
                AOServProtocol.REMOVE,
                SchemaTable.EMAIL_LISTS,
                pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setAddressList(String addresses) {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "setAddressList(String)", null);
        try {
            table.connector.requestUpdate(AOServProtocol.SET_EMAIL_LIST_ADDRESS_LIST, pkey, addresses);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, EmailList.class, "setBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.EMAIL_LISTS, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, EmailList.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeUTF(path);
            out.writeCompressedInt(linux_account);
            out.writeCompressedInt(linux_group);
            out.writeShort(backup_level);
            out.writeShort(backup_retention);
            out.writeCompressedInt(disable_log);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}