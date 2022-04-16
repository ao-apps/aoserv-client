/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with aoserv-client.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoindustries.aoserv.client.email;

import com.aoapps.hodgepodge.io.TerminalWriter;
import com.aoapps.lang.validation.ValidationResult;
import com.aoapps.net.Email;
import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.aosh.AOSH;
import com.aoindustries.aoserv.client.aosh.Command;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  Address
 *
 * @author  AO Industries, Inc.
 */
public final class AddressTable extends CachedTableIntegerKey<Address> {

	AddressTable(AOServConnector connector) {
		super(connector, Address.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(Address.COLUMN_ADDRESS_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	public int addEmailAddress(String address, Domain domainObject) throws SQLException, IOException {
		ValidationResult result = Email.validate(address, domainObject.getDomain());
		if (!result.isValid()) throw new SQLException("Invalid email address: " + result.toString());
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.EMAIL_ADDRESSES,
			address,
			domainObject.getPkey()
		);
	}

	@Override
	public Address get(int pkey) throws IOException, SQLException {
		return getUniqueRow(Address.COLUMN_PKEY, pkey);
	}

	public Address getEmailAddress(String address, Domain domain) throws IOException, SQLException {
		// Uses index on domain first, then searched on address
		for(Address emailAddress : domain.getEmailAddresses()) {
			if(emailAddress.getAddress().equals(address)) return emailAddress;
		}
		return null;
	}

	List<Address> getEmailAddresses(Domain domain) throws IOException, SQLException {
		return getIndexedRows(Address.COLUMN_DOMAIN, domain.getPkey());
	}

	public List<Address> getEmailAddresses(Server ao) throws IOException, SQLException {
		int aoPKey=ao.getPkey();
		List<Address> addresses = getRows();
		int len = addresses.size();
		List<Address> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			Address address = addresses.get(c);
			if (address.getDomain().getLinuxServer_host_id() == aoPKey) matches.add(address);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_ADDRESSES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.CHECK_EMAIL_ADDRESS)) {
			if(AOSH.checkMinParamCount(Command.CHECK_EMAIL_ADDRESS, args, 1, err)) {
				for(int c=1;c<args.length;c++) {
					String addr=args[c];
					ValidationResult validationResult = Email.validate(addr);
					if(args.length>2) {
						out.print(addr);
						out.print(": ");
					}
					out.println(validationResult.isValid());
					out.flush();
					if(!validationResult.isValid()) {
						err.print("aosh: "+Command.CHECK_EMAIL_ADDRESS+": invalid email address: ");
						if(args.length>2) {
							err.print(addr);
							err.print(": ");
						}
						err.println(validationResult.toString());
						err.flush();
					}
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_EMAIL_ADDRESS)) {
			if(AOSH.checkMinParamCount(Command.REMOVE_EMAIL_ADDRESS, args, 2, err)) {
				if((args.length&1)!=0) {
					for(int c=1;c<args.length;c+=2) {
						String addr=args[c];
						int pos=addr.indexOf('@');
						if(pos==-1) {
							err.print("aosh: "+Command.REMOVE_EMAIL_ADDRESS+": invalid email address: ");
							err.println(addr);
							err.flush();
						} else {
							connector.getSimpleAOClient().removeEmailAddress(
								addr.substring(0, pos),
								AOSH.parseDomainName(addr.substring(pos+1), "address"),
								args[c+1]
							);
						}
					}
				} else throw new IllegalArgumentException("aosh: "+Command.REMOVE_EMAIL_ADDRESS+": must have even number of parameters.");
			}
			return true;
		} else return false;
	}
}
