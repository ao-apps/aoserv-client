package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

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