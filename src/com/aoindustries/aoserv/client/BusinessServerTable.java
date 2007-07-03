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
 * @see  BusinessServerTable
 *
 * @author  AO Industries, Inc.
 */
final public class BusinessServerTable extends CachedTableIntegerKey<BusinessServer> {

    BusinessServerTable(AOServConnector connector) {
	super(connector, BusinessServer.class);
    }

    int addBusinessServer(Business business, Server server, boolean can_configure_backup) {
	return connector.requestIntQueryIL(AOServProtocol.CommandID.ADD, SchemaTable.TableID.BUSINESS_SERVERS, business.pkey, server.pkey, can_configure_backup);
    }

    public BusinessServer get(Object pkey) {
        return get(((Integer)pkey).intValue());
    }

    public BusinessServer get(int pkey) {
	return getUniqueRow(BusinessServer.COLUMN_PKEY, pkey);
    }

    List<BusinessServer> getBusinessServers(Business bu) {
        return getIndexedRows(BusinessServer.COLUMN_ACCOUNTING, bu.pkey);
    }

    List<BusinessServer> getBusinessServers(Server server) {
        return getIndexedRows(BusinessServer.COLUMN_SERVER, server.pkey);
    }

    List<Business> getBusinesses(Server server) {
        // Use the cache and convert
	List<BusinessServer> cached=getBusinessServers(server);
	int size=cached.size();
	List<Business> businesses=new ArrayList<Business>(size);
        for(int c=0;c<size;c++) businesses.add(cached.get(c).getBusiness());
	return businesses;
    }

    BusinessServer getBusinessServer(Business bu, Server se) {
        int pkey=se.pkey;
        
        // Use the index first
	List<BusinessServer> cached=getBusinessServers(bu);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            BusinessServer bs=cached.get(c);
            if(bs.server==pkey) return bs;
	}
	return null;
    }

    Server getDefaultServer(Business business) {
        // Use index first
	List<BusinessServer> cached=getBusinessServers(business);
	int size=cached.size();
	for(int c=0;c<size;c++) {
            BusinessServer bs=cached.get(c);
            if(bs.is_default) return bs.getServer();
	}
	return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.BUSINESS_SERVERS;
    }

    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_SERVER, args, 3, err)) {
                int pkey=connector.simpleAOClient.addBusinessServer(
                    args[1],
                    args[2],
                    AOSH.parseBoolean(args[3], "can_configure_backup")
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_BUSINESS_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_BUSINESS_SERVER, args, 2, err)) {
                connector.simpleAOClient.removeBusinessServer(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_DEFAULT_BUSINESS_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_DEFAULT_BUSINESS_SERVER, args, 2, err)) {
                connector.simpleAOClient.setDefaultBusinessServer(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else return false;
    }
}