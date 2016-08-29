/*
 * Copyright 2003-2009, 2016 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @see  BackupRetention
 *
 * @author  AO Industries, Inc.
 */
final public class BackupRetentionTable extends GlobalTable<Short,BackupRetention> {

	BackupRetentionTable(AOServConnector connector) {
		super(connector, BackupRetention.class);
	}

	private static final OrderBy[] defaultOrderBy = {
		new OrderBy(BackupRetention.COLUMN_DAYS_name, ASCENDING)
	};
	@Override
	OrderBy[] getDefaultOrderBy() {
		return defaultOrderBy;
	}

	@Override
	public BackupRetention get(Object days) throws IOException, SQLException {
		return get(((Short)days).shortValue());
	}

	public BackupRetention get(short days) throws IOException, SQLException {
		return getUniqueRow(BackupRetention.COLUMN_DAYS, days);
	}

	@Override
	public SchemaTable.TableID getTableID() {
		return SchemaTable.TableID.BACKUP_RETENTIONS;
	}
}
