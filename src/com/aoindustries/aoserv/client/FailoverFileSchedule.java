/*
 * Copyright 2003-2011 by AO Industries, Inc.,
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
final public class FailoverFileSchedule extends AOServObjectIntegerKey implements Comparable<FailoverFileSchedule>, DtoFactory<com.aoindustries.aoserv.client.dto.FailoverFileSchedule> {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private static final long serialVersionUID = 3128236449184268134L;

    final private int replication;
    final private short hour;
    final private short minute;
    final private boolean enabled;

    public FailoverFileSchedule(
        AOServConnector connector,
        int pkey,
        int replication,
        short hour,
        short minute,
        boolean enabled
    ) {
        super(connector, pkey);
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
            int diff = replication==other.replication ? 0 : getReplication().compareTo(other.getReplication());
            if(diff!=0) return diff;
            diff = compare(hour, other.hour);
            if(diff!=0) return diff;
            return compare(minute, other.minute);
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

    public static final MethodColumn COLUMN_REPLICATION = getMethodColumn(FailoverFileSchedule.class, "replication");
    @DependencySingleton
    @SchemaColumn(order=1, index=IndexType.INDEXED, description="the replication that will be started")
    public FailoverFileReplication getReplication() throws RemoteException {
        return getConnector().getFailoverFileReplications().get(replication);
    }

    @SchemaColumn(order=2, description="the hour of day (in server timezone)")
    public short getHour() {
        return hour;
    }

    @SchemaColumn(order=3, description="the minute (in server timezone)")
    public short getMinute() {
        return minute;
    }

    @SchemaColumn(order=4, description="indicates this schedule is enabled")
    public boolean isEnabled() {
        return enabled;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="DTO">
    public FailoverFileSchedule(AOServConnector connector, com.aoindustries.aoserv.client.dto.FailoverFileSchedule dto) {
        this(
            connector,
            dto.getPkey(),
            dto.getReplication(),
            dto.getHour(),
            dto.getMinute(),
            dto.isEnabled()
        );
    }

    @Override
    public com.aoindustries.aoserv.client.dto.FailoverFileSchedule getDto() {
        return new com.aoindustries.aoserv.client.dto.FailoverFileSchedule(getKeyInt(), replication, hour, minute, enabled);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="i18n">
    @Override
    String toStringImpl() throws RemoteException {
        StringBuilder SB = new StringBuilder();
        SB.append(getReplication().toStringImpl());
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
