/*
 * Copyright 2003-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * Represents MySQL replication for one a <code>FailoverFileReplication</code> or <code>AOServer</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplication extends AOServObjectIntegerKey implements Comparable<FailoverMySQLReplication>, DtoFactory<com.aoindustries.aoserv.client.dto.FailoverMySQLReplication> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 6717356004076691856L;

    final private Integer aoServer;
    final private Integer replication;
    final private int mysqlServer;
    final private Integer monitoringSecondsBehindLow;
    final private Integer monitoringSecondsBehindMedium;
    final private Integer monitoringSecondsBehindHigh;
    final private Integer monitoringSecondsBehindCritical;

    public FailoverMySQLReplication(
        AOServConnector connector,
        int pkey,
        Integer aoServer,
        Integer replication,
        int mysqlServer,
        Integer monitoringSecondsBehindLow,
        Integer monitoringSecondsBehindMedium,
        Integer monitoringSecondsBehindHigh,
        Integer monitoringSecondsBehindCritical
    ) {
        super(connector, pkey);
        this.aoServer = aoServer;
        this.replication = replication;
        this.mysqlServer = mysqlServer;
        this.monitoringSecondsBehindLow = monitoringSecondsBehindLow;
        this.monitoringSecondsBehindMedium = monitoringSecondsBehindMedium;
        this.monitoringSecondsBehindHigh = monitoringSecondsBehindHigh;
        this.monitoringSecondsBehindCritical = monitoringSecondsBehindCritical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(FailoverMySQLReplication other) {
        try {
            int diff = mysqlServer==other.mysqlServer ? 0 : getMysqlServer().compareTo(other.getMysqlServer());
            if(diff!=0) return diff;
            diff = aoServer==other.aoServer ? 0 : getAoServer().compareTo(other.getAoServer());
            if(diff!=0) return diff;
            return replication==other.replication ? 0 : getReplication().compareTo(other.getReplication());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated, unique id")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_AO_SERVER = getMethodColumn(FailoverMySQLReplication.class, "aoServer");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the ao_server that receives the replication")
    public AOServer getAoServer() throws RemoteException {
        if(aoServer==null) return null;
        return getConnector().getAoServers().get(aoServer);
    }

    public static final MethodColumn COLUMN_REPLICATION = getMethodColumn(FailoverMySQLReplication.class, "replication");
    @DependencySingleton
    @SchemaColumn(order=2, index=IndexType.INDEXED, description="the failover server that receives the replication")
    public FailoverFileReplication getReplication() throws RemoteException {
        if(replication==null) return null;
        return getConnector().getFailoverFileReplications().get(replication);
    }

    public static final MethodColumn COLUMN_MYSQL_SERVER = getMethodColumn(FailoverMySQLReplication.class, "mysqlServer");
    @DependencySingleton
    @SchemaColumn(order=3, index=IndexType.INDEXED, description="the MySQL Server that is being replicated")
    public MySQLServer getMysqlServer() throws RemoteException {
        return getConnector().getMysqlServers().get(mysqlServer);
    }

    @SchemaColumn(order=4, description="the seconds behind where will trigger low alert level")
    public Integer getMonitoringSecondsBehindLow() {
        return monitoringSecondsBehindLow;
    }

    @SchemaColumn(order=5, description="the seconds behind where will trigger medium alert level")
    public Integer getMonitoringSecondsBehindMedium() {
        return monitoringSecondsBehindMedium;
    }

    @SchemaColumn(order=6, description="the seconds behind where will trigger high alert level")
    public Integer getMonitoringSecondsBehindHigh() {
        return monitoringSecondsBehindHigh;
    }

    @SchemaColumn(order=7, description="the seconds behind where will trigger critical alert level")
    public Integer getMonitoringSecondsBehindCritical() {
        return monitoringSecondsBehindCritical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public FailoverMySQLReplication(AOServConnector connector, com.aoindustries.aoserv.client.dto.FailoverMySQLReplication dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getAoServer(),
            dto.getReplication(),
            dto.getMysqlServer(),
            dto.getMonitoringSecondsBehindLow(),
            dto.getMonitoringSecondsBehindMedium(),
            dto.getMonitoringSecondsBehindHigh(),
            dto.getMonitoringSecondsBehindCritical()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.FailoverMySQLReplication getDto() {
        return new com.aoindustries.aoserv.client.dto.FailoverMySQLReplication(getKeyInt(), aoServer, replication, mysqlServer, monitoringSecondsBehindLow, monitoringSecondsBehindMedium, monitoringSecondsBehindHigh, monitoringSecondsBehindCritical);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        if(aoServer!=null) return getMysqlServer().toStringImpl()+"->"+getAoServer().toStringImpl();
        else return getMysqlServer().toStringImpl()+"->"+getReplication().toStringImpl();
    }
    // </editor-fold>
}
