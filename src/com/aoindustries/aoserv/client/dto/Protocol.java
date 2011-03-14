/*
 * Copyright 2009-2011 by AO Industries, Inc.,
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
    private boolean userService;
    private String netProtocol;

    public Protocol() {
    }

    public Protocol(String protocol, NetPort port, String name, boolean userService, String netProtocol) {
        this.protocol = protocol;
        this.port = port;
        this.name = name;
        this.userService = userService;
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

    public boolean isUserService() {
        return userService;
    }

    public void setUserService(boolean userService) {
        this.userService = userService;
    }

    public String getNetProtocol() {
        return netProtocol;
    }

    public void setNetProtocol(String netProtocol) {
        this.netProtocol = netProtocol;
    }
}
