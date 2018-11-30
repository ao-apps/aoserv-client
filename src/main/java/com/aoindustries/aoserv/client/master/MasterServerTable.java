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
package com.aoindustries.aoserv.client.master;

import com.aoindustries.aoserv.client.AOServConnector;
import com.aoindustries.aoserv.client.CachedTableIntegerKey;
import com.aoindustries.aoserv.client.billing.Package;
import com.aoindustries.aoserv.client.net.Server;
import com.aoindustries.aoserv.client.schema.SchemaTable;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  MasterServer
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerTable extends CachedTableIntegerKey<MasterServer> {

	public MasterServerTable(AOServConnector connector) {
	super(connector, MasterServer.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(MasterServer.COLUMN_USERNAME_name, ASCENDING),
		new OrderBy(MasterServer.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
		new OrderBy(MasterServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING)
	};
	@Override
	protected OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public MasterServer get(int pkey) throws IOException, SQLException {
		return getUniqueRow(MasterServer.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.MASTER_SERVERS;
	}
}
