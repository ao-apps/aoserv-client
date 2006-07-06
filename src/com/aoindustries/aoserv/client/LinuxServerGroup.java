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
import java.util.*;

/**
 * A <code>LinuxServerGroup</code> adds a <code>LinuxGroup</code>
 * to an <code>AOServer</code>, so that <code>LinuxServerAccount</code> with
 * access to the group may use the group on the server.
 *
 * @see  LinuxGroup
 * @see  LinuxServerAccount
 * @see  AOServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class LinuxServerGroup extends CachedObjectIntegerKey<LinuxServerGroup> implements Removable {

    static final int
        COLUMN_PKEY=0,
        COLUMN_NAME=1,
        COLUMN_AO_SERVER=2
    ;

    String name;
    int ao_server;
    int gid;
    long created;

    public List<LinuxServerAccount> getAlternateLinuxServerAccounts() {
        Profiler.startProfile(Profiler.FAST, LinuxServerGroup.class, "getAlternateLinuxServerAccounts()", null);
        try {
            return table.connector.linuxServerAccounts.getAlternateLinuxServerAccounts(this);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, LinuxServerGroup.class, "getColValueImpl(int)", null);
        try {
            switch(i) {
                case COLUMN_PKEY: return Integer.valueOf(pkey);
                case COLUMN_NAME: return name;
                case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
                case 3: return Integer.valueOf(gid);
                case 4: return new java.sql.Date(created);
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public LinuxID getGID() {
        LinuxID obj=table.connector.linuxIDs.get(gid);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find LinuxID: "+gid));
        return obj;
    }

    public long getCreated() {
        return created;
    }

    public LinuxGroup getLinuxGroup() {
        LinuxGroup group = table.connector.linuxGroups.get(name);
        if (group == null) throw new WrappedException(new SQLException("Unable to find LinuxGroup: " + name));
        return group;
    }

    public AOServer getAOServer() {
        Profiler.startProfile(Profiler.FAST, LinuxServerGroup.class, "getAOServer()", null);
        try {
            AOServer ao=table.connector.aoServers.get(ao_server);
            if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
            return ao;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected int getTableIDImpl() {
        return SchemaTable.LINUX_SERVER_GROUPS;
    }

    void initImpl(ResultSet result) throws SQLException {
        Profiler.startProfile(Profiler.FAST, LinuxServerGroup.class, "initImpl(ResultSet)", null);
        try {
            pkey = result.getInt(1);
            name = result.getString(2);
            ao_server = result.getInt(3);
            gid = result.getInt(4);
            created = result.getTimestamp(5).getTime();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxServerGroup.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readCompressedInt();
            name=in.readUTF();
            ao_server=in.readCompressedInt();
            gid=in.readCompressedInt();
            created=in.readLong();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<CannotRemoveReason> getCannotRemoveReasons() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerGroup.class, "getCannotRemoveReasons()", null);
        try {
            List<CannotRemoveReason> reasons=new ArrayList<CannotRemoveReason>();

            AOServer ao=getAOServer();

            for(CvsRepository cr : ao.getCvsRepositories()) {
                if(cr.linux_server_group==pkey) reasons.add(new CannotRemoveReason<CvsRepository>("Used by CVS repository "+cr.getPath()+" on "+cr.getLinuxServerGroup().getAOServer().getServer().getHostname(), cr));
            }

            for(EmailList el : table.connector.emailLists.getRows()) {
                if(el.linux_group==pkey) reasons.add(new CannotRemoveReason<EmailList>("Used by email list "+el.getPath()+" on "+el.getLinuxServerGroup().getAOServer().getServer().getHostname(), el));
            }

            for(HttpdServer hs : ao.getHttpdServers()) {
                if(hs.linux_server_group==pkey) reasons.add(new CannotRemoveReason<HttpdServer>("Used by Apache server #"+hs.getNumber()+" on "+hs.getAOServer().getServer().getHostname(), hs));
            }

            for(HttpdSharedTomcat hst : ao.getHttpdSharedTomcats()) {
                if(hst.linux_server_group==pkey) reasons.add(new CannotRemoveReason<HttpdSharedTomcat>("Used by Multi-Site Tomcat JVM "+hst.getInstallDirectory()+" on "+hst.getAOServer().getServer().getHostname(), hst));
            }

            // httpd_sites
            for(HttpdSite site : ao.getHttpdSites()) {
                if(site.linuxGroup.equals(name)) reasons.add(new CannotRemoveReason<HttpdSite>("Used by website "+site.getInstallDirectory()+" on "+site.getAOServer().getServer().getHostname(), site));
            }

            for(InterBaseDBGroup idg : table.connector.interBaseDBGroups.getRows()) {
                if(idg.linux_server_group==pkey) reasons.add(new CannotRemoveReason<InterBaseDBGroup>("Used by InterBase DB Group "+idg.getPath()+" on "+idg.getLinuxServerGroup().getAOServer().getServer().getHostname(), idg));
            }

            for(MajordomoServer ms : ao.getMajordomoServers()) {
                if(ms.linux_server_group==pkey) {
                    EmailDomain ed=ms.getDomain();
                    reasons.add(new CannotRemoveReason<MajordomoServer>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getServer().getHostname(), ms));
                }
            }

            for(PrivateFTPServer pfs : ao.getPrivateFTPServers()) {
                if(pfs.pub_linux_server_group==pkey) reasons.add(new CannotRemoveReason<PrivateFTPServer>("Used by private FTP server "+pfs.getRoot()+" on "+pfs.getLinuxServerGroup().getAOServer().getServer().getHostname(), pfs));
            }

            return reasons;
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public void remove() {
        Profiler.startProfile(Profiler.UNKNOWN, LinuxServerGroup.class, "remove()", null);
        try {
            table.connector.requestUpdateIL(
                AOServProtocol.REMOVE,
                SchemaTable.LINUX_SERVER_GROUPS,
                pkey
            );
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    String toStringImpl() {
        return name;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, LinuxServerGroup.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeCompressedInt(pkey);
            out.writeUTF(name);
            out.writeCompressedInt(ao_server);
            out.writeCompressedInt(gid);
            out.writeLong(created);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}