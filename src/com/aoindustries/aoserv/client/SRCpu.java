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
public final class SRCpu extends ServerReportSection<SRCpu> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER_REPORT=1
    ;
    
    private int pkey;
    private int cpu_number;
    private float user_min;
    private float user_avg;
    private float user_max;
    private float nice_min;
    private float nice_avg;
    private float nice_max;
    private float sys_min;
    private float sys_avg;
    private float sys_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 2: return Integer.valueOf(cpu_number);
            case 3: return new Float(user_min);
            case 4: return new Float(user_avg);
            case 5: return new Float(user_max);
            case 6: return new Float(nice_min);
            case 7: return new Float(nice_avg);
            case 8: return new Float(nice_max);
            case 9: return new Float(sys_min);
            case 10: return new Float(sys_avg);
            case 11: return new Float(sys_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPkey() {
        return pkey;
    }

    public int getCPUNumber() {
        return cpu_number;
    }

    public float getUserMin() {
        return user_min;
    }
    
    public float getUserAvg() {
        return user_avg;
    }
    
    public float getUserMax() {
        return user_max;
    }

    public float getNiceMin() {
        return nice_min;
    }
    
    public float getNiceAvg() {
        return nice_avg;
    }
    
    public float getNiceMax() {
        return nice_max;
    }

    public float getSysMin() {
        return sys_min;
    }
    
    public float getSysAvg() {
        return sys_avg;
    }
    
    public float getSysMax() {
        return sys_max;
    }

    public Integer getKey() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_CPU;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server_report=result.getInt(2);
        cpu_number=result.getInt(3);
        user_min=result.getFloat(4);
        user_avg=result.getFloat(5);
        user_max=result.getFloat(6);
        nice_min=result.getFloat(7);
        nice_avg=result.getFloat(8);
        nice_max=result.getFloat(9);
        sys_min=result.getFloat(10);
        sys_avg=result.getFloat(11);
        sys_max=result.getFloat(12);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server_report=in.readCompressedInt();
        cpu_number=in.readCompressedInt();
        user_min=in.readFloat();
        user_avg=in.readFloat();
        user_max=in.readFloat();
        nice_min=in.readFloat();
        nice_avg=in.readFloat();
        nice_max=in.readFloat();
        sys_min=in.readFloat();
        sys_avg=in.readFloat();
        sys_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server_report);
        out.writeCompressedInt(cpu_number);
        out.writeFloat(user_min);
        out.writeFloat(user_avg);
        out.writeFloat(user_max);
        out.writeFloat(nice_min);
        out.writeFloat(nice_avg);
        out.writeFloat(nice_max);
        out.writeFloat(sys_min);
        out.writeFloat(sys_avg);
        out.writeFloat(sys_max);
    }
}