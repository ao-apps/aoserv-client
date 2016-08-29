/*
 * Copyright 2012, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

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

	IpReputationSetTable(AOServConnector connector) {
		super(connector, IpReputationSet.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(IpReputationSet.COLUMN_IDENTIFIER_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
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
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
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
