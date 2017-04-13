/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.net.Email;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  EmailAddress
 *
 * @author  AO Industries, Inc.
 */
final public class EmailAddressTable extends CachedTableIntegerKey<EmailAddress> {

	EmailAddressTable(AOServConnector connector) {
		super(connector, EmailAddress.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(EmailAddress.COLUMN_DOMAIN_name+'.'+EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(EmailAddress.COLUMN_ADDRESS_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addEmailAddress(String address, EmailDomain domainObject) throws SQLException, IOException {
		ValidationResult result = Email.validate(address, domainObject.getDomain());
		if (!result.isValid()) throw new SQLException("Invalid email address: " + result.toString());
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.EMAIL_ADDRESSES,
			address,
			domainObject.pkey
		);
	}

	@Override
	public EmailAddress get(int pkey) throws IOException, SQLException {
		return getUniqueRow(EmailAddress.COLUMN_PKEY, pkey);
	}

	EmailAddress getEmailAddress(String address, EmailDomain domain) throws IOException, SQLException {
		// Uses index on domain first, then searched on address
		for(EmailAddress emailAddress : domain.getEmailAddresses()) {
			if(emailAddress.address.equals(address)) return emailAddress;
		}
		return null;
	}

	List<EmailAddress> getEmailAddresses(EmailDomain domain) throws IOException, SQLException {
		return getIndexedRows(EmailAddress.COLUMN_DOMAIN, domain.pkey);
	}

	List<EmailAddress> getEmailAddresses(AOServer ao) throws IOException, SQLException {
		int aoPKey=ao.pkey;
		List<EmailAddress> addresses = getRows();
		int len = addresses.size();
		List<EmailAddress> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			EmailAddress address = addresses.get(c);
			if (address.getDomain().ao_server==aoPKey) matches.add(address);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_ADDRESSES;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.CHECK_EMAIL_ADDRESS)) {
			if(AOSH.checkMinParamCount(AOSHCommand.CHECK_EMAIL_ADDRESS, args, 1, err)) {
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
						err.print("aosh: "+AOSHCommand.CHECK_EMAIL_ADDRESS+": invalid email address: ");
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
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_ADDRESS)) {
			if(AOSH.checkMinParamCount(AOSHCommand.REMOVE_EMAIL_ADDRESS, args, 2, err)) {
				if((args.length&1)!=0) {
					for(int c=1;c<args.length;c+=2) {
						String addr=args[c];
						int pos=addr.indexOf('@');
						if(pos==-1) {
							err.print("aosh: "+AOSHCommand.REMOVE_EMAIL_ADDRESS+": invalid email address: ");
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
				} else throw new IllegalArgumentException("aosh: "+AOSHCommand.REMOVE_EMAIL_ADDRESS+": must have even number of parameters.");
			}
			return true;
		} else return false;
	}
}
