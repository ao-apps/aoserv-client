/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PrivateFTPServer
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateFTPServerTable extends CachedTableIntegerKey<PrivateFTPServer> {

	PrivateFTPServerTable(AOServConnector connector) {
		super(connector, PrivateFTPServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(PrivateFTPServer.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PrivateFTPServer.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
		new OrderBy(PrivateFTPServer.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
		new OrderBy(PrivateFTPServer.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
		new OrderBy(PrivateFTPServer.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_PORT_name, ASCENDING),
		new OrderBy(PrivateFTPServer.COLUMN_NET_BIND_name+'.'+NetBind.COLUMN_NET_PROTOCOL_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public PrivateFTPServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(PrivateFTPServer.COLUMN_NET_BIND, pkey);
	}

	List<PrivateFTPServer> getPrivateFTPServers(AOServer ao) throws IOException, SQLException {
		int aoPKey=ao.pkey;

		List<PrivateFTPServer> cached=getRows();
		int size=cached.size();
		List<PrivateFTPServer> matches=new ArrayList<>(size);
		for(int c=0;c<size;c++) {
			PrivateFTPServer obj=cached.get(c);
			if(obj.getNetBind().server==aoPKey) matches.add(obj);
		}
		return matches;
	}

	/*
	PrivateFTPServer getPrivateFTPServer(AOServer ao, String path) {
		int aoPKey=ao.pkey;

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
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.PRIVATE_FTP_SERVERS;
	}
}
