package com.aoindustries.aoserv.client;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import java.io.*;
import java.sql.*;
import java.util.List;

/**
 * Each <code>AOServer</code> has several entries in <code>/etc/aliases</code>
 * that do not belong to any particular <code>EmailDomain</code> or
 * <code>Package</code>.  These are a standard part of the email
 * configuration and are contained in <code>SystemEmailAlias</code>es.
 *
 * @version  1.0a
 *
 * @author  AO Industries, Inc.
 */
final public class SystemEmailAlias extends CachedObjectIntegerKey<SystemEmailAlias> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_ADDRESS_name = "address";

    int ao_server;
    private String address;
    private String destination;

    public String getAddress() {
	return address;
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(ao_server);
            case 2: return address;
            case 3: return destination;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getDestination() {
	return destination;
    }

    public AOServer getAOServer() throws SQLException, IOException {
	AOServer ao=table.connector.getAoServers().get(ao_server);
	if(ao==null) throw new SQLException("Unable to find AOServer: "+ao_server);
	return ao;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.SYSTEM_EMAIL_ALIASES;
    }

    public void init(ResultSet result) throws SQLException {
        pkey = result.getInt(1);
        ao_server = result.getInt(2);
        address = result.getString(3);
        destination = result.getString(4);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        ao_server=in.readCompressedInt();
        address=in.readUTF().intern();
        destination=in.readUTF().intern();
    }

    public List<AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getAOServer()
        );
    }

    public List<AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(ao_server);
        out.writeUTF(address);
        out.writeUTF(destination);
    }
}