/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  IPAddress
 *
 * @author  AO Industries, Inc.
 */
final public class IPAddressTable extends CachedTableIntegerKey<IPAddress> {

	IPAddressTable(AOServConnector connector) {
		super(connector, IPAddress.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public IPAddress get(int pkey) throws IOException, SQLException {
		return getUniqueRow(IPAddress.COLUMN_PKEY, pkey);
	}

	IPAddress getIPAddress(NetDevice device, InetAddress ipAddress) throws IOException, SQLException {
		int pkey=device.getPkey();

		List<IPAddress> cached = getRows();
		int len = cached.size();
		for (int c = 0; c < len; c++) {
			IPAddress address=cached.get(c);
			if(
				address.net_device==pkey
				&& address.ip_address.equals(ipAddress)
			) return address;
		}
		return null;
	}

	List<IPAddress> getIPAddresses(NetDevice device) throws IOException, SQLException {
		return getIndexedRows(IPAddress.COLUMN_NET_DEVICE, device.pkey);
	}

	public List<IPAddress> getIPAddresses(InetAddress ipAddress) throws IOException, SQLException {
		List<IPAddress> cached = getRows();
		int len = cached.size();
		List<IPAddress> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			IPAddress address=cached.get(c);
			if(address.ip_address.equals(ipAddress)) matches.add(address);
		}
		return matches;
	}

	List<IPAddress> getIPAddresses(Package pack) throws IOException, SQLException {
		return getIndexedRows(IPAddress.COLUMN_PACKAGE, pack.name);
	}

	List<IPAddress> getIPAddresses(Server se) throws IOException, SQLException {
		int sePKey=se.pkey;

		List<IPAddress> cached = getRows();
		int len = cached.size();
		List<IPAddress> matches=new ArrayList<>(len);
		for(IPAddress address : cached) {
			if(address.net_device==-1 && address.ip_address.isUnspecified()) matches.add(address);
			else {
				NetDevice netDevice = address.getNetDevice();
				if(netDevice!=null && netDevice.server==sePKey) matches.add(address);
			}
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.IP_ADDRESSES;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.CHECK_IP_ADDRESS)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_IP_ADDRESS, args, 1, err)) {
				try {
					SimpleAOClient.checkIPAddress(args[1]);
					out.println("true");
				} catch(IllegalArgumentException iae) {
					out.print("aosh: "+AOSHCommand.CHECK_IP_ADDRESS+": ");
					out.println(iae.getMessage());
				}
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_IP_ADDRESS_USED)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_IP_ADDRESS_USED, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().isIPAddressUsed(
						AOSH.parseInetAddress(args[1], "ip_address"),
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.MOVE_IP_ADDRESS)) {
			if(AOSH.checkParamCount(AOSHCommand.MOVE_IP_ADDRESS, args, 4, err)) {
				connector.getSimpleAOClient().moveIPAddress(
					AOSH.parseInetAddress(args[1], "ip_address"),
					args[2],
					args[3],
					args[4]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_IP_ADDRESS_DHCP_ADDRESS)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_IP_ADDRESS_DHCP_ADDRESS, args, 2, err)) {
				connector.getSimpleAOClient().setIPAddressDHCPAddress(
					AOSH.parseInt(args[1], "pkey"),
					AOSH.parseInetAddress(args[2], "ip_address")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_IP_ADDRESS_HOSTNAME)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_IP_ADDRESS_HOSTNAME, args, 4, err)) {
				connector.getSimpleAOClient().setIPAddressHostname(
					AOSH.parseInetAddress(args[1], "ip_address"),
					args[2],
					args[3],
					AOSH.parseDomainName(args[4], "hostname")
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.SET_IP_ADDRESS_PACKAGE)) {
			if(AOSH.checkParamCount(AOSHCommand.SET_IP_ADDRESS_PACKAGE, args, 4, err)) {
				connector.getSimpleAOClient().setIPAddressPackage(
					AOSH.parseInetAddress(args[1], "ip_address"),
					args[2],
					args[3],
					args[4]
				);
			}
			return true;
		}
		return false;
	}
}
