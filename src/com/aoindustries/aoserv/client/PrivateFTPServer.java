/*
 * Copyright 2000-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * When a <code>PrivateFTPServer</code> is attached to a
 * <code>NetBind</code>, the FTP server reponds as configured
 * in the <code>PrivateFTPServer</code>.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateFTPServer extends CachedObjectIntegerKey<PrivateFTPServer> {

    static final int COLUMN_NET_BIND=0;
    static final String COLUMN_NET_BIND_name = "net_bind";

    private String logfile;
    private DomainName hostname;
    private String email;
    private long created;
    int pub_linux_server_account;
    private boolean allow_anonymous;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NET_BIND: return Integer.valueOf(pkey);
            case 1: return logfile;
            case 2: return hostname;
            case 3: return email;
            case 4: return getCreated();
            case 5: return Integer.valueOf(pub_linux_server_account);
            case 6: return allow_anonymous?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Timestamp getCreated() {
        return new Timestamp(created);
    }

    public String getEmail() {
        return email;
    }

    public DomainName getHostname() {
        return hostname;
    }

    public NetBind getNetBind() throws SQLException, IOException {
        NetBind nb=table.connector.getNetBinds().get(pkey);
        if(nb==null) throw new SQLException("Unable to find NetBind: "+pkey);
        return nb;
    }

    public String getLogfile() {
        return logfile;
    }

    public LinuxServerAccount getLinuxServerAccount() throws SQLException, IOException {
        LinuxServerAccount lsa=table.connector.getLinuxServerAccounts().get(pub_linux_server_account);
        if(lsa==null) throw new SQLException("Unable to find LinuxServerAccount: "+pub_linux_server_account);
        return lsa;
    }

    /**
     * @deprecated  use getLinuxServerAccount().getPrimaryLinuxServerGroup()
     */
    public LinuxServerGroup getLinuxServerGroup() throws SQLException, IOException {
        return getLinuxServerAccount().getPrimaryLinuxServerGroup();
    }

    public boolean allowAnonymous() {
        return allow_anonymous;
    }

    /**
     * @deprecated  use getLinuxServerAccount().getHome()
     */
    public String getRoot() throws SQLException, IOException {
        return getLinuxServerAccount().getHome();
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.PRIVATE_FTP_SERVERS;
    }

    public void init(ResultSet result) throws SQLException {
        try {
            pkey = result.getInt(1);
            logfile = result.getString(2);
            hostname = DomainName.valueOf(result.getString(3));
            email = result.getString(4);
            created = result.getTimestamp(5).getTime();
            pub_linux_server_account=result.getInt(6);
            allow_anonymous=result.getBoolean(7);
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        try {
            pkey=in.readCompressedInt();
            logfile=in.readUTF();
            hostname=DomainName.valueOf(in.readUTF());
            email=in.readUTF();
            created=in.readLong();
            pub_linux_server_account=in.readCompressedInt();
            allow_anonymous=in.readBoolean();
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    @Override
    String toStringImpl() {
        return hostname.toString();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_113)<0) throw new IOException("PrivateFTPServer on AOServProtocol version less than "+AOServProtocol.Version.VERSION_1_0_A_113.getVersion()+" is no longer supported.  Please upgrade your AOServ Client software packages.");
        out.writeCompressedInt(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_38)<=0) out.writeUTF("Upgrade AOServClient to version "+AOServProtocol.Version.VERSION_1_39+" or newer");
        out.writeUTF(logfile);
        out.writeUTF(hostname.toString());
        out.writeUTF(email);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_122)<=0) out.writeCompressedInt(-1);
        out.writeLong(created);
        out.writeCompressedInt(pub_linux_server_account);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_38)<=0) out.writeCompressedInt(-1);
        out.writeBoolean(allow_anonymous);
    }
}