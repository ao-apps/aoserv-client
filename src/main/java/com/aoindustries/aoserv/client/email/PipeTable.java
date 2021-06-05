/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Pipe
 *
 * @author  AO Industries, Inc.
 */
final public class PipeTable extends CachedTableIntegerKey<Pipe> {

	PipeTable(AOServConnector connector) {
		super(connector, Pipe.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Pipe.COLUMN_COMMAND_name, ASCENDING),
		new OrderBy(Pipe.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addEmailPipe(Server ao, String command, Package packageObject) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.EMAIL_PIPES,
			ao.getPkey(),
			command,
			packageObject.getName()
		);
		return pkey;
	}

	@Override
	public Pipe get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Pipe.COLUMN_PKEY, pkey);
	}

	public List<Pipe> getEmailPipes(Package pack) throws IOException, SQLException {
		return getIndexedRows(Pipe.COLUMN_PACKAGE, pack.getName());
	}

	public List<Pipe> getEmailPipes(Server ao) throws IOException, SQLException {
		return getIndexedRows(Pipe.COLUMN_AO_SERVER, ao.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_PIPES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(Command.ADD_EMAIL_PIPE, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().addEmailPipe(
						args[1],
						args[2],
						AOSH.parseAccountingCode(args[3], "package")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.DISABLE_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(Command.DISABLE_EMAIL_PIPE, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableEmailPipe(
						AOSH.parseInt(args[1], "pkey"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.ENABLE_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(Command.ENABLE_EMAIL_PIPE, args, 1, err)) {
				connector.getSimpleAOClient().enableEmailPipe(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(Command.REMOVE_EMAIL_PIPE, args, 1, err)) {
				connector.getSimpleAOClient().removeEmailPipe(
					AOSH.parseInt(args[1], "pkey")
				);
			}
			return true;
		}
		return false;
	}
}
