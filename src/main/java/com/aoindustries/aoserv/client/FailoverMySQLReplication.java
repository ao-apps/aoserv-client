/*
 * Copyright 2003-2009, 2014, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents MySQL replication for one a <code>FailoverFileReplication</code> or <code>AOServer</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverMySQLReplication extends CachedObjectIntegerKey<FailoverMySQLReplication> {

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=1,
		COLUMN_REPLICATION=2,
		COLUMN_MYSQL_SERVER=3
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_REPLICATION_name = "replication";
	static final String COLUMN_MYSQL_SERVER_name = "mysql_server";

	int ao_server;
	int replication;
	private int mysql_server;
	private int monitoring_seconds_behind_low;
	private int monitoring_seconds_behind_medium;
	private int monitoring_seconds_behind_high;
	private int monitoring_seconds_behind_critical;
	private AlertLevel maxAlertLevel;

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY : return pkey;
			case COLUMN_AO_SERVER : return ao_server==-1 ? null : ao_server;
			case COLUMN_REPLICATION : return replication==-1 ? null : replication;
			case COLUMN_MYSQL_SERVER : return mysql_server;
			case 4 : return monitoring_seconds_behind_low==-1 ? null : monitoring_seconds_behind_low;
			case 5 : return monitoring_seconds_behind_medium==-1 ? null : monitoring_seconds_behind_medium;
			case 6 : return monitoring_seconds_behind_high==-1 ? null : monitoring_seconds_behind_high;
			case 7 : return monitoring_seconds_behind_critical==-1 ? null : monitoring_seconds_behind_critical;
			case 8 : return maxAlertLevel.name();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public AOServer getAOServer() throws SQLException, IOException {
		if(ao_server==-1) return null;
		AOServer ao=table.connector.getAoServers().get(ao_server);
		if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return ao;
	}

	public FailoverFileReplication getFailoverFileReplication() throws SQLException, IOException {
		if(replication==-1) return null;
		FailoverFileReplication ffr=table.connector.getFailoverFileReplications().get(replication);
		if(ffr==null) throw new SQLException("Unable to find FailoverFileReplication: "+replication);
		return ffr;
	}

	public MySQLServer getMySQLServer() throws IOException, SQLException {
		MySQLServer ms=table.connector.getMysqlServers().get(mysql_server);
		if(ms==null) throw new SQLException("Unable to find MySQLServer: "+mysql_server);
		return ms;
	}

	public int getMonitoringSecondsBehindLow() {
		return monitoring_seconds_behind_low;
	}

	public int getMonitoringSecondsBehindMedium() {
		return monitoring_seconds_behind_medium;
	}

	public int getMonitoringSecondsBehindHigh() {
		return monitoring_seconds_behind_high;
	}

	public int getMonitoringSecondsBehindCritical() {
		return monitoring_seconds_behind_critical;
	}

	public AlertLevel getMaxAlertLevel() {
		return maxAlertLevel;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.FAILOVER_MYSQL_REPLICATIONS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		int pos = 1;
		pkey=result.getInt(pos++);
		ao_server=result.getInt(pos++);
		if(result.wasNull()) ao_server = -1;
		replication=result.getInt(pos++);
		if(result.wasNull()) replication = -1;
		mysql_server=result.getInt(pos++);
		monitoring_seconds_behind_low = result.getInt(pos++);
		if(result.wasNull()) monitoring_seconds_behind_low = -1;
		monitoring_seconds_behind_medium = result.getInt(pos++);
		if(result.wasNull()) monitoring_seconds_behind_medium = -1;
		monitoring_seconds_behind_high = result.getInt(pos++);
		if(result.wasNull()) monitoring_seconds_behind_high = -1;
		monitoring_seconds_behind_critical = result.getInt(pos++);
		if(result.wasNull()) monitoring_seconds_behind_critical = -1;
		maxAlertLevel = AlertLevel.valueOf(result.getString(pos++));
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		pkey = in.readCompressedInt();
		ao_server = in.readCompressedInt();
		replication = in.readCompressedInt();
		mysql_server = in.readCompressedInt();
		monitoring_seconds_behind_low = in.readCompressedInt();
		monitoring_seconds_behind_medium = in.readCompressedInt();
		monitoring_seconds_behind_high = in.readCompressedInt();
		monitoring_seconds_behind_critical = in.readCompressedInt();
		maxAlertLevel = AlertLevel.valueOf(in.readCompressedUTF());
	}

	@Override
	String toStringImpl() throws IOException, SQLException {
		if(ao_server!=-1) return getMySQLServer().toStringImpl()+"->"+getAOServer().toStringImpl();
		else return getMySQLServer().toStringImpl()+"->"+getFailoverFileReplication().toStringImpl();
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeCompressedInt(pkey);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_59)>=0) out.writeCompressedInt(ao_server);
		out.writeCompressedInt(replication);
		out.writeCompressedInt(mysql_server);
		if(version.compareTo(AOServProtocol.Version.VERSION_1_56)>=0) {
			out.writeCompressedInt(monitoring_seconds_behind_low);
			out.writeCompressedInt(monitoring_seconds_behind_medium);
			out.writeCompressedInt(monitoring_seconds_behind_high);
			out.writeCompressedInt(monitoring_seconds_behind_critical);
		}
		if(version.compareTo(AOServProtocol.Version.VERSION_1_74) >= 0) {
			out.writeCompressedUTF(maxAlertLevel.name());
		}
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

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(AOServProtocol.CommandID.GET_MYSQL_SLAVE_STATUS.ordinal());
					out.writeCompressedInt(pkey);
				}

				@Override
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

				@Override
				public SlaveStatus afterRelease() {
					return result;
				}
			}
		);
	}
}