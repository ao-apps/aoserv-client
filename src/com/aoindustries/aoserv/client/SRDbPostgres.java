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
final public class SRDbPostgres extends ServerReportSection<SRDbPostgres> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private int conn_min;
    private float conn_avg;
    private int conn_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return Integer.valueOf(conn_min);
            case 2: return new Float(conn_avg);
            case 3: return Integer.valueOf(conn_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getConnectionsMin() {
        return conn_min;
    }
    
    public float getConnectionsAvg() {
        return conn_avg;
    }
    
    public int getConnectionsMax() {
        return conn_max;
    }
    
    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_DB_POSTGRES;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        conn_min=result.getInt(2);
        conn_avg=result.getFloat(3);
        conn_max=result.getInt(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        conn_min=in.readCompressedInt();
        conn_avg=in.readFloat();
        conn_max=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeCompressedInt(conn_min);
        out.writeFloat(conn_avg);
        out.writeCompressedInt(conn_max);
    }
}