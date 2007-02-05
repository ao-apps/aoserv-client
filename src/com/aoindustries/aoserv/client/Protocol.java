package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>Protocol</code> represents one type of application
 * protocol used in <code>NetBind</code>s.  Monitoring is performed
 * in protocol-specific ways.
 *
 * @see  NetBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class Protocol extends GlobalObjectStringKey<Protocol> {

    static final int COLUMN_PROTOCOL=0;

    private int port;
    private String name;
    private boolean is_user_service;
    private String net_protocol;

    public static final String
        AOSERV_DAEMON="aoserv-daemon",
        AOSERV_DAEMON_SSL="aoserv-daemon-ssl",
        AOSERV_MASTER="aoserv-master",
        AOSERV_MASTER_SSL="aoserv-master-ssl",
        AUTH="auth",
        CVSPSERVER="cvspserver",
        DNS="DNS",
        FTP="FTP",
        FTP_DATA="FTP-DATA",
        HTTP="HTTP",
        HTTPS="HTTPS",
        HYPERSONIC="hypersonic",
        IMAP2="IMAP2",
        INTERSERVER="InterServer",
        JMX="JMX",
        JNP="JNP",
        NTALK="ntalk",
        POP2="POP2",
        POP3="POP3",
        RMI="RMI",
        SIMAP="SIMAP",
        SPOP3="SPOP3",
        SSH="SSH",
        SMTP="SMTP",
        SMTPS="SMTPS",
        SUBMISSION="submission",
        TALK="talk",
        TELNET="Telnet",
        TOMCAT4_SHUTDOWN="tomcat4-shutdown",
        WEBSERVER="webserver"
    ;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PROTOCOL: return pkey;
            case 1: return Integer.valueOf(port);
            case 2: return name;
            case 3: return is_user_service?Boolean.TRUE:Boolean.FALSE;
            case 4: return net_protocol;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public HttpdJKProtocol getHttpdJKProtocol(AOServConnector connector) {
        return connector.httpdJKProtocols.get(pkey);
    }

    public String getName() {
        return name;
    }
    
    public boolean isUserService() {
        return is_user_service;
    }

    public NetProtocol getNetProtocol(AOServConnector connector) {
        NetProtocol np=connector.netProtocols.get(net_protocol);
        if(np==null) throw new WrappedException(new SQLException("Unable to find NetProtocol: "+net_protocol));
        return np;
    }

    public NetPort getPort(AOServConnector connector) {
        NetPort obj=connector.netPorts.get(port);
        if(obj==null) throw new WrappedException(new SQLException("Unable to find NetPort: "+port));
        return obj;
    }

    public String getProtocol() {
        return pkey;
    }

    protected int getTableIDImpl() {
        return SchemaTable.PROTOCOLS;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        port = result.getInt(2);
        name = result.getString(3);
        is_user_service = result.getBoolean(4);
        net_protocol = result.getString(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF();
        port=in.readCompressedInt();
        name=in.readUTF();
        is_user_service=in.readBoolean();
        net_protocol=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeUTF(pkey);
        out.writeCompressedInt(port);
        out.writeUTF(name);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_105)>=0) {
            out.writeBoolean(is_user_service);
            out.writeUTF(net_protocol);
        }
    }
}