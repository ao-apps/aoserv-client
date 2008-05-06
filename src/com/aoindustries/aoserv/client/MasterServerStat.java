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
 * To aid in system reliability, scalability, and debugging, many server
 * runtime statistics are maintained.  <code>MasterServerStat</code>
 * provides table-like access to this data.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerStat extends AOServObject<String,MasterServerStat> implements SingleTableObject<String,MasterServerStat> {

    public static final String
        BYTE_ARRAY_CACHE_CREATES="byte_array_cache_creates",
        BYTE_ARRAY_CACHE_USES="byte_array_cache_uses",
        CHAR_ARRAY_CACHE_CREATES="char_array_cache_creates",
        CHAR_ARRAY_CACHE_USES="char_array_cache_uses",
        DAEMON_CONCURRENCY="daemon_concurrency",
        DAEMON_CONNECTIONS="daemon_connections",
        DAEMON_CONNECTS="daemon_connects",
        DAEMON_COUNT="daemon_count",
        DAEMON_DOWN_COUNT="daemon_down_count",
        DAEMON_MAX_CONCURRENCY="daemon_max_concurrency",
        DAEMON_POOL_SIZE="daemon_pool_size",
        DAEMON_TOTAL_TIME="daemon_total_time",
        DAEMON_TRANSACTIONS="daemon_transactions",
        DB_CONCURRENCY="db_concurrency",
        DB_CONNECTIONS="db_connections",
        DB_CONNECTS="db_connects",
        DB_MAX_CONCURRENCY="db_max_concurrency",
        DB_POOL_SIZE="db_pool_size",
        DB_QUERIES="db_queries",
        DB_TOTAL_TIME="db_total_time",
        DB_TRANSACTIONS="db_transactions",
        DB_UPDATES="db_updates",
        ENTROPY_AVAIL="entropy_avail",
        ENTROPY_POOLSIZE="entropy_poolsize",
        ENTROPY_READ_BYTES="entropy_read_bytes",
        ENTROPY_READ_COUNT="entropy_read_count",
        ENTROPY_WRITE_BYTES="entropy_write_bytes",
        ENTROPY_WRITE_COUNT="entropy_write_count",
        MEMORY_FREE="memory_free",
        MEMORY_TOTAL="memory_total",
        METHOD_CONCURRENCY="method_concurrency",
        METHOD_MAX_CONCURRENCY="method_max_concurrency",
        METHOD_PROFILE_LEVEL="method_profile_level",
        METHOD_USES="method_uses",
        PROTOCOL_VERSION="protocol_version",
        REQUEST_CONCURRENCY="request_concurrency",
        REQUEST_CONNECTIONS="request_connections",
        REQUEST_MAX_CONCURRENCY="request_max_concurrency",
        REQUEST_TOTAL_TIME="request_total_time",
        REQUEST_TRANSACTIONS="request_transactions",
        THREAD_COUNT="thread_count",
        UPTIME="uptime"
    ;

    String name;
    private String value;
    private String description;
    protected AOServTable<String,MasterServerStat> table;

    public MasterServerStat() {
    }

    public MasterServerStat(String name, String value, String description) {
	this.name=name;
	this.value=value;
	this.description=description;
    }

    public Object getColumn(int i) {
	if(i==0) return name;
	if(i==1) return value;
	if(i==2) return description;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public String getName() {
	return name;
    }

    public String getKey() {
	return name;
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<String,MasterServerStat> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_SERVER_STATS;
    }

    public String getValue() {
	return value;
    }

    void initImpl(ResultSet result) throws SQLException {
	throw new SQLException("Should not be read from the database, should be generated.");
    }

    public void read(CompressedDataInputStream in) throws IOException {
	name=in.readUTF().intern();
	value=in.readBoolean()?in.readUTF():null;
	description=in.readUTF();
    }

    public void setTable(AOServTable<String,MasterServerStat> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(name);
	out.writeBoolean(value!=null); if(value!=null) out.writeUTF(value);
	out.writeUTF(description);
    }
}