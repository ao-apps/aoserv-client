package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.IntList;
import com.aoindustries.util.WrappedException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  NetBind
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NetBindTable extends CachedTableIntegerKey<NetBind> {

    NetBindTable(AOServConnector connector) {
        super(connector, NetBind.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(NetBind.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
        new OrderBy(NetBind.COLUMN_IP_ADDRESS_name+'.'+IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING),
        new OrderBy(NetBind.COLUMN_PORT_name, ASCENDING),
        new OrderBy(NetBind.COLUMN_NET_PROTOCOL_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    int addNetBind(
        Server se,
        Package pk,
        IPAddress ia,
        NetPort netPort,
        NetProtocol netProtocol,
        Protocol appProtocol,
        boolean openFirewall,
        boolean monitoringEnabled
    ) throws IOException, SQLException {
        int pkey;
        IntList invalidateList;
        AOServConnection connection=connector.getConnection();
        try {
            CompressedDataOutputStream out=connection.getOutputStream();
            out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
            out.writeCompressedInt(SchemaTable.TableID.NET_BINDS.ordinal());
            out.writeCompressedInt(se.pkey);
            out.writeUTF(pk.name);
            out.writeCompressedInt(ia.pkey);
            out.writeCompressedInt(netPort.port);
            out.writeUTF(netProtocol.pkey);
            out.writeUTF(appProtocol.pkey);
            out.writeBoolean(openFirewall);
            out.writeBoolean(monitoringEnabled);
            out.flush();

            CompressedDataInputStream in=connection.getInputStream();
            int code=in.readByte();
            if(code==AOServProtocol.DONE) {
                pkey=in.readCompressedInt();
                invalidateList=AOServConnector.readInvalidateList(in);
            } else {
                AOServProtocol.checkResult(code, in);
                throw new IOException("Unexpected response code: "+code);
            }
        } catch(IOException err) {
            connection.close();
            throw err;
        } finally {
            connector.releaseConnection(connection);
        }
        connector.tablesUpdated(invalidateList);
        return pkey;
    }

    public NetBind get(Object pkey) {
        try {
            return getUniqueRow(NetBind.COLUMN_PKEY, pkey);
        } catch (IOException err) {
            throw new WrappedException(err);
        } catch (SQLException err) {
            throw new WrappedException(err);
        }
    }

    public NetBind get(int pkey) throws IOException, SQLException {
	return getUniqueRow(NetBind.COLUMN_PKEY, pkey);
    }

    List<NetBind> getNetBinds(IPAddress ia) throws IOException, SQLException {
        return getIndexedRows(NetBind.COLUMN_IP_ADDRESS, ia.pkey);
    }

    List<NetBind> getNetBinds(Package pk) throws IOException, SQLException {
        return getIndexedRows(NetBind.COLUMN_PACKAGE, pk.name);
    }

    List<NetBind> getNetBinds(Package pk, IPAddress ip) throws IOException, SQLException {
	String packageName=pk.name;
        // Use the index first
	List<NetBind> cached=getNetBinds(ip);
	int size=cached.size();
        List<NetBind> matches=new ArrayList<NetBind>(size);
	for(int c=0;c<size;c++) {
            NetBind nb=cached.get(c);
            if(nb.packageName.equals(packageName)) matches.add(nb);
	}
	return matches;
    }

    List<NetBind> getNetBinds(Server se) throws IOException, SQLException {
        return getIndexedRows(NetBind.COLUMN_SERVER, se.pkey);
    }

    List<NetBind> getNetBinds(Server se, IPAddress ip) throws IOException, SQLException {
	int ipAddress=ip.pkey;

        // Use the index first
	List<NetBind> cached=getNetBinds(se);
	int size=cached.size();
        List<NetBind> matches=new ArrayList<NetBind>(size);
	for(int c=0;c<size;c++) {
            NetBind nb=cached.get(c);
            if(nb.ip_address==ipAddress) matches.add(nb);
	}
	return matches;
    }

    NetBind getNetBind(
        Server se,
        IPAddress ip,
        NetPort netPort,
        NetProtocol netProtocol
    ) throws IOException, SQLException {
        int sePKey=se.pkey;
        int port=netPort.getPort();
        String netProt=netProtocol.getProtocol();

        // Use the index first
	List<NetBind> cached=getNetBinds(ip);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            NetBind nb=cached.get(c);
            if(
                nb.server==sePKey
                && nb.port==port
                && nb.net_protocol.equals(netProt)
            ) return nb;
	}
	return null;
    }

    List<NetBind> getNetBinds(Server se, Protocol protocol) throws IOException, SQLException {
	String prot=protocol.pkey;

        // Use the index first
	List<NetBind> cached=getNetBinds(se);
	int size=cached.size();
        List<NetBind> matches=new ArrayList<NetBind>(size);
	for(int c=0;c<size;c++) {
            NetBind nb=cached.get(c);
            if(nb.app_protocol.equals(prot)) matches.add(nb);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NET_BINDS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_NET_BIND)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_NET_BIND, args, 9, err)) {
                connector.simpleAOClient.addNetBind(
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    AOSH.parseInt(args[5], "port"),
                    args[6],
                    args[7],
                    AOSH.parseBoolean(args[8], "open_firewall"),
                    AOSH.parseBoolean(args[9], "monitoring_enabled")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_NET_BIND)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_NET_BIND, args, 1, err)) {
                connector.simpleAOClient.removeNetBind(
                    AOSH.parseInt(args[1], "pkey")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_NET_BIND_MONITORING_ENABLED)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_NET_BIND_MONITORING_ENABLED, args, 2, err)) {
                connector.simpleAOClient.setNetBindMonitoringEnabled(
                    AOSH.parseInt(args[1], "pkey"),
                    AOSH.parseBoolean(args[2], "enabled")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_NET_BIND_OPEN_FIREWALL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_NET_BIND_OPEN_FIREWALL, args, 2, err)) {
                connector.simpleAOClient.setNetBindOpenFirewall(
                    AOSH.parseInt(args[1], "pkey"),
                    AOSH.parseBoolean(args[2], "open_firewall")
                );
            }
            return true;
        }
        return false;
    }
}