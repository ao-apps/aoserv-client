/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class NetTcpRedirect {

    private int netBind;
    private int cps;
    private int cpsOverloadSleepTime;
    private Hostname destinationHost;
    private NetPort destinationPort;

    public NetTcpRedirect() {
    }

    public NetTcpRedirect(
        int netBind,
        int cps,
        int cpsOverloadSleepTime,
        Hostname destinationHost,
        NetPort destinationPort
    ) {
        this.netBind = netBind;
        this.cps = cps;
        this.cpsOverloadSleepTime = cpsOverloadSleepTime;
        this.destinationHost = destinationHost;
        this.destinationPort = destinationPort;
    }

    public int getNetBind() {
        return netBind;
    }

    public void setNetBind(int netBind) {
        this.netBind = netBind;
    }

    public int getCps() {
        return cps;
    }

    public void setCps(int cps) {
        this.cps = cps;
    }

    public int getCpsOverloadSleepTime() {
        return cpsOverloadSleepTime;
    }

    public void setCpsOverloadSleepTime(int cpsOverloadSleepTime) {
        this.cpsOverloadSleepTime = cpsOverloadSleepTime;
    }

    public Hostname getDestinationHost() {
        return destinationHost;
    }

    public void setDestinationHost(Hostname destinationHost) {
        this.destinationHost = destinationHost;
    }

    public NetPort getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(NetPort destinationPort) {
        this.destinationPort = destinationPort;
    }
}
