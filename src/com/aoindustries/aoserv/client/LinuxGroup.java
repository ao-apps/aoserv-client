package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
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
        HTTPD="httpd",
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

    String packageName;
    private String type;
    public static final int MAX_LENGTH=255;

    public int addLinuxAccount(LinuxAccount account) {
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "addLinuxAccount(LinuxAccount)", null);
        try {
            return table.connector.linuxGroupAccounts.addLinuxGroupAccount(this, account);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public int addLinuxServerGroup(AOServer aoServer) {
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "addLinuxServerGroup(AOServer)", null);
        try {
            return table.connector.linuxServerGroups.addLinuxServerGroup(this, aoServer);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_NAME: return pkey;
                case COLUMN_PACKAGE: return packageName;
                case 2: return type;
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxGroupType getLinuxGroupType() {
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "getLinuxGroupType()", null);
        try {
            LinuxGroupType typeObject = table.connector.linuxGroupTypes.get(type);
            if (typeObject == null) throw new WrappedException(new SQLException("Unable to find LinuxGroupType: " + type));
            return typeObject;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxServerGroup getLinuxServerGroup(AOServer aoServer) {
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "getLinuxServerGroup(AOServer)", null);
        try {
            return table.connector.linuxServerGroups.getLinuxServerGroup(aoServer, pkey);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public List<LinuxServerGroup> getLinuxServerGroups() {
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "getLinuxServerGroups()", null);
        try {
            return table.connector.linuxServerGroups.getLinuxServerGroups(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getName() {
        return pkey;
    }

    public Package getPackage() {
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "getPackage()", null);
        try {
            // null OK because data may be filtered at this point, like the linux group 'mail'
            return table.connector.packages.get(packageName);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.LINUX_GROUPS;
    }

    void initImpl(ResultSet result) throws SQLException {
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
        Profiler.startProfile(Profiler.FAST, LinuxGroup.class, "isValidGroupname(String)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxGroup.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF().intern();
            packageName=in.readUTF().intern();
            type=in.readUTF().intern();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxGroup.class, "getCannotRemoveReasons()", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void remove() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxGroup.class, "remove()", null);
        try {
            table.connector.requestUpdateIL(
                AOServProtocol.CommandID.REMOVE,
                SchemaTable.TableID.LINUX_GROUPS,
                pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxGroup.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            out.writeUTF(packageName);
            out.writeUTF(type);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}