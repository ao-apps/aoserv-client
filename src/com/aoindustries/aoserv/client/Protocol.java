/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
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
final public class Protocol extends AOServObjectStringKey implements Comparable<Protocol>, DtoFactory<com.aoindustries.aoserv.client.dto.Protocol> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
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
    private static final long serialVersionUID = -8837153421233886350L;

    final private NetPort port;
    private String name;
    final private boolean userService;
    private String netProtocol;

    public Protocol(
        AOServConnector connector,
        String protocol,
        NetPort port,
        String name,
        boolean userService,
        String netProtocol
    ) {
        super(connector, protocol);
        this.port = port;
        this.name = name;
        this.userService = userService;
        this.netProtocol = netProtocol;
        intern();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        intern();
    }

    private void intern() {
        name = intern(name);
        netProtocol = intern(netProtocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(Protocol other) {
        try {
            int diff = port.compareTo(other.port);
            if(diff!=0) return diff;
            return netProtocol==other.netProtocol ? 0 : getNetProtocol().compareTo(other.getNetProtocol()); // OK - interned
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="the unique name of the protocol")
    public String getProtocol() {
        return getKey();
    }

    @SchemaColumn(order=1, description="the default port of the protocol")
    public NetPort getPort() {
        return port;
    }

    @SchemaColumn(order=2, description="the name of the service")
    public String getName() {
        return name;
    }

    @SchemaColumn(order=3, description="indicates that a user may add and remove this service")
    public boolean isUserService() {
        return userService;
    }

    public static final MethodColumn COLUMN_NET_PROTOCOL = getMethodColumn(Protocol.class, "netProtocol");
    @DependencySingleton
    @SchemaColumn(order=4, index=IndexType.INDEXED, description="the default network protocol for this protocol")
    public NetProtocol getNetProtocol() throws RemoteException {
        return getConnector().getNetProtocols().get(netProtocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public Protocol(AOServConnector connector, com.aoindustries.aoserv.client.dto.Protocol dto) throws ValidationException {
        this(
            connector,
            dto.getProtocol(),
            getNetPort(dto.getPort()),
            dto.getName(),
            dto.isUserService(),
            dto.getNetProtocol()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.Protocol getDto() {
        return new com.aoindustries.aoserv.client.dto.Protocol(getKey(), getDto(port), name, userService, netProtocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSingleton
    public HttpdJKProtocol getHttpdJKProtocol() throws RemoteException {
        return getConnector().getHttpdJKProtocols().filterUnique(HttpdJKProtocol.COLUMN_PROTOCOL, this);
    }

    @DependentObjectSet
    public IndexedSet<NetBind> getNetBinds() throws RemoteException {
        return getConnector().getNetBinds().filterIndexed(NetBind.COLUMN_APP_PROTOCOL, this);
    }

    /* TODO
    public HttpdJKProtocol getHttpdJKProtocol(AOServConnector connector) throws IOException, SQLException {
        return connector.getHttpdJKProtocols().get(pkey);
    }*/
    // </editor-fold>
}