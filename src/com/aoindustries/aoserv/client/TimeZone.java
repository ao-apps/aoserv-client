package com.aoindustries.aoserv.client;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.profiler.Profiler;
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

    public Object getColumn(int i) {
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

    void initImpl(ResultSet result) throws SQLException {
        pkey = result.getString(1);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        out.writeUTF(pkey);
    }
}