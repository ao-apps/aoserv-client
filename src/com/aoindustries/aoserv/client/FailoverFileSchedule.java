package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>FailoverFileSchedule</code> controls which hour of the day (in server
 * time zone) the failover file replications will occur.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileSchedule extends CachedObjectIntegerKey<FailoverFileSchedule> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_REPLICATION=1
    ;

    int replication;
    private short hour;
    private boolean enabled;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_REPLICATION: return Integer.valueOf(replication);
            case 2: return Short.valueOf(hour);
            case 3: return enabled?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public FailoverFileReplication getFailoverFileReplication() {
        FailoverFileReplication ffr=table.connector.failoverFileReplications.get(replication);
        if(ffr==null) throw new WrappedException(new SQLException("Unable to find FailoverFileReplication: "+replication));
        return ffr;
    }

    public short getHour() {
        return hour;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    protected int getTableIDImpl() {
	return SchemaTable.FAILOVER_FILE_SCHEDULE;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        replication=result.getInt(2);
        hour=result.getShort(3);
        enabled=result.getBoolean(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        replication=in.readCompressedInt();
        hour=in.readShort();
        enabled=in.readBoolean();
    }

    String toStringImpl() {
        return getFailoverFileReplication().toString()+"@"+(hour<10?"0":"")+hour+":00";
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(replication);
        out.writeShort(hour);
        out.writeBoolean(enabled);
    }
}