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
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.io.TerminalWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PipeAddress
 *
 * @author  AO Industries, Inc.
 */
final public class PipeAddressTable extends CachedTableIntegerKey<PipeAddress> {

	public PipeAddressTable(AOServConnector connector) {
		super(connector, PipeAddress.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PipeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(PipeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_DOMAIN_name+'.'+Domain.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(PipeAddress.COLUMN_EMAIL_ADDRESS_name+'.'+Address.COLUMN_ADDRESS_name, ASCENDING),
		new OrderBy(PipeAddress.COLUMN_EMAIL_PIPE_name+'.'+Pipe.COLUMN_COMMAND_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	int addEmailPipeAddress(Address emailAddressObject, Pipe emailPipeObject) throws IOException, SQLException {
		return connector.requestIntQueryIL(true,
			AoservProtocol.CommandID.ADD,
			Table.TableID.EMAIL_PIPE_ADDRESSES,
			emailAddressObject.getPkey(),
			emailPipeObject.getPkey()
		);
	}

	@Override
	public PipeAddress get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PipeAddress.COLUMN_PKEY, pkey);
	}

	List<Pipe> getEmailPipes(Address address) throws IOException, SQLException {
		int pkey=address.getPkey();
		List<PipeAddress> cached=getRows();
		int len = cached.size();
		List<Pipe> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			PipeAddress pipe=cached.get(c);
			if (pipe.email_address==pkey) {
				// The pipe might be filtered
				Pipe ep=pipe.getEmailPipe();
				if(ep!=null) matches.add(pipe.getEmailPipe());
			}
		}
		return matches;
	}

	List<PipeAddress> getEmailPipeAddresses(Address address) throws IOException, SQLException {
		return getIndexedRows(PipeAddress.COLUMN_EMAIL_ADDRESS, address.getPkey());
	}

	List<PipeAddress> getEnabledEmailPipeAddresses(Address address) throws IOException, SQLException {
		// Use the index first
		List<PipeAddress> cached = getEmailPipeAddresses(address);
		int len = cached.size();
		List<PipeAddress> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			PipeAddress pipe=cached.get(c);
			if(!pipe.getEmailPipe().isDisabled()) matches.add(pipe);
		}
		return matches;
	}

	PipeAddress getEmailPipeAddress(Address address, Pipe pipe) throws IOException, SQLException {
		int pkey=address.getPkey();
		int pipePKey=pipe.getPkey();
		List<PipeAddress> cached = getRows();
		int len = cached.size();
		for (int c = 0; c < len; c++) {
			PipeAddress epa = cached.get(c);
			if (epa.email_address==pkey && epa.email_pipe==pipePKey) return epa;
		}
		return null;
	}

	public List<PipeAddress> getEmailPipeAddresses(Server ao) throws IOException, SQLException {
		int aoPKey=ao.getPkey();
		List<PipeAddress> cached = getRows();
		int len = cached.size();
		List<PipeAddress> matches=new ArrayList<>(len);
		for (int c = 0; c < len; c++) {
			PipeAddress pipe = cached.get(c);
			if(pipe.getEmailAddress().getDomain().ao_server==aoPKey) matches.add(pipe);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_PIPE_ADDRESSES;
	}

	@Override
	public boolean handleCommand(String[] args, Reader in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, IOException, SQLException {
		String command=args[0];
		if(command.equalsIgnoreCase(Command.ADD_EMAIL_PIPE_ADDRESS)) {
			if(AOSH.checkMinParamCount(Command.ADD_EMAIL_PIPE_ADDRESS, args, 2, err)) {
				if((args.length&1)==0) {
					err.println("aosh: "+Command.ADD_EMAIL_PIPE_ADDRESS+": must have even number of parameters");
					err.flush();
				} else {
					for(int c=1;c<args.length;c+=2) {
						String addr=args[c];
						int pos=addr.indexOf('@');
						if(pos==-1) {
							err.print("aosh: "+Command.ADD_EMAIL_PIPE_ADDRESS+": invalid email address: ");
							err.println(addr);
							err.flush();
						} else {
							out.println(
								connector.getSimpleAOClient().addEmailPipeAddress(
									addr.substring(0, pos),
									AOSH.parseDomainName(addr.substring(pos+1), "address"),
									AOSH.parseInt(args[c+1], "pkey")
								)
							);
							out.flush();
						}
					}
				}
			}
			return true;
		} else if(command.equalsIgnoreCase(Command.REMOVE_EMAIL_PIPE_ADDRESS)) {
			if(AOSH.checkParamCount(Command.REMOVE_EMAIL_PIPE_ADDRESS, args, 2, err)) {
				String addr=args[1];
				int pos=addr.indexOf('@');
				if(pos==-1) {
					err.print("aosh: "+Command.REMOVE_EMAIL_PIPE_ADDRESS+": invalid email address: ");
					err.println(addr);
					err.flush();
				} else {
					connector.getSimpleAOClient().removeEmailPipeAddress(
						addr.substring(0, pos),
						AOSH.parseDomainName(addr.substring(pos+1), "address"),
						AOSH.parseInt(args[2], "pkey")
					);
				}
			}
			return true;
		}
		return false;
	}
}
