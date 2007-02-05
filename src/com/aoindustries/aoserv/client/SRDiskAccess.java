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
final public class SRDiskAccess extends ServerReportSection<SRDiskAccess> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER_REPORT=1
    ;
    
    private int pkey;
    private int device_major;
    private int device_minor;
    private long blocks_min;
    private float blocks_avg;
    private long blocks_max;
    private float rios_min;
    private float rios_avg;
    private float rios_max;
    private float rmerges_min;
    private float rmerges_avg;
    private float rmerges_max;
    private float rsect_min;
    private float rsect_avg;
    private float rsect_max;
    private float ruse_min;
    private float ruse_avg;
    private float ruse_max;
    private float wios_min;
    private float wios_avg;
    private float wios_max;
    private float wmerge_min;
    private float wmerge_avg;
    private float wmerge_max;
    private float wsect_min;
    private float wsect_avg;
    private float wsect_max;
    private float wuse_min;
    private float wuse_avg;
    private float wuse_max;
    private int running_min;
    private float running_avg;
    private int running_max;
    private float use_min;
    private float use_avg;
    private float use_max;
    private float aveq_min;
    private float aveq_avg;
    private float aveq_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 2: return Integer.valueOf(device_major);
            case 3: return Integer.valueOf(device_minor);
            case 4: return Long.valueOf(blocks_min);
            case 5: return new Float(blocks_avg);
            case 6: return Long.valueOf(blocks_max);
            case 7: return new Float(rios_min);
            case 8: return new Float(rios_avg);
            case 9: return new Float(rios_max);
            case 10: return new Float(rmerges_min);
            case 11: return new Float(rmerges_avg);
            case 12: return new Float(rmerges_max);
            case 13: return new Float(rsect_min);
            case 14: return new Float(rsect_avg);
            case 15: return new Float(rsect_max);
            case 16: return new Float(ruse_min);
            case 17: return new Float(ruse_avg);
            case 18: return new Float(ruse_max);
            case 19: return new Float(wios_min);
            case 20: return new Float(wios_avg);
            case 21: return new Float(wios_max);
            case 22: return new Float(wmerge_min);
            case 23: return new Float(wmerge_avg);
            case 24: return new Float(wmerge_max);
            case 25: return new Float(wsect_min);
            case 26: return new Float(wsect_avg);
            case 27: return new Float(wsect_max);
            case 28: return new Float(wuse_min);
            case 29: return new Float(wuse_avg);
            case 30: return new Float(wuse_max);
            case 31: return Integer.valueOf(running_min);
            case 32: return new Float(running_avg);
            case 33: return Integer.valueOf(running_max);
            case 34: return new Float(use_min);
            case 35: return new Float(use_avg);
            case 36: return new Float(use_max);
            case 37: return new Float(aveq_min);
            case 38: return new Float(aveq_avg);
            case 39: return new Float(aveq_max);
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

    public long getBlocksMin() {
        return blocks_min;
    }
    
    public float getBlocksAvg() {
        return blocks_avg;
    }
    
    public long getBlocksMax() {
        return blocks_max;
    }

    public float getReadIOsMin() {
        return rios_min;
    }
    
    public float getReadIOsAvg() {
        return rios_avg;
    }
    
    public float getReadIOsMax() {
        return rios_max;
    }

    public float getReadMergesMin() {
        return rmerges_min;
    }
    
    public float getReadMergesAvg() {
        return rmerges_avg;
    }
    
    public float getReadMergesMax() {
        return rmerges_max;
    }

    public float getReadSectorsMin() {
        return rsect_min;
    }
    
    public float getReadSectorsAvg() {
        return rsect_avg;
    }
    
    public float getReadSectorsMax() {
        return rsect_max;
    }

    public float getReadUsesMin() {
        return ruse_min;
    }
    
    public float getReadUsesAvg() {
        return ruse_avg;
    }
    
    public float getReadUsesMax() {
        return ruse_max;
    }

    public float getWriteIOsMin() {
        return wios_min;
    }
    
    public float getWriteIOsAvg() {
        return wios_avg;
    }
    
    public float getWriteIOsMax() {
        return wios_max;
    }

    public float getWriteMergesMin() {
        return wmerge_min;
    }
    
    public float getWriteMergesAvg() {
        return wmerge_avg;
    }
    
    public float getWriteMergesMax() {
        return wmerge_max;
    }

    public float getWriteSectorsMin() {
        return wsect_min;
    }
    
    public float getWriteSectorsAvg() {
        return wsect_avg;
    }
    
    public float getWriteSectorsMax() {
        return wsect_max;
    }

    public float getWriteUsesMin() {
        return wuse_min;
    }
    
    public float getWriteUsesAvg() {
        return wuse_avg;
    }
    
    public float getWriteUsesMax() {
        return wuse_max;
    }

    public int getRunningMin() {
        return running_min;
    }
    
    public float getRunningAvg() {
        return running_avg;
    }
    
    public int getRunningMax() {
        return running_max;
    }

    public float getUsesMin() {
        return use_min;
    }
    
    public float getUsesAvg() {
        return use_avg;
    }
    
    public float getUsesMax() {
        return use_max;
    }

    public float getAveqMin() {
        return aveq_min;
    }
    
    public float getAveqAvg() {
        return aveq_avg;
    }
    
    public float getAveqMax() {
        return aveq_max;
    }

    public Integer getKey() {
	return pkey;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_DISK_ACCESS;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server_report=result.getInt(2);
        device_major=result.getInt(3);
        device_minor=result.getInt(4);
        blocks_min=result.getLong(5);
        blocks_avg=result.getFloat(6);
        blocks_max=result.getLong(7);
        rios_min=result.getFloat(8);
        rios_avg=result.getFloat(9);
        rios_max=result.getFloat(10);
        rmerges_min=result.getFloat(11);
        rmerges_avg=result.getFloat(12);
        rmerges_max=result.getFloat(13);
        rsect_min=result.getFloat(14);
        rsect_avg=result.getFloat(15);
        rsect_max=result.getFloat(16);
        ruse_min=result.getFloat(17);
        ruse_avg=result.getFloat(18);
        ruse_max=result.getFloat(19);
        wios_min=result.getFloat(20);
        wios_avg=result.getFloat(21);
        wios_max=result.getFloat(22);
        wmerge_min=result.getFloat(23);
        wmerge_avg=result.getFloat(24);
        wmerge_max=result.getFloat(25);
        wsect_min=result.getFloat(26);
        wsect_avg=result.getFloat(27);
        wsect_max=result.getFloat(28);
        wuse_min=result.getFloat(29);
        wuse_avg=result.getFloat(30);
        wuse_max=result.getFloat(31);
        running_min=result.getInt(32);
        running_avg=result.getFloat(33);
        running_max=result.getInt(34);
        use_min=result.getFloat(35);
        use_avg=result.getFloat(36);
        use_max=result.getFloat(37);
        aveq_min=result.getFloat(38);
        aveq_avg=result.getFloat(39);
        aveq_max=result.getFloat(40);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server_report=in.readCompressedInt();
        device_major=in.readCompressedInt();
        device_minor=in.readCompressedInt();
        blocks_min=in.readLong();
        blocks_avg=in.readFloat();
        blocks_max=in.readLong();
        rios_min=in.readFloat();
        rios_avg=in.readFloat();
        rios_max=in.readFloat();
        rmerges_min=in.readFloat();
        rmerges_avg=in.readFloat();
        rmerges_max=in.readFloat();
        rsect_min=in.readFloat();
        rsect_avg=in.readFloat();
        rsect_max=in.readFloat();
        ruse_min=in.readFloat();
        ruse_avg=in.readFloat();
        ruse_max=in.readFloat();
        wios_min=in.readFloat();
        wios_avg=in.readFloat();
        wios_max=in.readFloat();
        wmerge_min=in.readFloat();
        wmerge_avg=in.readFloat();
        wmerge_max=in.readFloat();
        wsect_min=in.readFloat();
        wsect_avg=in.readFloat();
        wsect_max=in.readFloat();
        wuse_min=in.readFloat();
        wuse_avg=in.readFloat();
        wuse_max=in.readFloat();
        running_min=in.readCompressedInt();
        running_avg=in.readFloat();
        running_max=in.readCompressedInt();
        use_min=in.readFloat();
        use_avg=in.readFloat();
        use_max=in.readFloat();
        aveq_min=in.readFloat();
        aveq_avg=in.readFloat();
        aveq_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server_report);
        out.writeCompressedInt(device_major);
        out.writeCompressedInt(device_minor);
        out.writeLong(blocks_min);
        out.writeFloat(blocks_avg);
        out.writeLong(blocks_max);
        out.writeFloat(rios_min);
        out.writeFloat(rios_avg);
        out.writeFloat(rios_max);
        out.writeFloat(rmerges_min);
        out.writeFloat(rmerges_avg);
        out.writeFloat(rmerges_max);
        out.writeFloat(rsect_min);
        out.writeFloat(rsect_avg);
        out.writeFloat(rsect_max);
        out.writeFloat(ruse_min);
        out.writeFloat(ruse_avg);
        out.writeFloat(ruse_max);
        out.writeFloat(wios_min);
        out.writeFloat(wios_avg);
        out.writeFloat(wios_max);
        out.writeFloat(wmerge_min);
        out.writeFloat(wmerge_avg);
        out.writeFloat(wmerge_max);
        out.writeFloat(wsect_min);
        out.writeFloat(wsect_avg);
        out.writeFloat(wsect_max);
        out.writeFloat(wuse_min);
        out.writeFloat(wuse_avg);
        out.writeFloat(wuse_max);
        out.writeCompressedInt(running_min);
        out.writeFloat(running_avg);
        out.writeCompressedInt(running_max);
        out.writeFloat(use_min);
        out.writeFloat(use_avg);
        out.writeFloat(use_max);
        out.writeFloat(aveq_min);
        out.writeFloat(aveq_avg);
        out.writeFloat(aveq_max);
    }
}