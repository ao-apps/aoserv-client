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
final public class SRNumUsers extends ServerReportSection<SRNumUsers> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private int min;
    private float avg;
    private int max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return Integer.valueOf(min);
            case 2: return Float.valueOf(avg);
            case 3: return Integer.valueOf(max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }
    
    public int getMin() {
        return min;
    }
    
    public float getAvg() {
        return avg;
    }
    
    public int getMax() {
        return max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_NUM_USERS;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        min=result.getInt(2);
        avg=result.getFloat(3);
        max=result.getInt(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        min=in.readCompressedInt();
        avg=in.readFloat();
        max=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeCompressedInt(min);
        out.writeFloat(avg);
        out.writeCompressedInt(max);
    }
}