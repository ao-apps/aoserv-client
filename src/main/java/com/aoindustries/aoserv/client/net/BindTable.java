/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoindustries.aoserv.client.net;

import com.aoapps.collections.AoCollections;
import com.aoapps.collections.IntList;
import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.net.Port;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;

/**
 * @see  Bind
 *
 * @author  AO Industries, Inc.
 */
public final class BindTable extends CachedTableIntegerKey<Bind> {

	BindTable(AOServConnector connector) {
		super(connector, Bind.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(Bind.COLUMN_PORT_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addNetBind(
		final Host se,
		final Package pk,
		final IpAddress ia,
		final Port port,
		final AppProtocol appProtocol,
		final boolean monitoringEnabled,
		final Set<FirewallZone.Name> firewalldZones
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AoservProtocol.CommandID.ADD,
			// Java 9: new AOServConnector.ResultRequest<>
			new AOServConnector.ResultRequest<Integer>() {
				private int pkey;
				private IntList invalidateList;

				@Override
				public void writeRequest(StreamableOutput out) throws IOException {
					out.writeCompressedInt(Table.TableID.NET_BINDS.ordinal());
					out.writeCompressedInt(se.getPkey());
					out.writeUTF(pk.getName().toString());
					out.writeCompressedInt(ia.getId());
					out.writeCompressedInt(port.getPort());
					out.writeEnum(port.getProtocol());
					out.writeUTF(appProtocol.getProtocol());
					out.writeBoolean(monitoringEnabled);
					int size = firewalldZones.size();
					out.writeCompressedInt(size);
					int count = 0;
					for(FirewallZone.Name firewalldZone : firewalldZones) {
						out.writeUTF(firewalldZone.toString());
						count++;
					}
					if(size != count) throw new ConcurrentModificationException();
				}

				@Override
				public void readResponse(StreamableInput in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AoservProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AoservProtocol.checkResult(code, in);
						throw new IOException("Unexpected response code: "+code);
					}
				}

				@Override
				public Integer afterRelease() {
					connector.tablesUpdated(invalidateList);
					return pkey;
				}
			}
		);
	}

	@Override
	public Bind get(int id) throws IOException, SQLException {
		return getUniqueRow(Bind.COLUMN_ID, id);
	}

	List<Bind> getNetBinds(IpAddress ia) throws IOException, SQLException {
		return getIndexedRows(Bind.COLUMN_IP_ADDRESS, ia.getPkey());
	}

	public List<Bind> getNetBinds(Package pk) throws IOException, SQLException {
		return getIndexedRows(Bind.COLUMN_PACKAGE, pk.getName());
	}

	public List<Bind> getNetBinds(Package pk, IpAddress ip) throws IOException, SQLException {
		Account.Name packageName=pk.getName();
		// Use the index first
		List<Bind> cached=getNetBinds(ip);
		int size=cached.size();
		List<Bind> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			Bind nb=cached.get(c);
			if(nb.getPackage_name().equals(packageName)) matches.add(nb);
		}
		return matches;
	}

	List<Bind> getNetBinds(Host se) throws IOException, SQLException {
		return getIndexedRows(Bind.COLUMN_SERVER, se.getPkey());
	}

	List<Bind> getNetBinds(Host se, IpAddress ip) throws IOException, SQLException {
		int ipAddress=ip.getPkey();

		// Use the index first
		List<Bind> cached=getNetBinds(se);
		int size=cached.size();
		List<Bind> matches=new ArrayList<>(size);
		for(Bind nb : cached) {
			if(nb.getIpAddress_id()==ipAddress) matches.add(nb);
		}
		return matches;
	}

	Bind getNetBind(
		Host se,
		IpAddress ip,
		Port port
	) throws IOException, SQLException {
		int sePKey=se.getPkey();

		// Use the index first
		List<Bind> cached=getNetBinds(ip);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			Bind nb=cached.get(c);
			if(
				nb.getServer_pkey()==sePKey
				&& nb.getPort()==port
			) return nb;
		}
		return null;
	}

	List<Bind> getNetBinds(Host se, AppProtocol protocol) throws IOException, SQLException {
		String prot=protocol.getProtocol();

		// Use the index first
		List<Bind> cached=getNetBinds(se);
		int size=cached.size();
		List<Bind> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			Bind nb=cached.get(c);
			if(nb.getAppProtocol_protocol().equals(prot)) matches.add(nb);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.NET_BINDS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_NET_BIND)) {
			if(AOSH.checkMinParamCount(Command.ADD_NET_BIND, args, 8, err)) {
				final int varargStart = 9;
				Set<FirewallZone.Name> firewalldZones = AoCollections.newLinkedHashSet(args.length - varargStart);
				for(int i = varargStart; i < args.length; i++) {
					FirewallZone.Name name = AOSH.parseFirewalldZoneName(args[i], "firewalld_zone[" + (i - varargStart) + "]");
					if(!firewalldZones.add(name)) {
						throw new IllegalArgumentException("Duplicate firewalld zone name: " + name);
					}
				}
				out.println(
					connector.getSimpleAOClient().addNetBind(
						args[1],
						AOSH.parseAccountingCode(args[2], "package"),
						AOSH.parseInetAddress(args[3], "ip_address"),
						args[4],
						AOSH.parsePort(
							args[5], "port",
							args[6], "net_protocol"
						),
						args[7],
						AOSH.parseBoolean(args[8], "monitoring_enabled"),
						firewalldZones
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_NET_BIND)) {
			if(AOSH.checkParamCount(Command.REMOVE_NET_BIND, args, 1, err)) {
				connector.getSimpleAOClient().removeNetBind(
					AOSH.parseInt(args[1], "pkey")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_NET_BIND_FIREWALLD_ZONES)) {
			if(AOSH.checkMinParamCount(Command.SET_NET_BIND_FIREWALLD_ZONES, args, 1, err)) {
				final int varargStart = 2;
				Set<FirewallZone.Name> firewalldZones = AoCollections.newLinkedHashSet(args.length - varargStart);
				for(int i = varargStart; i < args.length; i++) {
					FirewallZone.Name name = AOSH.parseFirewalldZoneName(args[i], "firewalld_zone[" + (i - varargStart) + "]");
					if(!firewalldZones.add(name)) {
						throw new IllegalArgumentException("Duplicate firewalld zone name: " + name);
					}
				}
				connector.getSimpleAOClient().setNetBindFirewalldZones(
					AOSH.parseInt(args[1], "pkey"),
					firewalldZones
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_NET_BIND_MONITORING_ENABLED)) {
			if(AOSH.checkParamCount(Command.SET_NET_BIND_MONITORING_ENABLED, args, 2, err)) {
				connector.getSimpleAOClient().setNetBindMonitoringEnabled(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseBoolean(args[2], "enabled")
				);
			}
			return true;
		}
		return false;
	}
}
