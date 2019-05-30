/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.SimpleAOClient;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  MajordomoList
 *
 * @author  AO Industries, Inc.
 */
final public class MajordomoListTable extends CachedTableIntegerKey<MajordomoList> {

	MajordomoListTable(AOServConnector connector) {
		super(connector, MajordomoList.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MajordomoList.COLUMN_MAJORDOMO_SERVER_name+'.'+MajordomoServer.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(MajordomoList.COLUMN_MAJORDOMO_SERVER_name+'.'+MajordomoServer.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(MajordomoList.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addMajordomoList(
		MajordomoServer majordomoServer,
		String listName
	) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.MAJORDOMO_LISTS,
			majordomoServer.getPkey(),
			listName
		);
	}

	@Override
	public MajordomoList get(int pkey) throws IOException, SQLException {
		return getUniqueRow(MajordomoList.COLUMN_EMAIL_LIST, pkey);
	}

	MajordomoList getMajordomoList(MajordomoServer ms, String listName) throws IOException, SQLException {
		int majordomo_server=ms.getPkey();
		List<MajordomoList> mls=getRows();
		int len=mls.size();
		for(int c=0;c<len;c++) {
			MajordomoList ml=mls.get(c);
			if(
				ml.majordomo_server==majordomo_server
				&& ml.name.equals(listName)
			) return ml;
		}
		return null;
	}

	List<MajordomoList> getMajordomoLists(MajordomoServer server) throws IOException, SQLException {
		return getIndexedRows(MajordomoList.COLUMN_MAJORDOMO_SERVER, server.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.MAJORDOMO_LISTS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_MAJORDOMO_LIST)) {
			if(AOSH.checkParamCount(Command.ADD_MAJORDOMO_LIST, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().addMajordomoList(
						AOSH.parseDomainName(args[1], "domain"),
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.CHECK_MAJORDOMO_LIST_NAME)) {
			if(AOSH.checkParamCount(Command.CHECK_MAJORDOMO_LIST_NAME, args, 1, err)) {
				try {
					SimpleAOClient.checkMajordomoListName(args[1]);
					out.println("true");
				} catch(IllegalArgumentException iae) {
					out.print("aosh: "+Command.CHECK_MAJORDOMO_LIST_NAME+": ");
					out.println(iae.getMessage());
				}
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GET_MAJORDOMO_INFO_FILE)) {
			if(AOSH.checkParamCount(Command.GET_MAJORDOMO_INFO_FILE, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().getMajordomoInfoFile(
						AOSH.parseDomainName(args[1], "domain"),
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.GET_MAJORDOMO_INTRO_FILE)) {
			if(AOSH.checkParamCount(Command.GET_MAJORDOMO_INTRO_FILE, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().getMajordomoIntroFile(
						AOSH.parseDomainName(args[1], "domain"),
						args[2],
						args[3]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_MAJORDOMO_INFO_FILE)) {
			if(AOSH.checkParamCount(Command.SET_MAJORDOMO_INFO_FILE, args, 4, err)) {
				connector.getSimpleAOClient().setMajordomoInfoFile(
					AOSH.parseDomainName(args[1], "domain"),
					args[2],
					args[3],
					args[4]
				);
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.SET_MAJORDOMO_INTRO_FILE)) {
			if(AOSH.checkParamCount(Command.SET_MAJORDOMO_INTRO_FILE, args, 4, err)) {
				connector.getSimpleAOClient().setMajordomoIntroFile(
					AOSH.parseDomainName(args[1], "domain"),
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
