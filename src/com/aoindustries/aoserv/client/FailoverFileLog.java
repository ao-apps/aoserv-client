package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * The entire contents of servers are periodically replicated to another server.  In the
 * event of hardware failure, this other server may be booted to take place of the
 * failed machine.  All transfers to the failover server are logged.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileLog extends AOServObjectIntegerKey implements Comparable<FailoverFileLog>, DtoFactory<com.aoindustries.aoserv.client.dto.FailoverFileLog> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 2L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int replication;
    final private long startTime;
    final private long endTime;
    final private int scanned;
    final private int updated;
    final private long bytes;
    final private boolean isSuccessful;

    public FailoverFileLog(
        AOServConnector connector,
        int pkey,
        int replication,
        long startTime,
        long endTime,
        int scanned,
        int updated,
        long bytes,
        boolean isSuccessful
    ) {
        super(connector, pkey);
        this.replication = replication;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scanned = scanned;
        this.updated = updated;
        this.bytes = bytes;
        this.isSuccessful = isSuccessful;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(FailoverFileLog other) {
        try {
            int diff = AOServObjectUtils.compare(other.endTime, endTime); // Descending
            if(diff!=0) return diff;
            return replication==other.replication ? 0 : getFailoverFileReplication().compareTo(other.getFailoverFileReplication());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="pkey", index=IndexType.PRIMARY_KEY, description="a generated, unique id")
    public int getPkey() {
        return key;
    }

    static final String COLUMN_REPLICATION = "replication";
    @SchemaColumn(order=1, name=COLUMN_REPLICATION, index=IndexType.INDEXED, description="the replication that was performed")
    public FailoverFileReplication getFailoverFileReplication() throws RemoteException {
        return getConnector().getFailoverFileReplications().get(replication);
    }

    @SchemaColumn(order=2, name="start_time", description="the time the replication started")
    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    @SchemaColumn(order=3, name="end_time", description="the time the replication finished")
    public Timestamp getEndTime() {
        return new Timestamp(endTime);
    }

    @SchemaColumn(order=4, name="scanned", description="the number of files scanned")
    public int getScanned() {
    	return scanned;
    }

    @SchemaColumn(order=5, name="updated", description="the number of files updated")
    public int getUpdated() {
        return updated;
    }

    @SchemaColumn(order=6, name="bytes", description="the number of bytes transferred")
    public long getBytes() {
        return bytes;
    }

    @SchemaColumn(order=7, name="is_successful", description="keeps track of which passes completed successfully")
    public boolean isSuccessful() {
        return isSuccessful;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.FailoverFileLog getDto() {
        return new com.aoindustries.aoserv.client.dto.FailoverFileLog(
            key,
            replication,
            startTime,
            endTime,
            scanned,
            updated,
            bytes,
            isSuccessful
        );
    }
    // </editor-fold>
}
