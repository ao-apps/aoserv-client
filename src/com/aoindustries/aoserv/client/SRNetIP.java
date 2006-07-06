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
final public class SRNetIP extends ServerReportSection<SRNetIP> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private float packet_min;
    private float packet_avg;
    private float packet_max;
    private float invalid_headers_min;
    private float invalid_headers_avg;
    private float invalid_headers_max;
    private float forward_min;
    private float forward_avg;
    private float forward_max;
    private float discard_min;
    private float discard_avg;
    private float discard_max;
    private float deliver_min;
    private float deliver_avg;
    private float deliver_max;
    private float request_min;
    private float request_avg;
    private float request_max;
    private float out_drop_min;
    private float out_drop_avg;
    private float out_drop_max;
    private float out_drop_no_route_min;
    private float out_drop_no_route_avg;
    private float out_drop_no_route_max;
    private float out_drop_timeout_min;
    private float out_drop_timeout_avg;
    private float out_drop_timeout_max;
    private float ra_req_min;
    private float ra_req_avg;
    private float ra_req_max;
    private float ra_ok_min;
    private float ra_ok_avg;
    private float ra_ok_max;
    private float ra_fail_min;
    private float ra_fail_avg;
    private float ra_fail_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return new Float(packet_min);
            case 2: return new Float(packet_avg);
            case 3: return new Float(packet_max);
            case 4: return new Float(invalid_headers_min);
            case 5: return new Float(invalid_headers_avg);
            case 6: return new Float(invalid_headers_max);
            case 7: return new Float(forward_min);
            case 8: return new Float(forward_avg);
            case 9: return new Float(forward_max);
            case 10: return new Float(discard_min);
            case 11: return new Float(discard_avg);
            case 12: return new Float(discard_max);
            case 13: return new Float(deliver_min);
            case 14: return new Float(deliver_avg);
            case 15: return new Float(deliver_max);
            case 16: return new Float(request_min);
            case 17: return new Float(request_avg);
            case 18: return new Float(request_max);
            case 19: return new Float(out_drop_min);
            case 20: return new Float(out_drop_avg);
            case 21: return new Float(out_drop_max);
            case 22: return new Float(out_drop_no_route_min);
            case 23: return new Float(out_drop_no_route_avg);
            case 24: return new Float(out_drop_no_route_max);
            case 25: return new Float(out_drop_timeout_min);
            case 26: return new Float(out_drop_timeout_avg);
            case 27: return new Float(out_drop_timeout_max);
            case 28: return new Float(ra_req_min);
            case 29: return new Float(ra_req_avg);
            case 30: return new Float(ra_req_max);
            case 31: return new Float(ra_ok_min);
            case 32: return new Float(ra_ok_avg);
            case 33: return new Float(ra_ok_max);
            case 34: return new Float(ra_fail_min);
            case 35: return new Float(ra_fail_avg);
            case 36: return new Float(ra_fail_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public float getPacketMin() {
        return packet_min;
    }
    
    public float getPacketAvg() {
        return packet_avg;
    }
    
    public float getPacketMax() {
        return packet_max;
    }

    public float getInvalidHeadersMin() {
        return invalid_headers_min;
    }
    
    public float getInvalidHeadersAvg() {
        return invalid_headers_avg;
    }
    
    public float getInvalidHeadersMax() {
        return invalid_headers_max;
    }

    public float getForwardMin() {
        return forward_min;
    }
    
    public float getForwardAvg() {
        return forward_avg;
    }
    
    public float getForwardMax() {
        return forward_max;
    }

    public float getDiscardMin() {
        return discard_min;
    }
    
    public float getDiscardAvg() {
        return discard_avg;
    }
    
    public float getDiscardMax() {
        return discard_max;
    }

    public float getDeliverMin() {
        return deliver_min;
    }
    
    public float getDeliverAvg() {
        return deliver_avg;
    }
    
    public float getDeliverMax() {
        return deliver_max;
    }

    public float getRequestMin() {
        return request_min;
    }
    
    public float getRequestAvg() {
        return request_avg;
    }
    
    public float getRequestMax() {
        return request_max;
    }

    public float getOutDropMin() {
        return out_drop_min;
    }
    
    public float getOutDropAvg() {
        return out_drop_avg;
    }
    
    public float getOutDropMax() {
        return out_drop_max;
    }

    public float getOutDropNoRouteMin() {
        return out_drop_no_route_min;
    }
    
    public float getOutDropNoRouteAvg() {
        return out_drop_no_route_avg;
    }
    
    public float getOutDropNoRouteMax() {
        return out_drop_no_route_max;
    }

    public float getOutDropTimeoutMin() {
        return out_drop_timeout_min;
    }
    
    public float getOutDropTimeoutAvg() {
        return out_drop_timeout_avg;
    }
    
    public float getOutDropTimeoutMax() {
        return out_drop_timeout_max;
    }

    public float getRaReqMin() {
        return ra_req_min;
    }
    
    public float getRaReqAvg() {
        return ra_req_avg;
    }
    
    public float getRaReqMax() {
        return ra_req_max;
    }

    public float getRaOkMin() {
        return ra_ok_min;
    }
    
    public float getRaOkAvg() {
        return ra_ok_avg;
    }
    
    public float getRaOkMax() {
        return ra_ok_max;
    }

    public float getRaFailMin() {
        return ra_fail_min;
    }
    
    public float getRaFailAvg() {
        return ra_fail_avg;
    }
    
    public float getRaFailMax() {
        return ra_fail_max;
    }

    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_NET_IP;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        packet_min=result.getFloat(2);
        packet_avg=result.getFloat(3);
        packet_max=result.getFloat(4);
        invalid_headers_min=result.getFloat(5);
        invalid_headers_avg=result.getFloat(6);
        invalid_headers_max=result.getFloat(7);
        forward_min=result.getFloat(8);
        forward_avg=result.getFloat(9);
        forward_max=result.getFloat(10);
        discard_min=result.getFloat(11);
        discard_avg=result.getFloat(12);
        discard_max=result.getFloat(13);
        deliver_min=result.getFloat(14);
        deliver_avg=result.getFloat(15);
        deliver_max=result.getFloat(16);
        request_min=result.getFloat(17);
        request_avg=result.getFloat(18);
        request_max=result.getFloat(19);
        out_drop_min=result.getFloat(20);
        out_drop_avg=result.getFloat(21);
        out_drop_max=result.getFloat(22);
        out_drop_no_route_min=result.getFloat(23);
        out_drop_no_route_avg=result.getFloat(24);
        out_drop_no_route_max=result.getFloat(25);
        out_drop_timeout_min=result.getFloat(26);
        out_drop_timeout_avg=result.getFloat(27);
        out_drop_timeout_max=result.getFloat(28);
        ra_req_min=result.getFloat(29);
        ra_req_avg=result.getFloat(30);
        ra_req_max=result.getFloat(31);
        ra_ok_min=result.getFloat(32);
        ra_ok_avg=result.getFloat(33);
        ra_ok_max=result.getFloat(34);
        ra_fail_min=result.getFloat(35);
        ra_fail_avg=result.getFloat(36);
        ra_fail_max=result.getFloat(37);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        packet_min=in.readFloat();
        packet_avg=in.readFloat();
        packet_max=in.readFloat();
        invalid_headers_min=in.readFloat();
        invalid_headers_avg=in.readFloat();
        invalid_headers_max=in.readFloat();
        forward_min=in.readFloat();
        forward_avg=in.readFloat();
        forward_max=in.readFloat();
        discard_min=in.readFloat();
        discard_avg=in.readFloat();
        discard_max=in.readFloat();
        deliver_min=in.readFloat();
        deliver_avg=in.readFloat();
        deliver_max=in.readFloat();
        request_min=in.readFloat();
        request_avg=in.readFloat();
        request_max=in.readFloat();
        out_drop_min=in.readFloat();
        out_drop_avg=in.readFloat();
        out_drop_max=in.readFloat();
        out_drop_no_route_min=in.readFloat();
        out_drop_no_route_avg=in.readFloat();
        out_drop_no_route_max=in.readFloat();
        out_drop_timeout_min=in.readFloat();
        out_drop_timeout_avg=in.readFloat();
        out_drop_timeout_max=in.readFloat();
        ra_req_min=in.readFloat();
        ra_req_avg=in.readFloat();
        ra_req_max=in.readFloat();
        ra_ok_min=in.readFloat();
        ra_ok_avg=in.readFloat();
        ra_ok_max=in.readFloat();
        ra_fail_min=in.readFloat();
        ra_fail_avg=in.readFloat();
        ra_fail_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeFloat(packet_min);
        out.writeFloat(packet_avg);
        out.writeFloat(packet_max);
        out.writeFloat(invalid_headers_min);
        out.writeFloat(invalid_headers_avg);
        out.writeFloat(invalid_headers_max);
        out.writeFloat(forward_min);
        out.writeFloat(forward_avg);
        out.writeFloat(forward_max);
        out.writeFloat(discard_min);
        out.writeFloat(discard_avg);
        out.writeFloat(discard_max);
        out.writeFloat(deliver_min);
        out.writeFloat(deliver_avg);
        out.writeFloat(deliver_max);
        out.writeFloat(request_min);
        out.writeFloat(request_avg);
        out.writeFloat(request_max);
        out.writeFloat(out_drop_min);
        out.writeFloat(out_drop_avg);
        out.writeFloat(out_drop_max);
        out.writeFloat(out_drop_no_route_min);
        out.writeFloat(out_drop_no_route_avg);
        out.writeFloat(out_drop_no_route_max);
        out.writeFloat(out_drop_timeout_min);
        out.writeFloat(out_drop_timeout_avg);
        out.writeFloat(out_drop_timeout_max);
        out.writeFloat(ra_req_min);
        out.writeFloat(ra_req_avg);
        out.writeFloat(ra_req_max);
        out.writeFloat(ra_ok_min);
        out.writeFloat(ra_ok_avg);
        out.writeFloat(ra_ok_max);
        out.writeFloat(ra_fail_min);
        out.writeFloat(ra_fail_avg);
        out.writeFloat(ra_fail_max);
    }
}