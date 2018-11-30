/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017, 2018  AO Industries, Inc.
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
package com.aoindustries.aoserv.client.ftp;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.linux.Server;
import com.aoindustries.aoserv.client.net.Bind;
import com.aoindustries.aoserv.client.net.Device;
import com.aoindustries.aoserv.client.net.Host;
import com.aoindustries.aoserv.client.net.IpAddress;
import com.aoindustries.aoserv.client.schema.Table;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PrivateServer
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateServerTable extends CachedTableIntegerKey<PrivateServer> {

	public PrivateServerTable(AOServConnector connector) {
		super(connector, PrivateServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PrivateServer.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PrivateServer.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_SERVER_name+'.'+Host.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PrivateServer.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(PrivateServer.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_IP_ADDRESS_name+'.'+IpAddress.COLUMN_DEVICE_name+'.'+Device.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(PrivateServer.COLUMN_NET_BIND_name+'.'+Bind.COLUMN_PORT_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public PrivateServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PrivateServer.COLUMN_NET_BIND, pkey);
	}

	public List<PrivateServer> getPrivateFTPServers(Server ao) throws IOException, SQLException {
		int aoPKey=ao.getPkey();

		List<PrivateServer> cached=getRows();
		int size=cached.size();
		List<PrivateServer> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			PrivateServer obj=cached.get(c);
			if(obj.getNetBind().getServer_pkey()==aoPKey) matches.add(obj);
		}
		return matches;
	}

	/*
	PrivateFTPServer getPrivateFTPServer(AOServer ao, String path) {
		int aoPKey=ao.getPkey();

		List<PrivateFTPServer> cached=getRows();
		int size=cached.size();
		for(int c=0;c<size;c++) {
			PrivateFTPServer obj=cached.get(c);
			if(
				obj.getRoot().equals(path)
				&& obj.getNetBind().server==aoPKey
			) return obj;
		}
		return null;
	}*/

	@Override
	public Table.TableID getTableID() {
		return Table.TableID.PRIVATE_FTP_SERVERS;
	}
}
