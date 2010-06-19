/*
 * Copyright 2006-2010 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;
import com.aoindustries.util.UnionSet;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * All of the time zones on a server.
 *
 * @author  AO Industries, Inc.
 */
final public class TimeZone extends AOServObjectStringKey<TimeZone> implements BeanFactory<com.aoindustries.aoserv.client.beans.TimeZone> {

    // <editor-fold defaultstate="collapsed" desc="Constants">
    private static final long serialVersionUID = 1L;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Fields">
    public TimeZone(TimeZoneService<?,?> table, String name) {
        super(table, name);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Columns">
    @SchemaColumn(order=0, name="name", index=IndexType.PRIMARY_KEY, description="the unique name of this time zone")
    public String getName() {
        return getKey();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    @Override
    public com.aoindustries.aoserv.client.beans.TimeZone getBean() {
        return new com.aoindustries.aoserv.client.beans.TimeZone(getKey());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Dependencies">
    @Override
    protected UnionSet<AOServObject> addDependentObjects(UnionSet<AOServObject> unionSet) throws RemoteException {
        unionSet = AOServObjectUtils.addDependencySet(unionSet, getAoServers());
        return unionSet;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Relations">
    public IndexedSet<AOServer> getAoServers() throws RemoteException {
        return getService().getConnector().getAoServers().filterIndexed(AOServer.COLUMN_TIME_ZONE, this);
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
