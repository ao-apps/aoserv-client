package com.aoindustries.aoserv.client;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  FileBackupStat
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FileBackupStatTable extends CachedTableIntegerKey<FileBackupStat> {

    FileBackupStatTable(AOServConnector connector) {
	super(connector, FileBackupStat.class);
    }

    int addFileBackupStat(
        Server server,
        long startTime,
        long endTime,
        int scanned,
        int file_backup_attribute_matches,
        int not_matched_md5_files,
        int not_matched_md5_failures,
        int send_missing_backup_data_files,
        int send_missing_backup_data_failures,
        int temp_files,
        int temp_send_backup_data_files,
        int temp_failures,
        int added,
        int deleted,
        boolean is_successful
    ) {
        try {
            IntList invalidateList;
            AOServConnection connection=connector.getConnection();
            int pkey;
            try {
                CompressedDataOutputStream out=connection.getOutputStream();
                out.writeCompressedInt(AOServProtocol.ADD);
                out.writeCompressedInt(SchemaTable.FILE_BACKUP_STATS);
                out.writeCompressedInt(server.pkey);
                out.writeLong(startTime);
                out.writeLong(endTime);
                out.writeCompressedInt(scanned);
                out.writeCompressedInt(file_backup_attribute_matches);
                out.writeCompressedInt(not_matched_md5_files);
                out.writeCompressedInt(not_matched_md5_failures);
                out.writeCompressedInt(send_missing_backup_data_files);
                out.writeCompressedInt(send_missing_backup_data_failures);
                out.writeCompressedInt(temp_files);
                out.writeCompressedInt(temp_send_backup_data_files);
                out.writeCompressedInt(temp_failures);
                out.writeCompressedInt(added);
                out.writeCompressedInt(deleted);
                out.writeBoolean(is_successful);
                out.flush();

                CompressedDataInputStream in=connection.getInputStream();
                int code=in.readByte();
                if(code==AOServProtocol.DONE) {
                    pkey=in.readCompressedInt();
                    invalidateList=AOServConnector.readInvalidateList(in);
                } else {
                    AOServProtocol.checkResult(code, in);
                    throw new IOException("Unexpected response code: "+code);
                }
            } catch(IOException err) {
                connection.close();
                throw err;
            } finally {
                connector.releaseConnection(connection);
            }
            connector.tablesUpdated(invalidateList);
            return pkey;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public FileBackupStat get(Object pkey) {
	return getUniqueRow(FileBackupStat.COLUMN_PKEY, pkey);
    }

    public FileBackupStat get(int pkey) {
	return getUniqueRow(FileBackupStat.COLUMN_PKEY, pkey);
    }

    List<FileBackupStat> getFileBackupStats(Server se) {
        return getIndexedRows(FileBackupStat.COLUMN_SERVER, se.pkey);
    }

    int getTableID() {
	return SchemaTable.FILE_BACKUP_STATS;
    }
}