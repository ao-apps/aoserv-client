package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * A <code>FailoverFileSchedule</code> controls which time of day (in server
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
    static final String COLUMN_REPLICATION_name = "replication";
    static final String COLUMN_HOUR_name = "hour";
    static final String COLUMN_MINUTE_name = "minute";

    int replication;
    private short hour;
    private short minute;
    private boolean enabled;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_REPLICATION: return Integer.valueOf(replication);
            case 2: return hour;
            case 3: return minute;
            case 4: return enabled;
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
    
    public short getMinute() {
        return minute;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.FAILOVER_FILE_SCHEDULE;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        replication=result.getInt(2);
        hour=result.getShort(3);
        minute=result.getShort(4);
        enabled=result.getBoolean(5);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        replication=in.readCompressedInt();
        hour=in.readShort();
        minute=in.readShort();
        enabled=in.readBoolean();
    }

    String toStringImpl() {
        StringBuilder SB = new StringBuilder();
        SB.append(getFailoverFileReplication().toString());
        SB.append('@');
        if(hour<10) SB.append('0');
        SB.append(hour);
        SB.append(':');
        if(minute<10) SB.append('0');
        SB.append(minute);
        return SB.toString();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(replication);
        out.writeShort(hour);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_31)>=0) out.writeShort(minute);
        out.writeBoolean(enabled);
    }
}