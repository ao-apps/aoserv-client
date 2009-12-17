package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.sql.*;
import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Locale;

/**
 * Each server may perform TCP redirects via xinetd.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class NetTcpRedirect extends CachedObjectIntegerKey<NetTcpRedirect> {

    static final int COLUMN_NET_BIND=0;
    static final String COLUMN_NET_BIND_name = "net_bind";

    private int cps;
    private int cps_overload_sleep_time;
    private String destination_host;
    private int destination_port;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NET_BIND: return Integer.valueOf(pkey);
            case 1: return Integer.valueOf(cps);
            case 2: return Integer.valueOf(cps_overload_sleep_time);
            case 3: return destination_host;
            case 4: return Integer.valueOf(destination_port);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public NetBind getNetBind() throws IOException, SQLException {
        NetBind nb=table.connector.getNetBinds().get(pkey);
        if(nb==null) throw new SQLException("Unable to find NetBind: "+pkey);
        return nb;
    }

    public int getConnectionsPerSecond() {
        return cps;
    }
    
    public int getConnectionsPerSecondOverloadSleepTime() {
        return cps_overload_sleep_time;
    }
    
    public String getDestinationHost() {
        return destination_host;
    }
    
    public NetPort getDestinationPort() throws SQLException {
        NetPort np=table.connector.getNetPorts().get(destination_port);
        if(np==null) throw new SQLException("Unable to find NetPort: "+destination_port);
        return np;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NET_TCP_REDIRECTS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey=result.getInt(1);
        cps=result.getInt(2);
        cps_overload_sleep_time=result.getInt(3);
        destination_host=result.getString(4);
        destination_port=result.getInt(5);
   }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        cps=in.readCompressedInt();
        cps_overload_sleep_time=in.readCompressedInt();
        destination_host=in.readUTF().intern();
        destination_port=in.readCompressedInt();
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getNetBind()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    @Override
    String toStringImpl(Locale userLocale) throws SQLException, IOException {
        return getNetBind().toStringImpl(userLocale)+"->"+destination_host+':'+destination_port;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(cps);
        out.writeCompressedInt(cps_overload_sleep_time);
        out.writeUTF(destination_host);
        out.writeCompressedInt(destination_port);
    }
}