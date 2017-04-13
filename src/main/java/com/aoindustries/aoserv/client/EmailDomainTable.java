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

import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.net.DomainName;
import com.aoindustries.validation.ValidationResult;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  EmailDomain
 *
 * @author  AO Industries, Inc.
 */
public final class EmailDomainTable extends CachedTableIntegerKey<EmailDomain> {

	EmailDomainTable(AOServConnector connector) {
		super(connector, EmailDomain.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(EmailDomain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(EmailDomain.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addEmailDomain(DomainName domain, AOServer ao, Package packageObject) throws SQLException, IOException {
		return connector.requestIntQueryIL(
			true,
			AOServProtocol.CommandID.ADD,
			SchemaTable.TableID.EMAIL_DOMAINS,
			domain,
			ao.pkey,
			packageObject.name
		);
	}

	@Override
	public EmailDomain get(int pkey) throws IOException, SQLException {
		return getUniqueRow(EmailDomain.COLUMN_PKEY, pkey);
	}

	List<EmailDomain> getEmailDomains(Business owner) throws SQLException, IOException {
		AccountingCode accounting=owner.pkey;

		List<EmailDomain> cached = getRows();
		int len = cached.size();
		List<EmailDomain> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			EmailDomain domain = cached.get(c);
			if (domain.getPackage().accounting.equals(accounting)) matches.add(domain);
		}
		return matches;
	}

	List<EmailDomain> getEmailDomains(Package pack) throws IOException, SQLException {
		return getIndexedRows(EmailDomain.COLUMN_PACKAGE, pack.name);
	}

	List<EmailDomain> getEmailDomains(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(EmailDomain.COLUMN_AO_SERVER, ao.pkey);
	}

	EmailDomain getEmailDomain(AOServer ao, DomainName domain) throws IOException, SQLException {
		// Use the index first
		List<EmailDomain> cached = getEmailDomains(ao);
		int len = cached.size();
		for (int c = 0; c < len; c++) {
			EmailDomain sd = cached.get(c);
			if(domain.equals(sd.getDomain())) return sd;
		}
		return null;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.EMAIL_DOMAINS;
	}

	@Override
	boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(AOSHCommand.ADD_EMAIL_DOMAIN)) {
			if(AOSH.checkParamCount(AOSHCommand.ADD_EMAIL_DOMAIN, args, 3, err)) {
				out.println(
					connector.getSimpleAOClient().addEmailDomain(
						AOSH.parseDomainName(args[1], "domain"),
						args[2],
						AOSH.parseAccountingCode(args[3], "package")
					)
				);
				out.flush();
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.CHECK_EMAIL_DOMAIN)) {
			if(AOSH.checkParamCount(AOSHCommand.CHECK_EMAIL_DOMAIN, args, 1, err)) {
				ValidationResult validationResult = DomainName.validate(args[1]);
				out.println(validationResult.isValid());
				out.flush();
				if(!validationResult.isValid()) {
					err.print("aosh: "+AOSHCommand.CHECK_EMAIL_DOMAIN+": ");
					err.println(validationResult.toString());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.IS_EMAIL_DOMAIN_AVAILABLE)) {
			if(AOSH.checkParamCount(AOSHCommand.IS_EMAIL_DOMAIN_AVAILABLE, args, 2, err)) {
				try {
					out.println(
						connector.getSimpleAOClient().isEmailDomainAvailable(
							AOSH.parseDomainName(args[1], "domain"),
							args[2]
						)
					);
					out.flush();
				} catch(IllegalArgumentException iae) {
					err.print("aosh: "+AOSHCommand.IS_EMAIL_DOMAIN_AVAILABLE+": ");
					err.println(iae.getMessage());
					err.flush();
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_EMAIL_DOMAIN)) {
			if(AOSH.checkParamCount(AOSHCommand.REMOVE_EMAIL_DOMAIN, args, 2, err)) {
				connector.getSimpleAOClient().removeEmailDomain(
					AOSH.parseDomainName(args[1], "domain"),
					args[2]
				);
			}
			return true;
		}
		return false;
	}

	boolean isEmailDomainAvailable(AOServer aoServer, DomainName domain) throws SQLException, IOException {
		return connector.requestBooleanQuery(true, AOServProtocol.CommandID.IS_EMAIL_DOMAIN_AVAILABLE, aoServer.pkey, domain);
	}
}
