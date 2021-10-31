/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2019, 2021  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.pki;

import com.aoapps.hodgepodge.io.stream.StreamableInput;
import com.aoapps.hodgepodge.io.stream.StreamableOutput;
import com.aoapps.lang.validation.ValidationException;
import com.aoapps.net.DomainName;
import com.aoindustries.aoserv.client.CachedObjectIntegerKey;
import com.aoindustries.aoserv.client.schema.AoservProtocol;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author  AO Industries, Inc.
 */
public final class CertificateName extends CachedObjectIntegerKey<CertificateName> {

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
	public String toStringImpl() {
		if(isCommonName) return getName();
		else return getName() + " (Alt)";
	}

	@Override
	protected Object getColumnImpl(int i) {
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
	public Table.TableID getTableID() {
		return Table.TableID.SSL_CERTIFICATE_NAMES;
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
	public void read(StreamableInput in, AoservProtocol.Version protocolVersion) throws IOException {
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
	public void write(StreamableOutput out, AoservProtocol.Version protocolVersion) throws IOException {
		out.writeCompressedInt(pkey);
		out.writeCompressedInt(sslCertificate);
		out.writeBoolean(isCommonName);
		out.writeBoolean(isWildcard);
		out.writeUTF(domain.toString());
	}

	public Certificate getSslCertificate() throws SQLException, IOException {
		Certificate obj = table.getConnector().getPki().getCertificate().get(sslCertificate);
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
