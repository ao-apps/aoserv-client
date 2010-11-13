/*
 * Copyright 2003-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;

/**
 * A <code>FailoverFileSchedule</code> controls which time of day (in server
 * time zone) the failover file replications will occur.
 *
 * @author  AO Industries, Inc.
 */
final public class FailoverFileSchedule extends AOServObjectIntegerKey<FailoverFileSchedule> implements Comparable<FailoverFileSchedule>, DtoFactory<com.aoindustries.aoserv.client.dto.FailoverFileSchedule> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    final private int replication;
    final private short hour;
    final private short minute;
    final private boolean enabled;

    public FailoverFileSchedule(
        FailoverFileScheduleService<?,?> service,
        int pkey,
        int replication,
        short hour,
        short minute,
        boolean enabled
    ) {
        super(service, pkey);
        this.replication = replication;
        this.hour = hour;
        this.minute = minute;
        this.enabled = enabled;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(FailoverFileSchedule other) {
        try {
            int diff = replication==other.replication ? 0 : getFailoverFileReplication().compareTo(other.getFailoverFileReplication());
            if(diff!=0) return diff;
            diff = AOServObjectUtils.compare(hour, other.hour);
            if(diff!=0) return diff;
            return AOServObjectUtils.compare(minute, other.minute);
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
    @SchemaColumn(order=1, name=COLUMN_REPLICATION, index=IndexType.INDEXED, description="the replication that will be started")
    public FailoverFileReplication getFailoverFileReplication() throws RemoteException {
        return getService().getConnector().getFailoverFileReplications().get(replication);
    }

    @SchemaColumn(order=2, name="hour", description="the hour of day (in server timezone)")
    public short getHour() {
        return hour;
    }

    @SchemaColumn(order=3, name="minute", description="the minute (in server timezone)")
    public short getMinute() {
        return minute;
    }

    @SchemaColumn(order=4, name="enabled", description="indicates this schedule is enabled")
    public boolean isEnabled() {
        return enabled;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.FailoverFileSchedule getDto() {
        return new com.aoindustries.aoserv.client.dto.FailoverFileSchedule(key, replication, hour, minute, enabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        StringBuilder SB = new StringBuilder();
        SB.append(getFailoverFileReplication().toStringImpl());
        SB.append('@');
        if(hour<10) SB.append('0');
        SB.append(hour);
        SB.append(':');
        if(minute<10) SB.append('0');
        SB.append(minute);
        return SB.toString();
    }
    // </editor-fold>
}
