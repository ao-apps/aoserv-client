/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2000-2013, 2016, 2017, 2018  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.CannotRemoveReason;
import com.aoindustries.aoserv.client.Removable;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.GroupServer;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.linux.UserServer;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import com.aoindustries.aoserv.client.validator.AccountingCode;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.DomainName;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>EmailDomain</code> is one hostname/domain of email
 * addresses hosted on a <code>Server</code>.  Multiple, unique
 * email addresses may be hosted within the <code>EmailDomain</code>.
 * In order for mail to be routed to the <code>Server</code>, a
 * <code>DNSRecord</code> entry of type <code>MX</code> must
 * point to the <code>Server</code>.
 *
 * @see  Address
 * @see  DNSRecord
 * @see  DNSType#MX
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class Domain extends CachedObjectIntegerKey<Domain> implements Removable {

	static final int
		COLUMN_PKEY=0,
		COLUMN_AO_SERVER=2,
		COLUMN_PACKAGE=3
	;
	static final String COLUMN_AO_SERVER_name = "ao_server";
	static final String COLUMN_DOMAIN_name = "domain";

	private DomainName domain;
	int ao_server;
	AccountingCode packageName;

	public int addEmailAddress(String address) throws SQLException, IOException {
		return table.getConnector().getEmail().getAddress().addEmailAddress(address, this);
	}

	public void addMajordomoServer(
		UserServer linuxServerAccount,
		GroupServer linuxServerGroup,
		MajordomoVersion majordomoVersion
	) throws IOException, SQLException {
		table.getConnector().getEmail().getMajordomoServer().addMajordomoServer(
			this,
			linuxServerAccount,
			linuxServerGroup,
			majordomoVersion
		);
	}

	@Override
	protected Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case 1: return domain;
			case COLUMN_AO_SERVER: return ao_server;
			case COLUMN_PACKAGE: return packageName;
			default: throw new IllegalArgumentException("Invalid index: "+i);
		}
	}

	public DomainName getDomain() {
		return domain;
	}

	public Address getEmailAddress(String address) throws IOException, SQLException {
		return table.getConnector().getEmail().getAddress().getEmailAddress(address, this);
	}

	public List<Address> getEmailAddresses() throws IOException, SQLException {
		return table.getConnector().getEmail().getAddress().getEmailAddresses(this);
	}

	public MajordomoServer getMajordomoServer() throws IOException, SQLException {
		return table.getConnector().getEmail().getMajordomoServer().get(pkey);
	}

	public Package getPackage() throws SQLException, IOException {
		Package packageObject = table.getConnector().getBilling().getPackage().get(packageName);
		if (packageObject == null) throw new SQLException("Unable to find Package: " + packageName);
		return packageObject;
	}

	public Server getAOServer() throws SQLException, IOException {
		Server ao=table.getConnector().getLinux().getServer().get(ao_server);
		if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
		return ao;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.EMAIL_DOMAINS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=result.getInt(1);
			domain=DomainName.valueOf(result.getString(2));
			ao_server=result.getInt(3);
			packageName = AccountingCode.valueOf(result.getString(4));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			domain = DomainName.valueOf(in.readUTF());
			ao_server = in.readCompressedInt();
			packageName = AccountingCode.valueOf(in.readUTF()).intern();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<CannotRemoveReason<?>> getCannotRemoveReasons() throws SQLException, IOException {
		List<CannotRemoveReason<?>> reasons=new ArrayList<>();

		MajordomoServer ms=getMajordomoServer();
		if(ms!=null) {
			Domain ed=ms.getDomain();
			reasons.add(new CannotRemoveReason<>("Used by Majordomo server "+ed.getDomain()+" on "+ed.getAOServer().getHostname(), ms));
		}

		for(Address ea : getEmailAddresses()) reasons.addAll(ea.getCannotRemoveReasons());

		return reasons;
	}

	@Override
	public void remove() throws IOException, SQLException {
		table.getConnector().requestUpdateIL(true,
			AoservProtocol.CommandID.REMOVE,
			Table.TableID.EMAIL_DOMAINS,
			pkey
		);
	}

	@Override
	public void write(CompressedDataOutputStream out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeUTF(domain.toString());
		out.writeCompressedInt(ao_server);
		out.writeUTF(packageName.toString());
	}
}
