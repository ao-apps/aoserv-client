package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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
final public class SRNetICMP extends ServerReportSection<SRNetICMP> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private float
        in_message_min,
        in_message_avg,
        in_message_max,
        in_fail_min,
        in_fail_avg,
        in_fail_max,
        in_unreachable_min,
        in_unreachable_avg,
        in_unreachable_max,
        in_timeout_min,
        in_timeout_avg,
        in_timeout_max,
        in_quench_min,
        in_quench_avg,
        in_quench_max,
        in_redirect_min,
        in_redirect_avg,
        in_redirect_max,
        in_echo_request_min,
        in_echo_request_avg,
        in_echo_request_max,
        in_echo_reply_min,
        in_echo_reply_avg,
        in_echo_reply_max,
        out_message_min,
        out_message_avg,
        out_message_max,
        out_fail_min,
        out_fail_avg,
        out_fail_max,
        out_unreachable_min,
        out_unreachable_avg,
        out_unreachable_max,
        out_timeout_min,
        out_timeout_avg,
        out_timeout_max,
        out_redirect_min,
        out_redirect_avg,
        out_redirect_max,
        out_echo_reply_min,
        out_echo_reply_avg,
        out_echo_reply_max
    ;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return new Float(in_message_min);
            case 2: return new Float(in_message_avg);
            case 3: return new Float(in_message_max);
            case 4: return new Float(in_fail_min);
            case 5: return new Float(in_fail_avg);
            case 6: return new Float(in_fail_max);
            case 7: return new Float(in_unreachable_min);
            case 8: return new Float(in_unreachable_avg);
            case 9: return new Float(in_unreachable_max);
            case 10: return new Float(in_timeout_min);
            case 11: return new Float(in_timeout_avg);
            case 12: return new Float(in_timeout_max);
            case 13: return new Float(in_quench_min);
            case 14: return new Float(in_quench_avg);
            case 15: return new Float(in_quench_max);
            case 16: return new Float(in_redirect_min);
            case 17: return new Float(in_redirect_avg);
            case 18: return new Float(in_redirect_max);
            case 19: return new Float(in_echo_request_min);
            case 20: return new Float(in_echo_request_avg);
            case 21: return new Float(in_echo_request_max);
            case 22: return new Float(in_echo_reply_min);
            case 23: return new Float(in_echo_reply_avg);
            case 24: return new Float(in_echo_reply_max);
            case 25: return new Float(out_message_min);
            case 26: return new Float(out_message_avg);
            case 27: return new Float(out_message_max);
            case 28: return new Float(out_fail_min);
            case 29: return new Float(out_fail_avg);
            case 30: return new Float(out_fail_max);
            case 31: return new Float(out_unreachable_min);
            case 32: return new Float(out_unreachable_avg);
            case 33: return new Float(out_unreachable_max);
            case 34: return new Float(out_timeout_min);
            case 35: return new Float(out_timeout_avg);
            case 36: return new Float(out_timeout_max);
            case 37: return new Float(out_redirect_min);
            case 38: return new Float(out_redirect_avg);
            case 39: return new Float(out_redirect_max);
            case 40: return new Float(out_echo_reply_min);
            case 41: return new Float(out_echo_reply_avg);
            case 42: return new Float(out_echo_reply_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public float getInMessageMin() {
        return in_message_min;
    }
    
    public float getInMessageAvg() {
        return in_message_avg;
    }
    
    public float getInMessageMax() {
        return in_message_max;
    }

    public float getInFailMin() {
        return in_fail_min;
    }
    
    public float getInFailAvg() {
        return in_fail_avg;
    }
    
    public float getInFailMax() {
        return in_fail_max;
    }

    public float getInUnreachableMin() {
        return in_unreachable_min;
    }
    
    public float getInUnreachableAvg() {
        return in_unreachable_avg;
    }
    
    public float getInUnreachableMax() {
        return in_unreachable_max;
    }

    public float getInTimeoutMin() {
        return in_timeout_min;
    }
    
    public float getInTimeoutAvg() {
        return in_timeout_avg;
    }
    
    public float getInTimeoutMax() {
        return in_timeout_max;
    }

    public float getInQuenchMin() {
        return in_quench_min;
    }
    
    public float getInQuenchAvg() {
        return in_quench_avg;
    }
    
    public float getInQuenchMax() {
        return in_quench_max;
    }

    public float getInRedirectMin() {
        return in_redirect_min;
    }
    
    public float getInRedirectAvg() {
        return in_redirect_avg;
    }
    
    public float getInRedirectMax() {
        return in_redirect_max;
    }

    public float getInEchoRequestMin() {
        return in_echo_request_min;
    }
    
    public float getInEchoRequestAvg() {
        return in_echo_request_avg;
    }
    
    public float getInEchoRequestMax() {
        return in_echo_request_max;
    }

    public float getInEchoReplyMin() {
        return in_echo_reply_min;
    }
    
    public float getInEchoReplyAvg() {
        return in_echo_reply_avg;
    }
    
    public float getInEchoReplyMax() {
        return in_echo_reply_max;
    }

    public float getOutMessageMin() {
        return out_message_min;
    }
    
    public float getOutMessageAvg() {
        return out_message_avg;
    }
    
    public float getOutMessageMax() {
        return out_message_max;
    }

    public float getOutFailMin() {
        return out_fail_min;
    }
    
    public float getOutFailAvg() {
        return out_fail_avg;
    }
    
    public float getOutFailMax() {
        return out_fail_max;
    }

    public float getOutUnreachableMin() {
        return out_unreachable_min;
    }
    
    public float getOutUnreachableAvg() {
        return out_unreachable_avg;
    }
    
    public float getOutUnreachableMax() {
        return out_unreachable_max;
    }

    public float getOutTimeoutMin() {
        return out_timeout_min;
    }
    
    public float getOutTimeoutAvg() {
        return out_timeout_avg;
    }
    
    public float getOutTimeoutMax() {
        return out_timeout_max;
    }

    public float getOutRedirectMin() {
        return out_redirect_min;
    }
    
    public float getOutRedirectAvg() {
        return out_redirect_avg;
    }
    
    public float getOutRedirectMax() {
        return out_redirect_max;
    }

    public float getOutEchoReplyMin() {
        return out_echo_reply_min;
    }
    
    public float getOutEchoReplyAvg() {
        return out_echo_reply_avg;
    }
    
    public float getOutEchoReplyMax() {
        return out_echo_reply_max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_NET_ICMP;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        in_message_min=result.getFloat(2);
        in_message_avg=result.getFloat(3);
        in_message_max=result.getFloat(4);
        in_fail_min=result.getFloat(5);
        in_fail_avg=result.getFloat(6);
        in_fail_max=result.getFloat(7);
        in_unreachable_min=result.getFloat(8);
        in_unreachable_avg=result.getFloat(9);
        in_unreachable_max=result.getFloat(10);
        in_timeout_min=result.getFloat(11);
        in_timeout_avg=result.getFloat(12);
        in_timeout_max=result.getFloat(13);
        in_quench_min=result.getFloat(14);
        in_quench_avg=result.getFloat(15);
        in_quench_max=result.getFloat(16);
        in_redirect_min=result.getFloat(17);
        in_redirect_avg=result.getFloat(18);
        in_redirect_max=result.getFloat(19);
        in_echo_request_min=result.getFloat(20);
        in_echo_request_avg=result.getFloat(21);
        in_echo_request_max=result.getFloat(22);
        in_echo_reply_min=result.getFloat(23);
        in_echo_reply_avg=result.getFloat(24);
        in_echo_reply_max=result.getFloat(25);
        out_message_min=result.getFloat(26);
        out_message_avg=result.getFloat(27);
        out_message_max=result.getFloat(28);
        out_fail_min=result.getFloat(29);
        out_fail_avg=result.getFloat(30);
        out_fail_max=result.getFloat(31);
        out_unreachable_min=result.getFloat(32);
        out_unreachable_avg=result.getFloat(33);
        out_unreachable_max=result.getFloat(34);
        out_timeout_min=result.getFloat(35);
        out_timeout_avg=result.getFloat(36);
        out_timeout_max=result.getFloat(37);
        out_redirect_min=result.getFloat(38);
        out_redirect_avg=result.getFloat(39);
        out_redirect_max=result.getFloat(40);
        out_echo_reply_min=result.getFloat(41);
        out_echo_reply_avg=result.getFloat(42);
        out_echo_reply_max=result.getFloat(43);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        in_message_min=in.readFloat();
        in_message_avg=in.readFloat();
        in_message_max=in.readFloat();
        in_fail_min=in.readFloat();
        in_fail_avg=in.readFloat();
        in_fail_max=in.readFloat();
        in_unreachable_min=in.readFloat();
        in_unreachable_avg=in.readFloat();
        in_unreachable_max=in.readFloat();
        in_timeout_min=in.readFloat();
        in_timeout_avg=in.readFloat();
        in_timeout_max=in.readFloat();
        in_quench_min=in.readFloat();
        in_quench_avg=in.readFloat();
        in_quench_max=in.readFloat();
        in_redirect_min=in.readFloat();
        in_redirect_avg=in.readFloat();
        in_redirect_max=in.readFloat();
        in_echo_request_min=in.readFloat();
        in_echo_request_avg=in.readFloat();
        in_echo_request_max=in.readFloat();
        in_echo_reply_min=in.readFloat();
        in_echo_reply_avg=in.readFloat();
        in_echo_reply_max=in.readFloat();
        out_message_min=in.readFloat();
        out_message_avg=in.readFloat();
        out_message_max=in.readFloat();
        out_fail_min=in.readFloat();
        out_fail_avg=in.readFloat();
        out_fail_max=in.readFloat();
        out_unreachable_min=in.readFloat();
        out_unreachable_avg=in.readFloat();
        out_unreachable_max=in.readFloat();
        out_timeout_min=in.readFloat();
        out_timeout_avg=in.readFloat();
        out_timeout_max=in.readFloat();
        out_redirect_min=in.readFloat();
        out_redirect_avg=in.readFloat();
        out_redirect_max=in.readFloat();
        out_echo_reply_min=in.readFloat();
        out_echo_reply_avg=in.readFloat();
        out_echo_reply_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeFloat(in_message_min);
        out.writeFloat(in_message_avg);
        out.writeFloat(in_message_max);
        out.writeFloat(in_fail_min);
        out.writeFloat(in_fail_avg);
        out.writeFloat(in_fail_max);
        out.writeFloat(in_unreachable_min);
        out.writeFloat(in_unreachable_avg);
        out.writeFloat(in_unreachable_max);
        out.writeFloat(in_timeout_min);
        out.writeFloat(in_timeout_avg);
        out.writeFloat(in_timeout_max);
        out.writeFloat(in_quench_min);
        out.writeFloat(in_quench_avg);
        out.writeFloat(in_quench_max);
        out.writeFloat(in_redirect_min);
        out.writeFloat(in_redirect_avg);
        out.writeFloat(in_redirect_max);
        out.writeFloat(in_echo_request_min);
        out.writeFloat(in_echo_request_avg);
        out.writeFloat(in_echo_request_max);
        out.writeFloat(in_echo_reply_min);
        out.writeFloat(in_echo_reply_avg);
        out.writeFloat(in_echo_reply_max);
        out.writeFloat(out_message_min);
        out.writeFloat(out_message_avg);
        out.writeFloat(out_message_max);
        out.writeFloat(out_fail_min);
        out.writeFloat(out_fail_avg);
        out.writeFloat(out_fail_max);
        out.writeFloat(out_unreachable_min);
        out.writeFloat(out_unreachable_avg);
        out.writeFloat(out_unreachable_max);
        out.writeFloat(out_timeout_min);
        out.writeFloat(out_timeout_avg);
        out.writeFloat(out_timeout_max);
        out.writeFloat(out_redirect_min);
        out.writeFloat(out_redirect_avg);
        out.writeFloat(out_redirect_max);
        out.writeFloat(out_echo_reply_min);
        out.writeFloat(out_echo_reply_avg);
        out.writeFloat(out_echo_reply_max);
    }
}