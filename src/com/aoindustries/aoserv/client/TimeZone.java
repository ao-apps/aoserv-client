/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * All of the time zones on a server.
 *
 * @author  AO Industries, Inc.
 */
final public class TimeZone extends AOServObjectStringKey implements Comparable<TimeZone>, DtoFactory<com.aoindustries.aoserv.client.dto.TimeZone> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public TimeZone(AOServConnector connector, String name) {
        super(connector, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ordering">
    @Override
    public int compareTo(TimeZone other) {
        return AOServObjectUtils.compareIgnoreCaseConsistentWithEquals(getKey(), other.getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of this time zone")
    public String getName() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="DTO">
    public TimeZone(AOServConnector connector, com.aoindustries.aoserv.client.dto.TimeZone dto) {
        this(connector, dto.getName());
    }

    @Override
    public com.aoindustries.aoserv.client.dto.TimeZone getDto() {
        return new com.aoindustries.aoserv.client.dto.TimeZone(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    @DependentObjectSet
    public IndexedSet<AOServer> getAoServers() throws RemoteException {
        return getConnector().getAoServers().filterIndexed(AOServer.COLUMN_TIME_ZONE, this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="java.util.TimeZone compatibility">
    private static final ConcurrentMap<String,java.util.TimeZone> timeZones = new ConcurrentHashMap<String,java.util.TimeZone>();

    /**
     * Gets the Java TimeZone for this TimeZone.
     */
    public java.util.TimeZone getTimeZone() {
        String key = getKey();
        java.util.TimeZone timeZone = timeZones.get(key);
        if(timeZone==null) {
            String[] ids = java.util.TimeZone.getAvailableIDs();
            boolean found = false;
            for(String id : ids) {
                if(id.equals(key)) {
                    found = true;
                    break;
                }
            }
            if(!found) throw new IllegalArgumentException("TimeZone not found: "+key);
            java.util.TimeZone newTimeZone = java.util.TimeZone.getTimeZone(key);
            java.util.TimeZone existing = timeZones.putIfAbsent(key, newTimeZone);
            timeZone = existing==null ? newTimeZone : existing;
        }
        return timeZone;
    }
    // </editor-fold>
}
