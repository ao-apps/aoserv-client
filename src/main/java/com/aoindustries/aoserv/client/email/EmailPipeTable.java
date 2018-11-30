/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2012, 2016, 2017, 2018  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.schema.AOServProtocol;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  EmailPipe
 *
 * @author  AO Industries, Inc.
 */
final public class EmailPipeTable extends CachedTableIntegerKey<EmailPipe> {

	public EmailPipeTable(AOServConnector connector) {
		super(connector, EmailPipe.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(EmailPipe.COLUMN_COMMAND_name, ASCENDING),
		new OrderBy(EmailPipe.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addEmailPipe(AOServer ao, String command, Package packageObject) throws IOException, SQLException {
		int pkey=connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.EMAIL_PIPES,
			ao.getPkey(),
			command,
			packageObject.getName()
		);
		return pkey;
	}

	@Override
	public EmailPipe get(int pkey) throws IOException, SQLException {
		return getUniqueRow(EmailPipe.COLUMN_PKEY, pkey);
	}

	public List<EmailPipe> getEmailPipes(Package pack) throws IOException, SQLException {
		return getIndexedRows(EmailPipe.COLUMN_PACKAGE, pack.getName());
	}

	public List<EmailPipe> getEmailPipes(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(EmailPipe.COLUMN_AO_SERVER, ao.getPkey());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_PIPES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_PIPE, args, 3, err)) {
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
		} else if(command.equalsIgnoreCase(AOSHCommand.DISABLE_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(AOSHCommand.DISABLE_EMAIL_PIPE, args, 2, err)) {
				out.println(
					connector.getSimpleAOClient().disableEmailPipe(
						AOSH.parseInt(args[1], "pkey"),
						args[2]
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.ENABLE_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(AOSHCommand.ENABLE_EMAIL_PIPE, args, 1, err)) {
				connector.getSimpleAOClient().enableEmailPipe(AOSH.parseInt(args[1], "pkey"));
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_PIPE)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_PIPE, args, 1, err)) {
				connector.getSimpleAOClient().removeEmailPipe(
					AOSH.parseInt(args[1], "pkey")
				);
			}
			return true;
		}
		return false;
	}
}
