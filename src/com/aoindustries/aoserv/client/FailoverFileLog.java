package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * The entire contents of servers are periodically replicated to another server.  In the
 * event of hardware failure, this other server may be booted to take place of the
 * failed machine.  All transfers to the failover server are logged.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileLog extends AOServObject<Integer,FailoverFileLog> implements SingleTableObject<Integer,FailoverFileLog> {

    static final String COLUMN_REPLICATION_name = "replication";
    static final String COLUMN_END_TIME_name = "end_time";

    protected AOServTable<Integer,FailoverFileLog> table;

    private int pkey;
    private int replication;
    private long startTime;
    private long endTime;
    private int scanned;
    private int updated;
    private long bytes;
    private boolean is_successful;

    boolean equalsImpl(Object O) {
	return
            O instanceof FailoverFileLog
            && ((FailoverFileLog)O).pkey==pkey
	;
    }

    public long getBytes() {
	return bytes;
    }

    public Object getColumn(int i) {
        switch(i) {
            case 0: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(replication);
            case 2: return new java.sql.Date(startTime);
            case 3: return new java.sql.Date(endTime);
            case 4: return Integer.valueOf(scanned);
            case 5: return Integer.valueOf(updated);
            case 6: return Long.valueOf(bytes);
            case 7: return is_successful?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
	return endTime;
    }

    public int getPkey() {
	return pkey;
    }

    public Integer getKey() {
	return pkey;
    }

    public int getScanned() {
	return scanned;
    }

    public FailoverFileReplication getFailoverFileReplication() {
        FailoverFileReplication ffr=table.connector.failoverFileReplications.get(replication);
        if(ffr==null) throw new WrappedException(new SQLException("Unable to find FailoverFileReplication: "+replication));
        return ffr;
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<Integer,FailoverFileLog> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_FILE_LOG;
    }

    public int getUpdated() {
	return updated;
    }

    int hashCodeImpl() {
	return pkey;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	replication=result.getInt(2);
	startTime=result.getTimestamp(3).getTime();
	endTime=result.getTimestamp(4).getTime();
	scanned=result.getInt(5);
	updated=result.getInt(6);
	bytes=result.getLong(7);
        is_successful=result.getBoolean(8);
    }

    public boolean isSuccessful() {
        return is_successful;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	replication=in.readCompressedInt();
        startTime=in.readLong();
	endTime=in.readLong();
	scanned=in.readCompressedInt();
	updated=in.readCompressedInt();
	bytes=in.readLong();
        is_successful=in.readBoolean();
    }

    public void setTable(AOServTable<Integer,FailoverFileLog> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
        out.writeCompressedInt(replication);
        out.writeLong(startTime);
	out.writeLong(endTime);
	out.writeCompressedInt(scanned);
	out.writeCompressedInt(updated);
	out.writeLong(bytes);
        out.writeBoolean(is_successful);
    }
}