package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Each <code>Thread</code> on the master reports its activities so that
 * a query on this table shows a snapshot of the currently running system.
 *
 * @author  AO Industries, Inc.
 */
final public class MasterProcess extends AOServObject<Long,MasterProcess> implements SingleTableObject<Long,MasterProcess> {

    private static boolean logCommands=false;

    /**
     * Turns on/off command logging.
     */
    public static void setLogCommands(boolean state) {
        logCommands=state;
    }
    
    public static boolean getLogCommands() {
        return logCommands;
    }

    /**
     * The different states a process may be in.
     */
    public static final String
        LOGIN="login",
        RUN="run",
        SLEEP="sleep"
    ;

    long process_id;
    private long connector_id=-1;
    private String authenticated_user;
    private String effective_user;
    private int daemon_server;
    private String host;
    private String protocol;
    private String aoserv_protocol;
    private boolean is_secure;
    private long connect_time;
    private long use_count;
    private long total_time;
    private int priority;
    private String state;
    private Object[] command;
    private long state_start_time;

    protected AOServTable<Long,MasterProcess> table;

    public MasterProcess() {
    }

    public MasterProcess(
        long process_id,
        String host,
        String protocol,
        boolean is_secure,
        long connect_time
    ) {
        this.process_id=process_id;
        this.host=host;
        this.protocol=protocol;
        this.is_secure=is_secure;
        this.connect_time=connect_time;
        this.priority=Thread.NORM_PRIORITY;
        this.state=LOGIN;
        this.state_start_time=connect_time;
    }

    synchronized public void commandCompleted() {
        long time=System.currentTimeMillis();
        total_time+=time-state_start_time;
        state=SLEEP;
        command=null;
        state_start_time=time;
    }
    
    synchronized public void commandRunning() {
        use_count++;
        state=RUN;
        state_start_time=System.currentTimeMillis();
    }

    synchronized public void commandSleeping() {
        if(!state.equals(SLEEP)) {
            long time=System.currentTimeMillis();
            state=SLEEP;
            total_time+=time-state_start_time;
            state_start_time=time;
        }
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case 0: return Long.valueOf(process_id);
            case 1: return connector_id==-1?null:Long.valueOf(connector_id);
            case 2: return authenticated_user;
            case 3: return effective_user;
            case 4: return daemon_server==-1?null:Integer.valueOf(daemon_server);
            case 5: return host;
            case 6: return protocol;
            case 7: return aoserv_protocol;
            case 8: return is_secure?Boolean.TRUE:Boolean.FALSE;
            case 9: return new java.sql.Date(connect_time);
            case 10: return Long.valueOf(use_count);
            case 11: return Long.valueOf(total_time);
            case 12: return Integer.valueOf(priority);
            case 13: return state;
            case 14: return getCommand();
            case 15: return new java.sql.Date(state_start_time);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public int getDaemonServer() {
        return daemon_server;
    }

    public int getPriority() {
        return priority;
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
        // Null OK when filtered
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

    public String getAOServProtocol() {
        return aoserv_protocol;
    }
    
    public void setAOServProtocol(String aoserv_protocol) {
        this.aoserv_protocol=aoserv_protocol;
    }

    public boolean isSecure() {
        return is_secure;
    }

    public long getConnectTime() {
        return connect_time;
    }
    
    public long getUseCount() {
        return use_count;
    }
    
    public long getTotalTime() {
        return total_time;
    }
    
    public String getState() {
        return state;
    }
    
    public long getStateStartTime() {
        return state_start_time;
    }

    public Long getKey() {
	return process_id;
    }

    /**
     * Gets the <code>AOServTable</code> that contains this <code>AOServObject</code>.
     *
     * @return  the <code>AOServTable</code>.
     */
    final public AOServTable<Long,MasterProcess> getTable() {
	return table;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.MASTER_PROCESSES;
    }

    public void init(ResultSet result) throws SQLException {
	throw new SQLException("Should not be read from the database, should be generated.");
    }

    public void read(CompressedDataInputStream in) throws IOException {
        process_id=in.readLong();
        connector_id=in.readLong();
        authenticated_user=StringUtility.intern(in.readNullUTF());
        effective_user=StringUtility.intern(in.readNullUTF());
        daemon_server=in.readCompressedInt();
        host=in.readUTF().intern();
        protocol=in.readUTF().intern();
        aoserv_protocol=StringUtility.intern(in.readNullUTF());
        is_secure=in.readBoolean();
        connect_time=in.readLong();
        use_count=in.readLong();
        total_time=in.readLong();
        priority=in.readCompressedInt();
        state=in.readUTF().intern();
        if(in.readBoolean()) {
            command=new Object[] {in.readUTF()};
        } else {
            command=null;
        }
        state_start_time=in.readLong();
    }

    synchronized public void setCommand(Object ... command) {
        if(command==null) this.command=null;
        else {
            this.command=command;
        }
    }

    synchronized public String getCommand() {
        if(command==null) return null;
        StringBuilder SB=new StringBuilder();
        int len=command.length;
        for(int c=0;c<len;c++) {
            if(c>0) SB.append(' ');
            Object com=command[c];
            if(com==null) SB.append("''");
            else if(com instanceof Object[]) {
                Object[] oa=(Object[])com;
                int oaLen=oa.length;
                for(int d=0;d<oaLen;d++) {
                    if(d>0) SB.append(' ');
                    Object com2=oa[d];
                    if(com2==null) SB.append("''");
                    else SB.append(com2);
                }
            } else SB.append(com);
        }
        return SB.toString();
    }

    public void setAuthenticatedUser(String username) {
        authenticated_user=username;
    }

    public void setConnectorID(long id) {
        connector_id=id;
    }

    public void setDeamonServer(int server) {
        daemon_server=server;
    }

    public void setEffectiveUser(String username) {
        effective_user=username;
    }

    public void setPriority(int priority) {
        this.priority=priority;
    }

    public void setTable(AOServTable<Long,MasterProcess> table) {
	if(this.table!=null) throw new IllegalStateException("table already set");
	this.table=table;
    }
    
    @Override
    protected String toStringImpl(Locale userLocale) {
        return getCommand();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeLong(process_id);
        out.writeLong(connector_id);
        out.writeNullUTF(authenticated_user);
        out.writeNullUTF(effective_user);
        out.writeCompressedInt(daemon_server);
        out.writeUTF(host);
        out.writeUTF(protocol);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_0_A_101)>=0) out.writeNullUTF(aoserv_protocol);
        out.writeBoolean(is_secure);
        out.writeLong(connect_time);
        out.writeLong(use_count);
        out.writeLong(total_time);
        out.writeCompressedInt(priority);
        out.writeUTF(state);
        String myCommand=getCommand();
        out.writeBoolean(myCommand!=null);
        if(myCommand!=null) out.writeUTF(myCommand);
        out.writeLong(state_start_time);
    }
}