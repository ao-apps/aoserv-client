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
import com.aoindustries.aoserv.client.account.Account;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.net.Email;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An <code>Architecture</code> wraps all the data for a single supported
 * computer architecture.
 *
 * @author  AO Industries, Inc.
 */
final public class ForwardingTable extends CachedTableIntegerKey<Forwarding> {

	ForwardingTable(AOServConnector connector) {
		super(connector, Forwarding.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Forwarding.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(Forwarding.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(Forwarding.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_ADDRESS_name, ASCENDING),
		new OrderBy(Forwarding.COLUMN_DESTINATION_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addEmailForwarding(Address emailAddressObject, Email destination) throws IOException, SQLException {
		return connector.requestIntQueryIL(true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.EMAIL_FORWARDING,
			emailAddressObject.getPkey(),
			destination
		);
	}

	@Override
	public Forwarding get(int pkey) throws SQLException, IOException {
		return getUniqueRow(Forwarding.COLUMN_PKEY, pkey);
	}

	public List<Forwarding> getEmailForwarding(Account business) throws SQLException, IOException {
		List<Forwarding> cached = getRows();
		int len = cached.size();
		List<Forwarding> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			Forwarding forward = cached.get(c);
			if (forward
				.getEmailAddress()
				.getDomain()
				.getPackage()
				.getBusiness()
				.equals(business)
			) matches.add(forward);
		}
		return matches;
	}

	List<Forwarding> getEmailForwardings(Address ea) throws IOException, SQLException {
		return getIndexedRows(Forwarding.COLUMN_EMAIL_ADDRESS, ea.getPkey());
	}

	List<Forwarding> getEnabledEmailForwardings(Address ea) throws SQLException, IOException {
		if(!ea.getDomain().getPackage().isDisabled()) return getEmailForwardings(ea);
		else return Collections.emptyList();
	}

	Forwarding getEmailForwarding(Address ea, Email destination) throws IOException, SQLException {
		// Use index first
		List<Forwarding> cached=getEmailForwardings(ea);
		int len=cached.size();
		for (int c=0;c<len;c++) {
			Forwarding forward=cached.get(c);
			if(forward.destination.equals(destination.toString())) return forward;
		}
		return null;
	}

	public List<Forwarding> getEmailForwarding(Server ao) throws SQLException, IOException {
		int aoPKey=ao.getPkey();
		List<Forwarding> cached = getRows();
		int len = cached.size();
		List<Forwarding> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			Forwarding forward = cached.get(c);
			if (forward.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(forward);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_FORWARDING;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_EMAIL_FORWARDING)) {
			if(AOSH.checkMinParamCount(Command.ADD_EMAIL_FORWARDING, args, 3, err)) {
				if((args.length%3)!=1) {
					err.println("aosh: "+Command.ADD_EMAIL_FORWARDING+": must have multiple of three number of parameters");
					err.flush();
				} else {
					for(int c=1;c<args.length;c+=3) {
						String addr=args[c];
						int pos=addr.indexOf('@');
						if(pos==-1) {
							err.print("aosh: "+Command.ADD_EMAIL_FORWARDING+": invalid email address: ");
							err.println(addr);
							err.flush();
						} else {
							out.println(
								connector.getSimpleAOClient().addEmailForwarding(
									addr.substring(0, pos),
									AOSH.parseDomainName(addr.substring(pos+1), "address"),
									args[c+1],
									AOSH.parseEmail(args[c+2], "to_address")
								)
							);
							out.flush();
						}
					}
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_EMAIL_FORWARDING)) {
			if(AOSH.checkParamCount(Command.REMOVE_EMAIL_FORWARDING, args, 3, err)) {
				String addr=args[1];
				int pos=addr.indexOf('@');
				if(pos==-1) {
					err.print("aosh: "+Command.REMOVE_EMAIL_FORWARDING+": invalid email address: ");
					err.println(addr);
					err.flush();
				} else {
					connector.getSimpleAOClient().removeEmailForwarding(
						addr.substring(0, pos),
						AOSH.parseDomainName(addr.substring(pos+1), "domain"),
						args[2],
						AOSH.parseEmail(args[3], "destination")
					);
				}
			}
			return true;
		} else return false;
	}
}
