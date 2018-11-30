/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2012, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.net.reputation;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.AOSHCommand;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

/**
 * @see  IpReputationSet
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationSetTable extends CachedTableIntegerKey<IpReputationSet> {

	public IpReputationSetTable(AOServConnector connector) {
		super(connector, IpReputationSet.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(IpReputationSet.COLUMN_IDENTIFIER_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public IpReputationSet get(int pkey) throws IOException, SQLException {
		return getUniqueRow(IpReputationSet.COLUMN_PKEY, pkey);
	}

	public IpReputationSet get(String identifier) throws IOException, SQLException {
		return getUniqueRow(IpReputationSet.COLUMN_IDENTIFIER, identifier);
	}

	/*
	List<IpReputationSet> getIpReputationSets(Business bu) throws IOException, SQLException {
		return getIndexedRows(IpReputationSet.COLUMN_ACCOUNTING, bu.getAccounting());
	}
	 */

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.IP_REPUTATION_SETS;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_IP_REPUTATION)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_IP_REPUTATION, args, 5, err)) {
				connector.getSimpleAOClient().addIpReputation(
					args[1],
					args[2],
					args[3],
					args[4],
					AOSH.parseShort(args[5], "score")
				);
			}
			return true;
		} else {
			return false;
		}
	}
}
