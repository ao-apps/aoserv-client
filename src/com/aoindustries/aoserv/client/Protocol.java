package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * A <code>Protocol</code> represents one type of application
 * protocol used in <code>NetBind</code>s.  Monitoring is performed
 * in protocol-specific ways.
 *
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class Protocol extends AOServObjectStringKey<Protocol> implements BeanFactory<com.aoindustries.aoserv.client.beans.Protocol> {

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
    final private NetPort port;
    final private String name;
    final private boolean isUserService;
    final private String netProtocol;

    public Protocol(
        ProtocolService<?,?> service,
        String protocol,
        NetPort port,
        String name,
        boolean isUserService,
        String netProtocol
    ) {
        super(service, protocol);
        this.port = port;
        this.name = name;
        this.isUserService = isUserService;
        this.netProtocol = netProtocol.intern();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(Protocol other) throws RemoteException {
        int diff = port.compareTo(other.port);
        if(diff!=0) return diff;
        return netProtocol.equals(other.netProtocol) ? 0 : getNetProtocol().compareTo(other.getNetProtocol());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="protocol", index=IndexType.PRIMARY_KEY, description="the unique name of the protocol")
    public String getProtocol() {
        return key;
    }

    @SchemaColumn(order=1, name="port", description="the default port of the protocol")
    public NetPort getPort() {
        return port;
    }

    @SchemaColumn(order=2, name="name", description="the name of the service")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=3, name="is_user_service", description="indicates that a user may add and remove this service")
    public boolean isUserService() {
        return isUserService;
    }

    static final String COLUMN_NET_PROTOCOL = "net_protocol";
    @SchemaColumn(order=4, name=COLUMN_NET_PROTOCOL, index=IndexType.INDEXED, description="the default network protocol for this protocol")
    public NetProtocol getNetProtocol() throws RemoteException {
        return getService().getConnector().getNetProtocols().get(netProtocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.Protocol getBean() {
        return new com.aoindustries.aoserv.client.beans.Protocol(key, port.getBean(), name, isUserService, netProtocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getNetProtocol()
        );
    }

    @Override
    public Set<? extends AOServObject> getDependentObjects() throws RemoteException {
        return createDependencySet(
            getNetBinds()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public Set<NetBind> getNetBinds() throws RemoteException {
        return getService().getConnector().getNetBinds().getIndexed(NetBind.COLUMN_APP_PROTOCOL, this);
    }

    /* TODO
    public HttpdJKProtocol getHttpdJKProtocol(AOServConnector connector) throws IOException, SQLException {
        return connector.getHttpdJKProtocols().get(pkey);
    }*/
    // </editor-fold>
}