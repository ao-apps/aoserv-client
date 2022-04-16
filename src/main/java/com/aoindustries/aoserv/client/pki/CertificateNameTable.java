/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  CertificateName
 *
 * @author  AO Industries, Inc.
 */
public final class CertificateNameTable extends CachedTableIntegerKey<CertificateName> {

	CertificateNameTable(AOServConnector connector) {
		super(connector, CertificateName.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CertificateName.COLUMN_SSL_CERTIFICATE_name+'.'+Certificate.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(CertificateName.COLUMN_SSL_CERTIFICATE_name+'.'+Certificate.COLUMN_CERT_FILE_name, ASCENDING),
		new OrderBy(CertificateName.COLUMN_IS_COMMON_NAME_name, DESCENDING),
		new OrderBy(CertificateName.COLUMN_DOMAIN_name, ASCENDING),
		new OrderBy(CertificateName.COLUMN_IS_WILDCARD_name, DESCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public CertificateName get(int pkey) throws IOException, SQLException {
		return getUniqueRow(CertificateName.COLUMN_PKEY, pkey);
	}

	List<CertificateName> getNames(Certificate sslCert) throws IOException, SQLException {
		return getIndexedRows(CertificateName.COLUMN_SSL_CERTIFICATE, sslCert.getPkey());
	}

	CertificateName getCommonName(Certificate sslCert) throws SQLException, IOException {
		// Use the index first
		for(CertificateName name : getNames(sslCert)) {
			if(name.isCommon()) return name;
		}
		throw new SQLException("Unable to find Common Name (CN) for SslCertificate with pkey = " + sslCert.getPkey());
	}

	List<CertificateName> getAltNames(Certificate sslCert) throws IOException, SQLException {
		// Use the index first
		List<CertificateName> cached = getNames(sslCert);
		int size = cached.size();
		List<CertificateName> matches = new ArrayList<>(size - 1);
		for(int c = 0; c < size; c++) {
			CertificateName name = cached.get(c);
			if(!name.isCommon()) matches.add(name);
		}
		return matches;
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SSL_CERTIFICATE_NAMES;
	}
}
