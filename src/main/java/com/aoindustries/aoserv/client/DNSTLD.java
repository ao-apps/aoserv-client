/*
 * aoserv-client - Java client for the AOServ platform.
 * Copyright (C) 2001-2013, 2016  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.validator.DomainName;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A <code>DNSTLD</code> is a name server top level domain.  A top level domain
 * is a domain that is one level above that which is controlled by AO Industries'
 * name servers.  Some common examples include <code>com</code>, <code>net</code>,
 * <code>org</code>, <code>co.uk</code>, <code>aq</code> (OK - not so common), and
 * <code>med.pro</code>.  The domains added to the name servers must be in the
 * format <code>subdomain</code>.<code>dns_tld</code>, where <code>subdomain</code>
 * is a word without dots (<code>.</code>), and <code>dns_tld</code> is one of
 * the top level domains in the database.  If a top level domain does not exist
 * that properly should, please contact AO Industries to have it added.
 *
 * @see  DNSZone
 *
 * @author  AO Industries, Inc.
 */
final public class DNSTLD extends GlobalObjectDomainNameKey<DNSTLD> {

	static final int COLUMN_DOMAIN=0;
	static final String COLUMN_DOMAIN_name = "domain";

	private String description;

	@Override
	Object getColumnImpl(int i) {
		if(i==COLUMN_DOMAIN) return pkey;
		if(i==1) return description;
		throw new IllegalArgumentException("Invalid index: "+i);
	}

	public String getDescription() {
		return description;
	}

	public DomainName getDomain() {
		return pkey;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.DNS_TLDS;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			pkey=DomainName.valueOf(result.getString(1));
			description=result.getString(2);
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey=DomainName.valueOf(in.readUTF()).intern();
			description=in.readUTF();
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
		out.writeUTF(pkey.toString());
		out.writeUTF(description);
	}
}
