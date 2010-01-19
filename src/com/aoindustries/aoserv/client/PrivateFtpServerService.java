/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  PrivateFtpServer
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.private_ftp_servers)
public interface PrivateFtpServerService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServService<C,F,Integer,PrivateFtpServer> {

    /* TODO
    List<PrivateFtpServer> getPrivateFtpServers(AOServer ao) throws IOException, SQLException {
        int aoPKey=ao.pkey;

        List<PrivateFtpServer> cached=getRows();
        int size=cached.size();
        List<PrivateFtpServer> matches=new ArrayList<PrivateFtpServer>(size);
    	for(int c=0;c<size;c++) {
            PrivateFtpServer obj=cached.get(c);
            if(obj.getNetBind().getBusinessServer().server==aoPKey) matches.add(obj);
    	}
        return matches;
    }
     */

    /* TODO
    PrivateFtpServer getPrivateFtpServer(AOServer ao, String path) {
        int aoPKey=ao.pkey;

	List<PrivateFtpServer> cached=getRows();
	int size=cached.size();
	for(int c=0;c<size;c++) {
            PrivateFtpServer obj=cached.get(c);
            if(
                obj.getRoot().equals(path)
                && obj.getNetBind().server==aoPKey
            ) return obj;
	}
        return null;
    }*/
}