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
final public class SRNetTCP extends ServerReportSection<SRNetTCP> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private float
        active_connect_min,
        active_connect_avg,
        active_connect_max,
        passive_connect_min,
        passive_connect_avg,
        passive_connect_max,
        fail_connect_min,
        fail_connect_avg,
        fail_connect_max,
        in_reset_min,
        in_reset_avg,
        in_reset_max,
        connect_min,
        connect_avg,
        connect_max,
        segment_receive_min,
        segment_receive_avg,
        segment_receive_max,
        segment_send_min,
        segment_send_avg,
        segment_send_max,
        segment_resend_min,
        segment_resend_avg,
        segment_resend_max,
        bad_segment_receive_min,
        bad_segment_receive_avg,
        bad_segment_receive_max,
        out_reset_min,
        out_reset_avg,
        out_reset_max
    ;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return new Float(active_connect_min);
            case 2: return new Float(active_connect_avg);
            case 3: return new Float(active_connect_max);
            case 4: return new Float(passive_connect_min);
            case 5: return new Float(passive_connect_avg);
            case 6: return new Float(passive_connect_max);
            case 7: return new Float(fail_connect_min);
            case 8: return new Float(fail_connect_avg);
            case 9: return new Float(fail_connect_max);
            case 10: return new Float(in_reset_min);
            case 11: return new Float(in_reset_avg);
            case 12: return new Float(in_reset_max);
            case 13: return new Float(connect_min);
            case 14: return new Float(connect_avg);
            case 15: return new Float(connect_max);
            case 16: return new Float(segment_receive_min);
            case 17: return new Float(segment_receive_avg);
            case 18: return new Float(segment_receive_max);
            case 19: return new Float(segment_send_min);
            case 20: return new Float(segment_send_avg);
            case 21: return new Float(segment_send_max);
            case 22: return new Float(segment_resend_min);
            case 23: return new Float(segment_resend_avg);
            case 24: return new Float(segment_resend_max);
            case 25: return new Float(bad_segment_receive_min);
            case 26: return new Float(bad_segment_receive_avg);
            case 27: return new Float(bad_segment_receive_max);
            case 28: return new Float(out_reset_min);
            case 29: return new Float(out_reset_avg);
            case 30: return new Float(out_reset_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public float getActiveConnectMin() {
        return active_connect_min;
    }
    
    public float getActiveConnectAvg() {
        return active_connect_avg;
    }
    
    public float getActiveConnectMax() {
        return active_connect_max;
    }

    public float getPassiveConnectMin() {
        return passive_connect_min;
    }
    
    public float getPassiveConnectAvg() {
        return passive_connect_avg;
    }
    
    public float getPassiveConnectMax() {
        return passive_connect_max;
    }

    public float getFailConnectMin() {
        return fail_connect_min;
    }
    
    public float getFailConnectAvg() {
        return fail_connect_avg;
    }
    
    public float getFailConnectMax() {
        return fail_connect_max;
    }

    public float getInResetMin() {
        return in_reset_min;
    }
    
    public float getInResetAvg() {
        return in_reset_avg;
    }
    
    public float getInResetMax() {
        return in_reset_max;
    }

    public float getConnectMin() {
        return connect_min;
    }
    
    public float getConnectAvg() {
        return connect_avg;
    }
    
    public float getConnectMax() {
        return connect_max;
    }

    public float getSegmentReceiveMin() {
        return segment_receive_min;
    }
    
    public float getSegmentReceiveAvg() {
        return segment_receive_avg;
    }
    
    public float getSegmentReceiveMax() {
        return segment_receive_max;
    }

    public float getSegmentSendMin() {
        return segment_send_min;
    }
    
    public float getSegmentSendAvg() {
        return segment_send_avg;
    }
    
    public float getSegmentSendMax() {
        return segment_send_max;
    }

    public float getSegmentResendMin() {
        return segment_resend_min;
    }
    
    public float getSegmentResendAvg() {
        return segment_resend_avg;
    }
    
    public float getSegmentResendMax() {
        return segment_resend_max;
    }

    public float getBadSegmentReceiveMin() {
        return bad_segment_receive_min;
    }
    
    public float getBadSegmentReceiveAvg() {
        return bad_segment_receive_avg;
    }
    
    public float getBadSegmentReceiveMax() {
        return bad_segment_receive_max;
    }

    public float getOutResetMin() {
        return out_reset_min;
    }
    
    public float getOutResetAvg() {
        return out_reset_avg;
    }
    
    public float getOutResetMax() {
        return out_reset_max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_NET_TCP;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        active_connect_min=result.getFloat(2);
        active_connect_avg=result.getFloat(3);
        active_connect_max=result.getFloat(4);
        passive_connect_min=result.getFloat(5);
        passive_connect_avg=result.getFloat(6);
        passive_connect_max=result.getFloat(7);
        fail_connect_min=result.getFloat(8);
        fail_connect_avg=result.getFloat(9);
        fail_connect_max=result.getFloat(10);
        in_reset_min=result.getFloat(11);
        in_reset_avg=result.getFloat(12);
        in_reset_max=result.getFloat(13);
        connect_min=result.getFloat(14);
        connect_avg=result.getFloat(15);
        connect_max=result.getFloat(16);
        segment_receive_min=result.getFloat(17);
        segment_receive_avg=result.getFloat(18);
        segment_receive_max=result.getFloat(19);
        segment_send_min=result.getFloat(20);
        segment_send_avg=result.getFloat(21);
        segment_send_max=result.getFloat(22);
        segment_resend_min=result.getFloat(23);
        segment_resend_avg=result.getFloat(24);
        segment_resend_max=result.getFloat(25);
        bad_segment_receive_min=result.getFloat(26);
        bad_segment_receive_avg=result.getFloat(27);
        bad_segment_receive_max=result.getFloat(28);
        out_reset_min=result.getFloat(29);
        out_reset_avg=result.getFloat(30);
        out_reset_max=result.getFloat(31);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        active_connect_min=in.readFloat();
        active_connect_avg=in.readFloat();
        active_connect_max=in.readFloat();
        passive_connect_min=in.readFloat();
        passive_connect_avg=in.readFloat();
        passive_connect_max=in.readFloat();
        fail_connect_min=in.readFloat();
        fail_connect_avg=in.readFloat();
        fail_connect_max=in.readFloat();
        in_reset_min=in.readFloat();
        in_reset_avg=in.readFloat();
        in_reset_max=in.readFloat();
        connect_min=in.readFloat();
        connect_avg=in.readFloat();
        connect_max=in.readFloat();
        segment_receive_min=in.readFloat();
        segment_receive_avg=in.readFloat();
        segment_receive_max=in.readFloat();
        segment_send_min=in.readFloat();
        segment_send_avg=in.readFloat();
        segment_send_max=in.readFloat();
        segment_resend_min=in.readFloat();
        segment_resend_avg=in.readFloat();
        segment_resend_max=in.readFloat();
        bad_segment_receive_min=in.readFloat();
        bad_segment_receive_avg=in.readFloat();
        bad_segment_receive_max=in.readFloat();
        out_reset_min=in.readFloat();
        out_reset_avg=in.readFloat();
        out_reset_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeFloat(active_connect_min);
        out.writeFloat(active_connect_avg);
        out.writeFloat(active_connect_max);
        out.writeFloat(passive_connect_min);
        out.writeFloat(passive_connect_avg);
        out.writeFloat(passive_connect_max);
        out.writeFloat(fail_connect_min);
        out.writeFloat(fail_connect_avg);
        out.writeFloat(fail_connect_max);
        out.writeFloat(in_reset_min);
        out.writeFloat(in_reset_avg);
        out.writeFloat(in_reset_max);
        out.writeFloat(connect_min);
        out.writeFloat(connect_avg);
        out.writeFloat(connect_max);
        out.writeFloat(segment_receive_min);
        out.writeFloat(segment_receive_avg);
        out.writeFloat(segment_receive_max);
        out.writeFloat(segment_send_min);
        out.writeFloat(segment_send_avg);
        out.writeFloat(segment_send_max);
        out.writeFloat(segment_resend_min);
        out.writeFloat(segment_resend_avg);
        out.writeFloat(segment_resend_max);
        out.writeFloat(bad_segment_receive_min);
        out.writeFloat(bad_segment_receive_avg);
        out.writeFloat(bad_segment_receive_max);
        out.writeFloat(out_reset_min);
        out.writeFloat(out_reset_avg);
        out.writeFloat(out_reset_max);
    }
}