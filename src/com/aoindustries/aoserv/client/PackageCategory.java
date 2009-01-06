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
 * A <code>PackageCategory</code> represents one type of service
 *
 * @see  PackageDefinition
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
public final class PackageCategory extends GlobalObjectStringKey<PackageCategory> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    public static final String
        AOSERV="aoserv",
        BACKUP="backup",
        COLOCATION="colocation",
        DEDICATED="dedicated",
        MANAGED="managed",
        SYSADMIN="sysadmin",
        VIRTUAL="virtual",
        VIRTUAL_DEDICATED="virtual_dedicated",
        VIRTUAL_MANAGED="virtual_managed"
    ;

    private String display;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return display;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getName() {
        return pkey;
    }

    public String getDisplay() {
        return display;
    }

    public SchemaTable.TableID getTableID() {
        return SchemaTable.TableID.PACKAGE_CATEGORIES;
    }

    public void init(ResultSet results) throws SQLException {
        pkey=results.getString(1);
        display=results.getString(2);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readUTF().intern();
        display=in.readUTF();
    }

    String toStringImpl() {
        return display;
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeUTF(pkey);
        out.writeUTF(display);
    }
}