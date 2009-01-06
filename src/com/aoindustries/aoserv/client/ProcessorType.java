package com.aoindustries.aoserv.client;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * All of the types of processors.
 *
 * @author  AO Industries, Inc.
 */
final public class ProcessorType extends GlobalObjectStringKey<ProcessorType> {

    static final int COLUMN_TYPE = 0;
    static final int COLUMN_SORT_ORDER = 1;
    
    static final String COLUMN_SORT_ORDER_name = "sort_order";

    private short sortOrder;

    @Override
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_TYPE: return pkey;
            case COLUMN_SORT_ORDER : return sortOrder;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.PROCESSOR_TYPES;
    }

    public String getType() {
        return pkey;
    }

    public short getSortOrder() {
        return sortOrder;
    }


    public void init(ResultSet result) throws SQLException {
        pkey = result.getString(1);
        sortOrder = result.getShort(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        sortOrder = in.readShort();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeShort(sortOrder);
    }
}