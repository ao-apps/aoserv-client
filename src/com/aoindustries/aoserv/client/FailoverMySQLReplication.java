package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Set;

/**
 * Represents MySQL replication for one a <code>FailoverFileReplication</code> or <code>AOServer</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplication extends AOServObjectIntegerKey<FailoverMySQLReplication> implements BeanFactory<com.aoindustries.aoserv.client.beans.FailoverMySQLReplication> {

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
        FailoverMySQLReplicationService<?,?> service,
        int pkey,
        Integer aoServer,
        Integer replication,
        int mysqlServer,
        Integer monitoringSecondsBehindLow,
        Integer monitoringSecondsBehindMedium,
        Integer monitoringSecondsBehindHigh,
        Integer monitoringSecondsBehindCritical
    ) {
        super(service, pkey);
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
    protected int compareToImpl(FailoverMySQLReplication other) throws RemoteException {
        int diff = mysqlServer==other.mysqlServer ? 0 : getMySQLServer().compareTo(other.getMySQLServer());
        if(diff!=0) return diff;
        diff = aoServer==other.aoServer ? 0 : getAOServer().compareTo(other.getAOServer());
        if(diff!=0) return diff;
        return replication==other.replication ? 0 : getFailoverFileReplication().compareTo(other.getFailoverFileReplication());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated, unique id")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="ao_server", description="the ao_server that receives the replication")
    public AOServer getAOServer() throws RemoteException {
        if(aoServer==null) return null;
        return getService().getConnector().getAoServers().get(aoServer);
    }

    static final String COLUMN_REPLICATION = "replication";
    @SchemaColumn(order=2, name=COLUMN_REPLICATION, index=IndexType.INDEXED, description="the failover server that receives the replication")
    public FailoverFileReplication getFailoverFileReplication() throws RemoteException {
        if(replication==null) return null;
        return getService().getConnector().getFailoverFileReplications().get(replication);
    }

    static final String COLUMN_MYSQL_SERVER = "mysql_server";
    @SchemaColumn(order=3, name=COLUMN_MYSQL_SERVER, index=IndexType.INDEXED, description="the MySQL Server that is being replicated")
    public MySQLServer getMySQLServer() throws RemoteException {
        return getService().getConnector().getMysqlServers().get(mysqlServer);
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

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.FailoverMySQLReplication getBean() {
        return new com.aoindustries.aoserv.client.beans.FailoverMySQLReplication(key, aoServer, replication, mysqlServer, monitoringSecondsBehindLow, monitoringSecondsBehindMedium, monitoringSecondsBehindHigh, monitoringSecondsBehindCritical);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return AOServObjectUtils.createDependencySet(
            getAOServer(),
            getFailoverFileReplication(),
            getMySQLServer()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        if(aoServer!=null) return getMySQLServer().toString(userLocale)+"->"+getAOServer().toString(userLocale);
        else return getMySQLServer().toString(userLocale)+"->"+getFailoverFileReplication().toString(userLocale);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    final public static class SlaveStatus implements Serializable {

        private static final long serialVersionUID = 1L;

        final private String slaveIOState;
        final private String masterLogFile;
        final private String readMasterLogPos;
        final private String relayLogFile;
        final private String relayLogPos;
        final private String relayMasterLogFile;
        final private String slaveIORunning;
        final private String slaveSQLRunning;
        final private String lastErrno;
        final private String lastError;
        final private String skipCounter;
        final private String execMasterLogPos;
        final private String relayLogSpace;
        final private String secondsBehindMaster;
        
        public SlaveStatus(
            String slaveIOState,
            String masterLogFile,
            String readMasterLogPos,
            String relayLogFile,
            String relayLogPos,
            String relayMasterLogFile,
            String slaveIORunning,
            String slaveSQLRunning,
            String lastErrno,
            String lastError,
            String skipCounter,
            String execMasterLogPos,
            String relayLogSpace,
            String secondsBehindMaster
        ) {
            this.slaveIOState=slaveIOState;
            this.masterLogFile=masterLogFile;
            this.readMasterLogPos=readMasterLogPos;
            this.relayLogFile=relayLogFile;
            this.relayLogPos=relayLogPos;
            this.relayMasterLogFile=relayMasterLogFile;
            this.slaveIORunning=slaveIORunning;
            this.slaveSQLRunning=slaveSQLRunning;
            this.lastErrno=lastErrno;
            this.lastError=lastError;
            this.skipCounter=skipCounter;
            this.execMasterLogPos=execMasterLogPos;
            this.relayLogSpace=relayLogSpace;
            this.secondsBehindMaster=secondsBehindMaster;
        }

        public String getSlaveIOState() {
            return slaveIOState;
        }

        public String getMasterLogFile() {
            return masterLogFile;
        }

        public String getReadMasterLogPos() {
            return readMasterLogPos;
        }

        public String getRelayLogFile() {
            return relayLogFile;
        }

        public String getRelayLogPos() {
            return relayLogPos;
        }

        public String getRelayMasterLogFile() {
            return relayMasterLogFile;
        }

        public String getSlaveIORunning() {
            return slaveIORunning;
        }

        public String getSlaveSQLRunning() {
            return slaveSQLRunning;
        }

        public String getLastErrno() {
            return lastErrno;
        }

        public String getLastError() {
            return lastError;
        }

        public String getSkipCounter() {
            return skipCounter;
        }

        public String getExecMasterLogPos() {
            return execMasterLogPos;
        }

        public String getRelayLogSpace() {
            return relayLogSpace;
        }

        public String getSecondsBehindMaster() {
            return secondsBehindMaster;
        }
    }

    /**
     * Gets the slave status or <code>null</code> if no slave status provided by MySQL.  If any error occurs, throws either
     * IOException or SQLException.
     */
    /* TODO
    public SlaveStatus getSlaveStatus() throws IOException, SQLException {
        return getService().getConnector().requestResult(
            true,
            new AOServConnector.ResultRequest<SlaveStatus>() {
                SlaveStatus result;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.GET_MYSQL_SLAVE_STATUS.ordinal());
                    out.writeCompressedInt(pkey);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.NEXT) {
                        result = new SlaveStatus(
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF(),
                            in.readNullUTF()
                        );
                    } else if(code==AOServProtocol.DONE) {
                        result = null;
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public SlaveStatus afterRelease() {
                    return result;
                }
            }
        );
    }
     */
    // </editor-fold>
}
