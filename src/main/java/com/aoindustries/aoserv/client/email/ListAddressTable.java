/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2013, 2016, 2017, 2018, 2019  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @see  ListAddress
 *
 * @author  AO Industries, Inc.
 */
final public class ListAddressTable extends CachedTableIntegerKey<ListAddress> {

	ListAddressTable(AOServConnector connector) {
		super(connector, ListAddress.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(ListAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(ListAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(ListAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_ADDRESS_name, ASCENDING),
		new OrderBy(ListAddress.COLUMN_EMAIL_LIST_name+'.'+List.COLUMN_PATH_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addEmailListAddress(Address emailAddressObject, List emailListObject) throws IOException, SQLException {
		return connector.requestIntQueryIL(
			true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.EMAIL_LIST_ADDRESSES,
			emailAddressObject.getPkey(),
			emailListObject.getPkey()
		);
	}

	@Override
	public ListAddress get(int pkey) throws IOException, SQLException {
		return getUniqueRow(ListAddress.COLUMN_PKEY, pkey);
	}

	java.util.List<ListAddress> getEmailListAddresses(List list) throws IOException, SQLException {
		return getIndexedRows(ListAddress.COLUMN_EMAIL_LIST, list.getPkey());
	}

	java.util.List<Address> getEmailAddresses(List list) throws IOException, SQLException {
		// Use the index first
		java.util.List<ListAddress> cached=getEmailListAddresses(list);
		int len=cached.size();
		java.util.List<Address> eas=new ArrayList<>(len);
		for(int c=0;c<len;c++) eas.add(cached.get(c).getEmailAddress());
			return eas;
		}

		ListAddress getEmailListAddress(Address ea, List list) throws IOException, SQLException {
		int pkey=ea.getPkey();
		// Use the index first
		java.util.List<ListAddress> cached=getEmailListAddresses(list);
		int size=cached.size();
		for (int c = 0; c < size; c++) {
			ListAddress ela=cached.get(c);
			if(ela.getEmailAddress_pkey()==pkey) return ela;
		}
		return null;
	}

	java.util.List<ListAddress> getEmailListAddresses(Address ea) throws IOException, SQLException {
		return getIndexedRows(ListAddress.COLUMN_EMAIL_ADDRESS, ea.getPkey());
	}

	java.util.List<List> getEmailLists(Address ea) throws IOException, SQLException {
		// Use the cache first
		java.util.List<ListAddress> cached=getEmailListAddresses(ea);
		int len=cached.size();
		java.util.List<List> els=new ArrayList<>(len);
		for(int c=0;c<len;c++) els.add(cached.get(c).getEmailList());
		return els;
	}

	java.util.List<ListAddress> getEnabledEmailListAddresses(Address ea) throws IOException, SQLException {
		// Use the cache first
		java.util.List<ListAddress> cached=getEmailListAddresses(ea);
		int size=cached.size();
		java.util.List<ListAddress> matches=new ArrayList<>(size);
		for (int c = 0; c < size; c++) {
			ListAddress ela=cached.get(c);
			if(!ela.getEmailList().isDisabled()) matches.add(ela);
		}
		return matches;
	}

	public java.util.List<ListAddress> getEmailListAddresses(Server ao) throws IOException, SQLException {
		int aoPKey=ao.getPkey();
		java.util.List<ListAddress> cached = getRows();
		int len = cached.size();
		java.util.List<ListAddress> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			ListAddress list=cached.get(c);
			if(list.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(list);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_LIST_ADDRESSES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_EMAIL_LIST_ADDRESS)) {
			if(AOSH.checkMinParamCount(Command.ADD_EMAIL_LIST_ADDRESS, args, 3, err)) {
				if((args.length%3)!=1) {
					err.println("aosh: "+Command.ADD_EMAIL_LIST_ADDRESS+": must have multiples of three number of parameters");
					err.flush();
				} else {
					for(int c=1;c<args.length;c+=3) {
						String addr=args[c];
						int pos=addr.indexOf('@');
						if(pos==-1) {
							err.print("aosh: "+Command.ADD_EMAIL_LIST_ADDRESS+": invalid email address: ");
							err.println(addr);
							err.flush();
						} else {
							out.println(
								connector.getSimpleAOClient().addEmailListAddress(
									addr.substring(0, pos),
									AOSH.parseDomainName(addr.substring(pos+1), "address"),
									AOSH.parseUnixPath(args[c+1], "path"),
									args[c+2]
								)
							);
							out.flush();
						}
					}
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_EMAIL_LIST_ADDRESS)) {
			if(AOSH.checkParamCount(Command.REMOVE_EMAIL_LIST_ADDRESS, args, 3, err)) {
				String addr=args[1];
				int pos=addr.indexOf('@');
				if(pos==-1) {
					err.print("aosh: "+Command.REMOVE_EMAIL_LIST_ADDRESS+": invalid email address: ");
					err.println(addr);
					err.flush();
				} else {
					connector.getSimpleAOClient().removeEmailListAddress(
						addr.substring(0, pos),
						AOSH.parseDomainName(addr.substring(pos+1), "address"),
						AOSH.parseUnixPath(args[2], "path"),
						args[3]
					);
				}
			}
			return true;
		}
		return false;
	}
}
