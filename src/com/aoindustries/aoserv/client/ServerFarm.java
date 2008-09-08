package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;

/**
 * AO Industries provides greater reliability through the use of multiple network locations.
 * Each location is represented by a <code>ServerFarm</code> object.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class ServerFarm extends CachedObjectStringKey<ServerFarm> {

    static final int COLUMN_NAME=0;
    static final String COLUMN_NAME_name = "name";

    private String description;
    private int owner;
    private boolean use_restricted_smtp_port;

    @Override
    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return description;
            case 2: return Integer.valueOf(owner);
            case 3: return use_restricted_smtp_port;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public Package getOwner() {
        // May be filtered
        return table.connector.packages.get(owner);
    }

    public boolean useRestrictedSmtpPort() {
        return use_restricted_smtp_port;
    }

    public String getDescription() {
	return description;
    }

    public String getName() {
	return pkey;
    }

    @Override
    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SERVER_FARMS;
    }

    @Override
    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
        owner = result.getInt(3);
        use_restricted_smtp_port = result.getBoolean(4);
    }

    @Override
    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF().intern();
	description=in.readUTF();
        owner=in.readCompressedInt();
        use_restricted_smtp_port = in.readBoolean();
    }

    @Override
    String toStringImpl() {
	return description;
    }

    @Override
    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_30)<=0) {
            out.writeUTF("192.168.0.0/16");
            out.writeBoolean(false);
            out.writeUTF("mob");
        }
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_102)>=0) out.writeCompressedInt(owner);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_26)>=0) out.writeBoolean(use_restricted_smtp_port);
    }
}