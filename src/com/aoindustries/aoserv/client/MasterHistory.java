package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * For debugging, the last commands executed by the master are accessible as a table.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class MasterHistory extends AOServObject<Long,MasterHistory> implements SingleTableObject<Long,MasterHistory> {

    private static long nextCommandID=1;
    synchronized private static long getNextCommandID() {
        return nextCommandID++;
    }

    long command_id;
    private long process_id;
    private long connector_id;
    private String authenticated_user;
    private String effective_user;
    private String host;
    private String protocol;
    private boolean is_secure;
    private long start_time;
    private long end_time;
    private String command;

    protected AOServTable<Long,MasterHistory> table;

    public MasterHistory() {
    }

    public MasterHistory(
        long process_id,
        long connector_id,
        String authenticated_user,
        String effective_user,
        String host,
        String protocol,
        boolean is_secure,
        long start_time,
        long end_time,
        String command
    ) {
        this.command_id=getNextCommandID();
        this.process_id=process_id;
        this.connector_id=connector_id;
        this.authenticated_user=authenticated_user;
        this.effective_user=effective_user;
        this.host=host;
        this.protocol=protocol;
        this.is_secure=is_secure;
        this.start_time=start_time;
        this.end_time=end_time;
        this.command=command;
    }

    Object getColumnImpl(int i) {
        if(i==0) return Long.valueOf(command_id);
        if(i==1) return Long.valueOf(process_id);
        if(i==2) return Long.valueOf(connector_id);
        if(i==3) return authenticated_user;
        if(i==4) return effective_user;
        if(i==5) return host;
        if(i==6) return protocol;
        if(i==7) return is_secure?Boolean.TRUE:Boolean.FALSE;
        if(i==8) return new java.sql.Date(start_time);
        if(i==9) return new java.sql.Date(end_time);
        if(i==10) return command;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public long getCommandID() {
        return command_id;
    }

    public long getProcessID() {
        return process_id;
    }

    public long getConnectorID() {
        return connector_id;
    }
    
    public String getAuthenticatedUser() {
        return authenticated_user;
    }
    
    public BusinessAdministrator getAuthenticatedBusinessAdministrator() throws IOException, SQLException {
        // Null OK if filtered
        return table.connector.getBusinessAdministrators().get(authenticated_user);
    }

    public String getEffectiveUser() {
        return effective_user;
    }
    
    public BusinessAdministrator getEffectiveBusinessAdministrator() throws SQLException, IOException {
        BusinessAdministrator ba=table.connector.getBusinessAdministrators().get(effective_user);
        if(ba==null) throw new SQLException("Unable to find BusinessAdministrator: "+effective_user);
        return ba;
    }

    public String getHost() {
        return host;
    }
    
    public String getProtocol() {
        return protocol;
    }

    public boolean isSecure() {
        return is_secure;
    }
    
    public long getStartTime() {
        return start_time;
    }
    
    public long getEndTime() {
        return end_time;
    }
    
    public String getCommand() {
        return command;
    }

    @Override
    boolean equalsImpl(Object O) {
	return
            O instanceof MasterHistory
            && ((MasterHistory)O).command_id==command_id
	;
    }

    @Override
    int hashCodeImpl() {
        return (int)command_id;
    }

    public Long getKey() {
	return command_id;
    }

    final public AOServTable<Long,MasterHistory> getTable() {
    	return table;
    }

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.MASTER_HISTORY;
    }

    public void init(ResultSet result) throws SQLException {
    	throw new SQLException("Should not be read from the database, should be generated.");
    }

    public void read(CompressedDataInputStream in) throws IOException {
        command_id=in.readLong();
        process_id=in.readLong();
        connector_id=in.readLong();
        authenticated_user=in.readUTF().intern();
        effective_user=in.readUTF().intern();
        host=in.readUTF().intern();
        protocol=in.readUTF().intern();
        is_secure=in.readBoolean();
        start_time=in.readLong();
        end_time=in.readLong();
        command=in.readNullUTF();
    }

    public List<AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public List<AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public void setTable(AOServTable<Long,MasterHistory> table) {
        if(this.table!=null) throw new IllegalStateException("table already set");
        this.table=table;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeLong(command_id);
        out.writeLong(process_id);
        out.writeLong(connector_id);
        out.writeUTF(authenticated_user);
        out.writeUTF(effective_user);
        out.writeUTF(host);
        out.writeUTF(protocol);
        out.writeBoolean(is_secure);
        out.writeLong(start_time);
        out.writeLong(end_time);
        out.writeNullUTF(command);
    }
}