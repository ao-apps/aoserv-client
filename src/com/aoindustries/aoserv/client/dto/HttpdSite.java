/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class HttpdSite {

    private int aoServerResource;
    private DomainName siteName;
    private boolean listFirst;
    private int linuxAccountGroup;
    private Email serverAdmin;
    private boolean isManualConfig;
    private String awstatsSkipFiles;

    public HttpdSite() {
    }

    public HttpdSite(
        int aoServerResource,
        DomainName siteName,
        boolean listFirst,
        int linuxAccountGroup,
        Email serverAdmin,
        boolean isManualConfig,
        String awstatsSkipFiles
    ) {
        this.aoServerResource = aoServerResource;
        this.siteName = siteName;
        this.listFirst = listFirst;
        this.linuxAccountGroup = linuxAccountGroup;
        this.serverAdmin = serverAdmin;
        this.isManualConfig = isManualConfig;
        this.awstatsSkipFiles = awstatsSkipFiles;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
    }

    public DomainName getSiteName() {
        return siteName;
    }

    public void setSiteName(DomainName siteName) {
        this.siteName = siteName;
    }

    public boolean isListFirst() {
        return listFirst;
    }

    public void setListFirst(boolean listFirst) {
        this.listFirst = listFirst;
    }

    public int getLinuxAccountGroup() {
        return linuxAccountGroup;
    }

    public void setLinuxAccountGroup(int linuxAccountGroup) {
        this.linuxAccountGroup = linuxAccountGroup;
    }

    public Email getServerAdmin() {
        return serverAdmin;
    }

    public void setServerAdmin(Email serverAdmin) {
        this.serverAdmin = serverAdmin;
    }

    public boolean isIsManualConfig() {
        return isManualConfig;
    }

    public void setIsManualConfig(boolean isManualConfig) {
        this.isManualConfig = isManualConfig;
    }

    public String getAwstatsSkipFiles() {
        return awstatsSkipFiles;
    }

    public void setAwstatsSkipFiles(String awstatsSkipFiles) {
        this.awstatsSkipFiles = awstatsSkipFiles;
    }
}
