package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SRDiskMDStat extends ServerReportSection<SRDiskMDStat> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER_REPORT=1
    ;
    
    private int pkey;
    private int device_major;
    private int device_minor;
    private int total_partitions_min;
    private float total_partitions_avg;
    private int total_partitions_max;
    private int active_partitions_min;
    private float active_partitions_avg;
    private int active_partitions_max;
    private float rebuild_percent_min;
    private float rebuild_percent_avg;
    private float rebuild_percent_max;
    private int rebuild_rate_min;
    private float rebuild_rate_avg;
    private int rebuild_rate_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 2: return Integer.valueOf(device_major);
            case 3: return Integer.valueOf(device_minor);
            case 4: return Integer.valueOf(total_partitions_min);
            case 5: return new Float(total_partitions_avg);
            case 6: return Integer.valueOf(total_partitions_max);
            case 7: return Integer.valueOf(active_partitions_min);
            case 8: return new Float(active_partitions_avg);
            case 9: return Integer.valueOf(active_partitions_max);
            case 10: return rebuild_percent_min==Float.NaN?null:new Float(rebuild_percent_min);
            case 11: return rebuild_percent_avg==Float.NaN?null:new Float(rebuild_percent_avg);
            case 12: return rebuild_percent_max==Float.NaN?null:new Float(rebuild_percent_max);
            case 13: return rebuild_rate_min==-1?null:Integer.valueOf(rebuild_rate_min);
            case 14: return rebuild_rate_avg==Float.NaN?null:new Float(rebuild_rate_avg);
            case 15: return rebuild_rate_max==-1?null:Integer.valueOf(rebuild_rate_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPKey() {
        return pkey;
    }

    public int getDeviceMajor() {
        return device_major;
    }

    public int getDeviceMinor() {
        return device_minor;
    }

    public int getTotalPartitionsMin() {
        return total_partitions_min;
    }
    
    public float getTotalPartitionsAvg() {
        return total_partitions_avg;
    }
    
    public int getTotalPartitionsMax() {
        return total_partitions_max;
    }

    public int getActivePartitionsMin() {
        return active_partitions_min;
    }
    
    public float getActivePartitionsAvg() {
        return active_partitions_avg;
    }
    
    public int getActivePartitionsMax() {
        return active_partitions_max;
    }

    public float getRebuildPercentMin() {
        return rebuild_percent_min;
    }
    
    public float getRebuildPercentAvg() {
        return rebuild_percent_avg;
    }
    
    public float getRebuildPercentMax() {
        return rebuild_percent_max;
    }

    public int getRebuildRateMin() {
        return rebuild_rate_min;
    }
    
    public float getRebuildRateAvg() {
        return rebuild_rate_avg;
    }
    
    public int getRebuildRateMax() {
        return rebuild_rate_max;
    }

    public Integer getKey() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_DISK_MDSTAT;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server_report=result.getInt(2);
        device_major=result.getInt(3);
        device_minor=result.getInt(4);
        total_partitions_min=result.getInt(5);
        total_partitions_avg=result.getFloat(6);
        total_partitions_max=result.getInt(7);
        active_partitions_min=result.getInt(8);
        active_partitions_avg=result.getFloat(9);
        active_partitions_max=result.getInt(10);
        rebuild_percent_min=result.getFloat(11);
        if(result.wasNull()) rebuild_percent_min=Float.NaN;
        rebuild_percent_avg=result.getFloat(12);
        if(result.wasNull()) rebuild_percent_avg=Float.NaN;
        rebuild_percent_max=result.getFloat(13);
        if(result.wasNull()) rebuild_percent_max=Float.NaN;
        rebuild_rate_min=result.getInt(14);
        if(result.wasNull()) rebuild_rate_min=-1;
        rebuild_rate_avg=result.getFloat(15);
        if(result.wasNull()) rebuild_rate_avg=Float.NaN;
        rebuild_rate_max=result.getInt(16);
        if(result.wasNull()) rebuild_rate_max=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server_report=in.readCompressedInt();
        device_major=in.readCompressedInt();
        device_minor=in.readCompressedInt();
        total_partitions_min=in.readCompressedInt();
        total_partitions_avg=in.readFloat();
        total_partitions_max=in.readCompressedInt();
        active_partitions_min=in.readCompressedInt();
        active_partitions_avg=in.readFloat();
        active_partitions_max=in.readCompressedInt();
        rebuild_percent_min=in.readFloat();
        rebuild_percent_avg=in.readFloat();
        rebuild_percent_max=in.readFloat();
        rebuild_rate_min=in.readCompressedInt();
        rebuild_rate_avg=in.readFloat();
        rebuild_rate_max=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server_report);
        out.writeCompressedInt(device_major);
        out.writeCompressedInt(device_minor);
        out.writeCompressedInt(total_partitions_min);
        out.writeFloat(total_partitions_avg);
        out.writeCompressedInt(total_partitions_max);
        out.writeCompressedInt(active_partitions_min);
        out.writeFloat(active_partitions_avg);
        out.writeCompressedInt(active_partitions_max);
        out.writeFloat(rebuild_percent_min);
        out.writeFloat(rebuild_percent_avg);
        out.writeFloat(rebuild_percent_max);
        out.writeCompressedInt(rebuild_rate_min);
        out.writeFloat(rebuild_rate_avg);
        out.writeCompressedInt(rebuild_rate_max);
    }
}