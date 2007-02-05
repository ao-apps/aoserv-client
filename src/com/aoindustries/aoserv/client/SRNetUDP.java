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
final public class SRNetUDP extends ServerReportSection<SRNetUDP> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private float
        receive_min,
        receive_avg,
        receive_max,
        unknown_min,
        unknown_avg,
        unknown_max,
        error_min,
        error_avg,
        error_max,
        send_min,
        send_avg,
        send_max
    ;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return Float.valueOf(receive_min);
            case 2: return Float.valueOf(receive_avg);
            case 3: return Float.valueOf(receive_max);
            case 4: return Float.valueOf(unknown_min);
            case 5: return Float.valueOf(unknown_avg);
            case 6: return Float.valueOf(unknown_max);
            case 7: return Float.valueOf(error_min);
            case 8: return Float.valueOf(error_avg);
            case 9: return Float.valueOf(error_max);
            case 10: return Float.valueOf(send_min);
            case 11: return Float.valueOf(send_avg);
            case 12: return Float.valueOf(send_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public float getReceiveMin() {
        return receive_min;
    }
    
    public float getReceiveAvg() {
        return receive_avg;
    }
    
    public float getReceiveMax() {
        return receive_max;
    }
    
    public float getUnknownMin() {
        return unknown_min;
    }
    
    public float getUnknownAvg() {
        return unknown_avg;
    }
    
    public float getUnknownMax() {
        return unknown_max;
    }
    
    public float getErrorMin() {
        return error_min;
    }
    
    public float getErrorAvg() {
        return error_avg;
    }
    
    public float getErrorMax() {
        return error_max;
    }
    
    public float getSendMin() {
        return send_min;
    }
    
    public float getSendAvg() {
        return send_avg;
    }
    
    public float getSendMax() {
        return send_max;
    }
    
    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_NET_UDP;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        receive_min=result.getFloat(2);
        receive_avg=result.getFloat(3);
        receive_max=result.getFloat(4);
        unknown_min=result.getFloat(5);
        unknown_avg=result.getFloat(6);
        unknown_max=result.getFloat(7);
        error_min=result.getFloat(8);
        error_avg=result.getFloat(9);
        error_max=result.getFloat(10);
        send_min=result.getFloat(11);
        send_avg=result.getFloat(12);
        send_max=result.getFloat(13);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        receive_min=in.readFloat();
        receive_avg=in.readFloat();
        receive_max=in.readFloat();
        unknown_min=in.readFloat();
        unknown_avg=in.readFloat();
        unknown_max=in.readFloat();
        error_min=in.readFloat();
        error_avg=in.readFloat();
        error_max=in.readFloat();
        send_min=in.readFloat();
        send_avg=in.readFloat();
        send_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeFloat(receive_min);
        out.writeFloat(receive_avg);
        out.writeFloat(receive_max);
        out.writeFloat(unknown_min);
        out.writeFloat(unknown_avg);
        out.writeFloat(unknown_max);
        out.writeFloat(error_min);
        out.writeFloat(error_avg);
        out.writeFloat(error_max);
        out.writeFloat(send_min);
        out.writeFloat(send_avg);
        out.writeFloat(send_max);
    }
}