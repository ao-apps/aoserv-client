package com.aoindustries.aoserv.client;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * For debugging and optimization, code profiling may be enabled
 * on the local system.  When enabled, <code>ClientJvmProfile</code>
 * provides table-like access to this profiling information.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ClientJvmProfile extends AOServObject<String,ClientJvmProfile> implements SingleTableObject<String,ClientJvmProfile> {

    static final String COLUMN_CLASSNAME_name = "classname";
    static final String COLUMN_METHOD_NAME_name = "method_name";
    static final String COLUMN_PARAMETER_name = "parameter";

    public static ClientJvmProfile getClientJvmProfile(MethodProfile profile) {
        Object param1=profile.getParameter1();
        return new ClientJvmProfile(
            profile.getLevel(),
            profile.getProfiledClass().getName(),
            profile.getMethodName(),
            param1==null?null:param1.toString(),
            profile.getUseCount(),
            profile.getTotalTime(),
            profile.getMinTime(),
            profile.getMaxTime()
        );
    }

    private int level;
    private String classname;
    private String method_name;
    private String parameter;
    private long use_count;
    private long total_time;
    private long min_time;
    private long max_time;

    protected AOServTable<String,ClientJvmProfile> table;

    public ClientJvmProfile() {
    }

    public ClientJvmProfile(
        int level,
        String classname,
	String method_name,
	String parameter,
	long use_count,
	long total_time,
	long min_time,
	long max_time
    ) {
        this.level=level;
        this.classname=classname;
	this.method_name=method_name;
	this.parameter=parameter;
	this.use_count=use_count;
	this.total_time=total_time;
	this.min_time=min_time;
	this.max_time=max_time;
    }

    public int getLevel() {
        return level;
    }

    public String getClassname() {
        return classname;
    }

    public Object getColumn(int i) {
        switch(i) {
            case 0: return Integer.valueOf(level);
            case 1: return classname;
            case 2: return method_name;
            case 3: return parameter;
            case 4: return Long.valueOf(use_count);
            case 5: return Long.valueOf(total_time);
            case 6: return min_time==Long.MAX_VALUE?null:Long.valueOf(min_time);
            case 7: return max_time==Long.MIN_VALUE?null:Long.valueOf(max_time);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public long getMaxTime() {
	return max_time;
    }

    public String getMethodName() {
	return method_name;
    }

    public long getMinTime() {
	return min_time;
    }

    public String getParameter() {
	return parameter;
    }

    public String getKey() {
	return parameter==null?method_name:(method_name+':'+parameter);
    }

    final public AOServTable<String,ClientJvmProfile> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.CLIENT_JVM_PROFILE;
    }

    public long getTotalTime() {
	return total_time;
    }

    public long getUseCount() {
	return use_count;
    }

    void initImpl(ResultSet result) throws SQLException {
	throw new SQLException("Should not be read from the database, should be generated.");
    }

    public void read(CompressedDataInputStream in) throws IOException {
	throw new IOException("Should not be read from stream, should be generated.");
    }

    public void setTable(AOServTable<String,ClientJvmProfile> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        throw new IOException("Should not be written to stream, should be generated.");
    }
}