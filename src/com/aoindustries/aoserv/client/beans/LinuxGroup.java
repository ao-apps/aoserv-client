/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.beans;

/**
 * @author  AO Industries, Inc.
 */
public class LinuxGroup {

    private int aoServerResource;
    private String linuxGroupType;
    private GroupId groupName;
    private LinuxID gid;

    public LinuxGroup() {
    }

    public LinuxGroup(
        int aoServerResource,
        String linuxGroupType,
        GroupId groupName,
        LinuxID gid
    ) {
        this.aoServerResource = aoServerResource;
        this.linuxGroupType = linuxGroupType;
        this.groupName = groupName;
        this.gid = gid;
    }

    public int getAoServerResource() {
        return aoServerResource;
    }

    public void setAoServerResource(int aoServerResource) {
        this.aoServerResource = aoServerResource;
    }

    public String getLinuxGroupType() {
        return linuxGroupType;
    }

    public void setLinuxGroupType(String linuxGroupType) {
        this.linuxGroupType = linuxGroupType;
    }

    public GroupId getGroupName() {
        return groupName;
    }

    public void setGroupName(GroupId groupName) {
        this.groupName = groupName;
    }

    public LinuxID getGid() {
        return gid;
    }

    public void setGid(LinuxID gid) {
        this.gid = gid;
    }
}
