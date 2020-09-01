/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2017, 2018, 2020  AO Industries, Inc.
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
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.pki.Certificate;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  CyrusImapdBind
 *
 * @author  AO Industries, Inc.
 */
final public class CyrusImapdBindTable extends CachedTableIntegerKey<CyrusImapdBind> {

	CyrusImapdBindTable(AOServConnector connector) {
		super(connector, CyrusImapdBind.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_PORT_name, ASCENDING)
	};
	@Override
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	List<CyrusImapdBind> getCyrusImapdBinds(CyrusImapdServer server) throws IOException, SQLException {
		return getIndexedRows(CyrusImapdBind.COLUMN_CYRUS_IMAPD_SERVER, server.getPkey());
	}

	@Override
	public CyrusImapdBind get(int pkey) throws IOException, SQLException {
		return getUniqueRow(CyrusImapdBind.COLUMN_NET_BIND, pkey);
	}

	public List<CyrusImapdBind> getCyrusImapdBinds(Certificate sslCert) throws IOException, SQLException {
		return getIndexedRows(CyrusImapdBind.COLUMN_SSL_CERTIFICATE, sslCert.getPkey());
	}

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.CYRUS_IMAPD_BINDS;
	}
}
