/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2003-2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.io.TerminalWriter;
import com.aoindustries.io.WriterOutputStream;
import com.aoindustries.net.DomainName;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  AOServer
 *
 * @author  AO Industries, Inc.
 */
final public class AOServerTable extends CachedTableIntegerKey<AOServer> {

	AOServerTable(AOServConnector connector) {
		super(connector, AOServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	/**
	 * Supports both Integer (server) and DomainName (hostname) keys.
	 */
	@Override
	public AOServer get(Object pkey) throws IOException, SQLException {
		if(pkey instanceof Integer) return get(((Integer)pkey).intValue());
		else if(pkey instanceof DomainName) return get((DomainName)pkey);
		else throw new IllegalArgumentException("Must be an Integer or a DomainName");
	}

	@Override
	public AOServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(AOServer.COLUMN_SERVER, pkey);
	}

	public AOServer get(DomainName hostname) throws IOException, SQLException {
		return getUniqueRow(AOServer.COLUMN_HOSTNAME, hostname);
	}

	AOServer getAOServerByDaemonNetBind(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;
		List<AOServer> servers=getRows();
		int size=servers.size();
		for(int c=0;c<size;c++) {
			AOServer se=servers.get(c);
			if(se.daemon_bind==pkey) return se;
		}
		return null;
	}

	AOServer getAOServerByJilterNetBind(NetBind nb) throws IOException, SQLException {
		int pkey=nb.pkey;
		List<AOServer> servers=getRows();
		int size=servers.size();
		for(int c=0;c<size;c++) {
			AOServer se=servers.get(c);
			if(se.jilter_bind==pkey) return se;
		}
		return null;
	}

	/**
	 * @see  AOServer#getNestedAOServers()
	 */
	List<AOServer> getNestedAOServers(AOServer server) throws IOException, SQLException {
		int pkey=server.pkey;
		List<AOServer> servers=getRows();
		int size=servers.size();
		List<AOServer> objs=new ArrayList<>();
		for(int c=0;c<size;c++) {
			AOServer se=servers.get(c);
			int fs=se.failover_server;
			if(fs!=-1 && fs==pkey) objs.add(se);
		}
		return objs;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.AO_SERVERS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.GET_MRTG_FILE)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_MRTG_FILE, args, 2, err)) {
				connector.getSimpleAOClient().getMrtgFile(
					args[1],
					args[2],
					new WriterOutputStream(out)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.GET_UPS_STATUS)) {
			if(AOSH.checkParamCount(AOSHCommand.GET_UPS_STATUS, args, 1, err)) {
				out.write(connector.getSimpleAOClient().getUpsStatus(args[1]));
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_APACHE)) {
			if(AOSH.checkParamCount(AOSHCommand.RESTART_APACHE, args, 1, err)) {
				connector.getSimpleAOClient().restartApache(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_CRON)) {
			if(AOSH.checkParamCount(AOSHCommand.RESTART_CRON, args, 1, err)) {
				connector.getSimpleAOClient().restartCron(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_XFS)) {
			if(AOSH.checkParamCount(AOSHCommand.RESTART_XFS, args, 1, err)) {
				connector.getSimpleAOClient().restartXfs(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.RESTART_XVFB)) {
			if(AOSH.checkParamCount(AOSHCommand.RESTART_XVFB, args, 1, err)) {
				connector.getSimpleAOClient().restartXvfb(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.START_APACHE)) {
			if(AOSH.checkParamCount(AOSHCommand.START_APACHE, args, 1, err)) {
				connector.getSimpleAOClient().startApache(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.START_CRON)) {
			if(AOSH.checkParamCount(AOSHCommand.START_CRON, args, 1, err)) {
				connector.getSimpleAOClient().startCron(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.START_XFS)) {
			if(AOSH.checkParamCount(AOSHCommand.START_XFS, args, 1, err)) {
				connector.getSimpleAOClient().startXfs(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.START_XVFB)) {
			if(AOSH.checkParamCount(AOSHCommand.START_XVFB, args, 1, err)) {
				connector.getSimpleAOClient().startXvfb(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.STOP_APACHE)) {
			if(AOSH.checkParamCount(AOSHCommand.STOP_APACHE, args, 1, err)) {
				connector.getSimpleAOClient().stopApache(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.STOP_CRON)) {
			if(AOSH.checkParamCount(AOSHCommand.STOP_CRON, args, 1, err)) {
				connector.getSimpleAOClient().stopCron(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.STOP_XFS)) {
			if(AOSH.checkParamCount(AOSHCommand.STOP_XFS, args, 1, err)) {
				connector.getSimpleAOClient().stopXfs(
					args[1]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.STOP_XVFB)) {
			if(AOSH.checkParamCount(AOSHCommand.STOP_XVFB, args, 1, err)) {
				connector.getSimpleAOClient().stopXvfb(
					args[1]
				);
			}
			return true;
		}
		return false;
	}
}
