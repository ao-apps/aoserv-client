/*
 * Copyright 2000-2011 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.FastExternalizable;
import com.aoindustries.io.FastObjectInput;
import com.aoindustries.io.FastObjectOutput;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * The entire contents of servers are periodically replicated to another server.  In the
 * event of hardware failure, this other server may be booted to take place of the
 * failed machine.  All transfers to the failover server are logged.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileLog extends AOServObjectIntegerKey implements Comparable<FailoverFileLog>, DtoFactory<com.aoindustries.aoserv.client.dto.FailoverFileLog>, FastExternalizable {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private int replication;
    private long startTime;
    private long endTime;
    private int scanned;
    private int updated;
    private long bytes;
    private boolean successful;

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
        this.successful = isSuccessful;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = -4289674682220613055L;

    public FailoverFileLog() {
        replication = Integer.MIN_VALUE;
    }

    @Override
    public long getSerialVersionUID() {
        return super.getSerialVersionUID() ^ serialVersionUID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FastObjectOutput fastOut = FastObjectOutput.wrap(out);
        try {
            super.writeExternal(fastOut);
            fastOut.writeInt(replication);
            fastOut.writeLong(startTime);
            fastOut.writeLong(endTime);
            fastOut.writeInt(scanned);
            fastOut.writeInt(updated);
            fastOut.writeLong(bytes);
            fastOut.writeBoolean(successful);
        } finally {
            fastOut.unwrap();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if(replication!=Integer.MIN_VALUE) throw new IllegalStateException();
        FastObjectInput fastIn = FastObjectInput.wrap(in);
        try {
            super.readExternal(fastIn);
            replication = fastIn.readInt();
            startTime = fastIn.readLong();
            endTime = fastIn.readLong();
            scanned = fastIn.readInt();
            updated = fastIn.readInt();
            bytes = fastIn.readLong();
            successful = fastIn.readBoolean();
        } finally {
            fastIn.unwrap();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(FailoverFileLog other) {
        try {
            int diff = compare(other.endTime, endTime); // Descending
            if(diff!=0) return diff;
            return replication==other.replication ? 0 : getReplication().compareTo(other.getReplication());
        } catch(RemoteException err) {
            throw new WrappedException(err);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, index=IndexType.PRIMARY_KEY, description="a generated, unique id")
    public int getPkey() {
        return getKeyInt();
    }

    public static final MethodColumn COLUMN_REPLICATION = getMethodColumn(FailoverFileLog.class, "replication");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the replication that was performed")
    public FailoverFileReplication getReplication() throws RemoteException {
        return getConnector().getFailoverFileReplications().get(replication);
    }

    @SchemaColumn(order=2, description="the time the replication started")
    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    @SchemaColumn(order=3, description="the time the replication finished")
    public Timestamp getEndTime() {
        return new Timestamp(endTime);
    }

    @SchemaColumn(order=4, description="the number of files scanned")
    public int getScanned() {
    	return scanned;
    }

    @SchemaColumn(order=5, description="the number of files updated")
    public int getUpdated() {
        return updated;
    }

    @SchemaColumn(order=6, description="the number of bytes transferred")
    public long getBytes() {
        return bytes;
    }

    @SchemaColumn(order=7, description="keeps track of which passes completed successfully")
    public boolean isSuccessful() {
        return successful;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public FailoverFileLog(AOServConnector connector, com.aoindustries.aoserv.client.dto.FailoverFileLog dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getReplication(),
            getTimeMillis(dto.getStartTime()),
            getTimeMillis(dto.getEndTime()),
            dto.getScanned(),
            dto.getUpdated(),
            dto.getBytes(),
            dto.isSuccessful()
        );
    }
    @Override
    public com.aoindustries.aoserv.client.dto.FailoverFileLog getDto() {
        return new com.aoindustries.aoserv.client.dto.FailoverFileLog(
            getKeyInt(),
            replication,
            startTime,
            endTime,
            scanned,
            updated,
            bytes,
            successful
        );
    }
    // </editor-fold>
}
