package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
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
final public class SRProcesses extends ServerReportSection<SRProcesses> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private int total_sleep_min;
    private float total_sleep_avg;
    private int total_sleep_max;
    private int user_sleep_min;
    private float user_sleep_avg;
    private int user_sleep_max;
    private int total_run_min;
    private float total_run_avg;
    private int total_run_max;
    private int user_run_min;
    private float user_run_avg;
    private int user_run_max;
    private int total_zombie_min;
    private float total_zombie_avg;
    private int total_zombie_max;
    private int user_zombie_min;
    private float user_zombie_avg;
    private int user_zombie_max;
    private int total_trace_min;
    private float total_trace_avg;
    private int total_trace_max;
    private int user_trace_min;
    private float user_trace_avg;
    private int user_trace_max;
    private int total_uninterruptible_min;
    private float total_uninterruptible_avg;
    private int total_uninterruptible_max;
    private int user_uninterruptible_min;
    private float user_uninterruptible_avg;
    private int user_uninterruptible_max;
    private int total_unknown_min;
    private float total_unknown_avg;
    private int total_unknown_max;
    private int user_unknown_min;
    private float user_unknown_avg;
    private int user_unknown_max;
    private float rate_min;
    private float rate_avg;
    private float rate_max;
    private float context_min;
    private float context_avg;
    private float context_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return total_sleep_min==-1?null:Integer.valueOf(total_sleep_min);
            case 2: return total_sleep_avg==Float.NaN?null:Float.valueOf(total_sleep_avg);
            case 3: return total_sleep_max==-1?null:Integer.valueOf(total_sleep_max);
            case 4: return Integer.valueOf(user_sleep_min);
            case 5: return Float.valueOf(user_sleep_avg);
            case 6: return Integer.valueOf(user_sleep_max);
            case 7: return total_run_min==-1?null:Integer.valueOf(total_run_min);
            case 8: return total_run_avg==Float.NaN?null:Float.valueOf(total_run_avg);
            case 9: return total_run_max==-1?null:Integer.valueOf(total_run_max);
            case 10: return Integer.valueOf(user_run_min);
            case 11: return Float.valueOf(user_run_avg);
            case 12: return Integer.valueOf(user_run_max);
            case 13: return total_zombie_min==-1?null:Integer.valueOf(total_zombie_min);
            case 14: return total_zombie_avg==Float.NaN?null:Float.valueOf(total_zombie_avg);
            case 15: return total_zombie_max==-1?null:Integer.valueOf(total_zombie_max);
            case 16: return Integer.valueOf(user_zombie_min);
            case 17: return Float.valueOf(user_zombie_avg);
            case 18: return Integer.valueOf(user_zombie_max);
            case 19: return total_trace_min==-1?null:Integer.valueOf(total_trace_min);
            case 20: return total_trace_avg==Float.NaN?null:Float.valueOf(total_trace_avg);
            case 21: return total_trace_max==-1?null:Integer.valueOf(total_trace_max);
            case 22: return Integer.valueOf(user_trace_min);
            case 23: return Float.valueOf(user_trace_avg);
            case 24: return Integer.valueOf(user_trace_max);
            case 25: return total_uninterruptible_min==-1?null:Integer.valueOf(total_uninterruptible_min);
            case 26: return total_uninterruptible_avg==Float.NaN?null:Float.valueOf(total_uninterruptible_avg);
            case 27: return total_uninterruptible_max==-1?null:Integer.valueOf(total_uninterruptible_max);
            case 28: return Integer.valueOf(user_uninterruptible_min);
            case 29: return Float.valueOf(user_uninterruptible_avg);
            case 30: return Integer.valueOf(user_uninterruptible_max);
            case 31: return total_unknown_min==-1?null:Integer.valueOf(total_unknown_min);
            case 32: return total_unknown_avg==Float.NaN?null:Float.valueOf(total_unknown_avg);
            case 33: return total_unknown_max==-1?null:Integer.valueOf(total_unknown_max);
            case 34: return Integer.valueOf(user_unknown_min);
            case 35: return Float.valueOf(user_unknown_avg);
            case 36: return Integer.valueOf(user_unknown_max);
            case 37: return rate_min==Float.NaN?null:Float.valueOf(rate_min);
            case 38: return rate_avg==Float.NaN?null:Float.valueOf(rate_avg);
            case 39: return rate_max==Float.NaN?null:Float.valueOf(rate_max);
            case 40: return context_min==Float.NaN?null:Float.valueOf(context_min);
            case 41: return context_avg==Float.NaN?null:Float.valueOf(context_avg);
            case 42: return context_max==Float.NaN?null:Float.valueOf(context_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }
    
    public int getTotalSleepMin() {
        return total_sleep_min;
    }
    
    public float getTotalSleepAvg() {
        return total_sleep_avg;
    }
    
    public int getTotalSleepMax() {
        return total_sleep_max;
    }

    public int getUserSleepMin() {
        return user_sleep_min;
    }
    
    public float getUserSleepAvg() {
        return user_sleep_avg;
    }
    
    public int getUserSleepMax() {
        return user_sleep_max;
    }

    public int getTotalRunMin() {
        return total_run_min;
    }
    
    public float getTotalRunAvg() {
        return total_run_avg;
    }
    
    public int getTotalRunMax() {
        return total_run_max;
    }

    public int getUserRunMin() {
        return user_run_min;
    }
    
    public float getUserRunAvg() {
        return user_run_avg;
    }
    
    public int getUserRunMax() {
        return user_run_max;
    }

    public int getTotalZombieMin() {
        return total_zombie_min;
    }
    
    public float getTotalZombieAvg() {
        return total_zombie_avg;
    }
    
    public int getTotalZombieMax() {
        return total_zombie_max;
    }

    public int getUserZombieMin() {
        return user_zombie_min;
    }
    
    public float getUserZombieAvg() {
        return user_zombie_avg;
    }
    
    public int getUserZombieMax() {
        return user_zombie_max;
    }

    public int getTotalTraceMin() {
        return total_trace_min;
    }
    
    public float getTotalTraceAvg() {
        return total_trace_avg;
    }
    
    public int getTotalTraceMax() {
        return total_trace_max;
    }

    public int getUserTraceMin() {
        return user_trace_min;
    }
    
    public float getUserTraceAvg() {
        return user_trace_avg;
    }
    
    public int getUserTraceMax() {
        return user_trace_max;
    }

    public int getTotalUninterruptibleMin() {
        return total_uninterruptible_min;
    }
    
    public float getotalUninterruptibleAvg() {
        return total_uninterruptible_avg;
    }
    
    public int getotalUninterruptibleMax() {
        return total_uninterruptible_max;
    }

    public int getUserUninterruptibleMin() {
        return user_uninterruptible_min;
    }
    
    public float getUserUninterruptibleAvg() {
        return user_uninterruptible_avg;
    }
    
    public int getUserUninterruptibleMax() {
        return user_uninterruptible_max;
    }

    public int getTotalUnknownMin() {
        return total_unknown_min;
    }
    
    public float getTotalUnknownAvg() {
        return total_unknown_avg;
    }
    
    public int getTotalUnknownMax() {
        return total_unknown_max;
    }

    public int getUserUnknownMin() {
        return user_unknown_min;
    }
    
    public float getUserUnknownAvg() {
        return user_unknown_avg;
    }
    
    public int getUserUnknownMax() {
        return user_unknown_max;
    }

    public float getRateMin() {
        return rate_min;
    }
    
    public float getRateAvg() {
        return rate_avg;
    }
    
    public float getRateMax() {
        return rate_max;
    }

    public float getContextMin() {
        return context_min;
    }
    
    public float getContextAvg() {
        return context_avg;
    }
    
    public float getContextMax() {
        return context_max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_PROCESSES;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        total_sleep_min=result.getInt(2);
        if(result.wasNull()) total_sleep_min=-1;
        total_sleep_avg=result.getFloat(3);
        if(result.wasNull()) total_sleep_avg=Float.NaN;
        total_sleep_max=result.getInt(4);
        if(result.wasNull()) total_sleep_max=-1;
        user_sleep_min=result.getInt(5);
        user_sleep_avg=result.getFloat(6);
        user_sleep_max=result.getInt(7);
        total_run_min=result.getInt(8);
        if(result.wasNull()) total_run_min=-1;
        total_run_avg=result.getFloat(9);
        if(result.wasNull()) total_run_avg=Float.NaN;
        total_run_max=result.getInt(10);
        if(result.wasNull()) total_run_max=-1;
        user_run_min=result.getInt(11);
        user_run_avg=result.getFloat(12);
        user_run_max=result.getInt(13);
        total_zombie_min=result.getInt(14);
        if(result.wasNull()) total_zombie_min=-1;
        total_zombie_avg=result.getFloat(15);
        if(result.wasNull()) total_zombie_avg=Float.NaN;
        total_zombie_max=result.getInt(16);
        if(result.wasNull()) total_zombie_max=-1;
        user_zombie_min=result.getInt(17);
        user_zombie_avg=result.getFloat(18);
        user_zombie_max=result.getInt(19);
        total_trace_min=result.getInt(20);
        if(result.wasNull()) total_trace_min=-1;
        total_trace_avg=result.getFloat(21);
        if(result.wasNull()) total_trace_avg=Float.NaN;
        total_trace_max=result.getInt(22);
        if(result.wasNull()) total_trace_max=-1;
        user_trace_min=result.getInt(23);
        user_trace_avg=result.getFloat(24);
        user_trace_max=result.getInt(25);
        total_uninterruptible_min=result.getInt(26);
        if(result.wasNull()) total_uninterruptible_min=-1;
        total_uninterruptible_avg=result.getFloat(27);
        if(result.wasNull()) total_uninterruptible_avg=Float.NaN;
        total_uninterruptible_max=result.getInt(28);
        if(result.wasNull()) total_uninterruptible_max=-1;
        user_uninterruptible_min=result.getInt(29);
        user_uninterruptible_avg=result.getFloat(30);
        user_uninterruptible_max=result.getInt(31);
        total_unknown_min=result.getInt(32);
        if(result.wasNull()) total_unknown_min=-1;
        total_unknown_avg=result.getFloat(33);
        if(result.wasNull()) total_unknown_avg=Float.NaN;
        total_unknown_max=result.getInt(34);
        if(result.wasNull()) total_unknown_max=-1;
        user_unknown_min=result.getInt(35);
        user_unknown_avg=result.getFloat(36);
        user_unknown_max=result.getInt(37);
        rate_min=result.getFloat(38);
        if(result.wasNull()) rate_min=Float.NaN;
        rate_avg=result.getFloat(39);
        if(result.wasNull()) rate_avg=Float.NaN;
        rate_max=result.getFloat(40);
        if(result.wasNull()) rate_max=Float.NaN;
        context_min=result.getFloat(41);
        if(result.wasNull()) context_min=Float.NaN;
        context_avg=result.getFloat(42);
        if(result.wasNull()) context_avg=Float.NaN;
        context_max=result.getFloat(43);
        if(result.wasNull()) context_max=Float.NaN;
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        total_sleep_min=in.readCompressedInt();
        total_sleep_avg=in.readFloat();
        total_sleep_max=in.readCompressedInt();
        user_sleep_min=in.readCompressedInt();
        user_sleep_avg=in.readFloat();
        user_sleep_max=in.readCompressedInt();
        total_run_min=in.readCompressedInt();
        total_run_avg=in.readFloat();
        total_run_max=in.readCompressedInt();
        user_run_min=in.readCompressedInt();
        user_run_avg=in.readFloat();
        user_run_max=in.readCompressedInt();
        total_zombie_min=in.readCompressedInt();
        total_zombie_avg=in.readFloat();
        total_zombie_max=in.readCompressedInt();
        user_zombie_min=in.readCompressedInt();
        user_zombie_avg=in.readFloat();
        user_zombie_max=in.readCompressedInt();
        total_trace_min=in.readCompressedInt();
        total_trace_avg=in.readFloat();
        total_trace_max=in.readCompressedInt();
        user_trace_min=in.readCompressedInt();
        user_trace_avg=in.readFloat();
        user_trace_max=in.readCompressedInt();
        total_uninterruptible_min=in.readCompressedInt();
        total_uninterruptible_avg=in.readFloat();
        total_uninterruptible_max=in.readCompressedInt();
        user_uninterruptible_min=in.readCompressedInt();
        user_uninterruptible_avg=in.readFloat();
        user_uninterruptible_max=in.readCompressedInt();
        total_unknown_min=in.readCompressedInt();
        total_unknown_avg=in.readFloat();
        total_unknown_max=in.readCompressedInt();
        user_unknown_min=in.readCompressedInt();
        user_unknown_avg=in.readFloat();
        user_unknown_max=in.readCompressedInt();
        rate_min=in.readFloat();
        rate_avg=in.readFloat();
        rate_max=in.readFloat();
        context_min=in.readFloat();
        context_avg=in.readFloat();
        context_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeCompressedInt(total_sleep_min);
        out.writeFloat(total_sleep_avg);
        out.writeCompressedInt(total_sleep_max);
        out.writeCompressedInt(user_sleep_min);
        out.writeFloat(user_sleep_avg);
        out.writeCompressedInt(user_sleep_max);
        out.writeCompressedInt(total_run_min);
        out.writeFloat(total_run_avg);
        out.writeCompressedInt(total_run_max);
        out.writeCompressedInt(user_run_min);
        out.writeFloat(user_run_avg);
        out.writeCompressedInt(user_run_max);
        out.writeCompressedInt(total_zombie_min);
        out.writeFloat(total_zombie_avg);
        out.writeCompressedInt(total_zombie_max);
        out.writeCompressedInt(user_zombie_min);
        out.writeFloat(user_zombie_avg);
        out.writeCompressedInt(user_zombie_max);
        out.writeCompressedInt(total_trace_min);
        out.writeFloat(total_trace_avg);
        out.writeCompressedInt(total_trace_max);
        out.writeCompressedInt(user_trace_min);
        out.writeFloat(user_trace_avg);
        out.writeCompressedInt(user_trace_max);
        out.writeCompressedInt(total_uninterruptible_min);
        out.writeFloat(total_uninterruptible_avg);
        out.writeCompressedInt(total_uninterruptible_max);
        out.writeCompressedInt(user_uninterruptible_min);
        out.writeFloat(user_uninterruptible_avg);
        out.writeCompressedInt(user_uninterruptible_max);
        out.writeCompressedInt(total_unknown_min);
        out.writeFloat(total_unknown_avg);
        out.writeCompressedInt(total_unknown_max);
        out.writeCompressedInt(user_unknown_min);
        out.writeFloat(user_unknown_avg);
        out.writeCompressedInt(user_unknown_max);
        out.writeFloat(rate_min);
        out.writeFloat(rate_avg);
        out.writeFloat(rate_max);
        out.writeFloat(context_min);
        out.writeFloat(context_avg);
        out.writeFloat(context_max);
    }
}