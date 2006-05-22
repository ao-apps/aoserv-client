package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * When a <code>PrivateFTPServer</code> is attached to a
 * <code>NetBind</code>, the FTP server reponds as configured
 * in the <code>PrivateFTPServer</code>.
 *
 * @see  NetBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateFTPServer extends CachedObjectIntegerKey<PrivateFTPServer> {

    static final int COLUMN_NET_BIND=0;

    private String root;
    private String logfile;
    private String hostname;
    private String email;
    private long created;
    int pub_linux_server_account;
    int pub_linux_server_group;
    private boolean allow_anonymous;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NET_BIND: return Integer.valueOf(pkey);
            case 1: return root;
            case 2: return logfile;
            case 3: return hostname;
            case 4: return email;
            case 5: return new java.sql.Date(created);
            case 6: return Integer.valueOf(pub_linux_server_account);
            case 7: return Integer.valueOf(pub_linux_server_group);
            case 8: return allow_anonymous?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getCreated() {
        return created;
    }

    public String getEmail() {
        return email;
    }

    public String getHostname() {
        return hostname;
    }

    public NetBind getNetBind() {
        NetBind nb=table.connector.netBinds.get(pkey);
        if(nb==null) throw new WrappedException(new SQLException("Unable to find NetBind: "+pkey));
        return nb;
    }

    public String getLogfile() {
        return logfile;
    }

    public LinuxServerAccount getLinuxServerAccount() {
        LinuxServerAccount lsa=table.connector.linuxServerAccounts.get(pub_linux_server_account);
        if(lsa==null) throw new WrappedException(new SQLException("Unable to find LinuxServerAccount: "+pub_linux_server_account));
        return lsa;
    }

    public LinuxServerGroup getLinuxServerGroup() {
        LinuxServerGroup lsg=table.connector.linuxServerGroups.get(pub_linux_server_group);
        if(lsg==null) throw new WrappedException(new SQLException("Unable to find LinuxServerGroup: "+pub_linux_server_group));
        return lsg;
    }
    
    public boolean allowAnonymous() {
        return allow_anonymous;
    }

    public String getRoot() {
        return root;
    }

    protected int getTableIDImpl() {
        return SchemaTable.PRIVATE_FTP_SERVERS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        root = result.getString(2);
        logfile = result.getString(3);
        hostname = result.getString(4);
        email = result.getString(5);
        created = result.getTimestamp(6).getTime();
        pub_linux_server_account=result.getInt(7);
        pub_linux_server_group=result.getInt(8);
        allow_anonymous=result.getBoolean(9);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        root=in.readUTF();
        logfile=in.readUTF();
        hostname=in.readUTF();
        email=in.readUTF();
        created=in.readLong();
        pub_linux_server_account=in.readCompressedInt();
        pub_linux_server_group=in.readCompressedInt();
        allow_anonymous=in.readBoolean();
    }

    String toStringImpl() {
        return hostname;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_113)<0) throw new IOException("PrivateFTPServer on AOServProtocol version less than "+AOServProtocol.VERSION_1_0_A_113+" is no longer supported.  Please upgrade your AOServ Client software packages.");
        out.writeCompressedInt(pkey);
        out.writeUTF(root);
        out.writeUTF(logfile);
        out.writeUTF(hostname);
        out.writeUTF(email);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_122)<=0) out.writeCompressedInt(-1);
        out.writeLong(created);
        out.writeCompressedInt(pub_linux_server_account);
        out.writeCompressedInt(pub_linux_server_group);
        out.writeBoolean(allow_anonymous);
    }
}