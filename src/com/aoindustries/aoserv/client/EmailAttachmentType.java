package com.aoindustries.aoserv.client;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;

/**
 * An <code>EmailAttachmentType</code> represents one extension that may
 * be blocked by virus filters.
 *
 * @see  EmailAttachmentBlock
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class EmailAttachmentType extends GlobalObjectStringKey<EmailAttachmentType> {

    static final int COLUMN_EXTENSION=0;
    static final String COLUMN_EXTENSION_name = "extension";

    private String description;
    private boolean is_default_block;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_EXTENSION: return pkey;
            case 1: return description;
            case 2: return is_default_block?Boolean.TRUE:Boolean.FALSE;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getExtension() {
        return pkey;
    }
    
    public String getDescription() {
        return description;
    }

    public boolean isDefaultBlock() {
        return is_default_block;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.EMAIL_ATTACHMENT_TYPES;
    }

    public void init(ResultSet result) throws SQLException {
	pkey=result.getString(1);
        description=result.getString(2);
        is_default_block=result.getBoolean(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        description=in.readUTF();
        is_default_block=in.readBoolean();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
        out.writeUTF(description);
        out.writeBoolean(is_default_block);
    }
}