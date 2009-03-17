package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * A <code>LinuxGroup</code> may exist on multiple <code>Server</code>s.
 * The information common across all servers is stored is a <code>LinuxGroup</code>.
 *
 * @see  LinuxServerGroup
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxGroup extends CachedObjectStringKey<LinuxGroup> implements Removable {

    static final int
        COLUMN_NAME=0,
        COLUMN_PACKAGE=1
    ;
    static final String COLUMN_NAME_name = "name";

    /**
     * Some commonly used system and application groups.
     */
    public static final String
        ADM="adm",
        APACHE="apache",
        AWSTATS="awstats",
        BIN="bin",
        DAEMON="daemon",
        FTP="ftp",
        FTPONLY="ftponly",
        MAIL="mail",
        MAILONLY="mailonly",
        NAMED="named",
        NOGROUP="nogroup",
        POSTGRES="postgres",
        PROFTPD_JAILED="proftpd_jailed",
        ROOT="root",
        SYS="sys",
        TTY="tty"
    ;
    /**
     * @deprecated  Group httpd no longer used.
     */
    @Deprecated
    public static final String HTTPD="httpd";

    String packageName;
    private String type;
    public static final int MAX_LENGTH=255;

    public int addLinuxAccount(LinuxAccount account) throws IOException, SQLException {
        return table.connector.linuxGroupAccounts.addLinuxGroupAccount(this, account);
    }

    public int addLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
        return table.connector.linuxServerGroups.addLinuxServerGroup(this, aoServer);
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case COLUMN_PACKAGE: return packageName;
            case 2: return type;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public LinuxGroupType getLinuxGroupType() throws SQLException {
        LinuxGroupType typeObject = table.connector.linuxGroupTypes.get(type);
        if (typeObject == null) throw new SQLException("Unable to find LinuxGroupType: " + type);
        return typeObject;
    }

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) throws IOException, SQLException {
        return table.connector.linuxServerGroups.getLinuxServerGroup(aoServer, pkey);
    }

    public List<LinuxServerGroup> getLinuxServerGroups() throws IOException, SQLException {
        return table.connector.linuxServerGroups.getLinuxServerGroups(this);
    }

    public String getName() {
        return pkey;
    }

    public Package getPackage() throws IOException, SQLException {
        // null OK because data may be filtered at this point, like the linux group 'mail'
        return table.connector.packages.get(packageName);
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_GROUPS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        packageName = result.getString(2);
        type = result.getString(3);
    }

    /**
     * Determines if a name can be used as a group name.  A name is valid if
     * it is between 1 and 255 characters in length and uses only ASCII 0x21
     * through 0x7f, excluding the following characters:
     * <code>space , : ( ) [ ] ' " | & ; A-Z</code>
     */
    public static boolean isValidGroupname(String name) {
        int len = name.length();
        if (len == 0 || len > MAX_LENGTH)
                return false;
        // The first character must be [a-z]
        char ch = name.charAt(0);
        if (ch < 'a' || ch > 'z')
                return false;
        // The rest may have additional characters
        for (int c = 1; c < len; c++) {
            ch = name.charAt(c);
            if(
                ch<0x21
                || ch>0x7f
                || (ch>='A' && ch<='Z')
                || ch==','
                || ch==':'
                || ch=='('
                || ch==')'
                || ch=='['
                || ch==']'
                || ch=='\''
                || ch=='"'
                || ch=='|'
                || ch=='&'
                || ch==';'
            ) return false;
        }
        return true;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        packageName=in.readUTF().intern();
        type=in.readUTF().intern();
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() throws IOException, SQLException {
        List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

        // Cannot be the primary group for any linux accounts
        for(LinuxGroupAccount lga : table.connector.linuxGroupAccounts.getRows()) {
            if(lga.isPrimary() && equals(lga.getLinuxGroup())) {
                reasons.add(new CannotRemoveReason<LinuxGroupAccount>("Used as primary group for Linux account "+lga.getLinuxAccount().getUsername().getUsername(), lga));
            }
        }

        // All LinuxServerGroups must be removable
        for(LinuxServerGroup lsg : getLinuxServerGroups()) reasons.addAll(lsg.getCannotRemoveReasons());

        return reasons;
    }

    public void remove() throws IOException, SQLException {
        table.connector.requestUpdateIL(
            AOServProtocol.CommandID.REMOVE,
            SchemaTable.TableID.LINUX_GROUPS,
            pkey
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(packageName);
        out.writeUTF(type);
    }
}