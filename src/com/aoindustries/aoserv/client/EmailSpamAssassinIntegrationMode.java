package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>EmailSpamAssassinIntegrationMode</code> is a simple wrapper for the types
 * of SpamAssassin integration modes.
 *
 * @see  Server
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailSpamAssassinIntegrationMode extends GlobalObjectStringKey<EmailSpamAssassinIntegrationMode> {

    static final int COLUMN_NAME=0;

    public static final String
        NONE="none",
        POP3="pop3",
        IMAP="imap"
    ;

    public static final String DEFAULT_SPAMASSASSIN_INTEGRATION_MODE=POP3;

    private String display;
    private int sort_order;

    public Object getColumn(int i) {
        Profiler.startProfile(Profiler.FAST, EmailSpamAssassinIntegrationMode.class, "getColValueImpl()", null);
        try {
            switch(i) {
                case COLUMN_NAME: return pkey;
                case 1: return display;
                case 2: return Integer.valueOf(sort_order);
                default: throw new IllegalArgumentException("Invalid index: "+i);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public String getName() {
        return pkey;
    }

    public String getDisplay() {
        return display;
    }

    public int getSortOrder() {
        return sort_order;
    }

    protected int getTableIDImpl() {
        return SchemaTable.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
    }

    void initImpl(ResultSet results) throws SQLException {
        Profiler.startProfile(Profiler.FAST, EmailSpamAssassinIntegrationMode.class, "initImpl(ResultSet)", null);
        try {
            pkey=results.getString(1);
            display=results.getString(2);
            sort_order=results.getInt(3);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        Profiler.startProfile(Profiler.IO, EmailSpamAssassinIntegrationMode.class, "read(CompressedDataInputStream)", null);
        try {
            pkey=in.readUTF();
            display=in.readUTF();
            sort_order=in.readCompressedInt();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
        Profiler.startProfile(Profiler.IO, EmailSpamAssassinIntegrationMode.class, "write(CompressedDataOutputStream,String)", null);
        try {
            out.writeUTF(pkey);
            out.writeUTF(display);
            out.writeCompressedInt(sort_order);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}