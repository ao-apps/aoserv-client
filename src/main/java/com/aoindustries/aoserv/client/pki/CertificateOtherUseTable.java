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
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  CertificateOtherUse
 *
 * @author  AO Industries, Inc.
 */
final public class CertificateOtherUseTable extends CachedTableIntegerKey<CertificateOtherUse> {

	public CertificateOtherUseTable(AOServConnector connector) {
		super(connector, CertificateOtherUse.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CertificateOtherUse.COLUMN_SSL_CERTIFICATE_name+'.'+Certificate.COLUMN_AO_SERVER_name+'.'+Server.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(CertificateOtherUse.COLUMN_SSL_CERTIFICATE_name+'.'+Certificate.COLUMN_CERT_FILE_name, ASCENDING),
		new OrderBy(CertificateOtherUse.COLUMN_SORT_ORDER_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public CertificateOtherUse get(int pkey) throws IOException, SQLException {
		return getUniqueRow(CertificateOtherUse.COLUMN_PKEY, pkey);
	}

	List<CertificateOtherUse> getOtherUses(Certificate sslCert) throws IOException, SQLException {
		return getIndexedRows(CertificateOtherUse.COLUMN_SSL_CERTIFICATE, sslCert.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.SSL_CERTIFICATE_OTHER_USES;
	}
}
