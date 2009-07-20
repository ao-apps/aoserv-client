package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketAssignment extends CachedObjectIntegerKey<TicketAssignment> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_TICKET=1,
        COLUMN_RESELLER=2,
        COLUMN_ADMINISTRATOR=3
    ;
    static final String COLUMN_PKEY_name = "pkey";
    static final String COLUMN_TICKET_name = "ticket";
    static final String COLUMN_RESELLER_name = "reseller";
    static final String COLUMN_ADMINISTRATOR_name = "administrator";

    private int ticket;
    private String reseller;
    private String administrator;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_TICKET: return ticket;
            case COLUMN_RESELLER: return reseller;
            case COLUMN_ADMINISTRATOR: return administrator;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Ticket getTicket() throws IOException, SQLException {
        Ticket t = table.connector.getTickets().get(ticket);
        if(t==null) throw new SQLException("Unable to find Ticket: "+ticket);
        return t;
    }

    public Reseller getReseller() throws IOException, SQLException {
        Reseller r = table.connector.getResellers().get(reseller);
        if(r==null) throw new SQLException("Unable to find Reseller: "+reseller);
        return r;
    }

    public BusinessAdministrator getBusinessAdministrator() throws IOException, SQLException {
        BusinessAdministrator ba = table.connector.getBusinessAdministrators().get(administrator);
        if(ba==null) throw new SQLException("Unable to find BusinessAdministrator: "+administrator);
        return ba;
        //Username un=table.connector.getUsernames().get(administrator);
        //if(un==null) return null;
        //return un.getBusinessAdministrator();
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_ASSIGNMENTS;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        ticket = result.getInt(2);
        reseller = result.getString(3);
        administrator = result.getString(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        ticket = in.readCompressedInt();
        reseller = in.readUTF().intern();
        administrator = in.readUTF().intern();
    }

    @Override
    String toStringImpl(Locale userLocale) {
        return ticket+"|"+pkey+'|'+reseller+'|'+administrator;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ticket);
        out.writeUTF(reseller);
        out.writeUTF(administrator);
    }
}