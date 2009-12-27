package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Represents MySQL replication for one a <code>FailoverFileReplication</code> or <code>AOServer</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplication extends AOServObjectIntegerKey<FailoverMySQLReplication> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int ao_server;
    final private int replication;
    final private int mysql_server;
    final private int monitoring_seconds_behind_low;
    final private int monitoring_seconds_behind_medium;
    final private int monitoring_seconds_behind_high;
    final private int monitoring_seconds_behind_critical;

    public FailoverMySQLReplication(
        FailoverMySQLReplicationService<?,?> service,
        int pkey,
        int ao_server,
        int replication,
        int mysql_server,
        int monitoring_seconds_behind_low,
        int monitoring_seconds_behind_medium,
        int monitoring_seconds_behind_high,
        int monitoring_seconds_behind_critical
    ) {
        super(service, pkey);
        this.ao_server = ao_server;
        this.replication = replication;
        this.mysql_server = mysql_server;
        this.monitoring_seconds_behind_low = monitoring_seconds_behind_low;
        this.monitoring_seconds_behind_medium = monitoring_seconds_behind_medium;
        this.monitoring_seconds_behind_high = monitoring_seconds_behind_high;
        this.monitoring_seconds_behind_critical = monitoring_seconds_behind_critical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    protected int compareToImpl(FailoverMySQLReplication other) throws RemoteException {
        int diff = getMySQLServer().compareTo(other.getMySQLServer());
        if(diff!=0) return diff;
        diff = getAOServer().compareTo(other.getAOServer());
        if(diff!=0) return diff;
        return getFailoverFileReplication().compareTo(other.getFailoverFileReplication());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", unique=true, description="a generated, unique id")
    public int getPkey() {
        return key;
    }

    @SchemaColumn(order=1, name="ao_server", description="the ao_server the receives the replication")
    public AOServer getAOServer() throws RemoteException {
        if(ao_server==-1) return null;
        return getService().getConnector().getAoServers().get(ao_server);
    }

    @SchemaColumn(order=2, name="replication", description="the failover server that receives the replication")
    public FailoverFileReplication getFailoverFileReplication() throws RemoteException {
        if(replication==-1) return null;
        return getService().getConnector().getFailoverFileReplications().get(replication);
    }

    @SchemaColumn(order=3, name="mysql_server", description="the MySQL Server that is being replicated")
    public MySQLServer getMySQLServer() throws RemoteException {
        return getService().getConnector().getMysqlServers().get(mysql_server);
    }

    @SchemaColumn(order=4, name="monitoring_seconds_behind_low", description="the seconds behind where will trigger low alert level")
    public int getMonitoringSecondsBehindLow() {
        return monitoring_seconds_behind_low;
    }

    @SchemaColumn(order=5, name="monitoring_seconds_behind_medium", description="the seconds behind where will trigger medium alert level")
    public int getMonitoringSecondsBehindMedium() {
        return monitoring_seconds_behind_medium;
    }

    @SchemaColumn(order=6, name="monitoring_seconds_behind_high", description="the seconds behind where will trigger high alert level")
    public int getMonitoringSecondsBehindHigh() {
        return monitoring_seconds_behind_high;
    }

    @SchemaColumn(order=7, name="monitoring_seconds_behind_critical", description="the seconds behind where will trigger critical alert level")
    public int getMonitoringSecondsBehindCritical() {
        return monitoring_seconds_behind_critical;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    public Set<? extends AOServObject> getDependencies() throws RemoteException {
        return createDependencySet(
            getAOServer(),
            getFailoverFileReplication(),
            getMySQLServer()
        );
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl(Locale userLocale) throws RemoteException {
        if(ao_server!=-1) return getMySQLServer().toString(userLocale)+"->"+getAOServer().toString(userLocale);
        else return getMySQLServer().toString(userLocale)+"->"+getFailoverFileReplication().toString(userLocale);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TODO">
    /*
    final public static class SlaveStatus {

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
    */
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
