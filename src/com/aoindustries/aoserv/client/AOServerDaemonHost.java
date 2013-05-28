/*
 * Copyright 2001-2013 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.aoserv.client.validator.InetAddress;
import com.aoindustries.aoserv.client.validator.ValidationException;
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A limited number of hosts may connect to a <code>AOServer</code>'s daemon,
 * each is configured as an <code>AOServerDaemonHost</code>.
 *
 * @see  AOServer
 *
 * @author  AO Industries, Inc.
 */
public final class AOServerDaemonHost extends CachedObjectIntegerKey<AOServerDaemonHost>
    implements DtoFactory<com.aoindustries.aoserv.client.dto.AOServerDaemonHost> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_HOST_name = "host";

    int aoServer;
    private InetAddress host;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(aoServer);
            case 2: return host;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public InetAddress getHost() {
	return host;
    }

    public AOServer getAOServer() throws SQLException, IOException {
	AOServer ao=table.connector.getAoServers().get(aoServer);
	if(ao==null) throw new SQLException("Unable to find AOServer: "+aoServer);
	return ao;
    }

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.AO_SERVER_DAEMON_HOSTS;
    }

    public void init(ResultSet result) throws SQLException {
        try {
            pkey=result.getInt(1);
            aoServer=result.getInt(2);
            host=InetAddress.valueOf(result.getString(3));
        } catch(ValidationException e) {
            SQLException exc = new SQLException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    public void read(CompressedDataInputStream in) throws IOException {
        try {
            pkey=in.readCompressedInt();
            aoServer=in.readCompressedInt();
            host=InetAddress.valueOf(in.readUTF().intern());
        } catch(ValidationException e) {
            IOException exc = new IOException(e.getLocalizedMessage());
            exc.initCause(e);
            throw exc;
        }
    }

    @Override
    String toStringImpl() {
	return aoServer+"|"+host.toString();
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
	out.writeCompressedInt(pkey);
	out.writeCompressedInt(aoServer);
	out.writeUTF(host.toString());
    }

    // <editor-fold defaultstate="collapsed" desc="DTO">
    @Override
    public com.aoindustries.aoserv.client.dto.AOServerDaemonHost getDto() {
        return new com.aoindustries.aoserv.client.dto.AOServerDaemonHost(getPkey(), aoServer, getDto(host));
    }
    // </editor-fold>
}