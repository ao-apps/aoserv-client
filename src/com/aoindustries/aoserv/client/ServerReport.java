package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * Every minute, each <code>AOServer</code> polls its resources.  At five
 * minute intervals, resource statistics are logged in the database
 * as a <code>ServerReport</code>.
 *
 * @see  ServerReportSection
 * @see  AOServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ServerReport extends CachedObjectIntegerKey<ServerReport> {

    static final int COLUMN_PKEY=0;

    /**
     * The time the information was reported (ending time of the interval)
     */
    private long time;

    /**
     * The time interval in milliseconds that these stats apply to.
     */
    private long interval;

    /**
     * The server that generated this report.
     */
    private int ao_server;
    
    private int failover_server;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case 1: return new java.sql.Date(time);
            case 2: return Long.valueOf(interval);
            case 3: return Integer.valueOf(ao_server);
            case 4: return Integer.valueOf(failover_server);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public AOServer getFailoverAOServer() {
        if(failover_server==-1) return null;
        AOServer se=table.connector.aoServers.get(failover_server);
        if(se==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+failover_server));
        return se;
    }

    public long getInterval() {
	return interval;
    }

    public AOServer getAOServer() {
	AOServer server=table.connector.aoServers.get(ao_server);
	if(server==null) throw new WrappedException(new SQLException("Unable to find AOServer: "+ao_server));
	return server;
    }

    public List<SRCpu> getSRCpus() {
        return failover_server!=-1?null:table.connector.srCpu.getSRCpus(this);
    }

    public SRDbMySQL getSRDbMySQL() {
        return table.connector.srDbMySQL.get(pkey);
    }
    
    public SRDbPostgres getSRDbPostgres() {
        return failover_server!=-1?null:table.connector.srDbPostgres.get(pkey);
    }
    
    public List<SRDiskAccess> getSRDiskAccesses() {
        return failover_server!=-1?null:table.connector.srDiskAccess.getSRDiskAccesses(this);
    }

    public List<SRDiskMDStat> getSRDiskMDStats() {
        return failover_server!=-1?null:table.connector.srDiskMDStat.getSRDiskMDStats(this);
    }

    public List<SRDiskSpace> getSRDiskSpaces() {
        return failover_server!=-1?null:table.connector.srDiskSpace.getSRDiskSpaces(this);
    }

    public SRKernel getSRKernel() {
        if(failover_server!=-1) return null;
        SRKernel srs=table.connector.srKernel.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRKernel: "+pkey));
        return srs;
    }

    public SRLoad getSRLoad() {
        if(failover_server!=-1) return null;
        SRLoad srs=table.connector.srLoad.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRLoad: "+pkey));
        return srs;
    }

    public SRMemory getSRMemory() {
        if(failover_server!=-1) return null;
        SRMemory srs=table.connector.srMemory.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRMemory: "+pkey));
        return srs;
    }

    public List<SRNetDevice> getSRNetDevices() {
        return failover_server!=-1?null:table.connector.srNetDevice.getSRNetDevices(this);
    }

    public SRNetICMP getSRNetICMP() {
        if(failover_server!=-1) return null;
        SRNetICMP srs=table.connector.srNetICMP.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRNetICMP: "+pkey));
        return srs;
    }

    public SRNetIP getSRNetIP() {
        if(failover_server!=-1) return null;
        SRNetIP srs=table.connector.srNetIP.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRNetIP: "+pkey));
        return srs;
    }

    public SRNetTCP getSRNetTCP() {
        if(failover_server!=-1) return null;
        SRNetTCP srs=table.connector.srNetTCP.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRNetTCP: "+pkey));
        return srs;
    }

    public SRNetUDP getSRNetUDP() {
        if(failover_server!=-1) return null;
        SRNetUDP srs=table.connector.srNetUDP.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRNetUDP: "+pkey));
        return srs;
    }

    public SRNumUsers getSRNumUsers() {
        SRNumUsers srs=table.connector.srNumUsers.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRNumUsers: "+pkey));
        return srs;
    }

    public SRPaging getSRPaging() {
        if(failover_server!=-1) return null;
        SRPaging srs=table.connector.srPaging.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRPaging: "+pkey));
        return srs;
    }

    public SRProcesses getSRProcesses() {
        SRProcesses srs=table.connector.srProcesses.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRProcesses: "+pkey));
        return srs;
    }

    public SRSwapRate getSRSwapRate() {
        if(failover_server!=-1) return null;
        SRSwapRate srs=table.connector.srSwapRate.get(pkey);
        if(srs==null) throw new WrappedException(new SQLException("Unable to find SRSwapRate: "+pkey));
        return srs;
    }

    public List<SRSwapSize> getSRSwapSizes() {
        return failover_server!=-1?null:table.connector.srSwapSize.getSRSwapSizes(this);
    }

    protected int getTableIDImpl() {
	return SchemaTable.SERVER_REPORTS;
    }

    public long getTime() {
	return time;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey=result.getInt(1);
	time=result.getTimestamp(2).getTime();
	interval=result.getLong(3);
	ao_server=result.getInt(4);
        failover_server=result.getInt(5);
        if(result.wasNull()) failover_server=-1;
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readCompressedInt();
	time=in.readLong();
	interval=in.readLong();
	ao_server=in.readCompressedInt();
        failover_server=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeLong(time);
	out.writeLong(interval);
	out.writeCompressedInt(ao_server);
	out.writeCompressedInt(failover_server);
    }
}