/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.net.Port;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class NetBindTable extends CachedTableIntegerKey<NetBind> {

	NetBindTable(AOServConnector connector) {
		super(connector, NetBind.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(NetBind.COLUMN_PORT_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addNetBind(
		final Server se,
		final Package pk,
		final IPAddress ia,
		final Port port,
		final Protocol appProtocol,
		final boolean openFirewall,
		final boolean monitoringEnabled
	) throws IOException, SQLException {
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeCompressedInt(SchemaTable.TableID.NET_BINDS.ordinal());
					out.writeCompressedInt(se.pkey);
					out.writeUTF(pk.name.toString());
					out.writeCompressedInt(ia.pkey);
					out.writeCompressedInt(port.getPort());
					out.writeEnum(port.getProtocol());
					out.writeUTF(appProtocol.pkey);
					out.writeBoolean(openFirewall);
					out.writeBoolean(monitoringEnabled);
				}

				@Override
				public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
					int code=in.readByte();
					if(code==AOServProtocol.DONE) {
						pkey=in.readCompressedInt();
						invalidateList=AOServConnector.readInvalidateList(in);
					} else {
						AOServProtocol.checkResult(code, in);
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
	public NetBind get(int pkey) throws IOException, SQLException {
		return getUniqueRow(NetBind.COLUMN_PKEY, pkey);
	}

	List<NetBind> getNetBinds(IPAddress ia) throws IOException, SQLException {
		return getIndexedRows(NetBind.COLUMN_IP_ADDRESS, ia.pkey);
	}

	List<NetBind> getNetBinds(Package pk) throws IOException, SQLException {
		return getIndexedRows(NetBind.COLUMN_PACKAGE, pk.name);
	}

	List<NetBind> getNetBinds(Package pk, IPAddress ip) throws IOException, SQLException {
		AccountingCode packageName=pk.name;
		// Use the index first
		List<NetBind> cached=getNetBinds(ip);
		int size=cached.size();
		List<NetBind> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			NetBind nb=cached.get(c);
			if(nb.packageName.equals(packageName)) matches.add(nb);
		}
		return matches;
	}

	List<NetBind> getNetBinds(Server se) throws IOException, SQLException {
		return getIndexedRows(NetBind.COLUMN_SERVER, se.pkey);
	}

	List<NetBind> getNetBinds(Server se, IPAddress ip) throws IOException, SQLException {
		int ipAddress=ip.pkey;

		// Use the index first
		List<NetBind> cached=getNetBinds(se);
		int size=cached.size();
		List<NetBind> matches=new ArrayList<>(size);
		for(NetBind nb : cached) {
			if(nb.ip_address==ipAddress) matches.add(nb);
		}
		return matches;
	}

	NetBind getNetBind(
		Server se,
		IPAddress ip,
		Port port
	) throws IOException, SQLException {
		int sePKey=se.pkey;

		// Use the index first
		List<NetBind> cached=getNetBinds(ip);
		int size=cached.size();
		for(int c=0;c<size;c++) {
			NetBind nb=cached.get(c);
			if(
				nb.server==sePKey
				&& nb.port==port
			) return nb;
		}
		return null;
	}

	List<NetBind> getNetBinds(Server se, Protocol protocol) throws IOException, SQLException {
		String prot=protocol.pkey;

		// Use the index first
		List<NetBind> cached=getNetBinds(se);
		int size=cached.size();
		List<NetBind> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			NetBind nb=cached.get(c);
			if(nb.app_protocol.equals(prot)) matches.add(nb);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.NET_BINDS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_NET_BIND)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_NET_BIND, args, 9, err)) {
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
					AOSH.parseBoolean(args[8], "open_firewall"),
					AOSH.parseBoolean(args[9], "monitoring_enabled")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_NET_BIND)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_NET_BIND, args, 1, err)) {
				connector.getSimpleAOClient().removeNetBind(
					AOSH.parseInt(args[1], "pkey")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_NET_BIND_MONITORING_ENABLED)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_NET_BIND_MONITORING_ENABLED, args, 2, err)) {
				connector.getSimpleAOClient().setNetBindMonitoringEnabled(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseBoolean(args[2], "enabled")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_NET_BIND_OPEN_FIREWALL)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_NET_BIND_OPEN_FIREWALL, args, 2, err)) {
				connector.getSimpleAOClient().setNetBindOpenFirewall(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseBoolean(args[2], "open_firewall")
				);
			}
			return true;
		}
		return false;
	}
}
