package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.io.TerminalWriter;
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  NetBind
 *
 * @author  AO Industries, Inc.
 */
final public class NetBindTable extends CachedTableIntegerKey<NetBind> {

    NetBindTable(AOServConnector connector) {
        super(connector, NetBind.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(NetBind.COLUMN_BUSINESS_SERVER_name+'.'+BusinessServer.COLUMN_SERVER_name+'.'+Server.COLUMN_ACCOUNTING_name, ASCENDING),
        new OrderBy(NetBind.COLUMN_BUSINESS_SERVER_name+'.'+BusinessServer.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
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
        final Server se,
        final Business bu,
        final IPAddress ia,
        final NetPort netPort,
        final NetProtocol netProtocol,
        final Protocol appProtocol,
        final boolean openFirewall,
        final boolean monitoringEnabled
    ) throws IOException, SQLException {
        return connector.requestResult(
            true,
            new AOServConnector.ResultRequest<Integer>() {
                int pkey;
                IntList invalidateList;

                public void writeRequest(CompressedDataOutputStream out) throws IOException {
                    out.writeCompressedInt(AOServProtocol.CommandID.ADD.ordinal());
                    out.writeCompressedInt(SchemaTable.TableID.NET_BINDS.ordinal());
                    out.writeCompressedInt(se.pkey);
                    out.writeUTF(bu.pkey);
                    out.writeCompressedInt(ia.pkey);
                    out.writeCompressedInt(netPort.port);
                    out.writeUTF(netProtocol.pkey);
                    out.writeUTF(appProtocol.pkey);
                    out.writeBoolean(openFirewall);
                    out.writeBoolean(monitoringEnabled);
                }

                public void readResponse(CompressedDataInputStream in) throws IOException, SQLException {
                    int code=in.readByte();
                    if(code==AOServProtocol.DONE) {
                        pkey=in.readCompressedInt();
                        invalidateList=AOServConnector.readInvalidateList(in);
                    } else {
                        AOServProtocol.checkResult(code, in);
                        throw new IOException("Unexpected response code: "+code);
                    }
                }

                public Integer afterRelease() {
                    connector.tablesUpdated(invalidateList);
                    return pkey;
                }
            }
        );
    }

    public NetBind get(int pkey) throws IOException, SQLException {
        return getUniqueRow(NetBind.COLUMN_PKEY, pkey);
    }

    List<NetBind> getNetBinds(IPAddress ia) throws IOException, SQLException {
        return getIndexedRows(NetBind.COLUMN_IP_ADDRESS, ia.pkey);
    }

    /*
    List<NetBind> getNetBinds(Business bu) throws IOException, SQLException {
        return getIndexedRows(NetBind.COLUMN_ACCOUNTING, bu.pkey);
    }

    List<NetBind> getNetBinds(Business bu, IPAddress ip) throws IOException, SQLException {
        String accounting=bu.pkey;
        // Use the index first
        List<NetBind> cached=getNetBinds(ip);
        int size=cached.size();
        List<NetBind> matches=new ArrayList<NetBind>(size);
    	for(int c=0;c<size;c++) {
            NetBind nb=cached.get(c);
            if(nb.accounting.equals(accounting)) matches.add(nb);
        }
        return matches;
    }

    List<NetBind> getNetBinds(Server se, IPAddress ip) throws IOException, SQLException {
    	int ipAddress=ip.pkey;

        // Use the index first
        List<NetBind> cached=getNetBinds(se);
        int size=cached.size();
        List<NetBind> matches=new ArrayList<NetBind>(size);
        for(NetBind nb : cached) {
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
     */

    public SchemaTable.TableID getTableID() {
    	return SchemaTable.TableID.NET_BINDS;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
        String command=args[0];
        if(command.equalsIgnoreCase(AOSHCommand.ADD_NET_BIND)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_NET_BIND, args, 9, err)) {
                connector.getSimpleAOClient().addNetBind(
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
                connector.getSimpleAOClient().removeNetBind(
                    AOSH.parseInt(args[1], "pkey")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_NET_BIND_MONITORING_ENABLED)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_NET_BIND_MONITORING_ENABLED, args, 2, err)) {
                connector.getSimpleAOClient().setNetBindMonitoringEnabled(
                    AOSH.parseInt(args[1], "pkey"),
                    AOSH.parseBoolean(args[2], "enabled")
                );
            }
            return true;
        } else if(command.equalsIgnoreCase(AOSHCommand.SET_NET_BIND_OPEN_FIREWALL)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_NET_BIND_OPEN_FIREWALL, args, 2, err)) {
                connector.getSimpleAOClient().setNetBindOpenFirewall(
                    AOSH.parseInt(args[1], "pkey"),
                    AOSH.parseBoolean(args[2], "open_firewall")
                );
            }
            return true;
        }
        return false;
    }
}