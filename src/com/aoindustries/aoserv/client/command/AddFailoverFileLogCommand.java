/*
 * Copyright 2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client.command;

import com.aoindustries.aoserv.client.BusinessAdministrator;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
final public class AddFailoverFileLogCommand extends RemoteCommand<Integer> {

    private static final long serialVersionUID = 1L;

    final private int replication;
    final private Timestamp startTime;
    final private Timestamp endTime;
    final private int scanned;
    final private int updated;
    final private long bytes;
    final private boolean isSuccessful;

    public AddFailoverFileLogCommand(
        @Param(name="replication") int replication,
        @Param(name="startTime") Timestamp startTime,
        @Param(name="endTime") Timestamp endTime,
        @Param(name="scanned") int scanned,
        @Param(name="updated") int updated,
        @Param(name="bytes") long bytes,
        @Param(name="isSuccessful") boolean isSuccessful
    ) {
        this.replication = replication;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scanned = scanned;
        this.updated = updated;
        this.bytes = bytes;
        this.isSuccessful = isSuccessful;
    }

    public int getReplication() {
        return replication;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public int getScanned() {
        return scanned;
    }

    public int getUpdated() {
        return updated;
    }

    public long getBytes() {
        return bytes;
    }

    public boolean isIsSuccessful() {
        return isSuccessful;
    }

    @Override
    public Map<String, List<String>> validate(BusinessAdministrator connectedUser) throws RemoteException {
        // TODO
        return Collections.emptyMap();
    }
}
