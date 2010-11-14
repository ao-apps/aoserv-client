/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.*;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
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
    private String name;
    final private boolean isUserService;
    private String netProtocol;

    public Protocol(
        AOServConnector connector,
        String protocol,
        NetPort port,
        String name,
        boolean isUserService,
        String netProtocol
    ) {
        super(connector, protocol);
        this.port = port;
        this.name = name;
        this.isUserService = isUserService;
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
    @SchemaColumn(order=0, name="protocol", index=IndexType.PRIMARY_KEY, description="the unique name of the protocol")
    public String getProtocol() {
        return getKey();
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
        return getConnector().getNetProtocols().get(netProtocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.Protocol getDto() {
        return new com.aoindustries.aoserv.client.dto.Protocol(getKey(), getDto(port), name, isUserService, netProtocol);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetProtocol());
        return unionSet;
    }

    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependentObjects(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getHttpdJKProtocol());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getNetBinds());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public HttpdJKProtocol getHttpdJKProtocol() throws RemoteException {
        return getConnector().getHttpdJKProtocols().filterUnique(HttpdJKProtocol.COLUMN_PROTOCOL, this);
    }

    public IndexedSet<NetBind> getNetBinds() throws RemoteException {
        return getConnector().getNetBinds().filterIndexed(NetBind.COLUMN_APP_PROTOCOL, this);
    }

    /* TODO
    public HttpdJKProtocol getHttpdJKProtocol(AOServConnector connector) throws IOException, SQLException {
        return connector.getHttpdJKProtocols().get(pkey);
    }*/
    // </editor-fold>
}