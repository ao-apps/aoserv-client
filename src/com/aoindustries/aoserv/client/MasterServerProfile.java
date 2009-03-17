package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.MethodProfile;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * For debugging and optimization, code profiling may be enabled
 * on the system.  When enabled, <code>MasterServerProfile</code>
 * provides table-like access to this profiling information.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterServerProfile extends AOServObject<String,MasterServerProfile> implements SingleTableObject<String,MasterServerProfile> {

    static final String COLUMN_CLASSNAME_name = "classname";
    static final String COLUMN_METHOD_NAME_name = "method_name";
    static final String COLUMN_PARAMETER_name = "parameter";
    
    public static MasterServerProfile getMasterServerProfile(MethodProfile profile) {
        Object param1=profile.getParameter1();
        return new MasterServerProfile(
            profile.getLevel(),
            profile.getProfiledClassName(),
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
    protected AOServTable<String,MasterServerProfile> table;

    public MasterServerProfile() {
    }

    public MasterServerProfile(
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

    Object getColumnImpl(int i) {
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

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<String,MasterServerProfile> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_SERVER_PROFILE;
    }

    public long getTotalTime() {
	return total_time;
    }

    public long getUseCount() {
	return use_count;
    }

    public void init(ResultSet result) throws SQLException {
	throw new SQLException("Should not be read from the database, should be generated.");
    }

    public void read(CompressedDataInputStream in) throws IOException {
        level=in.readCompressedInt();
        classname=in.readUTF();
	method_name=in.readUTF();
	parameter=in.readNullUTF();
	use_count=in.readLong();
	total_time=in.readLong();
	min_time=in.readLong();
	max_time=in.readLong();
    }

    public void setTable(AOServTable<String,MasterServerProfile> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(level);
        out.writeUTF(classname);
	out.writeUTF(method_name);
        out.writeNullUTF(parameter);
	out.writeLong(use_count);
	out.writeLong(total_time);
	out.writeLong(min_time);
	out.writeLong(max_time);
    }
}