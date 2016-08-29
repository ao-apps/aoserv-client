/*
 * Copyright 2001-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
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
