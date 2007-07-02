package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * @see  PrivateFTPServer
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class PrivateFTPServerTable extends CachedTableIntegerKey<PrivateFTPServer> {

    PrivateFTPServerTable(AOServConnector connector) {
	super(connector, PrivateFTPServer.class);
    }

    public PrivateFTPServer get(Object pkey) {
	return getUniqueRow(PrivateFTPServer.COLUMN_NET_BIND, pkey);
    }

    public PrivateFTPServer get(int pkey) {
	return getUniqueRow(PrivateFTPServer.COLUMN_NET_BIND, pkey);
    }

    List<PrivateFTPServer> getPrivateFTPServers(AOServer ao) {
        int aoPKey=ao.pkey;

	List<PrivateFTPServer> cached=getRows();
	int size=cached.size();
        List<PrivateFTPServer> matches=new ArrayList<PrivateFTPServer>(size);
	for(int c=0;c<size;c++) {
            PrivateFTPServer obj=cached.get(c);
            if(obj.getNetBind().ao_server==aoPKey) matches.add(obj);
	}
	return matches;
    }

    PrivateFTPServer getPrivateFTPServer(AOServer ao, String path) {
        int aoPKey=ao.pkey;

	List<PrivateFTPServer> cached=getRows();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            PrivateFTPServer obj=cached.get(c);
            if(
                obj.getRoot().equals(path)
                && obj.getNetBind().ao_server==aoPKey
            ) return obj;
	}
        return null;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.PRIVATE_FTP_SERVERS;
    }
}