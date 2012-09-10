/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * One network tracked by an <code>IpReputationSet</code>.
 *
 * @author  AO Industries, Inc.
 */
final public class IpReputationSetNetwork extends CachedObjectLongKey<IpReputationSetNetwork> {

    static final int
        COLUMN_PKEY = 0,
        COLUMN_SET  = 1
    ;
    static final String
        COLUMN_SET_name = "set",
        COLUMN_NETWORK_name  = "network"
    ;

    int set;
    private int network;
    private int counter;

    public SchemaTable.TableID getTableID() {
	return SchemaTable.TableID.IP_REPUTATION_SET_NETWORKS;
    }

    public void init(ResultSet result) throws SQLException {
        int pos = 1;
        pkey     = result.getLong(pos++);
        set      = result.getInt(pos++);
        network  = result.getInt(pos++);
        counter  = result.getInt(pos++);
    }

    public void write(CompressedDataOutputStream out, AOServProtocol.Version version) throws IOException {
        out.writeLong(pkey);
        out.writeCompressedInt(set);
        out.writeInt(network);
        out.writeInt(counter);
    }

    public void read(CompressedDataInputStream in) throws IOException {
        pkey     = in.readLong();
        set      = in.readCompressedInt();
        network  = in.readInt();
        counter  = in.readInt();
    }

    Object getColumnImpl(int i) {
        switch(i) {
            case COLUMN_PKEY : return pkey;
            case COLUMN_SET  : return set;
            case 2           : return network;
            case 3           : return counter;
            default: throw new IllegalArgumentException("Invalid index: "+i);
        }
    }

    public IpReputationSet getSet() throws SQLException, IOException {
        IpReputationSet obj = table.connector.getIpReputationSets().get(set);
        if(obj==null) throw new SQLException("Unable to find IpReputationSet: " + set);
        return obj;
    }

    /**
     * Gets the 32-bit network address, with network range bits zero.
     */
    public int getNetwork() {
        return network;
    }

    /**
     * Gets the current counter for this host.
     */
    public int getCounter() {
        return counter;
    }
}
