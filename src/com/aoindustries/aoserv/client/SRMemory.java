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
final public class SRMemory extends ServerReportSection<SRMemory> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private int mem_total_min;
    private float mem_total_avg;
    private int mem_total_max;
    private int mem_free_min;
    private float mem_free_avg;
    private int mem_free_max;
    private int mem_shared_min;
    private float mem_shared_avg;
    private int mem_shared_max;
    private int buffers_min;
    private float buffers_avg;
    private int buffers_max;
    private int cached_min;
    private float cached_avg;
    private int cached_max;
    private int swap_cached_min;
    private float swap_cached_avg;
    private int swap_cached_max;
    private int active_min;
    private float active_avg;
    private int active_max;
    private int inact_dirty_min;
    private float inact_dirty_avg;
    private int inact_dirty_max;
    private int inact_clean_min;
    private float inact_clean_avg;
    private int inact_clean_max;
    private int inact_target_min;
    private float inact_target_avg;
    private int inact_target_max;
    private int high_total_min;
    private float high_total_avg;
    private int high_total_max;
    private int high_free_min;
    private float high_free_avg;
    private int high_free_max;
    private int low_total_min;
    private float low_total_avg;
    private int low_total_max;
    private int low_free_min;
    private float low_free_avg;
    private int low_free_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return Integer.valueOf(mem_total_min);
            case 2: return new Float(mem_total_avg);
            case 3: return Integer.valueOf(mem_total_max);
            case 4: return Integer.valueOf(mem_free_min);
            case 5: return new Float(mem_free_avg);
            case 6: return Integer.valueOf(mem_free_max);
            case 7: return Integer.valueOf(mem_shared_min);
            case 8: return new Float(mem_shared_avg);
            case 9: return Integer.valueOf(mem_shared_max);
            case 10: return Integer.valueOf(buffers_min);
            case 11: return new Float(buffers_avg);
            case 12: return Integer.valueOf(buffers_max);
            case 13: return Integer.valueOf(cached_min);
            case 14: return new Float(cached_avg);
            case 15: return Integer.valueOf(cached_max);
            case 16: return Integer.valueOf(swap_cached_min);
            case 17: return new Float(swap_cached_avg);
            case 18: return Integer.valueOf(swap_cached_max);
            case 19: return Integer.valueOf(active_min);
            case 20: return new Float(active_avg);
            case 21: return Integer.valueOf(active_max);
            case 22: return Integer.valueOf(inact_dirty_min);
            case 23: return new Float(inact_dirty_avg);
            case 24: return Integer.valueOf(inact_dirty_max);
            case 25: return Integer.valueOf(inact_clean_min);
            case 26: return new Float(inact_clean_avg);
            case 27: return Integer.valueOf(inact_clean_max);
            case 28: return Integer.valueOf(inact_target_min);
            case 29: return new Float(inact_target_avg);
            case 30: return Integer.valueOf(inact_target_max);
            case 31: return Integer.valueOf(high_total_min);
            case 32: return new Float(high_total_avg);
            case 33: return Integer.valueOf(high_total_max);
            case 34: return Integer.valueOf(high_free_min);
            case 35: return new Float(high_free_avg);
            case 36: return Integer.valueOf(high_free_max);
            case 37: return Integer.valueOf(low_total_min);
            case 38: return new Float(low_total_avg);
            case 39: return Integer.valueOf(low_total_max);
            case 40: return Integer.valueOf(low_free_min);
            case 41: return new Float(low_free_avg);
            case 42: return Integer.valueOf(low_free_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }
    
    public int getMemTotalMin() {
        return mem_total_min;
    }
    
    public float getMemTotalAvg() {
        return mem_total_avg;
    }
    
    public int getMemTotalMax() {
        return mem_total_max;
    }

    public int getMemFreeMin() {
        return mem_free_min;
    }
    
    public float getMemFreeAvg() {
        return mem_free_avg;
    }
    
    public int getMemFreeMax() {
        return mem_free_max;
    }

    public int getMemSharedMin() {
        return mem_shared_min;
    }
    
    public float getMemSharedAvg() {
        return mem_shared_avg;
    }
    
    public int getMemSharedMax() {
        return mem_shared_max;
    }

    public int getBuffersMin() {
        return buffers_min;
    }
    
    public float getBuffersAvg() {
        return buffers_avg;
    }
    
    public int getBuffersMax() {
        return buffers_max;
    }

    public int getCachedMin() {
        return cached_min;
    }
    
    public float getCachedAvg() {
        return cached_avg;
    }
    
    public int getCachedMax() {
        return cached_max;
    }

    public int getSwapCachedMin() {
        return swap_cached_min;
    }
    
    public float getSwapCachedAvg() {
        return swap_cached_avg;
    }
    
    public int getSwapCachedMax() {
        return swap_cached_max;
    }

    public int getActiveMin() {
        return active_min;
    }
    
    public float getActiveAvg() {
        return active_avg;
    }
    
    public int getActiveMax() {
        return active_max;
    }

    public int getInactDirtyMin() {
        return inact_dirty_min;
    }
    
    public float getInactDirtyAvg() {
        return inact_dirty_avg;
    }
    
    public int getInactDirtyMax() {
        return inact_dirty_max;
    }

    public int getInactCleanMin() {
        return inact_clean_min;
    }
    
    public float getInactCleanAvg() {
        return inact_clean_avg;
    }
    
    public int getInactCleanMax() {
        return inact_clean_max;
    }

    public int getInactTargetMin() {
        return inact_target_min;
    }
    
    public float getInactTargetAvg() {
        return inact_target_avg;
    }
    
    public int getInactTargetMax() {
        return inact_target_max;
    }

    public int getHighTotalMin() {
        return high_total_min;
    }
    
    public float getHighTotalAvg() {
        return high_total_avg;
    }
    
    public int getHighTotalMax() {
        return high_total_max;
    }

    public int getHighFreeMin() {
        return high_free_min;
    }
    
    public float getHighFreeAvg() {
        return high_free_avg;
    }
    
    public int getHighFreeMax() {
        return high_free_max;
    }

    public int getLowTotalMin() {
        return low_total_min;
    }
    
    public float getLowTotalAvg() {
        return low_total_avg;
    }
    
    public int getLowTotalMax() {
        return low_total_max;
    }

    public int getLowFreeMin() {
        return low_free_min;
    }
    
    public float getLowFreeAvg() {
        return low_free_avg;
    }
    
    public int getLowFreeMax() {
        return low_free_max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_MEMORY;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        mem_total_min=result.getInt(2);
        mem_total_avg=result.getFloat(3);
        mem_total_max=result.getInt(4);
        mem_free_min=result.getInt(5);
        mem_free_avg=result.getFloat(6);
        mem_free_max=result.getInt(7);
        mem_shared_min=result.getInt(8);
        mem_shared_avg=result.getFloat(9);
        mem_shared_max=result.getInt(10);
        buffers_min=result.getInt(11);
        buffers_avg=result.getFloat(12);
        buffers_max=result.getInt(13);
        cached_min=result.getInt(14);
        cached_avg=result.getFloat(15);
        cached_max=result.getInt(16);
        swap_cached_min=result.getInt(17);
        swap_cached_avg=result.getFloat(18);
        swap_cached_max=result.getInt(19);
        active_min=result.getInt(20);
        active_avg=result.getFloat(21);
        active_max=result.getInt(22);
        inact_dirty_min=result.getInt(23);
        inact_dirty_avg=result.getFloat(24);
        inact_dirty_max=result.getInt(25);
        inact_clean_min=result.getInt(26);
        inact_clean_avg=result.getFloat(27);
        inact_clean_max=result.getInt(28);
        inact_target_min=result.getInt(29);
        inact_target_avg=result.getFloat(30);
        inact_target_max=result.getInt(31);
        high_total_min=result.getInt(32);
        high_total_avg=result.getFloat(33);
        high_total_max=result.getInt(34);
        high_free_min=result.getInt(35);
        high_free_avg=result.getFloat(36);
        high_free_max=result.getInt(37);
        low_total_min=result.getInt(38);
        low_total_avg=result.getFloat(39);
        low_total_max=result.getInt(40);
        low_free_min=result.getInt(41);
        low_free_avg=result.getFloat(42);
        low_free_max=result.getInt(43);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        mem_total_min=in.readCompressedInt();
        mem_total_avg=in.readFloat();
        mem_total_max=in.readCompressedInt();
        mem_free_min=in.readCompressedInt();
        mem_free_avg=in.readFloat();
        mem_free_max=in.readCompressedInt();
        mem_shared_min=in.readCompressedInt();
        mem_shared_avg=in.readFloat();
        mem_shared_max=in.readCompressedInt();
        buffers_min=in.readCompressedInt();
        buffers_avg=in.readFloat();
        buffers_max=in.readCompressedInt();
        cached_min=in.readCompressedInt();
        cached_avg=in.readFloat();
        cached_max=in.readCompressedInt();
        swap_cached_min=in.readCompressedInt();
        swap_cached_avg=in.readFloat();
        swap_cached_max=in.readCompressedInt();
        active_min=in.readCompressedInt();
        active_avg=in.readFloat();
        active_max=in.readCompressedInt();
        inact_dirty_min=in.readCompressedInt();
        inact_dirty_avg=in.readFloat();
        inact_dirty_max=in.readCompressedInt();
        inact_clean_min=in.readCompressedInt();
        inact_clean_avg=in.readFloat();
        inact_clean_max=in.readCompressedInt();
        inact_target_min=in.readCompressedInt();
        inact_target_avg=in.readFloat();
        inact_target_max=in.readCompressedInt();
        high_total_min=in.readCompressedInt();
        high_total_avg=in.readFloat();
        high_total_max=in.readCompressedInt();
        high_free_min=in.readCompressedInt();
        high_free_avg=in.readFloat();
        high_free_max=in.readCompressedInt();
        low_total_min=in.readCompressedInt();
        low_total_avg=in.readFloat();
        low_total_max=in.readCompressedInt();
        low_free_min=in.readCompressedInt();
        low_free_avg=in.readFloat();
        low_free_max=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeCompressedInt(mem_total_min);
        out.writeFloat(mem_total_avg);
        out.writeCompressedInt(mem_total_max);
        out.writeCompressedInt(mem_free_min);
        out.writeFloat(mem_free_avg);
        out.writeCompressedInt(mem_free_max);
        out.writeCompressedInt(mem_shared_min);
        out.writeFloat(mem_shared_avg);
        out.writeCompressedInt(mem_shared_max);
        out.writeCompressedInt(buffers_min);
        out.writeFloat(buffers_avg);
        out.writeCompressedInt(buffers_max);
        out.writeCompressedInt(cached_min);
        out.writeFloat(cached_avg);
        out.writeCompressedInt(cached_max);
        out.writeCompressedInt(swap_cached_min);
        out.writeFloat(swap_cached_avg);
        out.writeCompressedInt(swap_cached_max);
        out.writeCompressedInt(active_min);
        out.writeFloat(active_avg);
        out.writeCompressedInt(active_max);
        out.writeCompressedInt(inact_dirty_min);
        out.writeFloat(inact_dirty_avg);
        out.writeCompressedInt(inact_dirty_max);
        out.writeCompressedInt(inact_clean_min);
        out.writeFloat(inact_clean_avg);
        out.writeCompressedInt(inact_clean_max);
        out.writeCompressedInt(inact_target_min);
        out.writeFloat(inact_target_avg);
        out.writeCompressedInt(inact_target_max);
        out.writeCompressedInt(high_total_min);
        out.writeFloat(high_total_avg);
        out.writeCompressedInt(high_total_max);
        out.writeCompressedInt(high_free_min);
        out.writeFloat(high_free_avg);
        out.writeCompressedInt(high_free_max);
        out.writeCompressedInt(low_total_min);
        out.writeFloat(low_total_avg);
        out.writeCompressedInt(low_total_max);
        out.writeCompressedInt(low_free_min);
        out.writeFloat(low_free_avg);
        out.writeCompressedInt(low_free_max);
    }
}