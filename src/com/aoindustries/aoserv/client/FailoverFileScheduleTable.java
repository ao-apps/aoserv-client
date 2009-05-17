package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see FailoverFileSchedule
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileScheduleTable extends CachedTableIntegerKey<FailoverFileSchedule> {

    FailoverFileScheduleTable(AOServConnector connector) {
	super(connector, FailoverFileSchedule.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_AO_SERVER_name+'.'+AOServer.COLUMN_HOSTNAME_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_REPLICATION_name+'.'+FailoverFileReplication.COLUMN_BACKUP_PARTITION_name+'.'+BackupPartition.COLUMN_PATH_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_HOUR_name, ASCENDING),
        new OrderBy(FailoverFileSchedule.COLUMN_MINUTE_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    List<FailoverFileSchedule> getFailoverFileSchedules(FailoverFileReplication replication) throws IOException, SQLException {
        return getIndexedRows(FailoverFileSchedule.COLUMN_REPLICATION, replication.pkey);
    }

    public FailoverFileSchedule get(int pkey) throws IOException, SQLException {
    	return getUniqueRow(FailoverFileSchedule.COLUMN_PKEY, pkey);
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_FILE_SCHEDULE;
    }
    
    void setFailoverFileSchedules(FailoverFileReplication ffr, List<Short> hours, List<Short> minutes) throws IOException, SQLException {
        if(hours.size()!=minutes.size()) throw new IllegalArgumentException("hours.size()!=minutes.size(): "+hours.size()+"!="+minutes.size());

        // Create the new profile
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.SET_FAILOVER_FILE_SCHEDULES.ordinal());
            out.writeCompressedInt(ffr.getPkey());
            int size = hours.size();
            out.writeCompressedInt(size);
            for(int c=0;c<size;c++) {
                out.writeShort(hours.get(c));
                out.writeShort(minutes.get(c));
            }
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
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
    }
}