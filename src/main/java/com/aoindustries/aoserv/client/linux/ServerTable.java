/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2003-2013, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.linux;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.io.WriterOutputStream;
import com.aoindustries.net.DomainName;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
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
		new OrderBy(Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	/**
	 * Supports both Integer (server) and DomainName (hostname) keys.
	 * @deprecated  Always try to lookup by specific keys; the compiler will help you more when types change.
	 */
	@Deprecated
	@Override
	public Server get(Object pkey) throws IOException, SQLException {
		if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
		else if(pkey instanceof DomainName) return get((DomainName)pkey);
		else throw new IllegalArgumentException("Must be an Integer or a DomainName");
	}

	@Override
	public Server get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Server.COLUMN_SERVER, pkey);
	}

	public Server get(DomainName hostname) throws IOException, SQLException {
		return getUniqueRow(Server.COLUMN_HOSTNAME, hostname);
	}

	public Server getAOServerByDaemonNetBind(Bind nb) throws IOException, SQLException {
		int bind_id=nb.getId();
		List<Server> servers=getRows();
		int size=servers.size();
		for(int c=0;c<size;c++) {
			Server se=servers.get(c);
			if(se.getDaemonBind_id() == bind_id) return se;
		}
		return null;
	}

	public Server getAOServerByJilterNetBind(Bind nb) throws IOException, SQLException {
		int bind_id=nb.getId();
		List<Server> servers=getRows();
		int size=servers.size();
		for(int c=0;c<size;c++) {
			Server se=servers.get(c);
			if(se.getJilterBind_id() == bind_id) return se;
		}
		return null;
	}

	/**
	 * @see  AOServer#getNestedAOServers()
	 */
	List<Server> getNestedAOServers(Server server) throws IOException, SQLException {
		int pkey=server.getPkey();
		List<Server> servers=getRows();
		int size=servers.size();
		List<Server> objs=new ArrayList<>();
		for(int c=0;c<size;c++) {
			Server se=servers.get(c);
			int fs = se.getFailoverServer_server_pkey();
			if(fs!=-1 && fs==pkey) objs.add(se);
		}
		return objs;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.AO_SERVERS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.GET_MRTG_FILE)) {
			if(AOSH.checkParamCount(Command.GET_MRTG_FILE, args, 2, err)) {
				connector.getSimpleAOClient().getMrtgFile(
					args[1],
					args[2],
					new WriterOutputStream(out)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GET_UPS_STATUS)) {
			if(AOSH.checkParamCount(Command.GET_UPS_STATUS, args, 1, err)) {
				out.write(connector.getSimpleAOClient().getUpsStatus(args[1]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.RESTART_APACHE)) {
			if(AOSH.checkParamCount(Command.RESTART_APACHE, args, 1, err)) {
				connector.getSimpleAOClient().restartApache(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.RESTART_CRON)) {
			if(AOSH.checkParamCount(Command.RESTART_CRON, args, 1, err)) {
				connector.getSimpleAOClient().restartCron(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.RESTART_XFS)) {
			if(AOSH.checkParamCount(Command.RESTART_XFS, args, 1, err)) {
				connector.getSimpleAOClient().restartXfs(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.RESTART_XVFB)) {
			if(AOSH.checkParamCount(Command.RESTART_XVFB, args, 1, err)) {
				connector.getSimpleAOClient().restartXvfb(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.START_APACHE)) {
			if(AOSH.checkParamCount(Command.START_APACHE, args, 1, err)) {
				connector.getSimpleAOClient().startApache(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.START_CRON)) {
			if(AOSH.checkParamCount(Command.START_CRON, args, 1, err)) {
				connector.getSimpleAOClient().startCron(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.START_XFS)) {
			if(AOSH.checkParamCount(Command.START_XFS, args, 1, err)) {
				connector.getSimpleAOClient().startXfs(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.START_XVFB)) {
			if(AOSH.checkParamCount(Command.START_XVFB, args, 1, err)) {
				connector.getSimpleAOClient().startXvfb(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.STOP_APACHE)) {
			if(AOSH.checkParamCount(Command.STOP_APACHE, args, 1, err)) {
				connector.getSimpleAOClient().stopApache(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.STOP_CRON)) {
			if(AOSH.checkParamCount(Command.STOP_CRON, args, 1, err)) {
				connector.getSimpleAOClient().stopCron(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.STOP_XFS)) {
			if(AOSH.checkParamCount(Command.STOP_XFS, args, 1, err)) {
				connector.getSimpleAOClient().stopXfs(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.STOP_XVFB)) {
			if(AOSH.checkParamCount(Command.STOP_XVFB, args, 1, err)) {
				connector.getSimpleAOClient().stopXvfb(
					args[1]
				);
			}
			return true;
		}
		return false;
	}
}
