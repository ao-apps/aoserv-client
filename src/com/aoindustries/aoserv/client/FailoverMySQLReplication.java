package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents MySQL replication for one A <code>FailoverFileReplication</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplication extends CachedObjectIntegerKey<FailoverMySQLReplication> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_REPLICATION=1,
        COLUMN_MYSQL_SERVER=2
    ;
    static final String COLUMN_REPLICATION_name = "replication";
    static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

    int replication;
    private int mysql_server;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_REPLICATION: return Integer.valueOf(replication);
            case COLUMN_MYSQL_SERVER: return mysql_server;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public FailoverFileReplication getFailoverFileReplication() throws SQLException, IOException {
        FailoverFileReplication ffr=table.connector.getFailoverFileReplications().get(replication);
        if(ffr==null) throw new SQLException("Unable to find FailoverFileReplication: "+replication);
        return ffr;
    }

    public MySQLServer getMySQLServer() throws IOException, SQLException {
        MySQLServer ms=table.connector.getMysqlServers().get(mysql_server);
        if(ms==null) throw new SQLException("Unable to find MySQLServer: "+mysql_server);
        return ms;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_MYSQL_REPLICATIONS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        replication=result.getInt(2);
        mysql_server=result.getInt(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        replication=in.readCompressedInt();
        mysql_server=in.readCompressedInt();
    }

    @Override
    String toStringImpl() throws IOException, SQLException {
        return getMySQLServer().getName()+", "+getFailoverFileReplication().toString();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(replication);
        out.writeCompressedInt(mysql_server);
    }
    
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

    /**
     * Gets the slave status or <code>null</code> if no slave status provided by MySQL.  If any error occurs, throws either
     * IOException or SQLException.
     */
    public SlaveStatus getSlaveStatus() throws IOException, SQLException {
        return table.connector.requestResult(
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
}
