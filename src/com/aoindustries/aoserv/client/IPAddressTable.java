package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @see  IPAddress
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class IPAddressTable extends CachedTableIntegerKey<IPAddress> {

    IPAddressTable(AOServConnector connector) {
	super(connector, IPAddress.class);
    }

    private static final OrderBy[] defaultOrderBy = {
        new OrderBy(IPAddress.COLUMN_IP_ADDRESS_name, ASCENDING),
        new OrderBy(IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_PACKAGE_name+'.'+Package.COLUMN_NAME_name, ASCENDING),
        new OrderBy(IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_SERVER_name+'.'+Server.COLUMN_NAME_name, ASCENDING),
        new OrderBy(IPAddress.COLUMN_NET_DEVICE_name+'.'+NetDevice.COLUMN_DEVICE_ID_name, ASCENDING)
    };
    @Override
    OrderBy[] getDefaultOrderBy() {
        return defaultOrderBy;
    }

    public IPAddress get(Object pkey) {
	return getUniqueRow(IPAddress.COLUMN_PKEY, pkey);
    }

    public IPAddress get(int pkey) {
	return getUniqueRow(IPAddress.COLUMN_PKEY, pkey);
    }

    IPAddress getIPAddress(NetDevice device, String ipAddress) {
	int pkey=device.getPkey();

	List<IPAddress> cached = getRows();
	int len = cached.size();
	for (int c = 0; c < len; c++) {
            IPAddress address=cached.get(c);
            if(
                address.net_device==pkey
                && address.ip_address.equals(ipAddress)
            ) return address;
	}
	return null;
    }

    List<IPAddress> getIPAddresses(NetDevice device) {
        return getIndexedRows(IPAddress.COLUMN_NET_DEVICE, device.pkey);
    }

    public List<IPAddress> getIPAddresses(String ipAddress) {
	List<IPAddress> cached = getRows();
	int len = cached.size();
        List<IPAddress> matches=new ArrayList<IPAddress>(len);
	for (int c = 0; c < len; c++) {
            IPAddress address=cached.get(c);
            if(address.ip_address.equals(ipAddress)) matches.add(address);
	}
	return matches;
    }

    List<IPAddress> getIPAddresses(Package pack) {
        return getIndexedRows(IPAddress.COLUMN_PACKAGE, pack.name);
    }

    List<IPAddress> getIPAddresses(Server se) {
        int sePKey=se.pkey;

	List<IPAddress> cached = getRows();
	int len = cached.size();
        List<IPAddress> matches=new ArrayList<IPAddress>(len);
	for (int c = 0; c < len; c++) {
            IPAddress address=cached.get(c);
            if(address.net_device==-1 || address.getNetDevice().server==sePKey) matches.add(address);
	}
	return matches;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_ADDRESSES;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.CHECK_IP_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.CHECK_IP_ADDRESS, args, 1, err)) {
                try {
                    SimpleAOClient.checkIPAddress(args[1]);
                    out.println("true");
                } catch(IllegalArgumentException iae) {
                    out.print("aosh: "+AOSHCommand.CHECK_IP_ADDRESS+": ");
                    out.println(iae.getMessage());
                }
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.IS_IP_ADDRESS_USED)) {
            if(AOSH.checkParamCount(AOSHCommand.IS_IP_ADDRESS_USED, args, 3, err)) {
                out.println(
                    connector.simpleAOClient.isIPAddressUsed(
                        args[1],
                        args[2],
                        args[3]
                    )
                );
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.MOVE_IP_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.MOVE_IP_ADDRESS, args, 4, err)) {
                connector.simpleAOClient.moveIPAddress(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_IP_ADDRESS_DHCP_ADDRESS)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_IP_ADDRESS_DHCP_ADDRESS, args, 2, err)) {
                connector.simpleAOClient.setIPAddressDHCPAddress(
                    AOSH.parseInt(args[1], "ip_address"),
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_IP_ADDRESS_HOSTNAME)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_IP_ADDRESS_HOSTNAME, args, 4, err)) {
                connector.simpleAOClient.setIPAddressHostname(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_IP_ADDRESS_PACKAGE)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_IP_ADDRESS_PACKAGE, args, 4, err)) {
                connector.simpleAOClient.setIPAddressPackage(
                    args[1],
                    args[2],
                    args[3],
                    args[4]
                );
            }
            return true;
	}
	return false;
    }
}