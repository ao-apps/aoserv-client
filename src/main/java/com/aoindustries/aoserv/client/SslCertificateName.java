/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2018  AO Industries, Inc.
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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.net.DomainName;
import com.aoindustries.validation.ValidationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  AO Industries, Inc.
 */
final public class SslCertificateName extends CachedObjectIntegerKey<SslCertificateName> {

	public static final String WILDCARD_PREFIX = "*.";

	static final int
		COLUMN_PKEY = 0,
		COLUMN_SSL_CERTIFICATE = 1
	;
	static final String COLUMN_SSL_CERTIFICATE_name = "ssl_certificate";
	static final String COLUMN_IS_COMMON_NAME_name = "is_common_name";
	static final String COLUMN_IS_WILDCARD_name = "is_wildcard";
	static final String COLUMN_DOMAIN_name = "domain";

	private int sslCertificate;
	private boolean isCommonName;
	private boolean isWildcard;
	private DomainName domain;

	@Override
	String toStringImpl() {
		if(isCommonName) return getName();
		else return getName() + " (Alt)";
	}

	@Override
	Object getColumnImpl(int i) {
		switch(i) {
			case COLUMN_PKEY: return pkey;
			case COLUMN_SSL_CERTIFICATE: return sslCertificate;
			case 2: return isCommonName;
			case 3: return isWildcard;
			case 4: return domain;
			default: throw new IllegalArgumentException("Invalid index: " + i);
		}
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SSL_CERTIFICATE_NAMES;
	}

	@Override
	public void init(ResultSet result) throws SQLException {
		try {
			int pos = 1;
			pkey = result.getInt(pos++);
			sslCertificate = result.getInt(pos++);
			isCommonName = result.getBoolean(pos++);
			isWildcard = result.getBoolean(pos++);
			domain = DomainName.valueOf(result.getString(pos++));
		} catch(ValidationException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public void read(CompressedDataInputStream in) throws IOException {
		try {
			pkey = in.readCompressedInt();
			sslCertificate = in.readCompressedInt();
			isCommonName = in.readBoolean();
			isWildcard = in.readBoolean();
			domain = DomainName.valueOf(in.readUTF());
		} catch(ValidationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(CompressedDataOutputStream out, AOServProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(sslCertificate);
		out.writeBoolean(isCommonName);
		out.writeBoolean(isWildcard);
		out.writeUTF(domain.toString());
	}

	public SslCertificate getSslCertificate() throws SQLException, IOException {
		SslCertificate obj = table.connector.getSslCertificates().get(sslCertificate);
		if(obj == null) throw new SQLException("Unable to find SslCertificate: " + sslCertificate);
		return obj;
	}

	public boolean isCommon() {
		return isCommonName;
	}

	public boolean isWildcard() {
		return isWildcard;
	}

	public DomainName getDomain() {
		return domain;
	}

	/**
	 * Gets the name, which is {@link #getDomain()} or non-wildcard,
	 * or "*." + {@link #getDomain()} for wildcard domains.
	 *
	 * @see  #WILDCARD_PREFIX
	 * @see  #isWildcard()
	 * @see  #getDomain()
	 */
	public String getName() {
		if(isWildcard) {
			return WILDCARD_PREFIX + domain;
		} else {
			return domain.toString();
		}
	}
}
