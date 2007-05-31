package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.io.unix.*;
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>LinuxServerAccount</code> grants a <code>LinuxAccount</code>
 * access to an <code>AOServer</code>.
 *
 * @see  LinuxAccount
 * @see  AOServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxServerAccount extends CachedObjectIntegerKey<LinuxServerAccount> implements Removable, PasswordProtected, Disablable {

    /**
     * The default number of days email messages will be kept in the "Trash" folder.
     */
    public static final int DEFAULT_TRASH_EMAIL_RETENTION=31;

    /**
     * The default number of days email messages will be kept in the "Junk" folder.
     */
    public static final int DEFAULT_JUNK_EMAIL_RETENTION=31;
    
    /**
     * The default SpamAssassin required score.
     */
    public static final float DEFAULT_SPAM_ASSASSIN_REQUIRED_SCORE=3.0F;

    static final int
        COLUMN_PKEY=0,
        COLUMN_USERNAME=1,
        COLUMN_AO_SERVER=2,
        COLUMN_CRON_BACKUP_LEVEL=5,
        COLUMN_CRON_BACKUP_RETENTION=6,
        COLUMN_HOME_BACKUP_LEVEL=7,
        COLUMN_HOME_BACKUP_RETENTION=8,
        COLUMN_INBOX_BACKUP_LEVEL=9,
        COLUMN_INBOX_BACKUP_RETENTION=10
    ;

    /**
     * The default number of days to keep backups.
     */
    public static final short
        DEFAULT_CRON_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL,
        DEFAULT_CRON_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION,
        DEFAULT_HOME_BACKUP_LEVEL=BackupLevel.DEFAULT_BACKUP_LEVEL,
        DEFAULT_HOME_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION,
        DEFAULT_INBOX_BACKUP_LEVEL=BackupLevel.BACKUP_PRIMARY,
        DEFAULT_INBOX_BACKUP_RETENTION=BackupRetention.DEFAULT_BACKUP_RETENTION
    ;

    String username;
    int ao_server;
    int uid;
    private String home;
    private short cron_backup_level;
    private short cron_backup_retention;
    private short home_backup_level;
    private short home_backup_retention;
    private short inbox_backup_level;
    private short inbox_backup_retention;
    int autoresponder_from;
    private String autoresponder_subject;
    private String autoresponder_path;
    private boolean is_autoresponder_enabled;
    int disable_log;
    private String predisable_password;
    private long created;
    private boolean use_inbox;
    private int trash_email_retention;
    private int junk_email_retention;
    private String sa_integration_mode;
    private float sa_required_score;

    public boolean canDisable() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "canDisable()", null);
        try {
            // already disabled
            if(disable_log!=-1) return false;

            // is a system user
            if(uid<UnixFile.MINIMUM_USER_UID) return false;

            // cvs_repositories
            for(CvsRepository cr : getCvsRepositories()) if(cr.disable_log==-1) return false;

            // httpd_shared_tomcats
            for(HttpdSharedTomcat hst : getHttpdSharedTomcats()) if(hst.disable_log==-1) return false;

            // email_lists
            for(EmailList el : getEmailLists()) if(el.disable_log==-1) return false;

            // httpd_sites
            for(HttpdSite hs : getHttpdSites()) if(hs.disable_log==-1) return false;

            return true;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public boolean canEnable() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "canEnable()", null);
        try {
            DisableLog dl=getDisableLog();
            if(dl==null) return false;
            else return dl.canEnable() && getLinuxAccount().disable_log==-1;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public PasswordChecker.Result[] checkPassword(String password) {
        return getLinuxAccount().checkPassword(password);
    }
/* String checkPasswordDescribe(String password) {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "checkPasswordDescribe(String)", null);
        try {
            return getLinuxAccount().checkPasswordDescribe(password);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
*/
    public long copyHomeDirectory(AOServer toServer) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "copyHomeDirectory(AOServer)", null);
        try {
            try {
                AOServConnection connection=table.connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.COPY_HOME_DIRECTORY);
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(toServer.pkey);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code!=AOServProtocol.DONE) AOServProtocol.checkResult(code, in);
                    return in.readLong();
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
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void copyPassword(LinuxServerAccount other) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "copyPassword(LinuxServerAccount)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.COPY_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, other.pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void disable(DisableLog dl) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "disable(DisableLog)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.LINUX_SERVER_ACCOUNTS, dl.pkey, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
    
    public void enable() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "enable()", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.LINUX_SERVER_ACCOUNTS, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public BackupLevel getCronBackupLevel() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getCronBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(cron_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+cron_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getCronBackupRetention() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getCronBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(cron_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+cron_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupLevel getHomeBackupLevel() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getHomeBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(home_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+home_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getHomeBackupRetention() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getHomeBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(home_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+home_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupLevel getInboxBackupLevel() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getInboxBackupLevel()", null);
        try {
            BackupLevel bl=table.connector.backupLevels.get(inbox_backup_level);
            if(bl==null) throw new WrappedException(new SQLException("Unable to find BackupLevel: "+inbox_backup_level));
            return bl;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BackupRetention getInboxBackupRetention() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getInboxBackupRetention()", null);
        try {
            BackupRetention br=table.connector.backupRetentions.get(inbox_backup_retention);
            if(br==null) throw new WrappedException(new SQLException("Unable to find BackupRetention: "+inbox_backup_retention));
            return br;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Integer.valueOf(pkey);
                case COLUMN_USERNAME: return username;
                case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
                case 3: return Integer.valueOf(uid);
                case 4: return home;
                case COLUMN_CRON_BACKUP_LEVEL: return Short.valueOf(cron_backup_level);
                case COLUMN_CRON_BACKUP_RETENTION: return Short.valueOf(cron_backup_retention);
                case COLUMN_HOME_BACKUP_LEVEL: return Short.valueOf(home_backup_level);
                case COLUMN_HOME_BACKUP_RETENTION: return Short.valueOf(home_backup_retention);
                case COLUMN_INBOX_BACKUP_LEVEL: return Short.valueOf(inbox_backup_level);
                case COLUMN_INBOX_BACKUP_RETENTION: return Short.valueOf(inbox_backup_retention);
                case 11: return autoresponder_from==-1?null:Integer.valueOf(autoresponder_from);
                case 12: return autoresponder_subject;
                case 13: return autoresponder_path;
                case 14: return is_autoresponder_enabled?Boolean.TRUE:Boolean.FALSE;
                case 15: return disable_log==-1?null:Integer.valueOf(disable_log);
                case 16: return predisable_password;
                case 17: return new java.sql.Date(created);
                case 18: return use_inbox?Boolean.TRUE:Boolean.FALSE;
                case 19: return trash_email_retention==-1?null:Integer.valueOf(trash_email_retention);
                case 20: return junk_email_retention==-1?null:Integer.valueOf(junk_email_retention);
                case 21: return sa_integration_mode;
                case 22: return new Float(sa_required_score);
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<CvsRepository> getCvsRepositories() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getCvsRepositories()", null);
        try {
            return table.connector.cvsRepositories.getCvsRepositories(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<EmailAttachmentBlock> getEmailAttachmentBlocks() {
        return table.connector.emailAttachmentBlocks.getEmailAttachmentBlocks(this);
    }

    public String getAutoresponderContent() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getAutoresponderContent()", null);
        try {
            String content=table.connector.requestStringQuery(AOServProtocol.GET_AUTORESPONDER_CONTENT, pkey);
            if(content.length()==0) return null;
            return content;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxAccAddress getAutoresponderFrom() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getAutoresponderFrom()", null);
        try {
            if(autoresponder_from==-1) return null;
            // Might be filtered
            return table.connector.linuxAccAddresses.get(autoresponder_from);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getAutoresponderSubject() {
        return autoresponder_subject;
    }
    
    public String getAutoresponderPath() {
        return autoresponder_path;
    }

    public boolean isAutoresponderEnabled() {
        return is_autoresponder_enabled;
    }
    
    public String getCronTable() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "getCronTable()", null);
        try {
            return table.connector.requestStringQuery(AOServProtocol.GET_CRON_TABLE, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    /**
     * @deprecated  Please provide the locale for generated errors.
     */
    public static String getDefaultHomeDirectory(String username) {
        return getDefaultHomeDirectory(username, Locale.getDefault());
    }

    public static String getDefaultHomeDirectory(String username, Locale locale) {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getDefaultHomeDirectory(String,Locale)", null);
        try {
            String check = Username.checkUsername(username, locale);
            if(check!=null) throw new IllegalArgumentException(check);
            return "/home/"+username.charAt(0)+'/'+username;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public DisableLog getDisableLog() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getDisableLog()", null);
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
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getEmailAddresses()", null);
        try {
            return table.connector.linuxAccAddresses.getEmailAddresses(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<HttpdSharedTomcat> getHttpdSharedTomcats() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getHttpdSharedTomcats()", null);
        try {
            return table.connector.httpdSharedTomcats.getHttpdSharedTomcats(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<HttpdSite> getHttpdSites() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getHttpdSites()", null);
        try {
            return table.connector.httpdSites.getHttpdSites(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public InboxAttributes getInboxAttributes() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "getInboxAttributes()", null);
        try {
            try {
                AOServConnection connection=table.connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.GET_INBOX_ATTRIBUTES);
                    out.writeCompressedInt(pkey);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        InboxAttributes attr;
                        if(in.readBoolean()) {
                            attr=new InboxAttributes(table.connector, this);
                            attr.read(in);
                        } else attr=null;
                        return attr;
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
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
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public long[] getImapFolderSizes(String[] folderNames) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "getImapFolderSizes(String[])", null);
        try {
            try {
                long[] sizes=new long[folderNames.length];
                if(sizes.length>0) {
                    AOServConnection connection=table.connector.getConnection();
                    try {
                        CompressedDataOutputStream out=connection.getOutputStream();
                        out.writeCompressedInt(AOServProtocol.GET_IMAP_FOLDER_SIZES);
                        out.writeCompressedInt(pkey);
                        out.writeCompressedInt(folderNames.length);
                        for(int c=0;c<folderNames.length;c++) out.writeUTF(folderNames[c]);
                        out.flush();

                        CompressedDataInputStream in=connection.getInputStream();
                        int code=in.readByte();
                        if(code==AOServProtocol.DONE) {
                            for(int c=0;c<folderNames.length;c++) {
                                sizes[c]=in.readLong();
                            }
                        } else {
                            AOServProtocol.checkResult(code, in);
                            throw new IOException("Unexpected response code: "+code);
                        }
                    } catch(IOException err) {
                        connection.close();
                        throw err;
                    } finally {
                        table.connector.releaseConnection(connection);
                    }
                }
                return sizes;
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public List<LinuxAccAddress> getLinuxAccAddresses() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getLinuxAccAddresses()", null);
        try {
            return table.connector.linuxAccAddresses.getLinuxAccAddresses(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getHome() {
        return home;
    }

    public LinuxAccount getLinuxAccount() {
        Username usernameObj=table.connector.usernames.get(username);
        if(usernameObj==null) throw new WrappedException(new SQLException("Unable to find Username: "+username));
        LinuxAccount linuxAccountObject = usernameObj.getLinuxAccount();
        if (linuxAccountObject == null) throw new WrappedException(new SQLException("Unable to find LinuxAccount: " + username));
        return linuxAccountObject;
    }

    public String getPredisablePassword() {
        return predisable_password;
    }
    
    public long getCreated() {
        return created;
    }
    
    public boolean useInbox() {
        return use_inbox;
    }
    
    public int getTrashEmailRetention() {
        return trash_email_retention;
    }
    
    public int getJunkEmailRetention() {
        return junk_email_retention;
    }
    
    public EmailSpamAssassinIntegrationMode getEmailSpamAssassinIntegrationMode() {
        EmailSpamAssassinIntegrationMode esaim=table.connector.emailSpamAssassinIntegrationModes.get(sa_integration_mode);
        if(esaim==null) throw new WrappedException(new SQLException("Unable to find EmailSpamAssassinIntegrationMode: "+sa_integration_mode));
        return esaim;
    }
    
    public float getSpamAssassinRequiredScore() {
        return sa_required_score;
    }

    /**
     * Gets the primary <code>LinuxServerGroup</code> for this <code>LinuxServerAccount</code>
     *
     * @exception  SQLException  if the primary group is not found
     *                           or two or more groups are marked as primary
     *                           or the primary group does not exist on the same server
     */
    public LinuxServerGroup getPrimaryLinuxServerGroup() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getPrimaryLinuxServerGroup()", null);
        try {
            return table.connector.linuxServerGroups.getPrimaryLinuxServerGroup(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public AOServer getAOServer() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getAOServer()", null);
        try {
            AOServer ao=table.connector.aoServers.get(ao_server);
            if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: " + ao_server));
            return ao;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected int getTableIDImpl() {
        return SchemaTable.LINUX_SERVER_ACCOUNTS;
    }

    public LinuxID getUID() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getUID()", null);
        try {
            LinuxID obj=table.connector.linuxIDs.get(uid);
            if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxID: "+uid));
            return obj;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "initImpl(ResultSet)", null);
        try {
            pkey=result.getInt(1);
            username=result.getString(2);
            ao_server=result.getInt(3);
            uid=result.getInt(4);
            home=result.getString(5);
            cron_backup_level = result.getShort(6);
            cron_backup_retention = result.getShort(7);
            home_backup_level = result.getShort(8);
            home_backup_retention = result.getShort(9);
            inbox_backup_level = result.getShort(10);
            inbox_backup_retention = result.getShort(11);
            autoresponder_from=result.getInt(12);
            if(result.wasNull()) autoresponder_from=-1;
            autoresponder_subject = result.getString(13);
            autoresponder_path = result.getString(14);
            is_autoresponder_enabled=result.getBoolean(15);
            disable_log=result.getInt(16);
            if(result.wasNull()) disable_log=-1;
            predisable_password=result.getString(17);
            created=result.getTimestamp(18).getTime();
            use_inbox=result.getBoolean(19);
            trash_email_retention=result.getInt(20);
            if(result.wasNull()) trash_email_retention=-1;
            junk_email_retention=result.getInt(21);
            if(result.wasNull()) junk_email_retention=-1;
            sa_integration_mode=result.getString(22);
            sa_required_score=result.getFloat(23);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public int isProcmailManual() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "isProcmailManual()", null);
        try {
            return table.connector.requestIntQuery(AOServProtocol.IS_LINUX_SERVER_ACCOUNT_PROCMAIL_MANUAL, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public int arePasswordsSet() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "arePasswordsSet()", null);
        try {
            return table.connector.requestBooleanQuery(AOServProtocol.IS_LINUX_SERVER_ACCOUNT_PASSWORD_SET, pkey)?PasswordProtected.ALL:PasswordProtected.NONE;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxServerAccount.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            username=in.readUTF();
            ao_server=in.readCompressedInt();
            uid=in.readCompressedInt();
            home=in.readUTF();
            cron_backup_level=in.readShort();
            cron_backup_retention=in.readShort();
            home_backup_level=in.readShort();
            home_backup_retention=in.readShort();
            inbox_backup_level=in.readShort();
            inbox_backup_retention=in.readShort();
            autoresponder_from=in.readCompressedInt();
            autoresponder_subject=readNullUTF(in);
            autoresponder_path=readNullUTF(in);
            is_autoresponder_enabled=in.readBoolean();
            disable_log=in.readCompressedInt();
            predisable_password=readNullUTF(in);
            created=in.readLong();
            use_inbox=in.readBoolean();
            trash_email_retention=in.readCompressedInt();
            junk_email_retention=in.readCompressedInt();
            sa_integration_mode=in.readUTF();
            sa_required_score=in.readFloat();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<EmailList> getEmailLists() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "getEmailLists()", null);
        try {
            return table.connector.emailLists.getEmailLists(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "getCannotRemoveReasons()", null);
        try {
            List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

            if(uid<UnixFile.MINIMUM_USER_UID) reasons.add(new CannotRemoveReason<LinuxServerAccount>("Not allowed to remove accounts with UID less than "+UnixFile.MINIMUM_USER_UID));

            AOServer ao=getAOServer();

            // No CVS repositories
            for(CvsRepository cr : ao.getCvsRepositories()) {
                if(cr.linux_server_account==pkey) reasons.add(new CannotRemoveReason<CvsRepository>("Used by CVS repository "+cr.getPath()+" on "+cr.getLinuxServerAccount().getAOServer().getServer().getHostname(), cr));
            }

            // No email lists
            for(EmailList el : getEmailLists()) {
                reasons.add(new CannotRemoveReason<EmailList>("Used by email list "+el.getPath()+" on "+el.getLinuxServerAccount().getAOServer().getServer().getHostname(), el));
            }

            // No httpd_servers
            for(HttpdServer hs : ao.getHttpdServers()) {
                if(hs.linux_server_account==pkey) reasons.add(new CannotRemoveReason<HttpdServer>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getServer().getHostname(), hs));
            }

            // No httpd shared tomcats
            for(HttpdSharedTomcat hst : ao.getHttpdSharedTomcats()) {
                if(hst.linux_server_account==pkey) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used by Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getServer().getHostname(), hst));
            }

            // No majordomo_servers
            for(MajordomoServer ms : ao.getMajordomoServers()) {
                if(ms.linux_server_account==pkey) {
                    EmailDomain ed=ms.getDomain();
                    reasons.add(new CannotRemoveReason<MajordomoServer>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getServer().getHostname(), ms));
                }
            }

            // No private FTP servers
            for(PrivateFTPServer pfs : ao.getPrivateFTPServers()) {
                if(pfs.pub_linux_server_account==pkey) reasons.add(new CannotRemoveReason<PrivateFTPServer>("Used by private FTP server "+pfs.getRoot()+" on "+pfs.getLinuxServerAccount().getAOServer().getServer().getHostname(), pfs));
            }

            // No httpd_sites
            for(HttpdSite site : ao.getHttpdSites()) {
                if(site.linuxAccount.equals(username)) reasons.add(new CannotRemoveReason<HttpdSite>("Used by website "+site.getInstallDirectory()+" on "+site.getAOServer().getServer().getHostname(), site));
            }

            return reasons;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void remove() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "remove()", null);
        try {
            table.connector.requestUpdateIL(
                AOServProtocol.REMOVE,
                SchemaTable.LINUX_SERVER_ACCOUNTS,
                pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setCronBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setCronBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.LINUX_SERVER_ACCOUNTS, pkey, COLUMN_CRON_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setHomeBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setHomeBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.LINUX_SERVER_ACCOUNTS, pkey, COLUMN_HOME_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setImapFolderSubscribed(String folder, boolean subscribed) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setImapFolderSubscribed(String,boolean)", null);
        try {
            table.connector.requestUpdate(AOServProtocol.SET_IMAP_FOLDER_SUBSCRIBED, pkey, folder, subscribed);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setInboxBackupRetention(short days) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setInboxBackupRetention(short)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_BACKUP_RETENTION, days, SchemaTable.LINUX_SERVER_ACCOUNTS, pkey, COLUMN_INBOX_BACKUP_RETENTION);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setCronTable(String cronTable) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setCronTable(String)", null);
        try {
            table.connector.requestUpdate(AOServProtocol.SET_CRON_TABLE, pkey, cronTable);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setPassword(String password) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setPassword(String)", null);
        try {
            AOServConnector connector=table.connector;
            if(!connector.isSecure()) throw new WrappedException(new IOException("Passwords for linux accounts may only be set when using secure protocols.  Currently using the "+connector.getProtocol()+" protocol, which is not secure."));
            connector.requestUpdateIL(AOServProtocol.SET_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setAutoresponder(
        LinuxAccAddress from,
        String subject,
        String content,
        boolean enabled
    ) {
        Profiler.startProfile(Profiler.IO, LinuxServerAccount.class, "setAutoresponder(LinuxAccAddress,String,String,boolean)", null);
        try {
            try {
                IntList invalidateList;
                AOServConnection connection=table.connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.SET_AUTORESPONDER);
                    out.writeCompressedInt(pkey);
                    out.writeCompressedInt(from==null?-1:from.getPKey());
                    out.writeBoolean(subject!=null);
                    if(subject!=null) out.writeUTF(subject);
                    out.writeBoolean(content!=null);
                    if(content!=null) out.writeUTF(content);
                    out.writeBoolean(enabled);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    table.connector.releaseConnection(connection);
                }
                table.connector.tablesUpdated(invalidateList);
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void setTrashEmailRetention(int days) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setTrashEmailRetention(int)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_SERVER_ACCOUNT_TRASH_EMAIL_RETENTION, pkey, days);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setJunkEmailRetention(int days) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setJunkEmailRetention(int)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_SERVER_ACCOUNT_JUNK_EMAIL_RETENTION, pkey, days);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setEmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationMode mode) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setEmailSpamAssassinIntegrationMode(EmailSpamAssassinIntegrationMode)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_SERVER_ACCOUNT_EMAIL_SPAMASSASSIN_INTEGRATION_MODE, pkey, mode.pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setSpamAssassinRequiredScore(float required_score) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setSpamAssassinRequiredScore(float)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_SERVER_ACCOUNT_SPAMASSASSIN_REQUIRED_SCORE, pkey, required_score);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setUseInbox(boolean useInbox) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "setUseInbox(boolean)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_SERVER_ACCOUNT_USE_INBOX, pkey, useInbox);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setPredisablePassword(String password) {
        Profiler.startProfile(Profiler.IO, LinuxServerAccount.class, "setPredisablePassword(String)", null);
        try {
            try {
                IntList invalidateList;
                AOServConnector connector=table.connector;
                AOServConnection connection=connector.getConnection();
                try {
                    CompressedDataOutputStream out=connection.getOutputStream();
                    out.writeCompressedInt(AOServProtocol.SET_LINUX_SERVER_ACCOUNT_PREDISABLE_PASSWORD);
                    out.writeCompressedInt(pkey);
                    writeNullUTF(out, password);
                    out.flush();

                    CompressedDataInputStream in=connection.getInputStream();
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) invalidateList=AOServConnector.readInvalidateList(in);
                    else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                } catch(IOException err) {
                    connection.close();
                    throw err;
                } finally {
                    connector.releaseConnection(connection);
                }
                connector.tablesUpdated(invalidateList);
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    String toStringImpl() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "toStringImpl()", null);
        try {
            return username+" on "+getAOServer().getServer().hostname;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxServerAccount.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeUTF(username);
            out.writeCompressedInt(ao_server);
            out.writeCompressedInt(uid);
            out.writeUTF(home);
            out.writeShort(cron_backup_level);
            out.writeShort(cron_backup_retention);
            out.writeShort(home_backup_level);
            out.writeShort(home_backup_retention);
            out.writeShort(inbox_backup_level);
            out.writeShort(inbox_backup_retention);
            out.writeCompressedInt(autoresponder_from);
            writeNullUTF(out, autoresponder_subject);
            writeNullUTF(out, autoresponder_path);
            out.writeBoolean(is_autoresponder_enabled);
            out.writeCompressedInt(disable_log);
            writeNullUTF(out, predisable_password);
            out.writeLong(created);
            out.writeBoolean(use_inbox);
            out.writeCompressedInt(trash_email_retention);
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_120)>=0) {
                out.writeCompressedInt(junk_email_retention);
                out.writeUTF(sa_integration_mode);
            }
            if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_124)>=0) {
                out.writeFloat(sa_required_score);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public boolean canSetPassword() {
        Profiler.startProfile(Profiler.FAST, LinuxServerAccount.class, "canSetPassword()", null);
        try {
            return disable_log==-1 && getLinuxAccount().canSetPassword();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public boolean passwordMatches(String password) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerAccount.class, "passwordMatches(String)", null);
        try {
            return table.connector.requestBooleanQuery(AOServProtocol.COMPARE_LINUX_SERVER_ACCOUNT_PASSWORD, pkey, password);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
}