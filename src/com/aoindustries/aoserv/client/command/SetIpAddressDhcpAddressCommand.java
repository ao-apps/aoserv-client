/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.*;
import com.aoindustries.aoserv.client.validator.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetIpAddressDhcpAddressCommand extends RemoteCommand<Void> {

    private static final long serialVersionUID = -6587189699084066656L;

    final private int ipAddress;
    final private InetAddress newAddress;

    public SetIpAddressDhcpAddressCommand(
        @Param(name="ipAddress") IPAddress ipAddress,
        @Param(name="newAddress") InetAddress newAddress
    ) {
        this.ipAddress = ipAddress.getPkey();
        this.newAddress = newAddress;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    public int getIpAddress() {
        return ipAddress;
    }

    public InetAddress getNewAddress() {
        return newAddress;
    }

    @Override
    protected Map<String,List<String>> checkCommand(AOServConnector userConn, AOServConnector rootConn, BusinessAdministrator rootUser) throws RemoteException {
        Map<String,List<String>> errors = Collections.emptyMap();
        // TODO
        return errors;
    }
}
