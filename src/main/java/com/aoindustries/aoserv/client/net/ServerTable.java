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
package com.aoindustries.aoserv.client.net;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.distribution.OperatingSystemVersion;
import com.aoindustries.aoserv.client.infrastructure.ServerFarm;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.aoserv.client.validator.UserId;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.net.DomainName;
import com.aoindustries.util.IntList;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
final public class ServerTable extends CachedTableIntegerKey<Server> {

	public ServerTable(AOServConnector connector) {
		super(connector, Server.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(Server.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addBackupServer(
		final String hostname,
		final ServerFarm farm,
		final Package owner,
		final String description,
		final int backup_hour,
		final OperatingSystemVersion os_version,
		final UserId username,
		final String password,
		final String contact_phone,
		final String contact_email
	) throws IOException, SQLException {
		// Create the new profile
		return connector.requestResult(
			true,
			AOServProtocol.CommandID.ADD_BACKUP_SERVER,
			new AOServConnector.ResultRequest<Integer>() {
				int pkey;
				IntList invalidateList;

				@Override
				public void writeRequest(CompressedDataOutputStream out) throws IOException {
					out.writeUTF(hostname);
					out.writeUTF(farm.getName());
					out.writeCompressedInt(owner.getPkey());
					out.writeUTF(description);
					out.writeCompressedInt(backup_hour);
					out.writeCompressedInt(os_version.getPkey());
					out.writeUTF(username.toString());
					out.writeUTF(password);
					out.writeUTF(contact_phone);
					out.writeUTF(contact_email);
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

	/**
	 * Supports both Integer (pkey) and String (server) keys.
	 *
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public Server get(Object pkey) throws IOException, SQLException {
		if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
		else if(pkey instanceof String) return get((String)pkey);
		else throw new IllegalArgumentException("Must be an Integer or a String");
	}

	/**
	 * Gets a <code>Server</code> based on its hostname, package/name, or pkey.
	 * If unambiguous, just "name" may be used without the package.
	 * This is compatible with the output of <code>Server.toString()</code>.
	 * Accepts either a hostname (for ao_servers), package/name, or pkey.
	 *
	 * @return  the <code>Server</code> or <code>null</code> if not found
	 *
	 * @see  Server#toString
	 */
	public Server get(String server) throws SQLException, IOException {
		// Is it the exact hostname of an ao_server?
		try {
			AOServer aoServer = DomainName.validate(server).isValid()
				? connector.getAoServers().get(DomainName.valueOf(server))
				: null;
			if(aoServer != null) return aoServer.getServer();
		} catch(ValidationException e) {
			throw new AssertionError("Already validated", e);
		}

		// Look for matching server name (but only one server)
		Server match = null;
		int numMatches = 0;
		for(Server se : connector.getServers().getRows()) {
			if(server.equals(se.getName())) {
				match = se;
				numMatches++;
			}
		}
		if(match!=null && numMatches==1) return match;

		// Is if a package/name combo?
		int slashPos = server.indexOf('/');
		if(slashPos!=-1) {
			String packageName = server.substring(0, slashPos);
			if(AccountingCode.validate(packageName).isValid()) {
				try {
					String name = server.substring(slashPos+1);
					Package pk = connector.getPackages().get(AccountingCode.valueOf(packageName));
					if(pk==null) return null;
					return pk.getServer(name);
				} catch(ValidationException e) {
					throw new AssertionError("Already validated", e);
				}
			}
		}

		// Is it an exact server pkey
		try {
			int pkey = Integer.parseInt(server);
			return connector.getServers().get(pkey);
		} catch(NumberFormatException err) {
			return null;
		}
	}

	@Override
	public Server get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Server.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SERVERS;
	}

	public Server getServer(Package pk, String name) throws IOException, SQLException {
		// Use index first
		for(Server se : getServers(pk)) if(se.getName().equals(name)) return se;
		return null;
	}

	public List<Server> getServers(Package pk) throws IOException, SQLException {
		return getIndexedRows(Server.COLUMN_PACKAGE, pk.getPkey());
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_BACKUP_SERVER)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_BACKUP_SERVER, args, 12, err)) {
				out.println(
					connector.getSimpleAOClient().addBackupServer(
						args[1],
						args[2],
						AOSH.parseAccountingCode(args[3], "owner"),
						args[4],
						AOSH.parseInt(args[5], "backup_hour"),
						args[6],
						args[7],
						args[8],
						AOSH.parseUserId(args[9], "username"),
						args[10],
						args[11],
						args[12]
					)
				);
				out.flush();
			}
			return true;
		}
		return false;
	}
}
