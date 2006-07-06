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
final public class SRPaging extends ServerReportSection<SRPaging> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private float in_min;
    private float in_avg;
    private float in_max;
    private float out_min;
    private float out_avg;
    private float out_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return Float.valueOf(in_min);
            case 2: return Float.valueOf(in_avg);
            case 3: return Float.valueOf(in_max);
            case 4: return Float.valueOf(out_min);
            case 5: return Float.valueOf(out_avg);
            case 6: return Float.valueOf(out_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }
    
    public float getInMin() {
        return in_min;
    }
    
    public float getInAvg() {
        return in_avg;
    }
    
    public float getInMax() {
        return in_max;
    }

    public float getOutMin() {
        return out_min;
    }
    
    public float getOutAvg() {
        return out_avg;
    }
    
    public float getOutMax() {
        return out_max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_PAGING;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        in_min=result.getFloat(2);
        in_avg=result.getFloat(3);
        in_max=result.getFloat(4);
        out_min=result.getFloat(5);
        out_avg=result.getFloat(6);
        out_max=result.getFloat(7);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        in_min=in.readFloat();
        in_avg=in.readFloat();
        in_max=in.readFloat();
        out_min=in.readFloat();
        out_avg=in.readFloat();
        out_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeFloat(in_min);
        out.writeFloat(in_avg);
        out.writeFloat(in_max);
        out.writeFloat(out_min);
        out.writeFloat(out_avg);
        out.writeFloat(out_max);
    }
}