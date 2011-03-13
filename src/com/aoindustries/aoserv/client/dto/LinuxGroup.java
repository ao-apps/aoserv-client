/*
 * Copyright 2010-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.dto;

/**
 * @author  AO Industries, Inc.
 */
public class LinuxGroup extends AOServerResource {

    private String linuxGroupType;
    private GroupId groupName;
    private LinuxID gid;

    public LinuxGroup() {
    }

    public LinuxGroup(
        int pkey,
        String resourceType,
        AccountingCode accounting,
        long created,
        UserId createdBy,
        Integer disableLog,
        long lastEnabled,
        int aoServer,
        int businessServer,
        String linuxGroupType,
        GroupId groupName,
        LinuxID gid
    ) {
        super(pkey, resourceType, accounting, created, createdBy, disableLog, lastEnabled, aoServer, businessServer);
        this.linuxGroupType = linuxGroupType;
        this.groupName = groupName;
        this.gid = gid;
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
