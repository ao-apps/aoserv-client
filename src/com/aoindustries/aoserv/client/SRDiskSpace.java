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
final public class SRDiskSpace extends ServerReportSection<SRDiskSpace> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER_REPORT=1
    ;
    
    private int pkey;
    private int device_major;
    private int device_minor;
    private long total_min;
    private float total_avg;
    private long total_max;
    private long used_min;
    private float used_avg;
    private long used_max;
    private long free_min;
    private float free_avg;
    private long free_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 2: return Integer.valueOf(device_major);
            case 3: return Integer.valueOf(device_minor);
            case 4: return Long.valueOf(total_min);
            case 5: return new Float(total_avg);
            case 6: return Long.valueOf(total_max);
            case 7: return Long.valueOf(used_min);
            case 8: return new Float(used_avg);
            case 9: return Long.valueOf(used_max);
            case 10: return Long.valueOf(free_min);
            case 11: return new Float(free_avg);
            case 12: return Long.valueOf(free_max);
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

    public long getTotalMin() {
        return total_min;
    }
    
    public float getTotalAvg() {
        return total_avg;
    }
    
    public long getTotalMax() {
        return total_max;
    }

    public long getUsedMin() {
        return used_min;
    }
    
    public float getUsedAvg() {
        return used_avg;
    }
    
    public long getUsedMax() {
        return used_max;
    }

    public long getFreeMin() {
        return free_min;
    }
    
    public float getFreeAvg() {
        return free_avg;
    }
    
    public long getFreeMax() {
        return free_max;
    }

    public Integer getKey() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_DISK_SPACE;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server_report=result.getInt(2);
        device_major=result.getInt(3);
        device_minor=result.getInt(4);
        total_min=result.getLong(5);
        total_avg=result.getFloat(6);
        total_max=result.getLong(7);
        used_min=result.getLong(8);
        used_avg=result.getFloat(9);
        used_max=result.getLong(10);
        free_min=result.getLong(11);
        free_avg=result.getFloat(12);
        free_max=result.getLong(13);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server_report=in.readCompressedInt();
        device_major=in.readCompressedInt();
        device_minor=in.readCompressedInt();
        total_min=in.readLong();
        total_avg=in.readFloat();
        total_max=in.readLong();
        used_min=in.readLong();
        used_avg=in.readFloat();
        used_max=in.readLong();
        free_min=in.readLong();
        free_avg=in.readFloat();
        free_max=in.readLong();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server_report);
        out.writeCompressedInt(device_major);
        out.writeCompressedInt(device_minor);
        out.writeLong(total_min);
        out.writeFloat(total_avg);
        out.writeLong(total_max);
        out.writeLong(used_min);
        out.writeFloat(used_avg);
        out.writeLong(used_max);
        out.writeLong(free_min);
        out.writeFloat(free_avg);
        out.writeLong(free_max);
    }
}