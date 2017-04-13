/*
 * aoserv-client - Java client for the AOServ Platform.
 * Copyright (C) 2001-2009, 2016, 2017  AO Industries, Inc.
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
import java.util.List;

/**
 * @see  SystemEmailAlias
 *
 * @author  AO Industries, Inc.
 */
final public class SystemEmailAliasTable extends CachedTableIntegerKey<SystemEmailAlias> {

	SystemEmailAliasTable(AOServConnector connector) {
		super(connector, SystemEmailAlias.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(SystemEmailAlias.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
		new OrderBy(SystemEmailAlias.COLUMN_ADDRESS_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	List<SystemEmailAlias> getSystemEmailAliases(AOServer ao) throws IOException, SQLException {
		return getIndexedRows(SystemEmailAlias.COLUMN_AO_SERVER, ao.pkey);
	}

	@Override
	public SystemEmailAlias get(int pkey) throws IOException, SQLException {
		return getUniqueRow(SystemEmailAlias.COLUMN_PKEY, pkey);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.SYSTEM_EMAIL_ALIASES;
	}
}
