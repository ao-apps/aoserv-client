/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * Represents MySQL replication for one a <code>FailoverFileReplication</code> or <code>AOServer</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplication extends AOServObjectIntegerKey implements Comparable<FailoverMySQLReplication>, DtoFactory<com.aoindustries.aoserv.client.dto.FailoverMySQLReplication> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private Integer aoServer;
    final private Integer replication;
    final private int mysqlServer;
    final private Integer monitoringSecondsBehindLow;
    final private Integer monitoringSecondsBehindMedium;
    final private Integer monitoringSecondsBehindHigh;
    final private Integer monitoringSecondsBehindCritical;

    public FailoverMySQLReplication(
        AOServConnector<?,?> connector,
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
            int diff = mysqlServer==other.mysqlServer ? 0 : getMySQLServer().compareTo(other.getMySQLServer());
            if(diff!=0) return diff;
            diff = aoServer==other.aoServer ? 0 : getAOServer().compareTo(other.getAOServer());
            if(diff!=0) return diff;
            return replication==other.replication ? 0 : getFailoverFileReplication().compareTo(other.getFailoverFileReplication());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated, unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_AO_SERVER = "ao_server";
    @SchemaColumn(order=1, name=COLUMN_AO_SERVER, index=IndexType.INDEXED, description="the ao_server that receives the replication")
    public AOServer getAOServer() throws RemoteException {
        if(aoServer==null) return null;
        return getConnector().getAoServers().get(aoServer);
    }

    static final String COLUMN_REPLICATION = "replication";
    @SchemaColumn(order=2, name=COLUMN_REPLICATION, index=IndexType.INDEXED, description="the failover server that receives the replication")
    public FailoverFileReplication getFailoverFileReplication() throws RemoteException {
        if(replication==null) return null;
        return getConnector().getFailoverFileReplications().get(replication);
    }

    static final String COLUMN_MYSQL_SERVER = "mysql_server";
    @SchemaColumn(order=3, name=COLUMN_MYSQL_SERVER, index=IndexType.INDEXED, description="the MySQL Server that is being replicated")
    public MySQLServer getMySQLServer() throws RemoteException {
        return getConnector().getMysqlServers().get(mysqlServer);
    }

    @SchemaColumn(order=4, name="monitoring_seconds_behind_low", description="the seconds behind where will trigger low alert level")
    public Integer getMonitoringSecondsBehindLow() {
        return monitoringSecondsBehindLow;
    }

    @SchemaColumn(order=5, name="monitoring_seconds_behind_medium", description="the seconds behind where will trigger medium alert level")
    public Integer getMonitoringSecondsBehindMedium() {
        return monitoringSecondsBehindMedium;
    }

    @SchemaColumn(order=6, name="monitoring_seconds_behind_high", description="the seconds behind where will trigger high alert level")
    public Integer getMonitoringSecondsBehindHigh() {
        return monitoringSecondsBehindHigh;
    }

    @SchemaColumn(order=7, name="monitoring_seconds_behind_critical", description="the seconds behind where will trigger critical alert level")
    public Integer getMonitoringSecondsBehindCritical() {
        return monitoringSecondsBehindCritical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.FailoverMySQLReplication getDto() {
        return new com.aoindustries.aoserv.client.dto.FailoverMySQLReplication(key, aoServer, replication, mysqlServer, monitoringSecondsBehindLow, monitoringSecondsBehindMedium, monitoringSecondsBehindHigh, monitoringSecondsBehindCritical);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependencies(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = super.addDependencies(unionSet);
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAOServer());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getFailoverFileReplication());
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getMySQLServer());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        if(aoServer!=null) return getMySQLServer().toStringImpl()+"->"+getAOServer().toStringImpl();
        else return getMySQLServer().toStringImpl()+"->"+getFailoverFileReplication().toStringImpl();
    }
    // </editor-fold>
}
