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
final public class SRNetDevice extends ServerReportSection<SRNetDevice> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_SERVER_REPORT=1
    ;
    
    private int pkey;
    private String device_id;
    private float rx_bytes_min;
    private float rx_bytes_avg;
    private float rx_bytes_max;
    private float rx_packets_min;
    private float rx_packets_avg;
    private float rx_packets_max;
    private float rx_errors_min;
    private float rx_errors_avg;
    private float rx_errors_max;
    private float rx_drop_min;
    private float rx_drop_avg;
    private float rx_drop_max;
    private float rx_fifo_min;
    private float rx_fifo_avg;
    private float rx_fifo_max;
    private float rx_frame_min;
    private float rx_frame_avg;
    private float rx_frame_max;
    private float rx_compress_min;
    private float rx_compress_avg;
    private float rx_compress_max;
    private float rx_multicast_min;
    private float rx_multicast_avg;
    private float rx_multicast_max;
    private float tx_bytes_min;
    private float tx_bytes_avg;
    private float tx_bytes_max;
    private float tx_packets_min;
    private float tx_packets_avg;
    private float tx_packets_max;
    private float tx_errors_min;
    private float tx_errors_avg;
    private float tx_errors_max;
    private float tx_drop_min;
    private float tx_drop_avg;
    private float tx_drop_max;
    private float tx_fifo_min;
    private float tx_fifo_avg;
    private float tx_fifo_max;
    private float tx_colls_min;
    private float tx_colls_avg;
    private float tx_colls_max;
    private float tx_carrier_min;
    private float tx_carrier_avg;
    private float tx_carrier_max;
    private float tx_compressed_min;
    private float tx_compressed_avg;
    private float tx_compressed_max;
    private int listen_min;
    private float listen_avg;
    private int listen_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 2: return device_id;
            case 3: return new Float(rx_bytes_min);
            case 4: return new Float(rx_bytes_avg);
            case 5: return new Float(rx_bytes_max);
            case 6: return new Float(rx_packets_min);
            case 7: return new Float(rx_packets_avg);
            case 8: return new Float(rx_packets_max);
            case 9: return new Float(rx_errors_min);
            case 10: return new Float(rx_errors_avg);
            case 11: return new Float(rx_errors_max);
            case 12: return new Float(rx_drop_min);
            case 13: return new Float(rx_drop_avg);
            case 14: return new Float(rx_drop_max);
            case 15: return new Float(rx_fifo_min);
            case 16: return new Float(rx_fifo_avg);
            case 17: return new Float(rx_fifo_max);
            case 18: return new Float(rx_frame_min);
            case 19: return new Float(rx_frame_avg);
            case 20: return new Float(rx_frame_max);
            case 21: return new Float(rx_compress_min);
            case 22: return new Float(rx_compress_avg);
            case 23: return new Float(rx_compress_max);
            case 24: return new Float(rx_multicast_min);
            case 25: return new Float(rx_multicast_avg);
            case 26: return new Float(rx_multicast_max);
            case 27: return new Float(tx_bytes_min);
            case 28: return new Float(tx_bytes_avg);
            case 29: return new Float(tx_bytes_max);
            case 30: return new Float(tx_packets_min);
            case 31: return new Float(tx_packets_avg);
            case 32: return new Float(tx_packets_max);
            case 33: return new Float(tx_errors_min);
            case 34: return new Float(tx_errors_avg);
            case 35: return new Float(tx_errors_max);
            case 36: return new Float(tx_drop_min);
            case 37: return new Float(tx_drop_avg);
            case 38: return new Float(tx_drop_max);
            case 39: return new Float(tx_fifo_min);
            case 40: return new Float(tx_fifo_avg);
            case 41: return new Float(tx_fifo_max);
            case 42: return new Float(tx_colls_min);
            case 43: return new Float(tx_colls_avg);
            case 44: return new Float(tx_colls_max);
            case 45: return new Float(tx_carrier_min);
            case 46: return new Float(tx_carrier_avg);
            case 47: return new Float(tx_carrier_max);
            case 48: return new Float(tx_compressed_min);
            case 49: return new Float(tx_compressed_avg);
            case 50: return new Float(tx_compressed_max);
            case 51: return Integer.valueOf(listen_min);
            case 52: return new Float(listen_avg);
            case 53: return Integer.valueOf(listen_max);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getPKey() {
        return pkey;
    }

    public NetDeviceID getDeviceID() {
        NetDeviceID ndi=table.connector.netDeviceIDs.get(device_id);
        if(ndi==null) throw new WrappedException(new SQLException("Unable to find NetDeviceID: "+device_id));
        return ndi;
    }

    public float getReceiveBytesMin() {
        return rx_bytes_min;
    }
    
    public float getReceiveBytesAvg() {
        return rx_bytes_avg;
    }
    
    public float getReceiveBytesMax() {
        return rx_bytes_max;
    }

    public float getReceivePacketsMin() {
        return rx_packets_min;
    }
    
    public float getReceivePacketsAvg() {
        return rx_packets_avg;
    }
    
    public float getReceivePacketsMax() {
        return rx_packets_max;
    }

    public float getReceiveErrorsMin() {
        return rx_errors_min;
    }
    
    public float getReceiveErrorsAvg() {
        return rx_errors_avg;
    }
    
    public float getReceiveErrorsMax() {
        return rx_errors_max;
    }

    public float getReceiveDropMin() {
        return rx_drop_min;
    }
    
    public float getReceiveDropAvg() {
        return rx_drop_avg;
    }
    
    public float getReceiveDropMax() {
        return rx_drop_max;
    }

    public float getReceiveFifoMin() {
        return rx_fifo_min;
    }
    
    public float getReceiveFifoAvg() {
        return rx_fifo_avg;
    }
    
    public float getReceiveFifoMax() {
        return rx_fifo_max;
    }

    public float getReceiveFrameMin() {
        return rx_frame_min;
    }
    
    public float getReceiveFrameAvg() {
        return rx_frame_avg;
    }
    
    public float getReceiveFrameMax() {
        return rx_frame_max;
    }

    public float getReceiveCompressMin() {
        return rx_compress_min;
    }
    
    public float getReceiveCompressAvg() {
        return rx_compress_avg;
    }
    
    public float getReceiveCompressMax() {
        return rx_compress_max;
    }

    public float getReceiveMulticastMin() {
        return rx_multicast_min;
    }
    
    public float getReceiveMulticastAvg() {
        return rx_multicast_avg;
    }
    
    public float getReceiveMulticastMax() {
        return rx_multicast_max;
    }

    public float getTransmitBytesMin() {
        return tx_bytes_min;
    }
    
    public float getTransmitBytesAvg() {
        return tx_bytes_avg;
    }
    
    public float getTransmitBytesMax() {
        return tx_bytes_max;
    }

    public float getTransmitPacketsMin() {
        return tx_packets_min;
    }
    
    public float getTransmitPacketsAvg() {
        return tx_packets_avg;
    }
    
    public float getTransmitPacketsMax() {
        return tx_packets_max;
    }

    public float getTransmitErrorsMin() {
        return tx_errors_min;
    }
    
    public float getTransmitErrorsAvg() {
        return tx_errors_avg;
    }
    
    public float getTransmitErrorsMax() {
        return tx_errors_max;
    }

    public float getTransmitDropMin() {
        return tx_drop_min;
    }
    
    public float getTransmitDropAvg() {
        return tx_drop_avg;
    }
    
    public float getTransmitDropMax() {
        return tx_drop_max;
    }

    public float getTransmitFifoMin() {
        return tx_fifo_min;
    }
    
    public float getTransmitFifoAvg() {
        return tx_fifo_avg;
    }
    
    public float getTransmitFifoMax() {
        return tx_fifo_max;
    }

    public float getTransmitCollsMin() {
        return tx_colls_min;
    }
    
    public float getTransmitCollsAvg() {
        return tx_colls_avg;
    }
    
    public float getTransmitCollsMax() {
        return tx_colls_max;
    }

    public float getTransmitCarrierMin() {
        return tx_carrier_min;
    }
    
    public float getTransmitCarrierAvg() {
        return tx_carrier_avg;
    }
    
    public float getTransmitCarrierMax() {
        return tx_carrier_max;
    }

    public float getTransmitCompressedMin() {
        return tx_compressed_min;
    }
    
    public float getTransmitCompressedAvg() {
        return tx_compressed_avg;
    }
    
    public float getTransmitCompressedMax() {
        return tx_compressed_max;
    }

    public int getListenMin() {
        return listen_min;
    }
    
    public float getListenAvg() {
        return listen_avg;
    }
    
    public int getListenMax() {
        return listen_max;
    }

    public Integer getKey() {
	return pkey;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SR_NET_DEVICES;
    }

    int hashCodeImpl() {
	return pkey;
    }

    void initImpl(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        server_report=result.getInt(2);
        device_id=result.getString(3);
        rx_bytes_min=result.getFloat(4);
        rx_bytes_avg=result.getFloat(5);
        rx_bytes_max=result.getFloat(6);
        rx_packets_min=result.getFloat(7);
        rx_packets_avg=result.getFloat(8);
        rx_packets_max=result.getFloat(9);
        rx_errors_min=result.getFloat(10);
        rx_errors_avg=result.getFloat(11);
        rx_errors_max=result.getFloat(12);
        rx_drop_min=result.getFloat(13);
        rx_drop_avg=result.getFloat(14);
        rx_drop_max=result.getFloat(15);
        rx_fifo_min=result.getFloat(16);
        rx_fifo_avg=result.getFloat(17);
        rx_fifo_max=result.getFloat(18);
        rx_frame_min=result.getFloat(19);
        rx_frame_avg=result.getFloat(20);
        rx_frame_max=result.getFloat(21);
        rx_compress_min=result.getFloat(22);
        rx_compress_avg=result.getFloat(23);
        rx_compress_max=result.getFloat(24);
        rx_multicast_min=result.getFloat(25);
        rx_multicast_avg=result.getFloat(26);
        rx_multicast_max=result.getFloat(27);
        tx_bytes_min=result.getFloat(28);
        tx_bytes_avg=result.getFloat(29);
        tx_bytes_max=result.getFloat(30);
        tx_packets_min=result.getFloat(31);
        tx_packets_avg=result.getFloat(32);
        tx_packets_max=result.getFloat(33);
        tx_errors_min=result.getFloat(34);
        tx_errors_avg=result.getFloat(35);
        tx_errors_max=result.getFloat(36);
        tx_drop_min=result.getFloat(37);
        tx_drop_avg=result.getFloat(38);
        tx_drop_max=result.getFloat(39);
        tx_fifo_min=result.getFloat(40);
        tx_fifo_avg=result.getFloat(41);
        tx_fifo_max=result.getFloat(42);
        tx_colls_min=result.getFloat(43);
        tx_colls_avg=result.getFloat(44);
        tx_colls_max=result.getFloat(45);
        tx_carrier_min=result.getFloat(46);
        tx_carrier_avg=result.getFloat(47);
        tx_carrier_max=result.getFloat(48);
        tx_compressed_min=result.getFloat(49);
        tx_compressed_avg=result.getFloat(50);
        tx_compressed_max=result.getFloat(51);
        listen_min=result.getInt(52);
        listen_avg=result.getFloat(53);
        listen_max=result.getInt(54);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        server_report=in.readCompressedInt();
        device_id=in.readUTF().intern();
        rx_bytes_min=in.readFloat();
        rx_bytes_avg=in.readFloat();
        rx_bytes_max=in.readFloat();
        rx_packets_min=in.readFloat();
        rx_packets_avg=in.readFloat();
        rx_packets_max=in.readFloat();
        rx_errors_min=in.readFloat();
        rx_errors_avg=in.readFloat();
        rx_errors_max=in.readFloat();
        rx_drop_min=in.readFloat();
        rx_drop_avg=in.readFloat();
        rx_drop_max=in.readFloat();
        rx_fifo_min=in.readFloat();
        rx_fifo_avg=in.readFloat();
        rx_fifo_max=in.readFloat();
        rx_frame_min=in.readFloat();
        rx_frame_avg=in.readFloat();
        rx_frame_max=in.readFloat();
        rx_compress_min=in.readFloat();
        rx_compress_avg=in.readFloat();
        rx_compress_max=in.readFloat();
        rx_multicast_min=in.readFloat();
        rx_multicast_avg=in.readFloat();
        rx_multicast_max=in.readFloat();
        tx_bytes_min=in.readFloat();
        tx_bytes_avg=in.readFloat();
        tx_bytes_max=in.readFloat();
        tx_packets_min=in.readFloat();
        tx_packets_avg=in.readFloat();
        tx_packets_max=in.readFloat();
        tx_errors_min=in.readFloat();
        tx_errors_avg=in.readFloat();
        tx_errors_max=in.readFloat();
        tx_drop_min=in.readFloat();
        tx_drop_avg=in.readFloat();
        tx_drop_max=in.readFloat();
        tx_fifo_min=in.readFloat();
        tx_fifo_avg=in.readFloat();
        tx_fifo_max=in.readFloat();
        tx_colls_min=in.readFloat();
        tx_colls_avg=in.readFloat();
        tx_colls_max=in.readFloat();
        tx_carrier_min=in.readFloat();
        tx_carrier_avg=in.readFloat();
        tx_carrier_max=in.readFloat();
        tx_compressed_min=in.readFloat();
        tx_compressed_avg=in.readFloat();
        tx_compressed_max=in.readFloat();
        listen_min=in.readCompressedInt();
        listen_avg=in.readFloat();
        listen_max=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(server_report);
        out.writeUTF(device_id);
        out.writeFloat(rx_bytes_min);
        out.writeFloat(rx_bytes_avg);
        out.writeFloat(rx_bytes_max);
        out.writeFloat(rx_packets_min);
        out.writeFloat(rx_packets_avg);
        out.writeFloat(rx_packets_max);
        out.writeFloat(rx_errors_min);
        out.writeFloat(rx_errors_avg);
        out.writeFloat(rx_errors_max);
        out.writeFloat(rx_drop_min);
        out.writeFloat(rx_drop_avg);
        out.writeFloat(rx_drop_max);
        out.writeFloat(rx_fifo_min);
        out.writeFloat(rx_fifo_avg);
        out.writeFloat(rx_fifo_max);
        out.writeFloat(rx_frame_min);
        out.writeFloat(rx_frame_avg);
        out.writeFloat(rx_frame_max);
        out.writeFloat(rx_compress_min);
        out.writeFloat(rx_compress_avg);
        out.writeFloat(rx_compress_max);
        out.writeFloat(rx_multicast_min);
        out.writeFloat(rx_multicast_avg);
        out.writeFloat(rx_multicast_max);
        out.writeFloat(tx_bytes_min);
        out.writeFloat(tx_bytes_avg);
        out.writeFloat(tx_bytes_max);
        out.writeFloat(tx_packets_min);
        out.writeFloat(tx_packets_avg);
        out.writeFloat(tx_packets_max);
        out.writeFloat(tx_errors_min);
        out.writeFloat(tx_errors_avg);
        out.writeFloat(tx_errors_max);
        out.writeFloat(tx_drop_min);
        out.writeFloat(tx_drop_avg);
        out.writeFloat(tx_drop_max);
        out.writeFloat(tx_fifo_min);
        out.writeFloat(tx_fifo_avg);
        out.writeFloat(tx_fifo_max);
        out.writeFloat(tx_colls_min);
        out.writeFloat(tx_colls_avg);
        out.writeFloat(tx_colls_max);
        out.writeFloat(tx_carrier_min);
        out.writeFloat(tx_carrier_avg);
        out.writeFloat(tx_carrier_max);
        out.writeFloat(tx_compressed_min);
        out.writeFloat(tx_compressed_avg);
        out.writeFloat(tx_compressed_max);
        out.writeCompressedInt(listen_min);
        out.writeFloat(listen_avg);
        out.writeCompressedInt(listen_max);
    }
}