package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  BackupLevel
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class BackupLevelTable extends GlobalTable<Short,BackupLevel> {

    BackupLevelTable(AOServConnector connector) {
	super(connector, BackupLevel.class);
    }

    public BackupLevel get(Object level) {
        return get((Short)level);
    }

    public BackupLevel get(short level) {
	return getUniqueRow(BackupLevel.COLUMN_LEVEL, level);
    }

    int getTableID() {
	return SchemaTable.BACKUP_LEVELS;
    }
}