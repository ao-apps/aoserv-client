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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  SslCertificateName
 *
 * @author  AO Industries, Inc.
 */
final public class SslCertificateNameTable extends CachedTableIntegerKey<SslCertificateName> {

	SslCertificateNameTable(AOServConnector connector) {
		super(connector, SslCertificateName.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(SslCertificateName.COLUMN_SSL_CERTIFICATE_name+'.'+SslCertificate.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(SslCertificateName.COLUMN_SSL_CERTIFICATE_name+'.'+SslCertificate.COLUMN_CERT_FILE_name, ASCENDING),
		new OrderBy(SslCertificateName.COLUMN_IS_COMMON_NAME_name, DESCENDING),
		new OrderBy(SslCertificateName.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(SslCertificateName.COLUMN_IS_WILDCARD_name, DESCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public SslCertificateName get(int pkey) throws IOException, SQLException {
		return getUniqueRow(SslCertificateName.COLUMN_PKEY, pkey);
	}

	List<SslCertificateName> getNames(SslCertificate sslCert) throws IOException, SQLException {
		return getIndexedRows(SslCertificateName.COLUMN_SSL_CERTIFICATE, sslCert.pkey);
	}

	SslCertificateName getCommonName(SslCertificate sslCert) throws SQLException, IOException {
		// Use the index first
		for(SslCertificateName name : getNames(sslCert)) {
			if(name.isCommon()) return name;
		}
		throw new SQLException("Unable to find Common Name (CN) for SslCertificate with pkey = " + sslCert.pkey);
	}

	List<SslCertificateName> getAltNames(SslCertificate sslCert) throws IOException, SQLException {
		// Use the index first
		List<SslCertificateName> cached = getNames(sslCert);
		int size = cached.size();
		List<SslCertificateName> matches = new ArrayList<>(size - 1);
		for(int c = 0; c < size; c++) {
			SslCertificateName name = cached.get(c);
			if(!name.isCommon()) matches.add(name);
		}
		return matches;
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SSL_CERTIFICATE_NAMES;
	}
}
