/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.account.User;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.InetAddress;
import com.aoindustries.security.Identifier;
import com.aoindustries.security.SmallIdentifier;
import com.aoindustries.sql.UnmodifiableTimestamp;
import com.aoindustries.util.InternUtils;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Each <code>Thread</code> on the master reports its activities so that
 * a query on this table shows a snapshot of the currently running system.
 *
 * @author  AO Industries, Inc.
 */
public class Process extends AOServObject<SmallIdentifier,Process> implements SingleTableObject<SmallIdentifier,Process> {

	static final int COLUMN_ID = 0;

	/**
	 * The different states a process may be in.
	 */
	public static final String
		LOGIN = "login",
		RUN = "run",
		SLEEP = "sleep"
	;

	protected SmallIdentifier id;
	protected Identifier connectorId;
	protected User.Name authenticated_user;
	protected User.Name effective_user;
	protected int daemon_server;
	protected InetAddress host;
	protected String protocol;
	protected String aoserv_protocol;
	protected boolean is_secure;
	protected UnmodifiableTimestamp connect_time;
	protected long use_count;
	protected long total_time;
	protected int priority;
	protected String state;
	private String[] command;
	protected UnmodifiableTimestamp state_start_time;

	private AOServTable<SmallIdentifier,Process> table;

	public Process() {
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_ID: return id;
			case 1: return connectorId;
			case 2: return authenticated_user;
			case 3: return effective_user;
			case 4: return daemon_server == -1 ? null : daemon_server;
			case 5: return host;
			case 6: return protocol;
			case 7: return aoserv_protocol;
			case 8: return is_secure;
			case 9: return connect_time;
			case 10: return use_count;
			case 11: return total_time;
			case 12: return priority;
			case 13: return state;
			case 14: return combineCommand(getCommand()); // TODO: Support arrays
			case 15: return state_start_time;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	public int getDaemonServer() {
		return daemon_server;
	}

	public int getPriority() {
		return priority;
	}

	public SmallIdentifier getId() {
		return id;
	}

	public Identifier getConnectorId() {
		return connectorId;
	}

	public User.Name getAuthenticatedAdministrator_username() {
		return authenticated_user;
	}

	public Administrator getAuthenticatedAdministrator() throws IOException, SQLException {
		// Null OK when filtered
		return table.getConnector().getAccount().getAdministrator().get(authenticated_user);
	}

	public User.Name getEffectiveAdministrator_username() {
		return effective_user;
	}

	public Administrator getEffectiveAdministrator() throws SQLException, IOException {
		Administrator obj = table.getConnector().getAccount().getAdministrator().get(effective_user);
		if(obj == null) throw new SQLException("Unable to find Administrator: " + effective_user);
		return obj;
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

	public boolean isSecure() {
		return is_secure;
	}

	public UnmodifiableTimestamp getConnectTime() {
		return connect_time;
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

	public UnmodifiableTimestamp getStateStartTime() {
		return state_start_time;
	}

	@Override
	public SmallIdentifier getKey() {
		return id;
	}

	@Override
	public AOServTable<SmallIdentifier,Process> getTable() {
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
	public void read(CompressedDataInputStream in, AoservProtocol.Version protocolVersion) throws IOException {
		try {
			id = in.readSmallIdentifier();
			connectorId = in.readNullIdentifier();
			authenticated_user = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			effective_user = InternUtils.intern(User.Name.valueOf(in.readNullUTF()));
			daemon_server = in.readCompressedInt();
			host = InetAddress.valueOf(in.readUTF()).intern();
			protocol = in.readUTF().intern();
			aoserv_protocol = InternUtils.intern(in.readNullUTF());
			is_secure = in.readBoolean();
			connect_time = in.readUnmodifiableTimestamp();
			use_count = in.readLong();
			total_time = in.readLong();
			priority = in.readCompressedInt();
			state = in.readUTF().intern();
			int len = in.readCompressedInt();
			if(len == -1) {
				command = null;
			} else {
				command = new String[len];
				for(int i = 0; i < len; i++) {
					command[i] = in.readNullUTF();
				}
			}
			state_start_time = in.readUnmodifiableTimestamp();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	public static String combineCommand(String[] command) {
		if(command == null) return null;
		StringBuilder SB = new StringBuilder();
		for(int c = 0, len = command.length; c < len; c++) {
			if(c > 0) SB.append(' ');
			String com = command[c];
			if(com == null) {
				SB.append("''");
			} else {
				SB.append(com);
			}
		}
		return SB.toString();
	}

	public String[] getCommand() {
		return command;
	}

	@Override
	public void setTable(AOServTable<SmallIdentifier,Process> table) {
		if(this.table != null) throw new IllegalStateException("table already set");
		this.table = table;
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		// Get values under lock before writing.  This is done here because
		// these objects are used on the master to track process states, and the objects can change at any time
		SmallIdentifier _id;
		Identifier _connectorId;
		User.Name _authenticated_user;
		User.Name _effective_user;
		int _daemon_server;
		InetAddress _host;
		String _protocol;
		String _aoserv_protocol;
		boolean _is_secure;
		UnmodifiableTimestamp _connect_time;
		long _use_count;
		long _total_time;
		int _priority;
		String _state;
		String[] _command;
		UnmodifiableTimestamp _state_start_time;
		synchronized(this) {
			_id = id;
			_connectorId = connectorId;
			_authenticated_user = authenticated_user;
			_effective_user = effective_user;
			_daemon_server = daemon_server;
			_host = host;
			_protocol = protocol;
			_aoserv_protocol = aoserv_protocol;
			_is_secure = is_secure;
			_connect_time = connect_time;
			_use_count = use_count;
			_total_time = total_time;
			_priority = priority;
			_state = state;
			_command = getCommand(); // Using method since subclass overrides this to generate command from master server-side objects
			_state_start_time = state_start_time;
		}
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(_id.getValue());
			// Old clients had a 64-bit ID, just send them the low-order bits
			out.writeLong(_connectorId == null ? -1 : _connectorId.getLo());
		} else {
			out.writeSmallIdentifier(_id);
			out.writeNullIdentifier(_connectorId);
		}
		out.writeNullUTF(Objects.toString(_authenticated_user, null));
		out.writeNullUTF(Objects.toString(_effective_user, null));
		out.writeCompressedInt(_daemon_server);
		out.writeUTF(_host.toString());
		out.writeUTF(_protocol);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_0_A_101) >= 0) {
			out.writeNullUTF(_aoserv_protocol);
		}
		out.writeBoolean(_is_secure);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeLong(_connect_time.getTime());
		} else {
			out.writeTimestamp(_connect_time);
		}
		out.writeLong(_use_count);
		out.writeLong(_total_time);
		out.writeCompressedInt(_priority);
		out.writeUTF(_state);
		if(protocolVersion.compareTo(AoservProtocol.Version.VERSION_1_83_0) < 0) {
			out.writeNullUTF(combineCommand(_command));
			out.writeLong(_state_start_time.getTime());
		} else {
			if(_command == null) {
				out.writeCompressedInt(-1);
			} else {
				int len = _command.length;
				out.writeCompressedInt(len);
				for(int i = 0; i < len; i++) {
					out.writeNullUTF(_command[i]);
				}
			}
			out.writeTimestamp(_state_start_time);
		}
	}
}
