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
 * One user may have shell, FTP, and/or email access to any number
 * of servers.  However, some of the information is common across
 * all machines, and that set of information is contained in a
 * <code>LinuxAccount</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxAccount extends CachedObjectStringKey<LinuxAccount> implements PasswordProtected, Removable, Disablable {

    static final int COLUMN_USERNAME=0;

    /**
     * Some commonly used system and application account usernames.
     */
    public static final String
        APACHE="apache",
        AWSTATS="awstats",
        BIN="bin",
        EMAILMON="emailmon",
        FTP="ftp",
        FTPMON="ftpmon",
        HTTPD="httpd",
        INTERBASE="interbase",
        MAIL="mail",
        NOBODY="nobody",
        OPERATOR="operator",
        POSTGRES="postgres",
        ROOT="root"
    ;

    public static final String NO_PASSWORD_CONFIG_VALUE="!!";

    private String name;
    private String office_location;
    private String office_phone;
    private String home_phone;
    private String type;
    private String shell;
    private long created;
    int disable_log;

    public int addEmailAddress(EmailAddress address) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "addEmailAddress(EmailAddress)", null);
        try {
            return table.connector.linuxAccAddresses.addLinuxAccAddress(address, this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void addFTPGuestUser() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "addFTPGuestUser()", null);
        try {
            table.connector.ftpGuestUsers.addFTPGuestUser(pkey);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void addLinuxGroup(LinuxGroup group) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "addLinuxGroup(LinuxGroup)", null);
        try {
            table.connector.linuxGroupAccounts.addLinuxGroupAccount(group, this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public int addLinuxServerAccount(AOServer aoServer, String home) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "addLinuxServerAccount(AOServer,String)", null);
        try {
            return table.connector.linuxServerAccounts.addLinuxServerAccount(this, aoServer, home);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public int arePasswordsSet() {
        return Username.groupPasswordsSet(getLinuxServerAccounts());
    }

    public boolean canDisable() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "canDisable()", null);
        try {
            // Already disabled
            if(disable_log!=-1) return false;

            // linux_server_accounts
            for(LinuxServerAccount lsa : getLinuxServerAccounts()) if(lsa.disable_log==-1) return false;

            return true;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public boolean canEnable() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "canEnable()", null);
        try {
            DisableLog dl=getDisableLog();
            if(dl==null) return false;
            else return dl.canEnable() && getUsername().disable_log==-1;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public PasswordChecker.Result[] checkPassword(String password) {
        return checkPassword(pkey, type, password);
    }

    /**
     * Checks the strength of a password as required for this
     * <code>LinuxAccount</code>.  The strength requirement
     * depends on the <code>LinuxAccountType</code>.
     *
     * @see  LinuxAccountType#enforceStrongPassword(String)
     * @see  PasswordChecker#checkPassword(String,String,boolean,boolean)
     */
    public static PasswordChecker.Result[] checkPassword(String username, String type, String password) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "checkPassword(String,String,String)", null);
        try {
            boolean enforceStrong=LinuxAccountType.enforceStrongPassword(type);
            return PasswordChecker.checkPassword(username, password, enforceStrong, !enforceStrong);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void disable(DisableLog dl) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "disable(DisableLog)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.DISABLE, SchemaTable.LINUX_ACCOUNTS, dl.pkey, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }
    
    public void enable() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "enable()", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.ENABLE, SchemaTable.LINUX_ACCOUNTS, pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_USERNAME: return pkey;
                case 1: return name;
                case 2: return office_location;
                case 3: return office_phone;
                case 4: return home_phone;
                case 5: return type;
                case 6: return shell;
                case 7: return new java.sql.Date(created);
                case 8: return disable_log==-1?null:Integer.valueOf(disable_log);
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public long getCreated() {
        return created;
    }

    public DisableLog getDisableLog() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getDisableLog()", null);
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
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getEmailAddresses()", null);
        try {
            return table.connector.linuxAccAddresses.getEmailAddresses(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public FTPGuestUser getFTPGuestUser() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getFTPGuestUser()", null);
        try {
            return table.connector.ftpGuestUsers.get(pkey);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getHomePhone() {
        return home_phone;
    }

    public List<LinuxGroup> getLinuxGroups() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getLinuxGroups()", null);
        try {
            return table.connector.linuxGroupAccounts.getLinuxGroups(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxServerAccount getLinuxServerAccount(AOServer aoServer) {
        return table.connector.linuxServerAccounts.getLinuxServerAccount(aoServer, pkey);
    }

    public List<LinuxServerAccount> getLinuxServerAccounts() {
        return table.connector.linuxServerAccounts.getLinuxServerAccounts(this);
    }

    public String getName() {
        return name;
    }

    public String getOfficeLocation() {
        return office_location;
    }

    public String getOfficePhone() {
        return office_phone;
    }

    public LinuxGroup getPrimaryGroup() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getPrimaryGroup()", null);
        try {
            return table.connector.linuxGroupAccounts.getPrimaryGroup(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Shell getShell() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getShell()", null);
        try {
            Shell shellObject = table.connector.shells.get(shell);
            if (shellObject == null) throw new WrappedException(new SQLException("Unable to find Shell: " + shell));
            return shellObject;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected int getTableIDImpl() {
        return SchemaTable.LINUX_ACCOUNTS;
    }

    public LinuxAccountType getType() {
        LinuxAccountType typeObject = table.connector.linuxAccountTypes.get(type);
        if (typeObject == null) throw new IllegalArgumentException(new SQLException("Unable to find LinuxAccountType: " + type));
        return typeObject;
    }

    public Username getUsername() {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "getUsername()", null);
        try {
            Username usernameObject = table.connector.usernames.get(pkey);
            if (usernameObject == null) throw new WrappedException(new SQLException("Unable to find Username: " + pkey));
            return usernameObject;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<String> getValidHomeDirectories(AOServer ao) {
        return getValidHomeDirectories(pkey, ao);
    }

    /**
     * @deprecated  Please provide the locale for locale-specific errors.
     */
    public static List<String> getValidHomeDirectories(String username, AOServer ao) {
        return getValidHomeDirectories(username, ao, Locale.getDefault());
    }

    public static List<String> getValidHomeDirectories(String username, AOServer ao, Locale locale) {
        List<String> dirs=new ArrayList<String>();
        if(username!=null) dirs.add(LinuxServerAccount.getDefaultHomeDirectory(username, locale));

        List<HttpdSite> hss=ao.getHttpdSites();
        int hsslen=hss.size();
        for(int c=0;c<hsslen;c++) {
            HttpdSite hs=hss.get(c);
            String siteDir=hs.getInstallDirectory();
            dirs.add(siteDir);
            if(hs.getHttpdTomcatSite()!=null) dirs.add(siteDir+"/webapps");
        }

        List<HttpdSharedTomcat> hsts=ao.getHttpdSharedTomcats();
        int hstslen=hsts.size();
        for(int c=0;c<hstslen;c++) {
            HttpdSharedTomcat hst=hsts.get(c);
            dirs.add(HttpdSharedTomcat.WWW_GROUP_DIR+'/'+hst.getName());
        }
        return dirs;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "initImpl(ResultSet)", null);
        try {
            pkey = result.getString(1);
            name = result.getString(2);
            office_location = result.getString(3);
            office_phone = result.getString(4);
            home_phone = result.getString(5);
            type = result.getString(6);
            shell = result.getString(7);
            created = result.getTimestamp(8).getTime();
            disable_log=result.getInt(9);
            if(result.wasNull()) disable_log=-1;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Determines if a name can be used as a GECOS field.  A GECOS field
     * is valid if it is between 1 and 100 characters in length and uses only
     * <code>[a-z,A-Z,0-9,-,_,@, ,.,#,=,/,$,%,^,&,*,(,),?,']</code> for each
     * character.<br>
     * <br>
     * Refer to <code>man 5 passwd</code>
     * @see  #setName
     * @see  #setOfficeLocation
     * @see  #setOfficePhone
     * @see  #setHomePhone
     */
    public static String checkGECOS(String name, String display) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "checkGECOS(String,String)", null);
        try {
            if(name!=null) {
                int len = name.length();
                if (len == 0 || len > 100) return "The "+display+" must be between 1 and 100 characters long.";

                for (int c = 0; c < len; c++) {
                    char ch = name.charAt(c);
                    if (
                        (ch < 'a' || ch > 'z')
                        && (ch<'A' || ch>'Z')
                        && (ch < '0' || ch > '9')
                        && ch != '-'
                        && ch != '_'
                        && ch != '@'
                        && ch != ' '
                        && ch != '.'
                        && ch != '#'
                        && ch != '='
                        && ch != '/'
                        && ch != '$'
                        && ch != '%'
                        && ch != '^'
                        && ch != '&'
                        && ch != '*'
                        && ch != '('
                        && ch != ')'
                        && ch != '?'
                        && ch != '\''
                        && ch != '+'
                    ) return "Invalid character found in "+display+": "+ch;
                }
            }
            return null;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxAccount.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF();
            name=in.readUTF();
            office_location=in.readBoolean()?in.readUTF():null;
            office_phone=in.readBoolean()?in.readUTF():null;
            home_phone=in.readBoolean()?in.readUTF():null;
            type=in.readUTF();
            shell=in.readUTF();
            created=in.readLong();
            disable_log=in.readCompressedInt();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "getCannotRemoveReasons()", null);
        try {
            List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

            // All LinuxServerAccounts must be removable
            for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
                reasons.addAll(lsa.getCannotRemoveReasons());
            }

            return reasons;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void remove() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "remove()", null);
        try {
            table.connector.requestUpdateIL(
                AOServProtocol.REMOVE,
                SchemaTable.LINUX_ACCOUNTS,
                pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void removeLinuxGroup(LinuxGroup group) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "removeLinuxGroup(LinuxGroup)", null);
        try {
            table.connector.linuxGroupAccounts.getLinuxGroupAccount(group.pkey, pkey).remove();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void setHomePhone(String phone) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "setHomePhone(String)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_ACCOUNT_HOME_PHONE, pkey, phone==null?"":phone);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setName(String name) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "setName(String)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_ACCOUNT_NAME, pkey, name);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setOfficeLocation(String location) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "setOfficeLocation(String)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_ACCOUNT_OFFICE_LOCATION, pkey, location==null?"":location);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setOfficePhone(String phone) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "setOfficePhone(String)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_ACCOUNT_OFFICE_PHONE, pkey, phone==null?"":phone);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setPassword(String password) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "setPassword(String)", null);
        try {
            for(LinuxServerAccount lsa : getLinuxServerAccounts()) {
                if(lsa.canSetPassword()) lsa.setPassword(password);
            }
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void setShell(Shell shell) {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxAccount.class, "setShell(Shell)", null);
        try {
            table.connector.requestUpdateIL(AOServProtocol.SET_LINUX_ACCOUNT_SHELL, pkey, shell.pkey);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxAccount.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            out.writeUTF(name);
            out.writeNullUTF(office_location);
            out.writeNullUTF(office_phone);
            out.writeNullUTF(home_phone);
            out.writeUTF(type);
            out.writeUTF(shell);
            out.writeLong(created);
            out.writeCompressedInt(disable_log);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Determines if a name can be used as a username.  The username restrictions are
     * inherited from <code>Username</code>, with the addition of not allowing
     * <code>postmaster</code> and <code>mailer-daemon</code>.  This is to prevent a
     * user from interfering with the delivery of system messages in qmail.
     *
     * @see  Username#isValidUsername
     */
    public static boolean isValidUsername(String username) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "isValidUsername(String)", null);
        try {
            return
                Username.checkUsername(username, Locale.getDefault())==null
                && !"bin".equals(username)
                && !"etc".equals(username)
                && !"lib".equals(username)
                && !"postmaster".equals(username)
                && !"mailer-daemon".equals(username)
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public boolean canSetPassword() {
        return disable_log==-1 && getType().canSetPassword();
    }

    public void setPrimaryLinuxGroup(LinuxGroup group) {
        Profiler.startProfile(Profiler.FAST, LinuxAccount.class, "setPrimaryLinuxGroup(LinuxGroup)", null);
        try {
            LinuxGroupAccount lga=table.connector.linuxGroupAccounts.getLinuxGroupAccount(group.getName(), pkey);
            if(lga==null) throw new WrappedException(new SQLException("Unable to find LinuxGroupAccount for username="+pkey+" and group="+group.getName()));
            lga.setAsPrimary();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}
