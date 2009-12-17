package com.aoindustries.aoserv.client;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * A limited number of hosts may connect to a <code>AOServer</code>'s daemon,
 * each is configured as an <code>AOServerDaemonHost</code>.
 *
 * @see  Server
 *
 * @author  AO Industries, Inc.
 */
public final class AOServerDaemonHost extends CachedObjectIntegerKey<AOServerDaemonHost> {

    static final int
        COLUMN_PKEY=0,
        COLUMN_AO_SERVER=1
    ;
    static final String COLUMN_AO_SERVER_name = "ao_server";
    static final String COLUMN_HOST_name = "host";

    int aoServer;
    private String host;

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY: return Integer.valueOf(pkey);
            case COLUMN_AO_SERVER: return Integer.valueOf(aoServer);
            case 2: return host;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public String getHost() {
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
        pkey=result.getInt(1);
        aoServer=result.getInt(2);
        host=result.getString(3);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey=in.readCompressedInt();
        aoServer=in.readCompressedInt();
        host=in.readUTF().intern();
    }

    @Override
    String toStringImpl(Locale userLocale) throws IOException, SQLException {
    	return host+"->"+getAOServer().toStringImpl(userLocale);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeCompressedInt(pkey);
        out.writeCompressedInt(aoServer);
        out.writeUTF(host);
    }

    public List<? extends AOServObject> getDependencies() throws IOException, SQLException {
        return createDependencyList(
            getAOServer()
        );
    }

    public List<? extends AOServObject> getDependentObjects() throws IOException, SQLException {
        return createDependencyList(
        );
    }
}