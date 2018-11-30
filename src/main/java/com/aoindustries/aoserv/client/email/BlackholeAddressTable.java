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
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see BlackholeAddress
 *
 * @author  AO Industries, Inc.
 */
final public class BlackholeAddressTable extends CachedTableIntegerKey<BlackholeAddress> {

	public BlackholeAddressTable(AOServConnector connector) {
		super(connector, BlackholeAddress.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BlackholeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(BlackholeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(BlackholeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_ADDRESS_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public BlackholeAddress get(int address) throws IOException, SQLException {
		return getUniqueRow(BlackholeAddress.COLUMN_EMAIL_ADDRESS, address);
	}

	public List<BlackholeAddress> getBlackholeEmailAddresses(Server ao) throws IOException, SQLException {
		int aoPKey=ao.getPkey();
		List<BlackholeAddress> cached = getRows();
		int len = cached.size();
		List<BlackholeAddress> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			BlackholeAddress blackhole=cached.get(c);
			if(blackhole.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(blackhole);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.BLACKHOLE_EMAIL_ADDRESSES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.REMOVE_BLACKHOLE_EMAIL_ADDRESS)) {
			if(AOSH.checkParamCount(Command.REMOVE_BLACKHOLE_EMAIL_ADDRESS, args, 2, err)) {
				String addr=args[1];
				int pos=addr.indexOf('@');
				if(pos==-1) {
					err.print("aosh: "+Command.REMOVE_BLACKHOLE_EMAIL_ADDRESS+": invalid email address: ");
					err.println(addr);
					err.flush();
				} else {
					connector.getSimpleAOClient().removeBlackholeEmailAddress(
						addr.substring(0, pos),
						AOSH.parseDomainName(addr.substring(pos+1), "address"),
						args[2]
					);
				}
			}
			return true;
		}
		return false;
	}
}
