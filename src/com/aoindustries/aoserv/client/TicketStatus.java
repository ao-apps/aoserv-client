package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import static com.aoindustries.aoserv.client.ApplicationResources.accessor;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <code>TicketStatus</code> of a <code>Ticket</code> changes
 * through each step of its life cycle.
 *
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketStatus extends GlobalObjectStringKey<TicketStatus> implements Comparable<TicketStatus> {

    static final int COLUMN_STATUS = 0;
    static final int COLUMN_SORT_ORDER = 1;
    static final String COLUMN_STATUS_name = "status";
    static final String COLUMN_SORT_ORDER_name = "sort_order";

    /**
     * The different ticket statuses.
     */
    public static final String
        JUNK="junk",
        DELETED="deleted",
        CLOSED="closed",
        BOUNCED="bounced",
        HOLD="hold",
        OPEN="open"
    ;

    private short sort_order;

    Object getColumnImpl(int i) {
        if(i==COLUMN_STATUS) return pkey;
        if(i==1) return sort_order;
        throw new IllegalArgumentException("Invalid index: "+i);
    }

    public short getSortOrder() {
        return sort_order;
    }

    public String getStatus() {
        return pkey;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_STATI;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        sort_order = result.getShort(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readUTF().intern();
        sort_order = in.readShort();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_43)<=0) out.writeUTF(pkey);
        if(version.compareTo(AOServProtocol.Version.VERSION_1_44)>=0) out.writeShort(sort_order);
    }

    @Override
    String toStringImpl() {
        return accessor.getMessage("TicketStatus."+pkey+".toString");
    }

    /**
     * Localized description.
     */
    public String getDescription() {
        return accessor.getMessage("TicketStatus."+pkey+".description");
    }

    public int compareTo(TicketStatus o) {
        short sortOrder1 = sort_order;
        short sortOrder2 = o.sort_order;
        if(sortOrder1<sortOrder2) return -1;
        if(sortOrder1>sortOrder2) return 1;
        return 0;
    }
}