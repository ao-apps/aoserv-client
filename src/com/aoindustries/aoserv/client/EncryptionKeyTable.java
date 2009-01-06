package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.util.List;

/**
 * @see  EncryptionKey
 *
 * @author  AO Industries, Inc.
 */
final public class EncryptionKeyTable extends CachedTableIntegerKey<EncryptionKey> {

    EncryptionKeyTable(AOServConnector connector) {
	super(connector, EncryptionKey.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(EncryptionKey.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(EncryptionKey.COLUMN_ID_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public EncryptionKey get(Object pkey) {
	return getUniqueRow(EncryptionKey.COLUMN_PKEY, pkey);
    }

    public EncryptionKey get(int pkey) {
	return getUniqueRow(EncryptionKey.COLUMN_PKEY, pkey);
    }

    List<EncryptionKey> getEncryptionKeys(Business business) {
        return getIndexedRows(EncryptionKey.COLUMN_ACCOUNTING, business.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.ENCRYPTION_KEYS;
    }
}
