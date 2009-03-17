package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All of the time zones on a server.
 *
 * @since  1.2
 *
 * @author  AO Industries, Inc.
 */
final public class TimeZone extends GlobalObjectStringKey<TimeZone> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * Gets the unique name for this time zone.
     */
    public String getName() {
        return pkey;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TIME_ZONES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
    }
 
    private java.util.TimeZone timeZone;

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
                if(id.equals(pkey)) {
                    found = true;
                    break;
                }
            }
            if(!found) throw new IllegalArgumentException("TimeZone not found: "+pkey);
            timeZone = java.util.TimeZone.getTimeZone(pkey);
        }
        return timeZone;
    }
}