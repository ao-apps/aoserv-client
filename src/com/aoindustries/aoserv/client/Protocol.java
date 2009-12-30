package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.rmi.RemoteException;

/**
 * A <code>Protocol</code> represents one type of application
 * protocol used in <code>NetBind</code>s.  Monitoring is performed
 * in protocol-specific ways.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class Protocol extends AOServObjectStringKey<Protocol> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;

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
        MYSQL="MySQL",
        NTALK="ntalk",
        POP3="POP3",
        POSTGRESQL="PostgreSQL",
        RMI="RMI",
        SIEVE="sieve",
        SIMAP="SIMAP",
        SOAP="SOAP",
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int port;
    final private String name;
    final private boolean is_user_service;
    final private String net_protocol;

    public Protocol(ProtocolService<?,?> service, String protocol, int port, String name, boolean is_user_service, String net_protocol) {
        super(service, protocol);
        this.port = port;
        this.name = name;
        this.is_user_service = is_user_service;
        this.net_protocol = net_protocol.intern();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Protocol other) {
        int diff = compare(port, other.port);
        if(diff!=0) return diff;
        return compareIgnoreCaseConsistentWithEquals(net_protocol, other.net_protocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="protocol", unique=true, description="the unique name of the protocol")
    public String getProtocol() {
        return key;
    }

    /* TODO
    @SchemaColumn(order=1, name="port", description="the default port of the protocol")
    public NetPort getPort(AOServConnector connector) throws RemoteException {
        NetPort obj=connector.getNetPorts().get(port);
        if(obj==null) throw new RemoteException("Unable to find NetPort: "+port);
        return obj;
    }*/

    @SchemaColumn(order=1, name="name", description="the name of the service")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=2, name="is_user_service", description="indicates that a user may add and remove this service")
    public boolean isUserService() {
        return is_user_service;
    }

    @SchemaColumn(order=3, name="net_protocol", description="the default network protocol for this protocol")
    public NetProtocol getNetProtocol() throws RemoteException {
        NetProtocol np=getService().getConnector().getNetProtocols().get(net_protocol);
        if(np==null) throw new RemoteException("Unable to find NetProtocol: "+net_protocol);
        return np;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    /* TODO
    public HttpdJKProtocol getHttpdJKProtocol(AOServConnector connector) throws IOException, SQLException {
        return connector.getHttpdJKProtocols().get(pkey);
    }*/
    // </editor-fold>
}