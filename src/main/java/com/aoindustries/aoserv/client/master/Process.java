/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aoserv-client.
 *
 * aoserv-client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aoserv-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aoserv-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.AOServObject;
import com.aoindustries.aoserv.client.AOServTable;
import com.aoindustries.aoserv.client.SingleTableObject;
import com.aoindustries.aoserv.client.account.Administrator;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.lang.ObjectUtils;
import com.aoindustries.net.InetAddress;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Each <code>Thread</code> on the master reports its activities so that
 * a query on this table shows a snapshot of the currently running system.
 *
 * @author  AO Industries, Inc.
 */
final public class Process extends AOServObject<Long,Process> implements SingleTableObject<Long,Process> {

	private static boolean logCommands=false;

	/**
	 * Turns on/off command logging.
	 */
	public static void setLogCommands(boolean state) {
		logCommands=state;
	}

	public static boolean getLogCommands() {
		return logCommands;
	}

	/**
	 * The different states a process may be in.
	 */
	public static final String
		LOGIN="login",
		RUN="run",
		SLEEP="sleep"
	;

	long process_id;
	private long connector_id=-1;
	private UserId authenticated_user;
	private UserId effective_user;
	private int daemon_server;
	private InetAddress host;
	private String protocol;
	private String aoserv_protocol;
	private boolean is_secure;
	private long connect_time;
	private long use_count;
	private long total_time;
	private int priority;
	private String state;
	private Object[] command;
	private long state_start_time;

	private AOServTable<Long,Process> table;

	public Process() {
	}

	public Process(
		long process_id,
		InetAddress host,
		String protocol,
		boolean is_secure,
		long connect_time
	) {
		this.process_id=process_id;
		this.host=host;
		this.protocol=protocol;
		this.is_secure=is_secure;
		this.connect_time=connect_time;
		this.priority=Thread.NORM_PRIORITY;
		this.state=LOGIN;
		this.state_start_time=connect_time;
	}

	synchronized public void commandCompleted() {
		long time=System.currentTimeMillis();
		total_time+=time-state_start_time;
		state=SLEEP;
		command=null;
		state_start_time=time;
	}

	synchronized public void commandRunning() {
		use_count++;
		state=RUN;
		state_start_time=System.currentTimeMillis();
	}

	synchronized public void commandSleeping() {
		if(!state.equals(SLEEP)) {
			long time=System.currentTimeMillis();
			state=SLEEP;
			total_time+=time-state_start_time;
			state_start_time=time;
		}
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case 0: return process_id;
			case 1: return connector_id == -1 ? null : connector_id;
			case 2: return authenticated_user;
			case 3: return effective_user;
			case 4: return daemon_server == -1 ? null : daemon_server;
			case 5: return host;
			case 6: return protocol;
			case 7: return aoserv_protocol;
			case 8: return is_secure;
			case 9: return getConnectTime();
			case 10: return use_count;
			case 11: return total_time;
			case 12: return priority;
			case 13: return state;
			case 14: return getCommand();
			case 15: return getStateStartTime();
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public int getDaemonServer() {
		return daemon_server;
	}

	public int getPriority() {
		return priority;
	}

	public long getProcessID() {
		return process_id;
	}

	public long getConnectorID() {
		return connector_id;
	}

	public UserId getAuthenticatedUser() {
		return authenticated_user;
	}

	public Administrator getAuthenticatedBusinessAdministrator() throws IOException, SQLException {
		// Null OK when filtered
		return table.getConnector().getAccount().getBusinessAdministrators().get(authenticated_user);
	}

	public UserId getEffectiveUser() {
		return effective_user;
	}

	public Administrator getEffectiveBusinessAdministrator() throws SQLException, IOException {
		Administrator ba=table.getConnector().getAccount().getBusinessAdministrators().get(effective_user);
		if(ba==null) throw new SQLException("Unable to find BusinessAdministrator: "+effective_user);
		return ba;
	}

	public InetAddress getHost() {
		return host;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getAOServProtocol() {
		return aoserv_protocol;
	}

	public void setAOServProtocol(String aoserv_protocol) {
		this.aoserv_protocol=aoserv_protocol;
	}

	public boolean isSecure() {
		return is_secure;
	}

	public Timestamp getConnectTime() {
		return new Timestamp(connect_time);
	}

	public long getUseCount() {
		return use_count;
	}

	public long getTotalTime() {
		return total_time;
	}

	public String getState() {
		return state;
	}

	public Timestamp getStateStartTime() {
		return new Timestamp(state_start_time);
	}

	@Override
	public Long getKey() {
		return process_id;
	}

	/**
	 * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
	 *
	 * @return  the <code>AOServTable</code>.
	 */
	@Override
	final public AOServTable<Long,Process> getTable() {
		return table;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MASTER_PROCESSES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		throw new SQLException("Should not be read from the database, should be generated.");
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			process_id=in.readLong();
			connector_id=in.readLong();
			authenticated_user = InternUtils.intern(UserId.valueOf(in.readNullUTF()));
			effective_user = InternUtils.intern(UserId.valueOf(in.readNullUTF()));
			daemon_server=in.readCompressedInt();
			host=InetAddress.valueOf(in.readUTF()).intern();
			protocol=in.readUTF().intern();
			aoserv_protocol=InternUtils.intern(in.readNullUTF());
			is_secure=in.readBoolean();
			connect_time=in.readLong();
			use_count=in.readLong();
			total_time=in.readLong();
			priority=in.readCompressedInt();
			state=in.readUTF().intern();
			if(in.readBoolean()) {
				command=new Object[] {in.readUTF()};
			} else {
				command=null;
			}
			state_start_time=in.readLong();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	synchronized public void setCommand(Object ... command) {
		if(command==null) this.command=null;
		else {
			this.command=command;
		}
	}

	synchronized public String getCommand() {
		if(command==null) return null;
		StringBuilder SB=new StringBuilder();
		int len=command.length;
		for(int c=0;c<len;c++) {
			if(c>0) SB.append(' ');
			Object com=command[c];
			if(com==null) SB.append("''");
			else if(com instanceof Object[]) {
				Object[] oa=(Object[])com;
				int oaLen=oa.length;
				for(int d=0;d<oaLen;d++) {
					if(d>0) SB.append(' ');
					Object com2=oa[d];
					if(com2==null) SB.append("''");
					else SB.append(com2);
				}
			} else SB.append(com);
		}
		return SB.toString();
	}

	public void setAuthenticatedUser(UserId username) {
		authenticated_user = username;
	}

	public void setConnectorID(long id) {
		connector_id=id;
	}

	public void setDeamonServer(int server) {
		daemon_server=server;
	}

	public void setEffectiveUser(UserId username) {
		effective_user = username;
	}

	public void setPriority(int priority) {
		this.priority=priority;
	}

	@Override
	public void setTable(AOServTable<Long,Process> table) {
		if(this.table!=null) throw new IllegalStateException("table already set");
		this.table=table;
	}

	@Override
	 public String toStringImpl() {
		return getCommand();
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeLong(process_id);
		out.writeLong(connector_id);
		out.writeNullUTF(ObjectUtils.toString(authenticated_user));
		out.writeNullUTF(ObjectUtils.toString(effective_user));
		out.writeCompressedInt(daemon_server);
		out.writeUTF(host.toString());
		out.writeUTF(protocol);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_101)>=0) out.writeNullUTF(aoserv_protocol);
		out.writeBoolean(is_secure);
		out.writeLong(connect_time);
		out.writeLong(use_count);
		out.writeLong(total_time);
		out.writeCompressedInt(priority);
		out.writeUTF(state);
		String myCommand=getCommand();
		out.writeBoolean(myCommand!=null);
		if(myCommand!=null) out.writeUTF(myCommand);
		out.writeLong(state_start_time);
	}
}
