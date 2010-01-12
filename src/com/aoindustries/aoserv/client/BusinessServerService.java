/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  BusinessServerTable
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.business_servers)
public interface BusinessServerService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceIntegerKey<C,F,BusinessServer> {

    /* TODO
    int addBusinessServer(Business business, Server server) throws IOException, SQLException {
    	return connector.requestIntQueryIL(true, AOServProtocol.CommandID.ADD, SchemaTable.TableID.BUSINESS_SERVERS, business.pkey, server.pkey);
    }

    List<BusinessServer> getBusinessServers(Server server) throws IOException, SQLException {
        return getIndexedRows(BusinessServer.COLUMN_SERVER, server.pkey);
    }

    List<Business> getBusinesses(Server server) throws IOException, SQLException {
        // Use the cache and convert
	List<BusinessServer> cached=getBusinessServers(server);
	int size=cached.size();
	List<Business> businesses=new ArrayList<Business>(size);
        for(int c=0;c<size;c++) businesses.add(cached.get(c).getBusiness());
	return businesses;
    }

    BusinessServer getBusinessServer(String accounting, int server) throws IOException, SQLException {
        // Use the index first
        List<BusinessServer> cached=getIndexedRows(BusinessServer.COLUMN_ACCOUNTING, accounting);
        int size=cached.size();
        for(int c=0;c<size;c++) {
            BusinessServer bs=cached.get(c);
            if(bs.server==server) return bs;
        }
        return null;
    }

    Server getDefaultServer(Business business) throws IOException, SQLException {
        // Use index first
        List<BusinessServer> cached=getIndexedRows(BusinessServer.COLUMN_ACCOUNTING, business.pkey);
        int size=cached.size();
        for(int c=0;c<size;c++) {
            BusinessServer bs=cached.get(c);
            if(bs.is_default) return bs.getServer();
        }
        return null;
    }

    @Override
    boolean handleCommand(String[] args, InputStream in, TerminalWriter out, TerminalWriter err, boolean isInteractive) throws IllegalArgumentException, SQLException, IOException {
	String command=args[0];
	if(command.equalsIgnoreCase(AOSHCommand.ADD_BUSINESS_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.ADD_BUSINESS_SERVER, args, 2, err)) {
                int pkey=connector.getSimpleAOClient().addBusinessServer(
                    args[1],
                    args[2]
                );
                out.println(pkey);
                out.flush();
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.REMOVE_BUSINESS_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.REMOVE_BUSINESS_SERVER, args, 2, err)) {
                connector.getSimpleAOClient().removeBusinessServer(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else if(command.equalsIgnoreCase(AOSHCommand.SET_DEFAULT_BUSINESS_SERVER)) {
            if(AOSH.checkParamCount(AOSHCommand.SET_DEFAULT_BUSINESS_SERVER, args, 2, err)) {
                connector.getSimpleAOClient().setDefaultBusinessServer(
                    args[1],
                    args[2]
                );
            }
            return true;
	} else return false;
    }
     */
}