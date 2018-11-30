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
package com.aoindustries.aoserv.client.email;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.IPAddress;
import com.aoindustries.aoserv.client.net.NetBind;
import com.aoindustries.aoserv.client.net.NetDevice;
import com.aoindustries.aoserv.client.net.Server;
import com.aoindustries.aoserv.client.pki.SslCertificate;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  CyrusImapdBind
 *
 * @author  AO Industries, Inc.
 */
final public class CyrusImapdBindTable extends CachedTableIntegerKey<CyrusImapdBind> {

	public CyrusImapdBindTable(AOServConnector connector) {
		super(connector, CyrusImapdBind.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(CyrusImapdBind.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_PORT_name, ASCENDING)
	};
	@Override
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

	public List<CyrusImapdBind> getCyrusImapdBinds(SslCertificate sslCert) throws IOException, SQLException {
		return getIndexedRows(CyrusImapdBind.COLUMN_SSL_CERTIFICATE, sslCert.getPkey());
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.CYRUS_IMAPD_BINDS;
	}
}
