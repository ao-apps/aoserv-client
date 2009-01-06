package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  BackupRetention
 *
 * @version  1.0a
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

    public BackupRetention get(Object days) {
        return get((Short)days);
    }

    public BackupRetention get(short days) {
	return getUniqueRow(BackupRetention.COLUMN_DAYS, days);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BACKUP_RETENTIONS;
    }
}