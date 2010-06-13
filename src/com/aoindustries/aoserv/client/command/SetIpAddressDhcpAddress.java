package com.aoindustries.aoserv.client.command;

/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.aoserv.client.BusinessAdministrator;
import com.aoindustries.aoserv.client.validator.InetAddress;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class SetIpAddressDhcpAddress extends RemoteCommand<Void> {

    private static final long serialVersionUID = 1L;

    final private int ipAddress;
    final private InetAddress newAddress;

    public SetIpAddressDhcpAddress(
        @Param(name="ipAddress") int ipAddress,
        @Param(name="newAddress") InetAddress newAddress
    ) {
        this.ipAddress = ipAddress;
        this.newAddress = newAddress;
    }

    public int getIpAddress() {
        return ipAddress;
    }

    public InetAddress getNewAddress() {
        return newAddress;
    }

    @Override
    public boolean isReadOnlyCommand() {
        return false;
    }

    public Map<String, List<String>> validate(Locale locale, BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
