/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.pki;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.linux.AOServer;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  SslCertificateOtherUse
 *
 * @author  AO Industries, Inc.
 */
final public class SslCertificateOtherUseTable extends CachedTableIntegerKey<SslCertificateOtherUse> {

	public SslCertificateOtherUseTable(AOServConnector connector) {
		super(connector, SslCertificateOtherUse.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(SslCertificateOtherUse.COLUMN_SSL_CERTIFICATE_name+'.'+SslCertificate.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(SslCertificateOtherUse.COLUMN_SSL_CERTIFICATE_name+'.'+SslCertificate.COLUMN_CERT_FILE_name, ASCENDING),
		new OrderBy(SslCertificateOtherUse.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public SslCertificateOtherUse get(int pkey) throws IOException, SQLException {
		return getUniqueRow(SslCertificateOtherUse.COLUMN_PKEY, pkey);
	}

	List<SslCertificateOtherUse> getOtherUses(SslCertificate sslCert) throws IOException, SQLException {
		return getIndexedRows(SslCertificateOtherUse.COLUMN_SSL_CERTIFICATE, sslCert.getPkey());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SSL_CERTIFICATE_OTHER_USES;
	}
}
