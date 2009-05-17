package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @see  Ticket
 *
 * @author  AO Industries, Inc.
 */
final public class TicketCategory extends CachedObjectIntegerKey<TicketCategory> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_PARENT=1,
        COLUMN_NAME=2
    ;
    static final String COLUMN_PKEY_name = "pkey";
    static final String COLUMN_PARENT_name = "parent";
    static final String COLUMN_NAME_name = "name";

    private int parent;
    private String name;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return pkey;
            case COLUMN_PARENT: return parent==-1 ? null : parent;
            case COLUMN_NAME: return name;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    /**
     * Gets the parent category or <code>null</code> if this is a top-level category.
     */
    public TicketCategory getParent() throws IOException, SQLException {
        if(parent==-1) return null;
        TicketCategory tc = table.connector.getTicketCategories().get(parent);
        if(tc==null) throw new SQLException("Unable to find TicketCategory: "+parent);
        return tc;
    }

    public String getName() {
        return name;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.TICKET_CATEGORIES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        parent = result.getInt(2);
        if(result.wasNull()) parent = -1;
        name = result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey = in.readCompressedInt();
        parent = in.readCompressedInt();
        name = in.readUTF().intern();
    }

    @Override
    String toStringImpl() {
        try {
            return parent==-1 ? name : (getParent()+"/"+name);
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(parent);
        out.writeUTF(name);
    }

    public List<TicketBrandCategory> getTicketBrandCategorys() throws IOException, SQLException {
        return table.connector.getTicketBrandCategories().getTicketBrandCategories(this);
    }

    public List<TicketCategory> getChildrenCategories() throws IOException, SQLException {
        return table.connector.getTicketCategories().getChildrenCategories(this);
    }
}