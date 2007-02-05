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
final public class SRLoad extends ServerReportSection<SRLoad> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private float min;
    private float avg;
    private float max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return new Float(min);
            case 2: return new Float(avg);
            case 3: return new Float(max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }
    
    public float getMin() {
        return min;
    }
    
    public float getAvg() {
        return avg;
    }
    
    public float getMax() {
        return max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_LOAD;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        min=result.getFloat(2);
        avg=result.getFloat(3);
        max=result.getFloat(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        min=in.readFloat();
        avg=in.readFloat();
        max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeFloat(min);
        out.writeFloat(avg);
        out.writeFloat(max);
    }
}