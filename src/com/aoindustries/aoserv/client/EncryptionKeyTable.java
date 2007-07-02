package com.aoindustries.aoserv.client;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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
