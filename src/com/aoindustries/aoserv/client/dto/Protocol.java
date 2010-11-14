/*
 * Copyright 2009-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class Protocol extends AOServObject {

    private String protocol;
    private NetPort port;
    private String name;
    private boolean isUserService;
    private String netProtocol;

    public Protocol() {
    }

    public Protocol(String protocol, NetPort port, String name, boolean isUserService, String netProtocol) {
        this.protocol = protocol;
        this.port = port;
        this.name = name;
        this.isUserService = isUserService;
        this.netProtocol = netProtocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public NetPort getPort() {
        return port;
    }

    public void setPort(NetPort port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsUserService() {
        return isUserService;
    }

    public void setIsUserService(boolean isUserService) {
        this.isUserService = isUserService;
    }

    public String getNetProtocol() {
        return netProtocol;
    }

    public void setNetProtocol(String netProtocol) {
        this.netProtocol = netProtocol;
    }
}
