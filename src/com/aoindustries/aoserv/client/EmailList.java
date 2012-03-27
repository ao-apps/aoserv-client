package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
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
        COLUMN_LINUX_SERVER_ACCOUNT=2
    ;
    static final String COLUMN_LINUX_SERVER_ACCOUNT_name = "linux_server_account";
    static final String COLUMN_PATH_name = "path";

    /**
     * The directory that email lists are normally contained in.
     */
    public static final String LIST_DIRECTORY="/etc/mail/lists";

    /**
     * The maximum length of an email list name.
     */
    public static final int MAX_NAME_LENGTH=64;

    String path;
    int linux_server_account;
    int linux_server_group;
    int disable_log;

    public int addEmailAddress(EmailAddress address) throws IOException, SQLException {
        return table.connector.getEmailListAddresses().addEmailListAddress(address, this);
    }

    public boolean canDisable() {
        return disable_log==-1;
    }
    
    public boolean canEnable() throws SQLException, IOException {
        DisableLog dl=getDisableLog();
        if(dl==null) return false;
        else return
            dl.canEnable()
            && getLinuxServerGroup().getLinuxGroup().getPackage().disable_log==-1
            && getLinuxServerAccount().disable_log==-1
        ;
    }

    public void disable(DisableLog dl) throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.DISABLE, SchemaTable.TableID.EMAIL_LISTS, dl.pkey, pkey);
    }
    
    public void enable() throws IOException, SQLException {
        table.connector.requestUpdateIL(true, AOServProtocol.CommandID.ENABLE, SchemaTable.TableID.EMAIL_LISTS, pkey);
    }

    /**
     * Gets the list of addresses that email will be sent to, one address per line.
     * The list is obtained from a file on the server that hosts the list.
     */
    public String getAddressList() throws IOException, SQLException {
        return table.connector.requestStringQuery(true, AOServProtocol.CommandID.GET_EMAIL_LIST_ADDRESS_LIST, pkey);
    }

    /**
     * Gets the number of addresses in an address list.  The number of addresses is equal to the number
     * of non-blank lines.
     */
    public int getAddressListCount() throws IOException, SQLException {
        String list=getAddressList();
        String[] lines=StringUtility.splitString(list, '\n');
        int count=0;
        for(int c=0;c<lines.length;c++) {
            if(lines[c].trim().length()>0) count++;
        }
        return count;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return path;
            case COLUMN_LINUX_SERVER_ACCOUNT: return Integer.valueOf(linux_server_account);
            case 3: return Integer.valueOf(linux_server_group);
            case 4: return disable_log==-1?null:Integer.valueOf(disable_log);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public boolean isDisabled() {
        return disable_log!=-1;
    }

    public DisableLog getDisableLog() throws SQLException, IOException {
        if(disable_log==-1) return null;
        DisableLog obj=table.connector.getDisableLogs().get(disable_log);
        if(obj==null) throw new SQLException("Unable to find DisableLog: "+disable_log);
        return obj;
    }

    public List<EmailAddress> getEmailAddresses() throws IOException, SQLException {
        return table.connector.getEmailListAddresses().getEmailAddresses(this);
    }

    public List<EmailListAddress> getEmailListAddresses() throws IOException, SQLException {
        return table.connector.getEmailListAddresses().getEmailListAddresses(this);
    }

    public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
        LinuxServerAccount linuxServerAccountObject = table.connector.getLinuxServerAccounts().get(linux_server_account);
        if (linuxServerAccountObject == null) throw new SQLException("Unable to find LinuxServerAccount: " + linux_server_account);
        return linuxServerAccountObject;
    }

    public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
        LinuxServerGroup linuxServerGroupObject = table.connector.getLinuxServerGroups().get(linux_server_group);
        if (linuxServerGroupObject == null) throw new SQLException("Unable to find LinuxServerGroup: " + linux_server_group);
        return linuxServerGroupObject;
    }

    /**
     * Gets the full path that should be used for normal email lists.
     */
    public static String getListPath(String name) {
        if(name.length()>1) {
            char ch=name.charAt(0);
            if(ch>='A' && ch<='Z') ch+=32;
            return LIST_DIRECTORY+'/'+ch+'/'+name;
        } else return LIST_DIRECTORY+"//";
    }

    public MajordomoList getMajordomoList() throws IOException, SQLException {
        return table.connector.getMajordomoLists().get(pkey);
    }

    public String getPath() {
        return path;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_LISTS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        path = result.getString(2);
        linux_server_account = result.getInt(3);
        linux_server_group = result.getInt(4);
        disable_log=result.getInt(5);
        if(result.wasNull()) disable_log=-1;
    }

    /**
     * Checks the validity of a list name.
     */
    public static boolean isValidRegularPath(String path) {
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
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        path=in.readUTF();
        linux_server_account=in.readCompressedInt();
        linux_server_group=in.readCompressedInt();
        disable_log=in.readCompressedInt();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        return Collections.emptyList();
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            true,
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.EMAIL_LISTS,
            pkey
        );
    }

    public void setAddressList(String addresses) throws IOException, SQLException {
        table.connector.requestUpdate(true, AOServProtocol.CommandID.SET_EMAIL_LIST_ADDRESS_LIST, pkey, addresses);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeUTF(path);
        out.writeCompressedInt(linux_server_account);
        out.writeCompressedInt(linux_server_group);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_30)<=0) {
            out.writeShort(0);
            out.writeShort(7);
        }
        out.writeCompressedInt(disable_log);
    }
}