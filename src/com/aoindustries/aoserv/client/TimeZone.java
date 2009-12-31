package com.aoindustries.aoserv.client;

import com.aoindustries.table.IndexType;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

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
        return key;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JavaBeans">
    public com.aoindustries.aoserv.client.beans.TimeZone getBean() {
        return new com.aoindustries.aoserv.client.beans.TimeZone(key);
    }
    // </editor-fold>

    private volatile transient java.util.TimeZone timeZone;

    /**
     * Gets the Java TimeZone for this TimeZone.
     * 
     * Not synchronized because double initialization is acceptable.
     */
    public java.util.TimeZone getTimeZone() {
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
            timeZone = java.util.TimeZone.getTimeZone(key);
        }
        return timeZone;
    }
}
