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
final public class SRSwapSize extends ServerReportSection<SRSwapSize> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER_REPORT=1
    ;
    
    private int pkey;
    private int device_major;
    private int device_minor;
    private int total_min;
    private float total_avg;
    private int total_max;
    private int used_min;
    private float used_avg;
    private int used_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 2: return Integer.valueOf(device_major);
            case 3: return Integer.valueOf(device_minor);
            case 4: return Integer.valueOf(total_min);
            case 5: return Float.valueOf(total_avg);
            case 6: return Integer.valueOf(total_max);
            case 7: return Integer.valueOf(used_min);
            case 8: return Float.valueOf(used_avg);
            case 9: return Integer.valueOf(used_max);
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

    public int getTotalMin() {
        return total_min;
    }
    
    public float getTotalAvg() {
        return total_avg;
    }
    
    public int getTotalMax() {
        return total_max;
    }

    public int getUsedMin() {
        return used_min;
    }
    
    public float getUsedAvg() {
        return used_avg;
    }
    
    public int getUsedMax() {
        return used_max;
    }

    public Integer getKey() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_SWAP_SIZE;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server_report=result.getInt(2);
        device_major=result.getInt(3);
        device_minor=result.getInt(4);
        total_min=result.getInt(5);
        total_avg=result.getFloat(6);
        total_max=result.getInt(7);
        used_min=result.getInt(8);
        used_avg=result.getFloat(9);
        used_max=result.getInt(10);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server_report=in.readCompressedInt();
        device_major=in.readCompressedInt();
        device_minor=in.readCompressedInt();
        total_min=in.readCompressedInt();
        total_avg=in.readFloat();
        total_max=in.readCompressedInt();
        used_min=in.readCompressedInt();
        used_avg=in.readFloat();
        used_max=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server_report);
        out.writeCompressedInt(device_major);
        out.writeCompressedInt(device_minor);
        out.writeCompressedInt(total_min);
        out.writeFloat(total_avg);
        out.writeCompressedInt(total_max);
        out.writeCompressedInt(used_min);
        out.writeFloat(used_avg);
        out.writeCompressedInt(used_max);
    }
}