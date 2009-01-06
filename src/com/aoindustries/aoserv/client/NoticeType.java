package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.sql.*;

/**
 * Each reason for notifying clients is represented by a
 * <code>NoticeType</code>.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class NoticeType extends GlobalObjectStringKey<NoticeType> {

    static final int COLUMN_TYPE=0;
    static final String COLUMN_TYPE_name = "type";

    private String description;

    public static final String
        NONPAY="nonpay",
        BADCARD="badcard",
        DISABLE_WARNING="disable_warning",
        DISABLED="disabled",
        ENABLED="enabled"
    ;

    public Object getColumn(int i) {
	if(i==COLUMN_TYPE) return pkey;
	if(i==1) return description;
	throw new IllegalArgumentException("Invalid index: "+i);
    }

    public String getDescription() {
	return description;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.NOTICE_TYPES;
    }

    public String getType() {
	return pkey;
    }

    public void init(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
    }
}