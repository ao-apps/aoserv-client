package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see FileBackupRoot
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupRootTable extends CachedTableIntegerKey<FileBackupRoot> {

    FileBackupRootTable(AOServConnector connector) {
	super(connector, FileBackupRoot.class);
    }

    List<FileBackupRoot> getFileBackupRoots(Server se) {
        return getIndexedRows(FileBackupRoot.COLUMN_SERVER, se.pkey);
    }

    public FileBackupRoot get(Object pkey) {
	return getUniqueRow(FileBackupRoot.COLUMN_PKEY, pkey);
    }

    public FileBackupRoot get(int pkey) {
	return getUniqueRow(FileBackupRoot.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FILE_BACKUP_ROOTS;
    }
}