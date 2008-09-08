package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * For debugging and optimization, code profiling may be enabled
 * on the system.  When enabled, <code>DaemonProfile</code>
 * provides table-like access to this profiling information.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class DaemonProfile extends AOServObject<Object,DaemonProfile> implements SingleTableObject<Object,DaemonProfile> {

    static final String COLUMN_AO_SERVER_name= "ao_server";
    static final String COLUMN_CLASSNAME_name= "classname";
    static final String COLUMN_METHOD_NAME_name= "method_name";
    static final String COLUMN_PARAMETER_name= "parameter";
    
    public static DaemonProfile getDaemonProfile(String ao_server, MethodProfile profile) {
        Object param1=profile.getParameter1();
        return new DaemonProfile(
            ao_server,
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

    private String ao_server;
    private int level;
    private String classname;
    private String method_name;
    private String parameter;
    private long use_count;
    private long total_time;
    private long min_time;
    private long max_time;
    protected AOServTable<Object,DaemonProfile> table;

    public DaemonProfile() {
    }

    public DaemonProfile(
        String ao_server,
        int level,
        String classname,
	String method_name,
	String parameter,
	long use_count,
	long total_time,
	long min_time,
	long max_time
    ) {
        this.ao_server=ao_server;
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
            case 0: return ao_server;
            case 1: return Integer.valueOf(level);
            case 2: return classname;
            case 3: return method_name;
            case 4: return parameter;
            case 5: return Long.valueOf(use_count);
            case 6: return Long.valueOf(total_time);
            case 7: return min_time==Long.MAX_VALUE?null:Long.valueOf(min_time);
            case 8: return max_time==Long.MIN_VALUE?null:Long.valueOf(max_time);
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

    public Object getKey() {
	return parameter==null?method_name:(method_name+':'+parameter);
    }

    public AOServer getAOServer() {
        AOServer ao=table.connector.aoServers.get(ao_server);
        if(ao==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
        return ao;
    }

    final public AOServTable<Object,DaemonProfile> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.DAEMON_PROFILE;
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
        ao_server=in.readUTF().intern();
        level=in.readCompressedInt();
        classname=in.readUTF();
	method_name=in.readUTF();
	parameter=in.readBoolean()?in.readUTF():null;
	use_count=in.readLong();
	total_time=in.readLong();
	min_time=in.readLong();
	max_time=in.readLong();
    }

    public void setTable(AOServTable<Object,DaemonProfile> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeUTF(ao_server);
        out.writeCompressedInt(level);
        out.writeUTF(classname);
	out.writeUTF(method_name);
	out.writeBoolean(parameter!=null); if(parameter!=null) out.writeUTF(parameter);
	out.writeLong(use_count);
	out.writeLong(total_time);
	out.writeLong(min_time);
	out.writeLong(max_time);
    }
}