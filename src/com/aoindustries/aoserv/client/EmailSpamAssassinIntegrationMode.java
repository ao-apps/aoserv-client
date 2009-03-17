package com.aoindustries.aoserv.client;

/*
 * Copyright 2005-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
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
    static final String COLUMN_SORT_ORDER_name = "sort_order";

    public static final String
        NONE="none",
        POP3="pop3",
        IMAP="imap"
    ;

    public static final String DEFAULT_SPAMASSASSIN_INTEGRATION_MODE=POP3;

    private String display;
    private int sort_order;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return display;
            case 2: return Integer.valueOf(sort_order);
            default: throw new IllegalArgumentException("Invalid index: "+i);
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

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.EMAIL_SPAMASSASSIN_INTEGRATION_MODES;
    }

    public void init(ResultSet results) throws SQLException {
        pkey=results.getString(1);
        display=results.getString(2);
        sort_order=results.getInt(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        display=in.readUTF();
        sort_order=in.readCompressedInt();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(display);
        out.writeCompressedInt(sort_order);
    }
}