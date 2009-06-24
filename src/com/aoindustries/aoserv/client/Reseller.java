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
import java.util.ArrayList;
import java.util.List;

/**
 * A reseller may handle support tickets..
 *
 * @see  Business
 * @see  Brand
 *
 * @author  AO Industries, Inc.
 */
final public class Reseller extends CachedObjectStringKey<Reseller> {

    static final int COLUMN_ACCOUNTING = 0;
    static final String COLUMN_ACCOUNTING_name = "accounting";

    private boolean ticket_auto_escalate;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_ACCOUNTING : return pkey;
            case 1: return ticket_auto_escalate;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Brand getBrand() throws SQLException, IOException {
        Brand br = table.connector.getBrands().get(pkey);
        if(br==null) throw new SQLException("Unable to find Brand: "+pkey);
        return br;
    }

    public boolean getTicketAutoEscalate() {
        return ticket_auto_escalate;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.RESELLERS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey = result.getString(pos++);
        ticket_auto_escalate = result.getBoolean(pos++);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        ticket_auto_escalate = in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeBoolean(ticket_auto_escalate);
    }

    public List<TicketAssignment> getTicketAssignments() throws IOException, SQLException {
        return table.connector.getTicketAssignments().getTicketAssignments(this);
    }

    /**
     * Gets the immediate parent of this reseller or <code>null</code> if none available.
     */
    public Reseller getParentReseller() throws IOException, SQLException {
        Business bu = getBrand().getBusiness();
        if(bu==null) return null;
        Business parent = bu.getParentBusiness();
        while(parent!=null) {
            Brand parentBrand = parent.getBrand();
            if(parentBrand!=null) {
                Reseller parentReseller = parentBrand.getReseller();
                if(parentReseller!=null) return parentReseller;
            }
        }
        return null;
    }

    /**
     * The children of the resller are any resellers that have their closest parent
     * business (that is a reseller) equal to this one.
     */
    public List<Reseller> getChildResellers() throws IOException, SQLException {
        List<Reseller> children = new ArrayList<Reseller>();
        for(Reseller reseller : table.connector.getResellers().getRows()) {
            if(!reseller.equals(this) && this.equals(reseller.getParentReseller())) children.add(reseller);
        }
        return children;
    }
}