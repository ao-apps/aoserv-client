package com.aoindustries.aoserv.client;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.SQLException;
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
        try {
            return getUniqueRow(EncryptionKey.COLUMN_PKEY, pkey);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public EncryptionKey get(int pkey) throws IOException, SQLException {
	return getUniqueRow(EncryptionKey.COLUMN_PKEY, pkey);
    }

    List<EncryptionKey> getEncryptionKeys(Business business) throws IOException, SQLException {
        return getIndexedRows(EncryptionKey.COLUMN_ACCOUNTING, business.pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.ENCRYPTION_KEYS;
    }
}
