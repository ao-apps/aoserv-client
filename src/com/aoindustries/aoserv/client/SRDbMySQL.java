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
final public class SRDbMySQL extends ServerReportSection<SRDbMySQL> {

    static final int COLUMN_SERVER_REPORT=0;
    
    private int conn_min;
    private float conn_avg;
    private int conn_max;
    private float questions_min;
    private float questions_avg;
    private float questions_max;
    private float slow_queries_min;
    private float slow_queries_avg;
    private float slow_queries_max;
    private float opens_min;
    private float opens_avg;
    private float opens_max;
    private float flush_min;
    private float flush_avg;
    private float flush_max;
    private int open_tables_min;
    private float open_tables_avg;
    private int open_tables_max;
    private float query_rate_min;
    private float query_rate_avg;
    private float query_rate_max;
    
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_SERVER_REPORT: return Integer.valueOf(server_report);
            case 1: return Integer.valueOf(conn_min);
            case 2: return new Float(conn_avg);
            case 3: return Integer.valueOf(conn_max);
            case 4: return new Float(questions_min);
            case 5: return new Float(questions_avg);
            case 6: return new Float(questions_max);
            case 7: return new Float(slow_queries_min);
            case 8: return new Float(slow_queries_avg);
            case 9: return new Float(slow_queries_max);
            case 10: return new Float(opens_min);
            case 11: return new Float(opens_avg);
            case 12: return new Float(opens_max);
            case 13: return new Float(flush_min);
            case 14: return new Float(flush_avg);
            case 15: return new Float(flush_max);
            case 16: return Integer.valueOf(open_tables_min);
            case 17: return new Float(open_tables_avg);
            case 18: return Integer.valueOf(open_tables_max);
            case 19: return new Float(query_rate_min);
            case 20: return new Float(query_rate_avg);
            case 21: return new Float(query_rate_max);
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
    
    public float getQuestionsMin() {
        return questions_min;
    }
    
    public float getQuestionsAvg() {
        return questions_avg;
    }
    
    public float getQuestionsMax() {
        return questions_max;
    }
    
    public float getSlowQueriesMin() {
        return slow_queries_min;
    }
    
    public float getSlowQueriesAvg() {
        return slow_queries_avg;
    }
    
    public float getSlowQueriesMax() {
        return slow_queries_max;
    }
    
    public float getOpensMin() {
        return opens_min;
    }
    
    public float getOpensAvg() {
        return opens_avg;
    }
    
    public float getOpensMax() {
        return opens_max;
    }
    
    public float getFlushMin() {
        return flush_min;
    }
    
    public float getFlushAvg() {
        return flush_avg;
    }
    
    public float getFlushMax() {
        return flush_max;
    }
    
    public int getOpenTablesMin() {
        return open_tables_min;
    }
    
    public float getOpenTablesAvg() {
        return open_tables_avg;
    }
    
    public int getOpenTablesMax() {
        return open_tables_max;
    }
    
    public float getQueryRateMin() {
        return query_rate_min;
    }
    
    public float getQueryRateAvg() {
        return query_rate_avg;
    }
    
    public float getQueryRateMax() {
        return query_rate_max;
    }
    
    public Integer getKey() {
	return server_report;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SR_DB_MYSQL;
    }

    int hashCodeImpl() {
	return server_report;
    }

    void initImpl(ResultSet result) throws SQLException {
        server_report=result.getInt(1);
        conn_min=result.getInt(2);
        conn_avg=result.getFloat(3);
        conn_max=result.getInt(4);
        questions_min=result.getFloat(5);
        questions_avg=result.getFloat(6);
        questions_max=result.getFloat(7);
        slow_queries_min=result.getFloat(8);
        slow_queries_avg=result.getFloat(9);
        slow_queries_max=result.getFloat(10);
        opens_min=result.getFloat(11);
        opens_avg=result.getFloat(12);
        opens_max=result.getFloat(13);
        flush_min=result.getFloat(14);
        flush_avg=result.getFloat(15);
        flush_max=result.getFloat(16);
        open_tables_min=result.getInt(17);
        open_tables_avg=result.getFloat(18);
        open_tables_max=result.getInt(19);
        query_rate_min=result.getFloat(20);
        query_rate_avg=result.getFloat(21);
        query_rate_max=result.getFloat(22);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        server_report=in.readCompressedInt();
        conn_min=in.readCompressedInt();
        conn_avg=in.readFloat();
        conn_max=in.readCompressedInt();
        questions_min=in.readFloat();
        questions_avg=in.readFloat();
        questions_max=in.readFloat();
        slow_queries_min=in.readFloat();
        slow_queries_avg=in.readFloat();
        slow_queries_max=in.readFloat();
        opens_min=in.readFloat();
        opens_avg=in.readFloat();
        opens_max=in.readFloat();
        flush_min=in.readFloat();
        flush_avg=in.readFloat();
        flush_max=in.readFloat();
        open_tables_min=in.readCompressedInt();
        open_tables_avg=in.readFloat();
        open_tables_max=in.readCompressedInt();
        query_rate_min=in.readFloat();
        query_rate_avg=in.readFloat();
        query_rate_max=in.readFloat();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(server_report);
        out.writeCompressedInt(conn_min);
        out.writeFloat(conn_avg);
        out.writeCompressedInt(conn_max);
        out.writeFloat(questions_min);
        out.writeFloat(questions_avg);
        out.writeFloat(questions_max);
        out.writeFloat(slow_queries_min);
        out.writeFloat(slow_queries_avg);
        out.writeFloat(slow_queries_max);
        out.writeFloat(opens_min);
        out.writeFloat(opens_avg);
        out.writeFloat(opens_max);
        out.writeFloat(flush_min);
        out.writeFloat(flush_avg);
        out.writeFloat(flush_max);
        out.writeCompressedInt(open_tables_min);
        out.writeFloat(open_tables_avg);
        out.writeCompressedInt(open_tables_max);
        out.writeFloat(query_rate_min);
        out.writeFloat(query_rate_avg);
        out.writeFloat(query_rate_max);
    }
}