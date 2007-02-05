package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
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

    private String
        description,
        protected_net
    ;
    private boolean allow_same_server_backup;
    private String backup_farm;
    private int owner;

    public Object getColumn(int i) {
        switch(i) {
            case COLUMN_NAME: return pkey;
            case 1: return description;
            case 2: return protected_net;
            case 3: return allow_same_server_backup?Boolean.TRUE:Boolean.FALSE;
            case 4: return backup_farm;
            case 5: return Integer.valueOf(owner);
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public boolean allowSameServerBackup() {
        return allow_same_server_backup;
    }

    public ServerFarm getBackupFarm() {
        ServerFarm sf=((ServerFarmTable)table).get(backup_farm);
        if(sf==null) throw new WrappedException(new SQLException("Unable to find ServerFarm: "+backup_farm));
        return sf;
    }

    public Package getOwner() {
        // May be filtered
        return table.connector.packages.get(owner);
    }

    public String getDescription() {
	return description;
    }

    public String getName() {
	return pkey;
    }

    public String getProtectedNet() {
	return protected_net;
    }

    protected int getTableIDImpl() {
	return SchemaTable.SERVER_FARMS;
    }

    void initImpl(ResultSet result) throws SQLException {
	pkey = result.getString(1);
	description = result.getString(2);
	protected_net = result.getString(3);
        allow_same_server_backup = result.getBoolean(4);
        backup_farm = result.getString(5);
        owner = result.getInt(6);
    }

    public void read(CompressedDataInputStream in) throws IOException {
	pkey=in.readUTF();
	description=in.readUTF();
	protected_net=in.readUTF();
        allow_same_server_backup=in.readBoolean();
        backup_farm=in.readUTF();
        owner=in.readCompressedInt();
    }

    String toStringImpl() {
	return description;
    }

    public void write(CompressedDataOutputStream out, String version) throws IOException {
	out.writeUTF(pkey);
	out.writeUTF(description);
	out.writeUTF(protected_net);
        out.writeBoolean(allow_same_server_backup);
        out.writeUTF(backup_farm);
        if(AOServProtocol.compareVersions(version, AOServProtocol.VERSION_1_0_A_102)>=0) out.writeCompressedInt(owner);
    }
}